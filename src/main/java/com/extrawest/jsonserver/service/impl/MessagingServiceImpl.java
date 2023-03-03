package com.extrawest.jsonserver.service.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import com.extrawest.jsonserver.model.emun.ImplementedMessagesSentType;
import com.extrawest.jsonserver.service.MessagingService;
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
import com.extrawest.jsonserver.validation.outcoming.request.TriggerMessageRequestHandler;
import com.extrawest.jsonserver.ws.handler.ServerCoreEventHandlerImpl;
import eu.chargetime.ocpp.NotConnectedException;
import eu.chargetime.ocpp.OccurenceConstraintException;
import eu.chargetime.ocpp.UnsupportedFeatureException;
import com.extrawest.jsonserver.model.emun.ImplementedReceivedMessageType;
import com.extrawest.jsonserver.model.exception.BddTestingException;
import com.extrawest.jsonserver.model.ChargePoint;
import com.extrawest.jsonserver.model.RequiredChargingData;
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
import eu.chargetime.ocpp.model.core.MeterValue;
import eu.chargetime.ocpp.model.core.MeterValuesConfirmation;
import eu.chargetime.ocpp.model.core.MeterValuesRequest;
import eu.chargetime.ocpp.model.core.ResetRequest;
import eu.chargetime.ocpp.model.core.ResetType;
import eu.chargetime.ocpp.model.core.SampledValue;
import eu.chargetime.ocpp.model.core.StartTransactionConfirmation;
import eu.chargetime.ocpp.model.core.StartTransactionRequest;
import eu.chargetime.ocpp.model.core.StatusNotificationRequest;
import eu.chargetime.ocpp.model.core.StopTransactionRequest;
import eu.chargetime.ocpp.model.firmware.DiagnosticsStatusNotificationRequest;
import eu.chargetime.ocpp.model.firmware.FirmwareStatusNotificationRequest;
import eu.chargetime.ocpp.model.remotetrigger.TriggerMessageRequest;
import eu.chargetime.ocpp.model.remotetrigger.TriggerMessageRequestType;
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

    private final TriggerMessageRequestHandler triggerMessageRequestHandler;

    @Override
    public void sendRequest(String chargePointId, ImplementedMessagesSentType type, Map<String, String> params) {
        UUID sessionUUID = sessionRepository.getSessionByChargerId(chargePointId);
        Request request;
        switch (type) {
            case TRIGGER_MESSAGE -> {
                TriggerMessageRequest message = new TriggerMessageRequest();
                message = triggerMessageRequestHandler.createValidatedMessage(params, message);
                bddDataRepository.addRequestedMessageType(chargePointId, message.getRequestedMessage());
                request = message;
            }
            case RESET -> request = new ResetRequest();
            default -> throw new BddTestingException("Message type is unavailable");
        }
        sendRequest(sessionUUID, request);
    }

    @Override
    public void sendTriggerMessage(String chargePointId, TriggerMessageRequestType type) {
        if (Objects.isNull(type)) {
            type = TriggerMessageRequestType.StatusNotification;
        }
        UUID sessionUUID = sessionRepository.getSessionByChargerId(chargePointId);
        bddDataRepository.addRequestedMessageType(chargePointId, type);
        Request request = new TriggerMessageRequest(type);
        try {
            server.send(sessionUUID, request);
        } catch (OccurenceConstraintException | UnsupportedFeatureException | NotConnectedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendResetMessage(String chargePointId, ResetType type) {
        if (Objects.isNull(type)) {
            type = ResetType.Soft;
        }
        UUID sessionUUID = sessionRepository.getSessionByChargerId(chargePointId);
        Request request = new ResetRequest(type);
        sendRequest(sessionUUID, request);
    }

    private void sendRequest(UUID sessionUUID, Request request) {
        try {
            server.send(sessionUUID, request);
        } catch (OccurenceConstraintException | UnsupportedFeatureException | NotConnectedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<CompletableFuture<Confirmation>> waitForSuccessfulResponse(UUID sessionIndex, int waitingTimeSec) {
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
                                                     ImplementedReceivedMessageType type) {
        String chargePointId = chargePoint.getChargePointId();
        Optional<List<ImplementedReceivedMessageType>> messageTypes = bddDataRepository.getRequestedMessageTypes(chargePointId);
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

    @Override
    public void validateReceivedMessageOrThrow(ChargePoint chargePoint,
                                               RequiredChargingData requiredData,
                                               Request request) {
        boolean isEquals = compareData(chargePoint, requiredData, request);
        if (!isEquals) {
            throw new BddTestingException("Received data is not equal to expected data. ");
        }
    }

    private Optional<Request> getAndHandleIfListContainMessage(String chargePointId,
                                                               List<Request> requests,
                                                               ImplementedReceivedMessageType messageType) {
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
    public void validateRequest(Map<String, String> parameters, Request request) {
        if (request instanceof BootNotificationRequest message) {
            bootNotificationRequestBddHandler.validateFields(parameters, message);
        } else if (request instanceof AuthorizeRequest message) {
            authorizeConfirmationBddHandler.setReceivedIdTag(message.getIdTag());
            authorizeRequestBddHandler.validateFields(parameters, message);
        } else if (request instanceof DataTransferRequest message) {
            dataTransferRequestBddHandler.validateFields(parameters, message);
        } else if (request instanceof HeartbeatRequest message) {
            heartbeatRequestBddHandler.validateFields(parameters, message);
        } else if (request instanceof MeterValuesRequest message) {
            meterValuesRequestBddHandler.validateFields(parameters, message);
        } else if (request instanceof StartTransactionRequest message) {
            startTransactionConfirmationBddHandler.setReceivedIdTag(message.getIdTag());
            startTransactionRequestBddHandler.validateFields(parameters, message);
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

    private boolean compareData(ChargePoint chargePoint, RequiredChargingData requiredData, Request request) {
        if (request instanceof BootNotificationRequest message) {
            return Objects.equals(chargePoint.getChargePointModel(), message.getChargePointModel())
                    && Objects.equals(chargePoint.getChargePointVendor(), message.getChargePointVendor())
                    && Objects.equals(chargePoint.getChargePointSerialNumber(), message.getChargePointSerialNumber())
                    && Objects.equals(chargePoint.getFirmwareVersion(), message.getFirmwareVersion())
                    && Objects.equals(chargePoint.getIccid(), message.getIccid())
                    && Objects.equals(chargePoint.getImsi(), message.getImsi())
                    && Objects.equals(chargePoint.getMeterSerialNumber(), message.getMeterSerialNumber())
                    && Objects.equals(chargePoint.getMeterType(), message.getMeterType());
        } else if (request instanceof DiagnosticsStatusNotificationRequest message) {
            log.info("Comparing of DiagnosticsStatusNotificationRequest not implemented" + message);
        } else if (request instanceof FirmwareStatusNotificationRequest message) {
            log.info("Comparing of FirmwareStatusNotificationRequest not implemented" + message);
        } else if (request instanceof StatusNotificationRequest message) {
            log.info("Comparing of StatusNotificationRequest not implemented" + message);
        } else if (request instanceof HeartbeatRequest message) {
            log.info("Comparing of HeartbeatRequest not implemented" + message);
        } else if (request instanceof AuthorizeRequest message) {
            return Objects.equals(message.getIdTag(), requiredData.getIdTag());
        } else if (request instanceof StartTransactionRequest message) {
            return Objects.equals(message.getIdTag(), requiredData.getIdTag())
                    && Objects.equals(message.getConnectorId(), requiredData.getConnectorId())
                    && Objects.equals(message.getMeterStart(), requiredData.getMeterStart())
                    && Objects.nonNull(message.getTimestamp());
        } else if (request instanceof MeterValuesRequest message) {
            if ((Objects.isNull(message.getMeterValue()) || Objects.isNull(message.getConnectorId()))
                    || (Objects.equals(message.getConnectorId(), 0) && Objects.nonNull(message.getTransactionId()))
                    || ((message.getConnectorId() > 0) && Objects.isNull(message.getTransactionId()))) {
                return false;
            }
            List<MeterValue> meterValues = Arrays.stream(message.getMeterValue()).toList();
            Optional<Boolean> optionalIsInvalid = meterValues.stream()
                    .map(this::validateMeterValue)
                    .filter(x -> Objects.equals(x, false))
                    .findFirst();
            return optionalIsInvalid.isEmpty()
                    && ((message.getConnectorId() > 0)
                    && Objects.equals(message.getConnectorId(), requiredData.getConnectorId()));
        } else if (request instanceof StopTransactionRequest message) {
            log.info("StopTransaction message: " + message);
            return Objects.nonNull(message.getMeterStop())
                    && Objects.nonNull(message.getTimestamp())
                    && Objects.nonNull(message.getTransactionId())
                    && (message.getMeterStop() >= requiredData.getMeterStart());
        }

        return true;
    }

    private boolean validateMeterValue(MeterValue meterValue) {
        SampledValue[] sampledValue = meterValue.getSampledValue();
        if (Objects.isNull(meterValue.getTimestamp()) || Objects.isNull(sampledValue) || (sampledValue.length == 0)) {
            log.error("MeterValue has invalid data: " + meterValue);
            return false;
        }
        List<SampledValue> sampledValues = Arrays.stream(sampledValue).toList();
        Optional<Boolean> optionalIsInvalid = sampledValues.stream()
                .map(this::validateSampledValue)
                .filter(x -> Objects.equals(x, false))
                .findFirst();
        return optionalIsInvalid.isEmpty();
    }

    private boolean validateSampledValue(SampledValue sampledValue) {
        if (Objects.isNull(sampledValue.getValue()) || sampledValue.getValue().isBlank()) {
            log.error("MeterValue has invalid sampled value data: " + sampledValue);
            return false;
        }
        return true;
    }

}
