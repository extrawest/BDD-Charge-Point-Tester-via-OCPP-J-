package com.extrawest.jsonserver.model.emun;

import lombok.AllArgsConstructor;
import lombok.Getter;

import static com.extrawest.jsonserver.util.EnumUtil.findByField;

/**
 * Only implemented message types for receiving
 */
@Getter
@AllArgsConstructor
public enum ImplementedReceivedMessageType {
    AUTHORIZE("Authorize"),
    BOOT_NOTIFICATION("BootNotification"),
    DATA_TRANSFER("DataTransfer"),
    DIAGNOSTICS_STATUS_NOTIFICATION("DiagnosticsStatusNotification"),
    FIRMWARE_STATUS_NOTIFICATION("FirmwareStatusNotification"),
    HEARTBEAT("Heartbeat"),
    METER_VALUES("MeterValues"),
    START_TRANSACTION("StartTransaction"),
    STATUS_NOTIFICATION("StatusNotification"),
    STOP_TRANSACTION("StopTransaction");

    private final String value;

    @Override
    public String toString() {
        return this.value;
    }

    public static ImplementedReceivedMessageType fromValue(String value) {
        return findByField(
                ImplementedReceivedMessageType.class,
                ImplementedReceivedMessageType::getValue,
                value
        );
    }

}
