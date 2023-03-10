package com.extrawest.jsonserver.repository.impl;

import static com.extrawest.jsonserver.util.TimeUtil.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import com.extrawest.jsonserver.model.emun.ImplementedMessageType;
import com.extrawest.jsonserver.repository.BddDataRepository;
import com.extrawest.jsonserver.repository.ServerSessionRepository;
import eu.chargetime.ocpp.model.Confirmation;
import eu.chargetime.ocpp.model.Request;
import eu.chargetime.ocpp.model.remotetrigger.TriggerMessageRequestType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BddDataRepositoryImpl implements BddDataRepository {
    private final ServerSessionRepository serverSessionRepository;

    // <chargePointId>
    private final List<String> chargingChargePoints = Collections.synchronizedList(new ArrayList<>());
    // <uniqueId, promise>
    private final Map<String, CompletableFuture<Confirmation>> completedPromises = new ConcurrentHashMap<>();
    // <chargePointId, type of requested messages>
    private final Map<String, List<ImplementedMessageType>> requestedMessageTypes = new ConcurrentHashMap<>();
    // <chargePointId, message>
    private final Map<String, List<Request>> requestedMessages = new ConcurrentHashMap<>();
    // <chargePointId>
    private final List<String> testingChargePoints = Collections.synchronizedList(new ArrayList<>());
    // <chargePointId, uniqueId>
    private final Map<String, String> uniqueIdByChargePointId = new ConcurrentHashMap<>();

    @Override
    public void addTestingChargePoint(String chargePointId) {
        if (!testingChargePoints.contains(chargePointId)) {
            testingChargePoints.add(chargePointId);
        }
    }

    @Override
    public boolean isContainsTestingChargePoint(String chargePointId) {
        return testingChargePoints.contains(chargePointId);
    }

    @Override
    public void removeChargePointFromTestingList(String chargePointId) {
        testingChargePoints.remove(chargePointId);
    }

    @Override
    public void addChargingChargePoint(String chargePointId) {
        if (!chargingChargePoints.contains(chargePointId)) {
            chargingChargePoints.add(chargePointId);
        }
    }

    @Override
    public void removeChargePointFromChargingList(String chargePointId) {
        chargingChargePoints.remove(chargePointId);
    }

    @Override
    public void addCompleted(String uniqueId, CompletableFuture<Confirmation> completedPromise) {
        completedPromises.put(uniqueId, completedPromise);
    }

    @Override
    public Optional<CompletableFuture<Confirmation>> getCompleted(UUID sessionIndex) {
        waitOneSecond();
        String chargePointId = serverSessionRepository.getChargerIdBySession(sessionIndex);
        if (Objects.isNull(chargePointId)) {
            return Optional.empty();
        }
        String uniqueId = uniqueIdByChargePointId.getOrDefault(chargePointId, null);
        if (Objects.isNull(uniqueId)) {
            return Optional.empty();
        }        return Optional.ofNullable(completedPromises.getOrDefault(uniqueId, null));
    }

    @Override
    public void addUniqueId(String uniqueId, UUID sessionIndex) {
        String chargePointId = serverSessionRepository.getChargerIdBySession(sessionIndex);
        uniqueIdByChargePointId.put(chargePointId, uniqueId);
    }

    @Override
    public void addRequestedMessageType(String chargePointId, TriggerMessageRequestType type) {
        List<ImplementedMessageType> messageTypes = requestedMessageTypes.getOrDefault(chargePointId, new ArrayList<>());
        messageTypes.add(ImplementedMessageType.fromValue(type.name()));
        requestedMessageTypes.put(chargePointId, messageTypes);
    }

    @Override
    public void addRequestedMessageType(String chargePointId, ImplementedMessageType type) {
        List<ImplementedMessageType> messageTypes = requestedMessageTypes.getOrDefault(chargePointId, new ArrayList<>());
        messageTypes.add(type);
        requestedMessageTypes.put(chargePointId, messageTypes);
    }

    @Override
    public Optional<List<ImplementedMessageType>> getRequestedMessageTypes(String chargePointId) {
        return Optional.ofNullable(requestedMessageTypes.getOrDefault(chargePointId, null));
    }

    @Override
    public void addRequestedMessage(String chargePointId, Request request) {
        List<Request> requests = requestedMessages.getOrDefault(chargePointId, new ArrayList<>());
        requests.add(request);
        requestedMessages.put(chargePointId, requests);
    }

    @Override
    public Optional<List<Request>> getRequestedMessage(String chargePointId) {
        return Optional.ofNullable(requestedMessages.getOrDefault(chargePointId, null));
    }

    @Override
    public void removeRequestedMessageType(String chargePointId, ImplementedMessageType requestedMessageType) {
        List<ImplementedMessageType> messageTypes = requestedMessageTypes.get(chargePointId);
        messageTypes.remove(requestedMessageType);
        requestedMessageTypes.put(chargePointId, messageTypes);
    }

    @Override
    public void removeRequestedMessage(String chargePointId, Request request) {
        List<Request> requests = requestedMessages.get(chargePointId);
        requests.remove(request);
        requestedMessages.put(chargePointId, requests);
    }

    @Override
    public void removeRequestedMessages(String chargePointId, List<Request> requestsForDelete) {
        List<Request> requests = requestedMessages.get(chargePointId);
        requests.removeAll(requestsForDelete);
        requestedMessages.put(chargePointId, requests);
    }

    @Override
    public void testFinished(String chargePointId) {
        requestedMessageTypes.remove(chargePointId);
        requestedMessages.remove(chargePointId);
        String uniqueId = uniqueIdByChargePointId.remove(chargePointId);
        if (Objects.nonNull(uniqueId)) {
            completedPromises.remove(uniqueId);
        }
        chargingChargePoints.remove(chargePointId);
        testingChargePoints.remove(chargePointId);
    }

    @Override
    public boolean isCharging(String chargePointId) {
        return chargingChargePoints.contains(chargePointId);
    }
}
