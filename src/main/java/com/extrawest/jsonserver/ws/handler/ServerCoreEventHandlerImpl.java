package com.extrawest.jsonserver.ws.handler;

import static com.extrawest.jsonserver.model.emun.ImplementedReceivedMessageType.Authorize;
import static com.extrawest.jsonserver.model.emun.ImplementedReceivedMessageType.BootNotification;
import static com.extrawest.jsonserver.model.emun.ImplementedReceivedMessageType.DataTransfer;
import static com.extrawest.jsonserver.model.emun.ImplementedReceivedMessageType.Heartbeat;
import static com.extrawest.jsonserver.model.emun.ImplementedReceivedMessageType.MeterValue;
import static com.extrawest.jsonserver.model.emun.ImplementedReceivedMessageType.StartTransaction;
import static com.extrawest.jsonserver.model.emun.ImplementedReceivedMessageType.StatusNotification;
import static com.extrawest.jsonserver.model.emun.ImplementedReceivedMessageType.StopTransaction;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import com.extrawest.jsonserver.repository.TransactionRepository;
import eu.chargetime.ocpp.feature.profile.ServerCoreEventHandler;
import com.extrawest.jsonserver.model.emun.ImplementedReceivedMessageType;
import com.extrawest.jsonserver.repository.BddDataRepository;
import com.extrawest.jsonserver.repository.ServerSessionRepository;
import eu.chargetime.ocpp.model.Request;
import eu.chargetime.ocpp.model.core.AuthorizationStatus;
import eu.chargetime.ocpp.model.core.AuthorizeConfirmation;
import eu.chargetime.ocpp.model.core.AuthorizeRequest;
import eu.chargetime.ocpp.model.core.BootNotificationConfirmation;
import eu.chargetime.ocpp.model.core.BootNotificationRequest;
import eu.chargetime.ocpp.model.core.DataTransferConfirmation;
import eu.chargetime.ocpp.model.core.DataTransferRequest;
import eu.chargetime.ocpp.model.core.DataTransferStatus;
import eu.chargetime.ocpp.model.core.HeartbeatConfirmation;
import eu.chargetime.ocpp.model.core.HeartbeatRequest;
import eu.chargetime.ocpp.model.core.IdTagInfo;
import eu.chargetime.ocpp.model.core.MeterValuesConfirmation;
import eu.chargetime.ocpp.model.core.MeterValuesRequest;
import eu.chargetime.ocpp.model.core.RegistrationStatus;
import eu.chargetime.ocpp.model.core.StartTransactionConfirmation;
import eu.chargetime.ocpp.model.core.StartTransactionRequest;
import eu.chargetime.ocpp.model.core.StatusNotificationConfirmation;
import eu.chargetime.ocpp.model.core.StatusNotificationRequest;
import eu.chargetime.ocpp.model.core.StopTransactionConfirmation;
import eu.chargetime.ocpp.model.core.StopTransactionRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ServerCoreEventHandlerImpl implements ServerCoreEventHandler {
    private final BddDataRepository bddDataRepository;
    private final TransactionRepository transactionRepository;
    private final ServerSessionRepository sessionRepository;

    @Override
    public AuthorizeConfirmation handleAuthorizeRequest(UUID sessionIndex, AuthorizeRequest request) {
        log.info("AuthorizeRequest: " + request);

        IdTagInfo idTagInfo = new IdTagInfo(AuthorizationStatus.Accepted);
        idTagInfo.setExpiryDate(ZonedDateTime.now().plusMonths(1));
        idTagInfo.setParentIdTag(request.getIdTag());

        AuthorizeConfirmation confirmation = new AuthorizeConfirmation(idTagInfo);

        String chargePointId = sessionRepository.getChargerIdBySession(sessionIndex);
        storeMessageIfItIsNeededForBDDPurpose(sessionIndex, request, Authorize);
        if (bddDataRepository.isCharging(chargePointId)) {
            bddDataRepository.addRequestedMessageType(chargePointId, StartTransaction);
            bddDataRepository.addRequestedMessageType(chargePointId, StopTransaction);
            bddDataRepository.removeRequestedMessageType(chargePointId, Authorize);
        }
        return confirmation;
    }

    @Override
    public BootNotificationConfirmation handleBootNotificationRequest(UUID sessionIndex, BootNotificationRequest request) {
        log.info("BootNotificationRequest: " + request);

        BootNotificationConfirmation confirmation =
                new BootNotificationConfirmation(ZonedDateTime.now(), 300, RegistrationStatus.Accepted);

        String chargePointId = sessionRepository.getChargerIdBySession(sessionIndex);
        bddDataRepository.addRequestedMessageType(chargePointId, Authorize);
        bddDataRepository.addRequestedMessageType(chargePointId, Heartbeat);
        storeMessageIfItIsNeededForBDDPurpose(sessionIndex, request, BootNotification);
        bddDataRepository.removeRequestedMessageType(chargePointId, BootNotification);

        return confirmation;
    }

    @Override
    public DataTransferConfirmation handleDataTransferRequest(UUID sessionIndex, DataTransferRequest request) {
        log.info("DataTransferRequest: " + request);
        // ... handle event

        DataTransferConfirmation confirmation = new DataTransferConfirmation(DataTransferStatus.UnknownVendorId);
        confirmation.setData("lalala");

        storeMessageIfItIsNeededForBDDPurpose(sessionIndex, request, DataTransfer);
        return confirmation;
    }

    @Override
    public HeartbeatConfirmation handleHeartbeatRequest(UUID sessionIndex, HeartbeatRequest request) {
        log.info("HeartbeatRequest: " + request);

        HeartbeatConfirmation confirmation = new HeartbeatConfirmation(ZonedDateTime.now());

        storeMessageIfItIsNeededForBDDPurpose(sessionIndex, request, Heartbeat);

        return confirmation;
    }

    @Override
    public MeterValuesConfirmation handleMeterValuesRequest(UUID sessionIndex, MeterValuesRequest request) {
        log.info("MeterValuesRequest: " + request + ", with SampledValues: " + Arrays.stream(request.getMeterValue())
                .flatMap(x -> Arrays.stream(x.getSampledValue()))
                .toList());

        MeterValuesConfirmation confirmation = new MeterValuesConfirmation();

        storeMessageIfItIsNeededForBDDPurpose(sessionIndex, request, MeterValue);

        return confirmation;
    }

    @Override
    public StartTransactionConfirmation handleStartTransactionRequest(UUID sessionIndex, StartTransactionRequest request) {
        log.info("StartTransactionRequest: " + request);

        IdTagInfo idTagInfo = new IdTagInfo(AuthorizationStatus.Accepted);
        idTagInfo.setExpiryDate(ZonedDateTime.now().plusMonths(1));
        idTagInfo.setParentIdTag(request.getIdTag());

        String chargePointId = sessionRepository.getChargerIdBySession(sessionIndex);
        int transactionId = transactionRepository.addTransaction(chargePointId);
        StartTransactionConfirmation confirmation = new StartTransactionConfirmation(idTagInfo, transactionId);

        bddDataRepository.addRequestedMessageType(sessionRepository.getChargerIdBySession(sessionIndex), Authorize);
        bddDataRepository.addRequestedMessageType(sessionRepository.getChargerIdBySession(sessionIndex), MeterValue);
        bddDataRepository.addRequestedMessageType(sessionRepository.getChargerIdBySession(sessionIndex), StopTransaction);
        storeMessageIfItIsNeededForBDDPurpose(sessionIndex, request, StartTransaction);
        bddDataRepository.removeRequestedMessageType(sessionRepository.getChargerIdBySession(sessionIndex), StartTransaction);

        return confirmation;
    }

    @Override
    public StatusNotificationConfirmation handleStatusNotificationRequest(UUID sessionIndex, StatusNotificationRequest request) {
        log.info("StatusNotificationRequest: " + request);

        StatusNotificationConfirmation confirmation = new StatusNotificationConfirmation();

        storeMessageIfItIsNeededForBDDPurpose(sessionIndex, request, StatusNotification);
        return confirmation;
    }

    @Override
    public StopTransactionConfirmation handleStopTransactionRequest(UUID sessionIndex, StopTransactionRequest request) {
        log.info("StopTransactionRequest: " + request);

        StopTransactionConfirmation confirmation = new StopTransactionConfirmation();
        IdTagInfo idTagInfo = new IdTagInfo(AuthorizationStatus.Accepted);
        idTagInfo.setExpiryDate(ZonedDateTime.now().plusMonths(1));
        idTagInfo.setParentIdTag(request.getIdTag());

        confirmation.setIdTagInfo(idTagInfo);

        storeMessageIfItIsNeededForBDDPurpose(sessionIndex, request, StopTransaction);
        bddDataRepository.removeRequestedMessageType(sessionRepository.getChargerIdBySession(sessionIndex), Authorize);
        bddDataRepository.removeRequestedMessageType(sessionRepository.getChargerIdBySession(sessionIndex), MeterValue);
        bddDataRepository.removeRequestedMessageType(sessionRepository.getChargerIdBySession(sessionIndex), StopTransaction);

        return confirmation;
    }

    private void storeMessageIfItIsNeededForBDDPurpose(UUID sessionIndex, Request request, ImplementedReceivedMessageType type) {
        String chargePointId = sessionRepository.getChargerIdBySession(sessionIndex);
        Optional<List<ImplementedReceivedMessageType>> requestedMessageTypes = bddDataRepository.getRequestedMessageTypes(chargePointId);
        if (requestedMessageTypes.isEmpty() || !requestedMessageTypes.get().contains(type)) {
            return;
        }
        bddDataRepository.addRequestedMessage(chargePointId, request);
    }

}
