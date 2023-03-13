package com.extrawest.jsonserver.validation.impl;

import static com.extrawest.jsonserver.model.emun.ApiErrorMessage.INVALID_CONFIRMATION_TYPE;
import static com.extrawest.jsonserver.model.emun.ApiErrorMessage.INVALID_OUTGOING_FACTORY;
import static com.extrawest.jsonserver.model.emun.ApiErrorMessage.INVALID_REQUEST_TYPE;
import static com.extrawest.jsonserver.model.emun.ImplementedMessageType.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import com.extrawest.jsonserver.model.emun.ImplementedMessageType;
import com.extrawest.jsonserver.model.exception.BddTestingException;
import com.extrawest.jsonserver.validation.AssertionAndValidationService;
import com.extrawest.jsonserver.validation.incoming.confirmation.CancelReservationConfirmationBddHandler;
import com.extrawest.jsonserver.validation.incoming.confirmation.ChangeAvailabilityConfirmationBddHandler;
import com.extrawest.jsonserver.validation.incoming.confirmation.ChangeConfigurationConfirmationBddHandler;
import com.extrawest.jsonserver.validation.incoming.confirmation.ClearCacheConfirmationBddHandler;
import com.extrawest.jsonserver.validation.incoming.confirmation.ClearChargingProfileConfirmationBddHandler;
import com.extrawest.jsonserver.validation.incoming.confirmation.DataTransferIncomingConfirmationBddHandler;
import com.extrawest.jsonserver.validation.incoming.confirmation.GetCompositeScheduleConfirmationBddHandler;
import com.extrawest.jsonserver.validation.incoming.confirmation.GetConfigurationConfirmationBddHandler;
import com.extrawest.jsonserver.validation.incoming.confirmation.GetDiagnosticsConfirmationBddHandler;
import com.extrawest.jsonserver.validation.incoming.confirmation.GetLocalListVersionConfirmationBddHandler;
import com.extrawest.jsonserver.validation.incoming.confirmation.RemoteStartTransactionConfirmationBddHandler;
import com.extrawest.jsonserver.validation.incoming.confirmation.RemoteStopTransactionConfirmationBddHandler;
import com.extrawest.jsonserver.validation.incoming.confirmation.ReserveNowConfirmationBddHandler;
import com.extrawest.jsonserver.validation.incoming.confirmation.ResetConfirmationBddHandler;
import com.extrawest.jsonserver.validation.incoming.confirmation.SendLocalListConfirmationBddHandler;
import com.extrawest.jsonserver.validation.incoming.confirmation.SetChargingProfileConfirmationBddHandler;
import com.extrawest.jsonserver.validation.incoming.confirmation.TriggerMessageConfirmationBddHandler;
import com.extrawest.jsonserver.validation.incoming.confirmation.UnlockConnectorConfirmationBddHandler;
import com.extrawest.jsonserver.validation.incoming.confirmation.UpdateFirmwareConfirmationBddHandler;
import com.extrawest.jsonserver.validation.incoming.request.AuthorizeRequestBddHandler;
import com.extrawest.jsonserver.validation.incoming.request.BootNotificationRequestBddHandler;
import com.extrawest.jsonserver.validation.incoming.request.DataTransferIncomingRequestBddHandler;
import com.extrawest.jsonserver.validation.incoming.request.DiagnosticsStatusNotificationRequestBddHandler;
import com.extrawest.jsonserver.validation.incoming.request.FirmwareStatusNotificationRequestBddHandler;
import com.extrawest.jsonserver.validation.incoming.request.HeartbeatRequestBddHandler;
import com.extrawest.jsonserver.validation.incoming.request.MeterValuesRequestBddHandler;
import com.extrawest.jsonserver.validation.incoming.request.StartTransactionRequestBddHandler;
import com.extrawest.jsonserver.validation.incoming.request.StatusNotificationRequestBddHandler;
import com.extrawest.jsonserver.validation.incoming.request.StopTransactionRequestBddHandler;
import com.extrawest.jsonserver.validation.outgoing.OutgoingMessageFactory;

import com.extrawest.jsonserver.validation.outgoing.confirmation.AuthorizeConfirmationBddHandler;
import com.extrawest.jsonserver.validation.outgoing.confirmation.BootNotificationConfirmationBddHandler;
import com.extrawest.jsonserver.validation.outgoing.confirmation.DataTransferOutgoingConfirmationBddHandler;
import com.extrawest.jsonserver.validation.outgoing.confirmation.DiagnosticsStatusNotificationConfirmationBddHandler;
import com.extrawest.jsonserver.validation.outgoing.confirmation.FirmwareStatusNotificationConfirmationBddHandler;
import com.extrawest.jsonserver.validation.outgoing.confirmation.HeartbeatConfirmationBddHandler;
import com.extrawest.jsonserver.validation.outgoing.confirmation.MeterValuesConfirmationBddHandler;
import com.extrawest.jsonserver.validation.outgoing.confirmation.StartTransactionConfirmationBddHandler;
import com.extrawest.jsonserver.validation.outgoing.confirmation.StatusNotificationConfirmationBddHandler;
import com.extrawest.jsonserver.validation.outgoing.confirmation.StopTransactionConfirmationBddHandler;
import com.extrawest.jsonserver.validation.outgoing.request.CancelReservationRequestBddHandler;
import com.extrawest.jsonserver.validation.outgoing.request.ChangeAvailabilityRequestBddHandler;
import com.extrawest.jsonserver.validation.outgoing.request.ChangeConfigurationRequestBddHandler;
import com.extrawest.jsonserver.validation.outgoing.request.ClearCacheRequestBddHandler;
import com.extrawest.jsonserver.validation.outgoing.request.ClearChargingProfileRequestBddHandler;
import com.extrawest.jsonserver.validation.outgoing.request.DataTransferOutgoingRequestBddHandler;
import com.extrawest.jsonserver.validation.outgoing.request.GetCompositeScheduleRequestBddHandler;
import com.extrawest.jsonserver.validation.outgoing.request.GetConfigurationRequestBddHandler;
import com.extrawest.jsonserver.validation.outgoing.request.GetDiagnosticsRequestBddHandler;
import com.extrawest.jsonserver.validation.outgoing.request.GetLocalListVersionRequestBddHandler;
import com.extrawest.jsonserver.validation.outgoing.request.RemoteStartTransactionRequestBddHandler;
import com.extrawest.jsonserver.validation.outgoing.request.RemoteStopTransactionRequestBddHandler;
import com.extrawest.jsonserver.validation.outgoing.request.ReserveNowRequestBddHandler;
import com.extrawest.jsonserver.validation.outgoing.request.ResetRequestBddHandler;
import com.extrawest.jsonserver.validation.outgoing.request.SendLocalListRequestBddHandler;
import com.extrawest.jsonserver.validation.outgoing.request.SetChargingProfileRequestBddHandler;
import com.extrawest.jsonserver.validation.outgoing.request.TriggerMessageRequestBddHandler;
import com.extrawest.jsonserver.validation.outgoing.request.UnlockConnectorRequestBddHandler;
import com.extrawest.jsonserver.validation.outgoing.request.UpdateFirmwareRequestBddFactory;
import eu.chargetime.ocpp.model.Confirmation;
import eu.chargetime.ocpp.model.Request;
import eu.chargetime.ocpp.model.core.AuthorizeRequest;
import eu.chargetime.ocpp.model.core.BootNotificationRequest;
import eu.chargetime.ocpp.model.core.ChangeAvailabilityConfirmation;
import eu.chargetime.ocpp.model.core.ChangeConfigurationConfirmation;
import eu.chargetime.ocpp.model.core.ClearCacheConfirmation;
import eu.chargetime.ocpp.model.core.DataTransferConfirmation;
import eu.chargetime.ocpp.model.core.DataTransferRequest;
import eu.chargetime.ocpp.model.core.GetConfigurationConfirmation;
import eu.chargetime.ocpp.model.core.HeartbeatRequest;
import eu.chargetime.ocpp.model.core.MeterValuesRequest;
import eu.chargetime.ocpp.model.core.RemoteStartTransactionConfirmation;
import eu.chargetime.ocpp.model.core.RemoteStopTransactionConfirmation;
import eu.chargetime.ocpp.model.core.ResetConfirmation;
import eu.chargetime.ocpp.model.core.StartTransactionRequest;
import eu.chargetime.ocpp.model.core.StatusNotificationRequest;
import eu.chargetime.ocpp.model.core.StopTransactionRequest;
import eu.chargetime.ocpp.model.core.UnlockConnectorConfirmation;
import eu.chargetime.ocpp.model.firmware.DiagnosticsStatusNotificationRequest;
import eu.chargetime.ocpp.model.firmware.FirmwareStatusNotificationRequest;
import eu.chargetime.ocpp.model.firmware.GetDiagnosticsConfirmation;
import eu.chargetime.ocpp.model.firmware.UpdateFirmwareConfirmation;
import eu.chargetime.ocpp.model.localauthlist.GetLocalListVersionConfirmation;
import eu.chargetime.ocpp.model.localauthlist.SendLocalListConfirmation;
import eu.chargetime.ocpp.model.remotetrigger.TriggerMessageConfirmation;
import eu.chargetime.ocpp.model.reservation.CancelReservationConfirmation;
import eu.chargetime.ocpp.model.reservation.ReserveNowConfirmation;
import eu.chargetime.ocpp.model.smartcharging.ClearChargingProfileConfirmation;
import eu.chargetime.ocpp.model.smartcharging.GetCompositeScheduleConfirmation;
import eu.chargetime.ocpp.model.smartcharging.SetChargingProfileConfirmation;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AssertionAndValidationServiceImpl implements AssertionAndValidationService {
    private final AuthorizeRequestBddHandler authorizeRequestBddHandler;
    private final BootNotificationRequestBddHandler bootNotificationRequestBddHandler;
    private final DataTransferIncomingRequestBddHandler dataTransferIncomingRequestBddHandler;
    private final DiagnosticsStatusNotificationRequestBddHandler diagnosticsStatusNotificationRequestBddHandler;
    private final FirmwareStatusNotificationRequestBddHandler firmwareStatusNotificationRequestBddHandler;
    private final HeartbeatRequestBddHandler heartbeatRequestBddHandler;
    private final MeterValuesRequestBddHandler meterValuesRequestBddHandler;
    private final StartTransactionRequestBddHandler startTransactionRequestBddHandler;
    private final StatusNotificationRequestBddHandler statusNotificationRequestBddHandler;
    private final StopTransactionRequestBddHandler stopTransactionRequestBddHandler;

    private final CancelReservationConfirmationBddHandler cancelReservationConfirmationBddHandler;
    private final ChangeAvailabilityConfirmationBddHandler changeAvailabilityConfirmationBddHandler;
    private final ChangeConfigurationConfirmationBddHandler changeConfigurationConfirmationBddHandler;
    private final ClearCacheConfirmationBddHandler clearCacheConfirmationBddHandler;
    private final ClearChargingProfileConfirmationBddHandler clearChargingProfileConfirmationBddHandler;
    private final DataTransferIncomingConfirmationBddHandler dataTransferIncomingConfirmationBddHandler;
    private final GetCompositeScheduleConfirmationBddHandler getCompositeScheduleConfirmationBddHandler;
    private final GetConfigurationConfirmationBddHandler getConfigurationConfirmationBddHandler;
    private final GetDiagnosticsConfirmationBddHandler getDiagnosticsConfirmationBddHandler;
    private final GetLocalListVersionConfirmationBddHandler getLocalListVersionConfirmationBddHandler;
    private final RemoteStartTransactionConfirmationBddHandler remoteStartTransactionConfirmationBddHandler;
    private final RemoteStopTransactionConfirmationBddHandler remoteStopTransactionConfirmationBddHandler;
    private final ReserveNowConfirmationBddHandler reserveNowConfirmationBddHandler;
    private final ResetConfirmationBddHandler resetConfirmationBddHandler;
    private final SendLocalListConfirmationBddHandler sendLocalListConfirmationBddHandler;
    private final SetChargingProfileConfirmationBddHandler setChargingProfileConfirmationBddHandler;
    private final TriggerMessageConfirmationBddHandler triggerMessageConfirmationBddHandler;
    private final UnlockConnectorConfirmationBddHandler unlockConnectorConfirmationBddHandler;
    private final UpdateFirmwareConfirmationBddHandler updateFirmwareConfirmationBddHandler;

    private final AuthorizeConfirmationBddHandler authorizeConfirmationBddHandler;
    private final BootNotificationConfirmationBddHandler bootNotificationConfirmationBddHandler;
    private final DataTransferOutgoingConfirmationBddHandler dataTransferOutgoingConfirmationBddHandler;
    private final DiagnosticsStatusNotificationConfirmationBddHandler diagnosticsStatusNotificationConfirmationBddHandler;
    private final FirmwareStatusNotificationConfirmationBddHandler firmwareStatusNotificationConfirmationBddHandler;
    private final HeartbeatConfirmationBddHandler heartbeatConfirmationBddHandler;
    private final MeterValuesConfirmationBddHandler meterValuesConfirmationBddHandler;
    private final StartTransactionConfirmationBddHandler startTransactionConfirmationBddHandler;
    private final StatusNotificationConfirmationBddHandler statusNotificationConfirmationBddHandler;
    private final StopTransactionConfirmationBddHandler stopTransactionConfirmationBddHandler;

    private final CancelReservationRequestBddHandler cancelReservationRequestBddHandler;
    private final ChangeAvailabilityRequestBddHandler changeAvailabilityRequestBddHandler;
    private final ChangeConfigurationRequestBddHandler changeConfigurationRequestBddHandler;
    private final ClearCacheRequestBddHandler clearCacheRequestBddHandler;
    private final ClearChargingProfileRequestBddHandler clearChargingProfileRequestBddHandler;
    private final DataTransferOutgoingRequestBddHandler dataTransferOutgoingRequestBddHandler;
    private final GetCompositeScheduleRequestBddHandler getCompositeScheduleRequestBddHandler;
    private final GetConfigurationRequestBddHandler getConfigurationRequestBddHandler;
    private final GetDiagnosticsRequestBddHandler getDiagnosticsRequestBddHandler;
    private final GetLocalListVersionRequestBddHandler getLocalListVersionRequestBddHandler;
    private final RemoteStartTransactionRequestBddHandler remoteStartTransactionRequestBddHandler;
    private final RemoteStopTransactionRequestBddHandler remoteStopTransactionRequestBddHandler;
    private final ReserveNowRequestBddHandler reserveNowRequestBddHandler;
    private final ResetRequestBddHandler resetRequestBddHandler;
    private final SendLocalListRequestBddHandler sendLocalListRequestBddHandler;
    private final SetChargingProfileRequestBddHandler setChargingProfileRequestBddHandler;
    private final TriggerMessageRequestBddHandler triggerMessageRequestBddHandler;
    private final UnlockConnectorRequestBddHandler unlockConnectorRequestBddHandler;
    private final UpdateFirmwareRequestBddFactory updateFirmwareRequestBddFactory;

    Map<ImplementedMessageType, OutgoingMessageFactory<? extends Request>>
            outgoingRequestHandlers;
    Map<ImplementedMessageType, OutgoingMessageFactory<? extends Confirmation>>
            outgoingConfirmationHandlers;

    @PostConstruct
    public void init() {
        createOutgoingConfirmationHandlers();
        createOutgoingRequestHandlers();

        validateForFactoriesForImplementedMessageTypes();
    }

    private void createOutgoingConfirmationHandlers() {
        outgoingConfirmationHandlers = Map.of(
                AUTHORIZE, authorizeConfirmationBddHandler,
                BOOT_NOTIFICATION, bootNotificationConfirmationBddHandler,
                DATA_TRANSFER_INCOMING, dataTransferOutgoingConfirmationBddHandler,
                DIAGNOSTICS_STATUS_NOTIFICATION, diagnosticsStatusNotificationConfirmationBddHandler,
                FIRMWARE_STATUS_NOTIFICATION, firmwareStatusNotificationConfirmationBddHandler,
                HEARTBEAT, heartbeatConfirmationBddHandler,
                METER_VALUES, meterValuesConfirmationBddHandler,
                START_TRANSACTION, startTransactionConfirmationBddHandler,
                STATUS_NOTIFICATION, statusNotificationConfirmationBddHandler,
                STOP_TRANSACTION, stopTransactionConfirmationBddHandler
        );
    }

    private void createOutgoingRequestHandlers() {
        Map<ImplementedMessageType, OutgoingMessageFactory<? extends Request>> handlers = new HashMap<>();
        Map<ImplementedMessageType, OutgoingMessageFactory<? extends Request>> theFirstPart = Map.of(
                CANCEL_RESERVATION, cancelReservationRequestBddHandler,
                CHANGE_AVAILABILITY, changeAvailabilityRequestBddHandler,
                CHANGE_CONFIGURATION, changeConfigurationRequestBddHandler,
                CLEAR_CACHE, clearCacheRequestBddHandler,
                CLEAR_CHARGING_PROFILE, clearChargingProfileRequestBddHandler,
                DATA_TRANSFER_OUTGOING, dataTransferOutgoingRequestBddHandler,
                GET_COMPOSITE_SCHEDULE, getCompositeScheduleRequestBddHandler,
                GET_CONFIGURATION, getConfigurationRequestBddHandler,
                GET_DIAGNOSTICS, getDiagnosticsRequestBddHandler,
                GET_LOCAL_LIST_VERSION, getLocalListVersionRequestBddHandler
        );
        Map<ImplementedMessageType, OutgoingMessageFactory<? extends Request>> theSecondPart = Map.of(
                REMOTE_START_TRANSACTION, remoteStartTransactionRequestBddHandler,
                REMOTE_STOP_TRANSACTION, remoteStopTransactionRequestBddHandler,
                RESERVE_NOW, reserveNowRequestBddHandler,
                RESET, resetRequestBddHandler,
                SEND_LOCAL_LIST, sendLocalListRequestBddHandler,
                SET_CHARGING_PROFILE, setChargingProfileRequestBddHandler,
                TRIGGER_MESSAGE, triggerMessageRequestBddHandler,
                UNLOCK_CONNECTOR, unlockConnectorRequestBddHandler,
                UPDATE_FIRMWARE, updateFirmwareRequestBddFactory
        );

        handlers.putAll(theFirstPart);
        handlers.putAll(theSecondPart);
        outgoingRequestHandlers = Map.copyOf(handlers);
    }

    private void validateForFactoriesForImplementedMessageTypes() {
        Arrays.stream(ImplementedMessageType.values()).forEach((t) -> {
                if (!outgoingRequestHandlers.containsKey(t) && !outgoingConfirmationHandlers.containsKey(t)) {
                    throw new BddTestingException(String.format(INVALID_OUTGOING_FACTORY.getValue(), t.getValue()));
                }
        });
    }
    
    @Override
    public ImplementedMessageType getMessageTypeAfterValidationAndAssertion(Request request,
                                                                            Map<String, String> parameters) {
        if (request instanceof AuthorizeRequest message) {
            authorizeConfirmationBddHandler.setReceivedIdTag(message.getIdTag());
            authorizeRequestBddHandler.validateAndAssertFieldsWithParams(parameters, message);
            return AUTHORIZE;
        } else if (request instanceof BootNotificationRequest message) {
            bootNotificationRequestBddHandler.validateAndAssertFieldsWithParams(parameters, message);
            return BOOT_NOTIFICATION;
        } else if (request instanceof DataTransferRequest message) {
            dataTransferIncomingRequestBddHandler.validateAndAssertFieldsWithParams(parameters, message);
            return DATA_TRANSFER_INCOMING;
        } else if (request instanceof DiagnosticsStatusNotificationRequest message) {
            diagnosticsStatusNotificationRequestBddHandler.validateAndAssertFieldsWithParams(parameters, message);
            return DIAGNOSTICS_STATUS_NOTIFICATION;
        } else if (request instanceof FirmwareStatusNotificationRequest message) {
            firmwareStatusNotificationRequestBddHandler.validateAndAssertFieldsWithParams(parameters, message);
            return DIAGNOSTICS_STATUS_NOTIFICATION;
        } else if (request instanceof HeartbeatRequest message) {
            heartbeatRequestBddHandler.validateAndAssertFieldsWithParams(parameters, message);
            return HEARTBEAT;
        } else if (request instanceof MeterValuesRequest message) {
            meterValuesRequestBddHandler.validateAndAssertFieldsWithParams(parameters, message);
            return METER_VALUES;
        } else if (request instanceof StartTransactionRequest message) {
            startTransactionConfirmationBddHandler.setReceivedIdTag(message.getIdTag());
            startTransactionRequestBddHandler.validateAndAssertFieldsWithParams(parameters, message);
            return START_TRANSACTION;
        } else if (request instanceof StatusNotificationRequest message) {
            statusNotificationRequestBddHandler.validateAndAssertFieldsWithParams(parameters, message);
            return STATUS_NOTIFICATION;
        } else if (request instanceof StopTransactionRequest message) {
            stopTransactionConfirmationBddHandler.setReceivedIdTag(message.getIdTag());
            stopTransactionRequestBddHandler.validateAndAssertFieldsWithParams(parameters, message);
            return STOP_TRANSACTION;
        } else {
            throw new BddTestingException(INVALID_REQUEST_TYPE.getValue() + request);
        }
    }

    @Override
    public void assertConfirmationMessage(Map<String, String> parameters,
                                          CompletableFuture<Confirmation> completableFuture) {
        try {
            Confirmation confirmation = completableFuture.get();
            if (confirmation instanceof CancelReservationConfirmation message) {
                cancelReservationConfirmationBddHandler.validateAndAssertFieldsWithParams(parameters, message);
            } else if (confirmation instanceof ChangeAvailabilityConfirmation message) {
                changeAvailabilityConfirmationBddHandler.validateAndAssertFieldsWithParams(parameters, message);
            } else if (confirmation instanceof ChangeConfigurationConfirmation message) {
                changeConfigurationConfirmationBddHandler.validateAndAssertFieldsWithParams(parameters, message);
            } else if (confirmation instanceof ClearCacheConfirmation message) {
                clearCacheConfirmationBddHandler.validateAndAssertFieldsWithParams(parameters, message);
            } else if (confirmation instanceof ClearChargingProfileConfirmation message) {
                clearChargingProfileConfirmationBddHandler.validateAndAssertFieldsWithParams(parameters, message);
            } else if (confirmation instanceof DataTransferConfirmation message) {
                dataTransferIncomingConfirmationBddHandler.validateAndAssertFieldsWithParams(parameters, message);
            } else if (confirmation instanceof GetCompositeScheduleConfirmation message) {
                getCompositeScheduleConfirmationBddHandler.validateAndAssertFieldsWithParams(parameters, message);
            } else if (confirmation instanceof GetConfigurationConfirmation message) {
                getConfigurationConfirmationBddHandler.validateAndAssertFieldsWithParams(parameters, message);
            } else if (confirmation instanceof GetDiagnosticsConfirmation message) {
                getDiagnosticsConfirmationBddHandler.validateAndAssertFieldsWithParams(parameters, message);
            } else if (confirmation instanceof GetLocalListVersionConfirmation message) {
                getLocalListVersionConfirmationBddHandler.validateAndAssertFieldsWithParams(parameters, message);
            } else if (confirmation instanceof RemoteStartTransactionConfirmation message) {
                remoteStartTransactionConfirmationBddHandler.validateAndAssertFieldsWithParams(parameters, message);
            } else if (confirmation instanceof RemoteStopTransactionConfirmation message) {
                remoteStopTransactionConfirmationBddHandler.validateAndAssertFieldsWithParams(parameters, message);
            } else if (confirmation instanceof ReserveNowConfirmation message) {
                reserveNowConfirmationBddHandler.validateAndAssertFieldsWithParams(parameters, message);
            } else if (confirmation instanceof ResetConfirmation message) {
                resetConfirmationBddHandler.validateAndAssertFieldsWithParams(parameters, message);
            } else if (confirmation instanceof SendLocalListConfirmation message) {
                sendLocalListConfirmationBddHandler.validateAndAssertFieldsWithParams(parameters, message);
            } else if (confirmation instanceof SetChargingProfileConfirmation message) {
                setChargingProfileConfirmationBddHandler.validateAndAssertFieldsWithParams(parameters, message);
            } else if (confirmation instanceof TriggerMessageConfirmation message) {
                triggerMessageConfirmationBddHandler.validateAndAssertFieldsWithParams(parameters, message);
            } else if (confirmation instanceof UpdateFirmwareConfirmation message) {
                updateFirmwareConfirmationBddHandler.validateAndAssertFieldsWithParams(parameters, message);
            } else if (confirmation instanceof UnlockConnectorConfirmation message) {
                unlockConnectorConfirmationBddHandler.validateAndAssertFieldsWithParams(parameters, message);
            } else {
                throw new BddTestingException(INVALID_CONFIRMATION_TYPE.getValue() + confirmation);
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public OutgoingMessageFactory<? extends Request> getOutgoingRequestFactory(
            ImplementedMessageType type) {
        OutgoingMessageFactory<? extends Request> factory = outgoingRequestHandlers
                .getOrDefault(type, null);
        if (Objects.isNull(factory)) {
            throw new BddTestingException(String.format(INVALID_OUTGOING_FACTORY.getValue(), type.getValue()));
        }
        return factory;
    }

    @Override
    public OutgoingMessageFactory<? extends Confirmation> getOutgoingConfirmationFactory(ImplementedMessageType type) {
        OutgoingMessageFactory<? extends Confirmation> factory =
                outgoingConfirmationHandlers.getOrDefault(type, null);
        if (Objects.isNull(factory)) {
            throw new BddTestingException(String.format(INVALID_OUTGOING_FACTORY.getValue(), type.getValue()));
        }
        return factory;
    }
    
}
