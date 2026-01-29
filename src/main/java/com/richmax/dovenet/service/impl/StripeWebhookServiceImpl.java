package com.richmax.dovenet.service.impl;

import com.richmax.dovenet.repository.UserRepository;
import com.richmax.dovenet.repository.data.User;
import com.richmax.dovenet.service.StripeWebhookService;
import com.richmax.dovenet.stripe.StripePriceResolver;
import com.richmax.dovenet.types.BillingPeriod;
import com.richmax.dovenet.types.SubscriptionType;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class StripeWebhookServiceImpl implements StripeWebhookService {

    private final UserRepository userRepository;
    private final StripePriceResolver priceResolver;

    public StripeWebhookServiceImpl(UserRepository userRepository,
                                    StripePriceResolver priceResolver) {
        this.userRepository = userRepository;
        this.priceResolver = priceResolver;
    }

    @Override
    public Event constructEvent(String payload, String sigHeader, String endpointSecret) throws SignatureVerificationException {
        return Webhook.constructEvent(payload, sigHeader, endpointSecret);
    }

    @Override
    @Transactional
    public void handleEvent(Event event) {
        String type = event.getType();
        System.out.println("Stripe Webhook received: " + type);

        switch (type) {
            case "checkout.session.completed":
                handleCheckoutSessionCompleted(event);
                break;

            case "customer.subscription.created":
            case "customer.subscription.updated":
                handleSubscriptionCreatedOrUpdated(event);
                break;

            case "customer.subscription.deleted":
                handleSubscriptionDeleted(event);
                break;
            case "payment_intent.succeeded":
            case "charge.succeeded":
                // Optional: log or track one-time payments
                break;

            case "payment_method.attached":
                // Usually no action needed
                break;

            default:
                // ignore other events
                break;
        }
    }

    private void handleCheckoutSessionCompleted(Event event) {
        EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
        StripeObject stripeObject = null;
        try {
            stripeObject = dataObjectDeserializer.deserializeUnsafe();
        } catch (Exception e) {
             System.err.println("Failed to deserialize session object for event: " + event.getId() + " Error: " + e.getMessage());
             return;
        }
        
        if (stripeObject == null) {
            System.err.println("Failed to deserialize session object for event: " + event.getId());
            return;
        }
        
        Session session = (Session) stripeObject;

        if (session.getSubscription() != null) {
            try {
                com.stripe.model.Subscription subscription = com.stripe.model.Subscription.retrieve(session.getSubscription());
                updateUserFromSubscription(subscription);
            } catch (StripeException e) {
                System.err.println("Error retrieving subscription from Stripe: " + e.getMessage());
            }
        }
    }

    private void handleSubscriptionCreatedOrUpdated(Event event) {
        EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
        StripeObject stripeObject = null;
        try {
            stripeObject = dataObjectDeserializer.deserializeUnsafe();
        } catch (Exception e) {
             System.err.println("Failed to deserialize subscription object for event: " + event.getId() + " Error: " + e.getMessage());
             return;
        }
        
        if (stripeObject == null) {
            System.err.println("Failed to deserialize subscription object for event: " + event.getId());
            return;
        }

        com.stripe.model.Subscription subscription = (com.stripe.model.Subscription) stripeObject;
        updateUserFromSubscription(subscription);
    }

    private void updateUserFromSubscription(com.stripe.model.Subscription subscription) {
        String customerId = subscription.getCustomer();
        System.out.println("Processing subscription for Customer ID: " + customerId);

        Optional<User> optionalUser = userRepository.findByStripeCustomerId(customerId);
        if (optionalUser.isEmpty()) {
            System.out.println("User not found for Stripe Customer ID: " + customerId);
            return;
        }

        User user = optionalUser.get();
        System.out.println("Found user: " + user.getUsername());

        user.setStripeSubscriptionId(subscription.getId());

        String priceId = subscription.getItems().getData().get(0).getPrice().getId();
        System.out.println("Subscription Price ID: " + priceId);

        SubscriptionType type = priceResolver.resolvePriceToSubscriptionType(priceId);
        System.out.println("Resolved Subscription Type: " + type);

        user.setSubscription(type);
        
        // Update auto-renew status
        user.setAutoRenew(!subscription.getCancelAtPeriodEnd());

        // Manual calculation since getCurrentPeriodEnd() is missing in this library version
        BillingPeriod period = priceResolver.resolvePriceToBillingPeriod(priceId);
        LocalDateTime validUntil = LocalDateTime.now();
        if (period == BillingPeriod.MONTHLY) {
            validUntil = validUntil.plusMonths(1);
        } else {
            validUntil = validUntil.plusYears(1);
        }
        user.setSubscriptionValidUntil(validUntil);

        userRepository.save(user);
        System.out.println("User updated successfully.");
    }

    private void handleSubscriptionDeleted(Event event) {
        EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
        StripeObject stripeObject = null;
        try {
            stripeObject = dataObjectDeserializer.deserializeUnsafe();
        } catch (Exception e) {
             System.err.println("Failed to deserialize subscription object for event: " + event.getId() + " Error: " + e.getMessage());
             return;
        }
        
        if (stripeObject == null) {
            System.err.println("Failed to deserialize subscription object for event: " + event.getId());
            return;
        }

        com.stripe.model.Subscription subscription = (com.stripe.model.Subscription) stripeObject;
        String customerId = subscription.getCustomer();
        Optional<User> optionalUser = userRepository.findByStripeCustomerId(customerId);
        
        optionalUser.ifPresent(user -> {
            // IMPORTANT: Only revert to FREE if the deleted subscription is the one we have on record.
            // This prevents an old, canceled subscription from overwriting a newer, active one.
            if (subscription.getId().equals(user.getStripeSubscriptionId())) {
                user.setSubscription(SubscriptionType.FREE);
                user.setStripeSubscriptionId(null);
                user.setSubscriptionValidUntil(null);
                user.setAutoRenew(false);
                userRepository.save(user);
                System.out.println("Active subscription " + subscription.getId() + " deleted for user: " + user.getUsername() + ". Reverting to FREE.");
            } else {
                System.out.println("Ignoring deletion of old subscription " + subscription.getId() + " for user: " + user.getUsername() + ". Active subscription is " + user.getStripeSubscriptionId());
            }
        });
    }
}
