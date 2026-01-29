package com.richmax.dovenet.service.impl;

import com.richmax.dovenet.repository.UserRepository;
import com.richmax.dovenet.repository.data.User;
import com.richmax.dovenet.service.BillingService;
import com.richmax.dovenet.service.UserService;
import com.richmax.dovenet.stripe.StripePriceResolver;
import com.richmax.dovenet.types.BillingPeriod;
import com.richmax.dovenet.types.SubscriptionType;
import com.stripe.exception.StripeException;
import com.stripe.model.Subscription;
import com.stripe.model.checkout.Session;
import com.stripe.param.SubscriptionUpdateParams;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.stereotype.Service;

@Service
public class BillingServiceImpl implements BillingService {

    private final UserService userService;
    private final StripePriceResolver stripePriceResolver;
    private final UserRepository userRepository;

    public BillingServiceImpl(UserService userService, StripePriceResolver stripePriceResolver, UserRepository userRepository) {
        this.userService = userService;
        this.stripePriceResolver = stripePriceResolver;
        this.userRepository = userRepository;
    }

    @Override
    public String createCheckoutSession(String username, SubscriptionType type, BillingPeriod period) {
        if (type == SubscriptionType.FREE) {
            throw new IllegalArgumentException("Invalid subscription type");
        }

        User user = userService.findByUsername(username);
        String customerId = userService.getOrCreateStripeCustomer(user);
        String priceId = stripePriceResolver.resolve(type, period);

        try {
            SessionCreateParams params = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                    .setCustomer(customerId)
                    .addLineItem(
                            SessionCreateParams.LineItem.builder()
                                    .setPrice(priceId)
                                    .setQuantity(1L)
                                    .build()
                    )
                    .setSuccessUrl("https://www.dovenet.eu/billing/success")
                    .setCancelUrl("https://www.dovenet.eu/billing/cancel")
                    .setAutomaticTax(SessionCreateParams.AutomaticTax.builder().setEnabled(true).build())
                    .setCustomerUpdate(
                            SessionCreateParams.CustomerUpdate.builder()
                                    .setAddress(SessionCreateParams.CustomerUpdate.Address.AUTO)
                                    .build()
                    )
                    .build();
            Session session = Session.create(params);

            return session.getUrl();

        } catch (StripeException e) {
            e.printStackTrace();
            throw new RuntimeException("Stripe payment failed: " + e.getStripeError().getMessage(), e);
        }
    }

    @Override
    public void cancelSubscription(String username) {
        User user = userService.findByUsername(username);
        String subscriptionId = user.getStripeSubscriptionId();

        if (subscriptionId == null || subscriptionId.isEmpty()) {
            throw new IllegalArgumentException("No active subscription found for user");
        }

        try {
            Subscription subscription = Subscription.retrieve(subscriptionId);
            
            SubscriptionUpdateParams params = SubscriptionUpdateParams.builder()
                    .setCancelAtPeriodEnd(true)
                    .build();
            
            subscription.update(params);

            // Update our DB immediately for instant UI feedback
            user.setAutoRenew(false);
            userRepository.save(user);
            
        } catch (StripeException e) {
            throw new RuntimeException("Failed to cancel subscription: " + e.getMessage(), e);
        }
    }
}
