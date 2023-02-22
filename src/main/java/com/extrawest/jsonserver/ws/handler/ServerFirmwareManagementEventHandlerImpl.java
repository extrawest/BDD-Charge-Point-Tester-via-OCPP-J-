package com.extrawest.jsonserver.ws.handler;

import java.util.UUID;
import eu.chargetime.ocpp.feature.profile.ServerFirmwareManagementEventHandler;
import eu.chargetime.ocpp.model.firmware.DiagnosticsStatusNotificationConfirmation;
import eu.chargetime.ocpp.model.firmware.DiagnosticsStatusNotificationRequest;
import eu.chargetime.ocpp.model.firmware.FirmwareStatusNotificationConfirmation;
import eu.chargetime.ocpp.model.firmware.FirmwareStatusNotificationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ServerFirmwareManagementEventHandlerImpl implements ServerFirmwareManagementEventHandler {

    @Override
    public DiagnosticsStatusNotificationConfirmation handleDiagnosticsStatusNotificationRequest(
            UUID sessionIndex, DiagnosticsStatusNotificationRequest request
    ) {
        return new DiagnosticsStatusNotificationConfirmation();
    }

    @Override
    public FirmwareStatusNotificationConfirmation handleFirmwareStatusNotificationRequest(
            UUID sessionIndex, FirmwareStatusNotificationRequest request
    ) {
        return new FirmwareStatusNotificationConfirmation();
    }
}
