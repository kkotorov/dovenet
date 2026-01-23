package com.richmax.dovenet.controller;

import com.richmax.dovenet.service.StripeWebhookService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/webhooks/stripe")
public class StripeWebhookController {

    private final StripeWebhookService webhookService;
    private final String endpointSecret;

    public StripeWebhookController(StripeWebhookService webhookService,
                                   @Value("${stripe.webhook-secret}") String endpointSecret) {
        this.webhookService = webhookService;
        this.endpointSecret = endpointSecret;
    }

    @PostMapping
    public ResponseEntity<String> handleWebhook(@RequestBody String payload,
                                                @RequestHeader("Stripe-Signature") String sigHeader) {
        try {
            Event event = webhookService.constructEvent(payload, sigHeader, endpointSecret);
            webhookService.handleEvent(event);
            return ResponseEntity.ok("Received");
        } catch (SignatureVerificationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid signature");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Webhook handling failed");
        }
    }
}
