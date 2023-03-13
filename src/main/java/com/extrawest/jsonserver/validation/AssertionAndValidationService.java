package com.extrawest.jsonserver.validation;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import com.extrawest.jsonserver.model.emun.ImplementedMessageType;
import com.extrawest.jsonserver.validation.outgoing.OutgoingMessageFactory;
import eu.chargetime.ocpp.model.Confirmation;
import eu.chargetime.ocpp.model.Request;

public interface AssertionAndValidationService {

    ImplementedMessageType getMessageTypeAfterValidationAndAssertion(Request request, Map<String, String> parameters);

    void assertConfirmationMessage(Map<String, String> parameters,
                                   CompletableFuture<Confirmation> completableFuture);

    OutgoingMessageFactory<? extends Request> getOutgoingRequestFactory(ImplementedMessageType type);

    OutgoingMessageFactory<? extends Confirmation> getOutgoingConfirmationFactory(ImplementedMessageType type);

}
