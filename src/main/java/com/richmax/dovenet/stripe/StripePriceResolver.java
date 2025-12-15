package com.richmax.dovenet.stripe;

import com.richmax.dovenet.types.BillingPeriod;
import com.richmax.dovenet.types.SubscriptionType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class StripePriceResolver {

    @Value("${stripe.price.premium.monthly}")
    private String premiumMonthly;

    @Value("${stripe.price.premium.yearly}")
    private String premiumYearly;

    @Value("${stripe.price.pro.monthly}")
    private String proMonthly;

    @Value("${stripe.price.pro.yearly}")
    private String proYearly;

    public String resolve(SubscriptionType type, BillingPeriod period) {
        return switch (type) {
            case PREMIUM -> period == BillingPeriod.MONTHLY ? premiumMonthly : premiumYearly;
            case PRO -> period == BillingPeriod.MONTHLY ? proMonthly : proYearly;
            default -> throw new IllegalArgumentException("FREE has no Stripe price");
        };
    }
}
