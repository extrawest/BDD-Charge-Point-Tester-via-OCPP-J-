package com.extrawest.jsonserver.service.impl;

import com.extrawest.jsonserver.model.emun.ImplementedMessageType;
import com.extrawest.jsonserver.model.exception.BddTestingException;
import com.extrawest.jsonserver.repository.BddDataRepository;
import com.extrawest.jsonserver.validation.AssertionAndValidationService;
import com.extrawest.jsonserver.repository.ServerSessionRepository;
import com.extrawest.jsonserver.service.MessagingService;
import com.extrawest.jsonserver.validation.outgoing.OutgoingMessageFactory;
import com.extrawest.jsonserver.ws.JsonWsServer;
import com.extrawest.jsonserver.ws.handler.ServerCoreEventHandlerImpl;
import eu.chargetime.ocpp.NotConnectedException;
import eu.chargetime.ocpp.OccurenceConstraintException;
import eu.chargetime.ocpp.UnsupportedFeatureException;
import eu.chargetime.ocpp.model.Confirmation;
import eu.chargetime.ocpp.model.Request;
import eu.chargetime.ocpp.model.core.*;
import eu.chargetime.ocpp.model.firmware.DiagnosticsStatusNotificationRequest;
import eu.chargetime.ocpp.model.firmware.FirmwareStatusNotificationRequest;
import eu.chargetime.ocpp.model.remotetrigger.TriggerMessageRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;

import static com.extrawest.jsonserver.model.emun.ImplementedMessageType.TRIGGER_MESSAGE;
import static com.extrawest.jsonserver.util.TimeUtil.waitHalfSecond;
import static com.extrawest.jsonserver.util.TimeUtil.waitOneSecond;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessagingServiceImpl implements MessagingService {
    private final ApplicationContext springBootContext;
    private final BddDataRepository bddDataRepository;
    private final AssertionAndValidationService factories;
    private final JsonWsServer server;
    private final ServerSessionRepository sessionRepository;


    @Override
    public ImplementedMessageType sendRequest(String chargePointId, ImplementedMessageType type,
                                              Map<String, String> params) {
        UUID sessionUUID = sessionRepository.getSessionByChargerId(chargePointId);
        ImplementedMessageType requestedMessageType = null;
        OutgoingMessageFactory<? extends Request> messageFactory =
                factories.getOutgoingRequestFactory(type);
        Request request = messageFactory.createMessageWithValidatedParams(params);

        if (Objects.equals(TRIGGER_MESSAGE, type)) {
                TriggerMessageRequest message = (TriggerMessageRequest) request;
                bddDataRepository.addRequestedMessageType(chargePointId, message.getRequestedMessage());
                requestedMessageType = ImplementedMessageType.fromValue(message.getRequestedMessage().name());
        }

        sendRequest(sessionUUID, request);
        return requestedMessageType;
    }

    private void sendRequest(UUID sessionUUID, Request request) {
        try {
            server.send(sessionUUID, request);
        } catch (OccurenceConstraintException | UnsupportedFeatureException | NotConnectedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<CompletableFuture<Confirmation>> waitForSuccessfulResponse(UUID sessionIndex, int waitingTimeSec,
                                                                               Map<String, String> parameters) {
        Optional<CompletableFuture<Confirmation>> completed = bddDataRepository.getCompleted(sessionIndex);
        if (completed.isEmpty()) {
            log.info(String.format("Waiting for successful response, up to %s seconds...", waitingTimeSec));
            for (int i = 1; i <= waitingTimeSec; i++) {
                completed = bddDataRepository.getCompleted(sessionIndex);
                if (completed.isPresent()) {
                    return completed;
                }
                waitOneSecond();
            }
        }
        return completed;
    }

    @Override
    public Optional<Request> waitForRequestedMessage(String chargePointId,
                                                     int waitingTimeSec,
                                                     ImplementedMessageType type) {
        Optional<List<ImplementedMessageType>> messageTypes = bddDataRepository.getRequestedMessageTypes(chargePointId);
        if (messageTypes.isEmpty() || !messageTypes.get().contains(type)) {
            throw new BddTestingException("There are no message requests awaiting with type: " + type);
        }
        Optional<List<Request>> requestedMessages = bddDataRepository.getRequestedMessage(chargePointId);
        Optional<Request> request = Optional.empty();
        if (requestedMessages.isPresent()) {
            request = getAndHandleIfListContainMessage(chargePointId, requestedMessages.get(), type);
            if (request.isPresent()) {
                return request;
            }
        }

        log.debug(String.format("Waiting for requested request, up to %s seconds...", waitingTimeSec));
        for (int i = 1; i <= waitingTimeSec; i++) {
            requestedMessages = bddDataRepository.getRequestedMessage(chargePointId);
            if (requestedMessages.isPresent()) {
                request = getAndHandleIfListContainMessage(chargePointId, requestedMessages.get(), type);
            }
            if (request.isPresent()) {
                break;
            }
            waitOneSecond();
        }

        return request;
    }

    private Optional<Request> getAndHandleIfListContainMessage(String chargePointId,
                                                               List<Request> requests,
                                                               ImplementedMessageType messageType) {
        if (Objects.isNull(requests) || requests.isEmpty()) {
            return Optional.empty();
        }
        for (Request request : requests) {
            switch (messageType) {
                case AUTHORIZE -> {
                    if (request instanceof AuthorizeRequest) {
                        bddDataRepository.removeRequestedMessage(chargePointId, request);
                        return Optional.of(request);
                    }
                }
                case BOOT_NOTIFICATION -> {
                    if (request instanceof BootNotificationRequest) {
                        bddDataRepository.removeRequestedMessage(chargePointId, request);
                        return Optional.of(request);
                    }
                }
                case DATA_TRANSFER_INCOMING -> {
                    if (request instanceof DataTransferRequest) {
                        bddDataRepository.removeRequestedMessage(chargePointId, request);
                        return Optional.of(request);
                    }
                }
                case DIAGNOSTICS_STATUS_NOTIFICATION -> {
                    if (request instanceof DiagnosticsStatusNotificationRequest) {
                        bddDataRepository.removeRequestedMessage(chargePointId, request);
                        return Optional.of(request);
                    }
                }
                case FIRMWARE_STATUS_NOTIFICATION -> {
                    if (request instanceof FirmwareStatusNotificationRequest) {
                        bddDataRepository.removeRequestedMessage(chargePointId, request);
                        return Optional.of(request);
                    }
                }
                case HEARTBEAT -> {
                    if (request instanceof HeartbeatRequest) {
                        bddDataRepository.removeRequestedMessage(chargePointId, request);
                        return Optional.of(request);
                    }
                }
                case METER_VALUES -> {
                    if (request instanceof MeterValuesRequest) {
                        bddDataRepository.removeRequestedMessage(chargePointId, request);
                        return Optional.of(request);
                    }
                }
                case START_TRANSACTION -> {
                    if (request instanceof StartTransactionRequest) {
                        bddDataRepository.removeRequestedMessage(chargePointId, request);
                        return Optional.of(request);
                    }
                }
                case STATUS_NOTIFICATION -> {
                    if (request instanceof StatusNotificationRequest) {
                        bddDataRepository.removeRequestedMessage(chargePointId, request);
                        return Optional.of(request);
                    }
                }
                case STOP_TRANSACTION -> {
                    if (request instanceof StopTransactionRequest) {
                        bddDataRepository.removeRequestedMessage(chargePointId, request);
                        return Optional.of(request);
                    }
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public ImplementedMessageType validateRequest(Map<String, String> parameters, Request request) {
        return factories.getMessageTypeAfterValidationAndAssertion(request, parameters);
    }

    @Override
    public Confirmation sendConfirmationResponse(Map<String, String> parameters,
                                                 ImplementedMessageType sendingMessageType) {
        ServerCoreEventHandlerImpl handler = springBootContext.getBean(ServerCoreEventHandlerImpl.class);
        while (Objects.nonNull(handler.getResponse())) {
            waitHalfSecond();
        }
        OutgoingMessageFactory<? extends Confirmation> confirmationFactory =
                factories.getOutgoingConfirmationFactory(sendingMessageType);
        Confirmation response = confirmationFactory.createMessageWithValidatedParams(parameters);

        handler.setResponse(response);
        while (Objects.nonNull(handler.getResponse())) {
            waitOneSecond();
        }

        return response;
    }

    @Override
    public void assertConfirmationMessage(Map<String, String> parameters,
                                          CompletableFuture<Confirmation> completableFuture) {
        factories.assertConfirmationMessage(parameters, completableFuture);
    }

}
