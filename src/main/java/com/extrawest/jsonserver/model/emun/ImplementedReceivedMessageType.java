package com.extrawest.jsonserver.model.emun;

/**
 * Only implemented message types for receiving
 */
public enum ImplementedReceivedMessageType {
    Authorize,
    BootNotification,
    DataTransfer,
    DiagnosticsStatusNotification,
    FirmwareStatusNotification,
    Heartbeat,
    MeterValue,
    StartTransaction,
    StatusNotification,
    StopTransaction

}
