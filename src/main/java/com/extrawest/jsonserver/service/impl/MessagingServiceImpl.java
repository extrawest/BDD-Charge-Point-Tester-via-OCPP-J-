package com.extrawest.jsonserver.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import com.extrawest.jsonserver.service.MessagingService;
import com.extrawest.jsonserver.validation.incoming.confirmation.ResetConfirmationHandler;
import com.extrawest.jsonserver.validation.incoming.confirmation.TriggerMessageConfirmationHandler;
import com.extrawest.jsonserver.validation.outcoming.confirmation.AuthorizeConfirmationBddHandlerValidation;
import com.extrawest.jsonserver.validation.outcoming.confirmation.BootNotificationConfirmationBddHandlerValidation;
import com.extrawest.jsonserver.validation.outcoming.confirmation.DataTransferConfirmationBddHandlerValidation;
import com.extrawest.jsonserver.validation.outcoming.confirmation.HeartbeatConfirmationBddHandlerValidation;
import com.extrawest.jsonserver.validation.outcoming.confirmation.MeterValuesConfirmationBddHandlerValidation;
import com.extrawest.jsonserver.validation.outcoming.confirmation.StartTransactionConfirmationBddHandlerValidation;
import com.extrawest.jsonserver.validation.incoming.request.AuthorizeRequestBddHandler;
import com.extrawest.jsonserver.validation.incoming.request.BootNotificationRequestBddHandler;
import com.extrawest.jsonserver.validation.incoming.request.DataTransferRequestBddHandler;
import com.extrawest.jsonserver.validation.incoming.request.HeartbeatRequestBddHandler;
import com.extrawest.jsonserver.validation.incoming.request.MeterValuesRequestBddHandler;
import com.extrawest.jsonserver.validation.incoming.request.StartTransactionRequestBddHandler;
import com.extrawest.jsonserver.validation.outcoming.request.ResetRequestHandler;
import com.extrawest.jsonserver.validation.outcoming.request.TriggerMessageRequestHandler;
import com.extrawest.jsonserver.ws.handler.ServerCoreEventHandlerImpl;
import eu.chargetime.ocpp.NotConnectedException;
import eu.chargetime.ocpp.OccurenceConstraintException;
import eu.chargetime.ocpp.UnsupportedFeatureException;
import com.extrawest.jsonserver.model.emun.ImplementedMessageType;
import com.extrawest.jsonserver.model.exception.BddTestingException;
import com.extrawest.jsonserver.model.ChargePoint;
import com.extrawest.jsonserver.repository.BddDataRepository;
import com.extrawest.jsonserver.repository.ServerSessionRepository;
import com.extrawest.jsonserver.ws.JsonWsServer;
import eu.chargetime.ocpp.model.Confirmation;
import eu.chargetime.ocpp.model.Request;
import eu.chargetime.ocpp.model.core.AuthorizeConfirmation;
import eu.chargetime.ocpp.model.core.AuthorizeRequest;
import eu.chargetime.ocpp.model.core.BootNotificationConfirmation;
import eu.chargetime.ocpp.model.core.BootNotificationRequest;
import eu.chargetime.ocpp.model.core.DataTransferConfirmation;
import eu.chargetime.ocpp.model.core.DataTransferRequest;
import eu.chargetime.ocpp.model.core.HeartbeatConfirmation;
import eu.chargetime.ocpp.model.core.HeartbeatRequest;
import eu.chargetime.ocpp.model.core.MeterValuesConfirmation;
import eu.chargetime.ocpp.model.core.MeterValuesRequest;
import eu.chargetime.ocpp.model.core.ResetConfirmation;
import eu.chargetime.ocpp.model.core.ResetRequest;
import eu.chargetime.ocpp.model.core.StartTransactionConfirmation;
import eu.chargetime.ocpp.model.core.StartTransactionRequest;
import eu.chargetime.ocpp.model.core.StatusNotificationRequest;
import eu.chargetime.ocpp.model.firmware.DiagnosticsStatusNotificationRequest;
import eu.chargetime.ocpp.model.firmware.FirmwareStatusNotificationRequest;
import eu.chargetime.ocpp.model.remotetrigger.TriggerMessageConfirmation;
import eu.chargetime.ocpp.model.remotetrigger.TriggerMessageRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import static com.extrawest.jsonserver.util.TimeUtil.waitHalfSecond;
import static com.extrawest.jsonserver.util.TimeUtil.waitOneSecond;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessagingServiceImpl implements MessagingService {
    private final ApplicationContext springBootContext;
    private final BddDataRepository bddDataRepository;
    private final JsonWsServer server;
    private final ServerSessionRepository sessionRepository;

    private final AuthorizeRequestBddHandler authorizeRequestBddHandler;
    private final AuthorizeConfirmationBddHandlerValidation authorizeConfirmationBddHandler;
    private final BootNotificationRequestBddHandler bootNotificationRequestBddHandler;
    private final BootNotificationConfirmationBddHandlerValidation bootNotificationConfirmationBddHandler;
    private final DataTransferRequestBddHandler dataTransferRequestBddHandler;
    private final DataTransferConfirmationBddHandlerValidation dataTransferConfirmationBddHandler;
    private final HeartbeatRequestBddHandler heartbeatRequestBddHandler;
    private final HeartbeatConfirmationBddHandlerValidation heartbeatConfirmationBddHandler;
    private final MeterValuesRequestBddHandler meterValuesRequestBddHandler;
    private final MeterValuesConfirmationBddHandlerValidation meterValuesConfirmationBddHandler;
    private final StartTransactionRequestBddHandler startTransactionRequestBddHandler;
    private final StartTransactionConfirmationBddHandlerValidation startTransactionConfirmationBddHandler;

    private final ResetRequestHandler resetRequestHandler;
    private final ResetConfirmationHandler resetConfirmationHandler;
    private final TriggerMessageRequestHandler triggerMessageRequestHandler;
    private final TriggerMessageConfirmationHandler triggerMessageConfirmationHandler;

    @Override
    public ImplementedMessageType sendRequest(String chargePointId, ImplementedMessageType type, Map<String, String> params) {
        UUID sessionUUID = sessionRepository.getSessionByChargerId(chargePointId);
        Request request;
        ImplementedMessageType requestedMessageType = null;
        switch (type) {
            case TRIGGER_MESSAGE -> {
                TriggerMessageRequest message = new TriggerMessageRequest();
                message = triggerMessageRequestHandler.createValidatedMessage(params, message);
                bddDataRepository.addRequestedMessageType(chargePointId, message.getRequestedMessage());
                requestedMessageType = ImplementedMessageType.fromValue(message.getRequestedMessage().name());
                request = message;
            }
            case RESET -> {
                ResetRequest message = new ResetRequest();
                request = resetRequestHandler.createValidatedMessage(params, message);
            }
            default -> throw new BddTestingException("Request message type is unavailable");
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
    public Optional<Request> waitForRequestedMessage(ChargePoint chargePoint,
                                                     int waitingTimeSec,
                                                     ImplementedMessageType type) {
        String chargePointId = chargePoint.getChargePointId();
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
                case BOOT_NOTIFICATION:
                    if (request instanceof BootNotificationRequest) {
                        bddDataRepository.removeRequestedMessage(chargePointId, request);
                        return Optional.of(request);
                    }
                    break;
                case HEARTBEAT:
                    if (request instanceof HeartbeatRequest) {
                        bddDataRepository.removeRequestedMessage(chargePointId, request);
                        return Optional.of(request);
                    }
                    break;
                case METER_VALUES:
                    if (request instanceof MeterValuesRequest) {
                        bddDataRepository.removeRequestedMessage(chargePointId, request);
                        return Optional.of(request);
                    }
                    break;
                case STATUS_NOTIFICATION:
                    if (request instanceof StatusNotificationRequest) {
                        bddDataRepository.removeRequestedMessage(chargePointId, request);
                        return Optional.of(request);
                    }
                    break;
                case FIRMWARE_STATUS_NOTIFICATION:
                    if (request instanceof FirmwareStatusNotificationRequest) {
                        bddDataRepository.removeRequestedMessage(chargePointId, request);
                        return Optional.of(request);
                    }
                    break;
                case DIAGNOSTICS_STATUS_NOTIFICATION:
                    if (request instanceof DiagnosticsStatusNotificationRequest) {
                        bddDataRepository.removeRequestedMessage(chargePointId, request);
                        return Optional.of(request);
                    }
                    break;
                case AUTHORIZE:
                    if (request instanceof AuthorizeRequest) {
                        bddDataRepository.removeRequestedMessage(chargePointId, request);
                        return Optional.of(request);
                    }
                case DATA_TRANSFER:
                    if (request instanceof DataTransferRequest) {
                        bddDataRepository.removeRequestedMessage(chargePointId, request);
                        return Optional.of(request);
                    }
                    break;
            }
        }
        return Optional.empty();
    }

    @Override
    public ImplementedMessageType validateRequest(Map<String, String> parameters, Request request) {
        if (request instanceof BootNotificationRequest message) {
            bootNotificationRequestBddHandler.validateFields(parameters, message);
            return ImplementedMessageType.BOOT_NOTIFICATION;
        } else if (request instanceof AuthorizeRequest message) {
            authorizeConfirmationBddHandler.setReceivedIdTag(message.getIdTag());
            authorizeRequestBddHandler.validateFields(parameters, message);
            return ImplementedMessageType.AUTHORIZE;
        } else if (request instanceof DataTransferRequest message) {
            dataTransferRequestBddHandler.validateFields(parameters, message);
            return ImplementedMessageType.DATA_TRANSFER;
        } else if (request instanceof HeartbeatRequest message) {
            heartbeatRequestBddHandler.validateFields(parameters, message);
            return ImplementedMessageType.HEARTBEAT;
        } else if (request instanceof MeterValuesRequest message) {
            meterValuesRequestBddHandler.validateFields(parameters, message);
            return ImplementedMessageType.METER_VALUES;
        } else if (request instanceof StartTransactionRequest message) {
            startTransactionConfirmationBddHandler.setReceivedIdTag(message.getIdTag());
            startTransactionRequestBddHandler.validateFields(parameters, message);
            return ImplementedMessageType.START_TRANSACTION;
        } else {
             throw new BddTestingException("Type is not implemented. Request: " + request);
        }
    }

    @Override
    public Confirmation sendConfirmationResponse(Map<String, String> parameters, Confirmation response) {
        ServerCoreEventHandlerImpl handler = springBootContext.getBean(ServerCoreEventHandlerImpl.class);
        while (Objects.nonNull(handler.getResponse())) {
            waitHalfSecond();
        }
        if (response instanceof BootNotificationConfirmation message) {
            response = bootNotificationConfirmationBddHandler.createValidatedMessage(parameters, message);
        } else if (response instanceof AuthorizeConfirmation message) {
            response = authorizeConfirmationBddHandler.createValidatedMessage(parameters, message);
        } else if (response instanceof DataTransferConfirmation message) {
            response = dataTransferConfirmationBddHandler.createValidatedMessage(parameters, message);
        } else if (response instanceof HeartbeatConfirmation message) {
            response = heartbeatConfirmationBddHandler.createValidatedMessage(parameters, message);
        } else if (response instanceof MeterValuesConfirmation message) {
            response = meterValuesConfirmationBddHandler.createValidatedMessage(parameters, message);
        } else if (response instanceof StartTransactionConfirmation message) {
            response = startTransactionConfirmationBddHandler.createValidatedMessage(parameters, message);
        } else {
            throw new BddTestingException("This type of confirmation message is not implemented. ");
        }
        handler.setResponse(response);

        while (Objects.nonNull(handler.getResponse())) {
            waitOneSecond();
        }

        return response;
    }

    @Override
    public void assertConfirmationMessage(Map<String, String> parameters,
                                          CompletableFuture<Confirmation> completableFuture) {
        try {
            Confirmation confirmation = completableFuture.get();
            if (confirmation instanceof TriggerMessageConfirmation message) {
                triggerMessageConfirmationHandler.validateFields(parameters, message);
            } else if (confirmation instanceof ResetConfirmation message) {
                resetConfirmationHandler.validateFields(parameters, message);
            } else {
                throw new BddTestingException("Type is not implemented. Confirmation: " + confirmation);
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

}
