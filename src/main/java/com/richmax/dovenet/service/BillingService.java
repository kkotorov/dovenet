package com.richmax.dovenet.service;

import com.richmax.dovenet.types.BillingPeriod;
import com.richmax.dovenet.types.SubscriptionType;

public interface BillingService {
    /**
     * Creates a Stripe checkout session and returns the URL
     */
    String createCheckoutSession(String username, SubscriptionType type, BillingPeriod period);
}
