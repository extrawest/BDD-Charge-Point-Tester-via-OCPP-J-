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
    CANCEL_RESERVATION("CancelReservation"),
    CHANGE_AVAILABILITY("ChangeAvailability"),
    CHANGE_CONFIGURATION("ChangeConfiguration"),
    CLEAR_CACHE("ClearCache"),
    CLEAR_CHARGING_PROFILE("ClearChargingProfile"),
    SET_CHARGING_PROFILE("SetChargingProfile"),
    GET_COMPOSITE_SCHEDULE("GetCompositeSchedule"),
    GET_CONFIGURATION("GetConfiguration"),
    RESET("Reset"),
    SEND_LOCAL_LIST("SendLocalList"),
    TRIGGER_MESSAGE("TriggerMessage"),
    UNLOCK_CONNECTOR("UnlockConnector"),
    UPDATE_FIRMWARE("UpdateFirmware");


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
