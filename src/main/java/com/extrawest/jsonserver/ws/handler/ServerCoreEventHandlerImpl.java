package com.extrawest.jsonserver.ws.handler;

import static com.extrawest.jsonserver.model.emun.ImplementedReceivedMessageType.Authorize;
import static com.extrawest.jsonserver.model.emun.ImplementedReceivedMessageType.BootNotification;
import static com.extrawest.jsonserver.model.emun.ImplementedReceivedMessageType.DataTransfer;
import static com.extrawest.jsonserver.model.emun.ImplementedReceivedMessageType.Heartbeat;
import static com.extrawest.jsonserver.model.emun.ImplementedReceivedMessageType.MeterValue;
import static com.extrawest.jsonserver.model.emun.ImplementedReceivedMessageType.StartTransaction;
import static com.extrawest.jsonserver.model.emun.ImplementedReceivedMessageType.StatusNotification;
import static com.extrawest.jsonserver.model.emun.ImplementedReceivedMessageType.StopTransaction;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import eu.chargetime.ocpp.feature.profile.ServerCoreEventHandler;
import com.extrawest.jsonserver.model.emun.ImplementedReceivedMessageType;
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
    @Getter @Setter private Confirmation response = null;

    @Value("${default.sleep.awaiting.time:100}")
    private long defaultSleepAwaitingTime;

    @Override
    public AuthorizeConfirmation handleAuthorizeRequest(UUID sessionIndex, AuthorizeRequest request) {
        log.debug("AuthorizeRequest: " + request);
        storeMessageIfItIsNeededForBDDPurpose(sessionIndex, request, Authorize);

        while (Objects.isNull(response) || !(response instanceof AuthorizeConfirmation)) {
            waitingDefaultTime();
        }
        AuthorizeConfirmation confirmation = (AuthorizeConfirmation) response;
        response = null;
        return confirmation;
    }

    @Override
    public BootNotificationConfirmation handleBootNotificationRequest(UUID sessionIndex,
                                                                      BootNotificationRequest request) {
        log.debug("BootNotificationRequest: " + request);
        storeMessageIfItIsNeededForBDDPurpose(sessionIndex, request, BootNotification);

        while (Objects.isNull(response) || !(response instanceof BootNotificationConfirmation)) {
            waitingDefaultTime();
        }
        BootNotificationConfirmation confirmation = (BootNotificationConfirmation) response;
        response = null;
        return confirmation;
    }

    @Override
    public DataTransferConfirmation handleDataTransferRequest(UUID sessionIndex, DataTransferRequest request) {
        log.debug("DataTransferRequest: " + request);
        storeMessageIfItIsNeededForBDDPurpose(sessionIndex, request, DataTransfer);

        while (Objects.isNull(response) || !(response instanceof DataTransferConfirmation)) {
            waitingDefaultTime();
        }
        DataTransferConfirmation confirmation = (DataTransferConfirmation) response;
        response = null;
        return confirmation;
    }

    @Override
    public HeartbeatConfirmation handleHeartbeatRequest(UUID sessionIndex, HeartbeatRequest request) {
        log.debug("HeartbeatRequest: " + request);
        storeMessageIfItIsNeededForBDDPurpose(sessionIndex, request, Heartbeat);

        while (Objects.isNull(response) || !(response instanceof HeartbeatConfirmation)) {
            waitingDefaultTime();
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
        storeMessageIfItIsNeededForBDDPurpose(sessionIndex, request, MeterValue);

        while (Objects.isNull(response) || !(response instanceof MeterValuesConfirmation)) {
            waitingDefaultTime();
        }
        MeterValuesConfirmation confirmation = (MeterValuesConfirmation) response;
        response = null;
        return confirmation;
    }

    @Override
    public StartTransactionConfirmation handleStartTransactionRequest(UUID sessionIndex,
                                                                      StartTransactionRequest request) {
        log.debug("StartTransactionRequest: " + request);
        storeMessageIfItIsNeededForBDDPurpose(sessionIndex, request, StartTransaction);

        while (Objects.isNull(response) || !(response instanceof StartTransactionConfirmation)) {
            waitingDefaultTime();
        }
        StartTransactionConfirmation confirmation = (StartTransactionConfirmation) response;
        response = null;
        return confirmation;
    }

    @Override
    public StatusNotificationConfirmation handleStatusNotificationRequest(UUID sessionIndex,
                                                                          StatusNotificationRequest request) {
        log.debug("StatusNotificationRequest: " + request);
        storeMessageIfItIsNeededForBDDPurpose(sessionIndex, request, StatusNotification);

        while (Objects.isNull(response) || !(response instanceof StatusNotificationConfirmation)) {
            waitingDefaultTime();
        }
        StatusNotificationConfirmation confirmation = (StatusNotificationConfirmation) response;
        response = null;
        return confirmation;
    }

    @Override
    public StopTransactionConfirmation handleStopTransactionRequest(UUID sessionIndex,
                                                                    StopTransactionRequest request) {
        log.debug("StopTransactionRequest: " + request);
        storeMessageIfItIsNeededForBDDPurpose(sessionIndex, request, StopTransaction);

        while (Objects.isNull(response) || !(response instanceof StopTransactionConfirmation)) {
            waitingDefaultTime();
        }
        StopTransactionConfirmation confirmation = (StopTransactionConfirmation) response;
        response = null;
        return confirmation;
    }

    private void storeMessageIfItIsNeededForBDDPurpose(UUID sessionIndex, Request request,
                                                       ImplementedReceivedMessageType type) {
        String chargePointId = sessionRepository.getChargerIdBySession(sessionIndex);
        Optional<List<ImplementedReceivedMessageType>> requestedMessageTypes =
                bddDataRepository.getRequestedMessageTypes(chargePointId);
        if (requestedMessageTypes.isEmpty() || !requestedMessageTypes.get().contains(type)) {
            return;
        }
        bddDataRepository.addRequestedMessage(chargePointId, request);
    }

    private void waitingDefaultTime() {
        try {
            Thread.sleep(defaultSleepAwaitingTime);
        } catch (InterruptedException e) {
            log.warn("Ups... sleep is unavailable... ");
        }
    }

}
