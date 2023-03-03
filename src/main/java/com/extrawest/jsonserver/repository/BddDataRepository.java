package com.extrawest.jsonserver.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import com.extrawest.jsonserver.model.emun.ImplementedMessageType;
import eu.chargetime.ocpp.model.Confirmation;
import eu.chargetime.ocpp.model.Request;
import eu.chargetime.ocpp.model.remotetrigger.TriggerMessageRequestType;

public interface BddDataRepository {
    void addTestingChargePoint(String chargePointId) ;

    boolean isContainsTestingChargePoint(String chargePointId);

    void removeChargePointFromTestingList(String chargePointId);

    void addChargingChargePoint(String chargePointId);

    void removeChargePointFromChargingList(String chargePointId);

    void addCompleted(String uniqueId, CompletableFuture<Confirmation> completedPromise);

    Optional<CompletableFuture<Confirmation>> getCompleted(UUID sessionIndex);

    void addUniqueId(String uniqueId, UUID sessionIndex);

    void addRequestedMessageType(String chargePointId, TriggerMessageRequestType type);

    void addRequestedMessageType(String chargePointId, ImplementedMessageType type);

    Optional<List<ImplementedMessageType>> getRequestedMessageTypes(String chargePointId);

    void addRequestedMessage(String chargePointId, Request request);

    Optional<List<Request>> getRequestedMessage(String chargePointId);

    void removeRequestedMessageType(String chargePointId, ImplementedMessageType requestedMessageType);

    void removeRequestedMessage(String chargePointId, Request request);

    void removeRequestedMessages(String chargePointId, List<Request> requestsForDelete);

    void testFinished(String chargePointId);

    boolean isCharging(String chargePointId);
}
