package com.extrawest.jsonserver.model.emun;

import lombok.AllArgsConstructor;
import lombok.Getter;

import static com.extrawest.jsonserver.util.EnumUtil.findByField;

import java.util.Arrays;
import java.util.Objects;

/**
 * Only implemented message types for receiving
 */
@Getter
@AllArgsConstructor
public enum ImplementedMessageType {
    AUTHORIZE("Authorize"),
    BOOT_NOTIFICATION("BootNotification"),
    DATA_TRANSFER("DataTransfer"),
    DIAGNOSTICS_STATUS_NOTIFICATION("DiagnosticsStatusNotification"),
    FIRMWARE_STATUS_NOTIFICATION("FirmwareStatusNotification"),
    HEARTBEAT("Heartbeat"),
    METER_VALUES("MeterValues"),
    START_TRANSACTION("StartTransaction"),
    STATUS_NOTIFICATION("StatusNotification"),
    STOP_TRANSACTION("StopTransaction"),
    TRIGGER_MESSAGE("TriggerMessage"),
    RESET("Reset");


    private final String value;

    @Override
    public String toString() {
        return this.value;
    }

    public static boolean contains(String value) {
        return Arrays.stream(ImplementedMessageType.values())
                .anyMatch(e -> Objects.equals(e.value, value));
    }

    public static ImplementedMessageType fromValue(String value) {
        return findByField(
                ImplementedMessageType.class,
                ImplementedMessageType::getValue,
                value
        );
    }

}
