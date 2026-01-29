package com.richmax.dovenet.controller;

import com.richmax.dovenet.service.BillingService;
import com.richmax.dovenet.types.BillingPeriod;
import com.richmax.dovenet.types.SubscriptionType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/billing")
public class BillingController {

    private final BillingService billingService;

    public BillingController(BillingService billingService) {
        this.billingService = billingService;
    }

    @PostMapping("/checkout")
    public Map<String, String> checkout(
            @RequestParam SubscriptionType type,
            @RequestParam BillingPeriod period,
            Authentication authentication
    ) {
        String url = billingService.createCheckoutSession(authentication.getName(), type, period);
        return Map.of("url", url);
    }

    @PostMapping("/cancel-subscription")
    public ResponseEntity<String> cancelSubscription(Authentication authentication) {
        try {
            billingService.cancelSubscription(authentication.getName());
            return ResponseEntity.ok("Subscription will be cancelled at the end of the billing period.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}
