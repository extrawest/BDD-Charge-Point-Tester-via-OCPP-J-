package com.extrawest.jsonserver.service;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import com.extrawest.jsonserver.model.emun.ImplementedMessagesSentType;
import com.extrawest.jsonserver.model.emun.ImplementedReceivedMessageType;
import com.extrawest.jsonserver.model.ChargePoint;
import com.extrawest.jsonserver.model.RequiredChargingData;
import eu.chargetime.ocpp.model.Confirmation;
import eu.chargetime.ocpp.model.Request;
import eu.chargetime.ocpp.model.core.ResetType;
import eu.chargetime.ocpp.model.remotetrigger.TriggerMessageRequestType;

public interface MessagingService {
    void sendRequest(String chargePointId, ImplementedMessagesSentType type, Map<String, String> params);

    void sendTriggerMessage(String chargePointId, TriggerMessageRequestType type);

    void sendResetMessage(String chargePointId, ResetType type);

    Optional<CompletableFuture<Confirmation>> waitForSuccessfulResponse(UUID sessionIndex, int waitingTimeSec);

    Optional<Request> waitForRequestedMessage(ChargePoint chargePoint, int waitingTimeSec, ImplementedReceivedMessageType type);

    void validateReceivedMessageOrThrow(ChargePoint chargePoint, RequiredChargingData requiredData, Request request);

    void validateRequest(Map<String, String> parameters, Request request);

    Confirmation sendConfirmationResponse(Map<String, String> parameters, Confirmation response);
}
