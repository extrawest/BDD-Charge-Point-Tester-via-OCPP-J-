package com.extrawest.jsonserver.service;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import com.extrawest.jsonserver.model.emun.ImplementedMessageType;
import com.extrawest.jsonserver.model.ChargePoint;
import eu.chargetime.ocpp.model.Confirmation;
import eu.chargetime.ocpp.model.Request;

public interface MessagingService {
    ImplementedMessageType sendRequest(String chargePointId, ImplementedMessageType type, Map<String, String> params);

    Optional<CompletableFuture<Confirmation>> waitForSuccessfulResponse(UUID sessionIndex, int waitingTimeSec,
                                                                        Map<String, String> parameters);

    Optional<Request> waitForRequestedMessage(ChargePoint chargePoint, int waitingTimeSec, ImplementedMessageType type);

    void validateRequest(Map<String, String> parameters, Request request);

    Confirmation sendConfirmationResponse(Map<String, String> parameters, Confirmation response);

    void assertConfirmationMessage(Map<String, String> params, CompletableFuture<Confirmation> completableFuture);

}
