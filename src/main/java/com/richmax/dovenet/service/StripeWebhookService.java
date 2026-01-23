package com.richmax.dovenet.service;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;

public interface StripeWebhookService {

    Event constructEvent(String payload, String sigHeader, String endpointSecret)
            throws SignatureVerificationException;

    void handleEvent(Event event);
}
