package com.extrawest.jsonserver.ws.handler;

import static com.extrawest.jsonserver.model.emun.ApiErrorMessage.UNEXPECTED_MESSAGE_RECEIVED;
import static com.extrawest.jsonserver.model.emun.ImplementedMessageType.AUTHORIZE;
import static com.extrawest.jsonserver.model.emun.ImplementedMessageType.BOOT_NOTIFICATION;
import static com.extrawest.jsonserver.model.emun.ImplementedMessageType.DATA_TRANSFER;
import static com.extrawest.jsonserver.model.emun.ImplementedMessageType.HEARTBEAT;
import static com.extrawest.jsonserver.model.emun.ImplementedMessageType.METER_VALUES;
import static com.extrawest.jsonserver.model.emun.ImplementedMessageType.START_TRANSACTION;
import static com.extrawest.jsonserver.model.emun.ImplementedMessageType.STATUS_NOTIFICATION;
import static com.extrawest.jsonserver.model.emun.ImplementedMessageType.STOP_TRANSACTION;
import static com.extrawest.jsonserver.util.TimeUtil.*;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import eu.chargetime.ocpp.feature.profile.ServerCoreEventHandler;
import com.extrawest.jsonserver.model.emun.ImplementedMessageType;
import com.extrawest.jsonserver.repository.BddDataRepository;
import com.extrawest.jsonserver.repository.ServerSessionRepository;
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
import eu.chargetime.ocpp.model.core.StartTransactionConfirmation;
import eu.chargetime.ocpp.model.core.StartTransactionRequest;
import eu.chargetime.ocpp.model.core.StatusNotificationConfirmation;
import eu.chargetime.ocpp.model.core.StatusNotificationRequest;
import eu.chargetime.ocpp.model.core.StopTransactionConfirmation;
import eu.chargetime.ocpp.model.core.StopTransactionRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Primary
@RequiredArgsConstructor
public class ServerCoreEventHandlerImpl implements ServerCoreEventHandler {

    private final BddDataRepository bddDataRepository;

    private final ServerSessionRepository sessionRepository;

    @Getter
    @Setter
    private Confirmation response = null;

    @Value("${default.sleep.awaiting.time:100}")
    private long defaultSleepAwaitingTime;

    @Override
    public AuthorizeConfirmation handleAuthorizeRequest(UUID sessionIndex, AuthorizeRequest request) {
        log.debug("AuthorizeRequest: " + request);
        storeMessageIfItIsNeededForBDDPurpose(sessionIndex, request, AUTHORIZE);

        while (Objects.isNull(response) || !(response instanceof AuthorizeConfirmation)) {
            sleep(defaultSleepAwaitingTime);
        }
        AuthorizeConfirmation confirmation = (AuthorizeConfirmation) response;
        response = null;
        return confirmation;
    }

    @Override
    public BootNotificationConfirmation handleBootNotificationRequest(UUID sessionIndex,
                                                                      BootNotificationRequest request) {
        log.debug("BootNotificationRequest: " + request);
        storeMessageIfItIsNeededForBDDPurpose(sessionIndex, request, BOOT_NOTIFICATION);

        while (Objects.isNull(response) || !(response instanceof BootNotificationConfirmation)) {
            sleep(defaultSleepAwaitingTime);
        }
        BootNotificationConfirmation confirmation = (BootNotificationConfirmation) response;
        response = null;
        return confirmation;
    }

    @Override
    public DataTransferConfirmation handleDataTransferRequest(UUID sessionIndex, DataTransferRequest request) {
        log.debug("DataTransferRequest: " + request);
        storeMessageIfItIsNeededForBDDPurpose(sessionIndex, request, DATA_TRANSFER);

        while (Objects.isNull(response) || !(response instanceof DataTransferConfirmation)) {
            sleep(defaultSleepAwaitingTime);
        }
        DataTransferConfirmation confirmation = (DataTransferConfirmation) response;
        response = null;
        return confirmation;
    }

    @Override
    public HeartbeatConfirmation handleHeartbeatRequest(UUID sessionIndex, HeartbeatRequest request) {
        log.debug("HeartbeatRequest: " + request);
        storeMessageIfItIsNeededForBDDPurpose(sessionIndex, request, HEARTBEAT);

        while (Objects.isNull(response) || !(response instanceof HeartbeatConfirmation)) {
            sleep(defaultSleepAwaitingTime);
        }
        HeartbeatConfirmation confirmation = (HeartbeatConfirmation) response;
        response = null;
        return confirmation;
    }

    @Override
    public MeterValuesConfirmation handleMeterValuesRequest(UUID sessionIndex, MeterValuesRequest request) {
        log.debug("MeterValuesRequest: " + request + ", with SampledValues: "
                + Arrays.stream(request.getMeterValue())
                .flatMap(x -> Arrays.stream(x.getSampledValue()))
                .toList()
        );
        storeMessageIfItIsNeededForBDDPurpose(sessionIndex, request, METER_VALUES);

        while (Objects.isNull(response) || !(response instanceof MeterValuesConfirmation)) {
            sleep(defaultSleepAwaitingTime);
        }
        MeterValuesConfirmation confirmation = (MeterValuesConfirmation) response;
        response = null;
        return confirmation;
    }

    @Override
    public StartTransactionConfirmation handleStartTransactionRequest(UUID sessionIndex,
                                                                      StartTransactionRequest request) {
        log.debug("StartTransactionRequest: " + request);
        storeMessageIfItIsNeededForBDDPurpose(sessionIndex, request, START_TRANSACTION);

        while (Objects.isNull(response) || !(response instanceof StartTransactionConfirmation)) {
            sleep(defaultSleepAwaitingTime);
        }
        StartTransactionConfirmation confirmation = (StartTransactionConfirmation) response;
        response = null;
        return confirmation;
    }

    @Override
    public StatusNotificationConfirmation handleStatusNotificationRequest(UUID sessionIndex,
                                                                          StatusNotificationRequest request) {
        log.debug("StatusNotificationRequest: " + request);
        storeMessageIfItIsNeededForBDDPurpose(sessionIndex, request, STATUS_NOTIFICATION);

        while (Objects.isNull(response) || !(response instanceof StatusNotificationConfirmation)) {
            sleep(defaultSleepAwaitingTime);
        }
        StatusNotificationConfirmation confirmation = (StatusNotificationConfirmation) response;
        response = null;
        return confirmation;
    }

    @Override
    public StopTransactionConfirmation handleStopTransactionRequest(UUID sessionIndex,
                                                                    StopTransactionRequest request) {
        log.debug("StopTransactionRequest: " + request);
        storeMessageIfItIsNeededForBDDPurpose(sessionIndex, request, STOP_TRANSACTION);

        while (Objects.isNull(response) || !(response instanceof StopTransactionConfirmation)) {
            sleep(defaultSleepAwaitingTime);
        }
        StopTransactionConfirmation confirmation = (StopTransactionConfirmation) response;
        response = null;
        return confirmation;
    }

    private void storeMessageIfItIsNeededForBDDPurpose(UUID sessionIndex, Request request,
                                                       ImplementedMessageType type) {
        String chargePointId = sessionRepository.getChargerIdBySession(sessionIndex);
        Optional<List<ImplementedMessageType>> requestedMessageTypes =
                bddDataRepository.getRequestedMessageTypes(chargePointId);
        if (requestedMessageTypes.isEmpty() || !requestedMessageTypes.get().contains(type)) {
            log.warn(String.format(UNEXPECTED_MESSAGE_RECEIVED.getValue(), request.getClass().getSimpleName(), request));
            return;
        }
        bddDataRepository.addRequestedMessage(chargePointId, request);
    }

}
