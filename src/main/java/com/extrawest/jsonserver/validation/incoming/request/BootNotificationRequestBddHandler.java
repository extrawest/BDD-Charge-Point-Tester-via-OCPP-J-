package com.extrawest.jsonserver.validation.incoming.request;

import java.util.Map;
import com.extrawest.jsonserver.validation.incoming.IncomingMessageFactory;
import com.extrawest.jsonserver.validation.incoming.IncomingMessageFieldsAssertionFactory;
import eu.chargetime.ocpp.model.core.BootNotificationRequest;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BootNotificationRequestBddHandler
        extends IncomingMessageFieldsAssertionFactory<BootNotificationRequest>
        implements IncomingMessageFactory<BootNotificationRequest> {
    public static final String CHARGE_BOX_SERIAL_NUMBER = "chargeBoxSerialNumber";
    public static final String CHARGE_POINT_MODEL_REQUIRED = "chargePointModel";
    public static final String CHARGE_POINT_SERIAL_NUMBER = "chargePointSerialNumber";
    public static final String CHARGE_POINT_VENDOR_REQUIRED = "chargePointVendor";
    public static final String FIRMWARE_VERSION = "firmwareVersion";
    public static final String ICCID = "iccid";
    public static final String IMSI = "imsi";
    public static final String METER_SERIAL_NUMBER = "meterSerialNumber";
    public static final String METER_TYPE = "meterType";

    @PostConstruct
    private void init() {
        this.requiredFieldsSetup = Map.of(
                CHARGE_POINT_MODEL_REQUIRED, (req, model) -> {
                    if (nonEqual(wildCard, model)) {
                        req.setChargePointModel(model);
                    }
                },
                CHARGE_POINT_VENDOR_REQUIRED, (req, vendor) -> {
                    if (nonEqual(wildCard, vendor)) {
                        req.setChargePointVendor(vendor);
                    }
                }
        );

        this.optionalFieldsSetup = Map.of(
                CHARGE_BOX_SERIAL_NUMBER, (req, serial) -> {
                    if (nonEqual(wildCard, serial)) {
                        req.setChargeBoxSerialNumber(serial);
                    }
                },
                CHARGE_POINT_SERIAL_NUMBER, (req, serial) -> {
                    if (nonEqual(wildCard, serial)) {
                        req.setChargePointSerialNumber(serial);
                    }
                },
                FIRMWARE_VERSION, (req, version) -> {
                    if (nonEqual(wildCard, version)) {
                        req.setFirmwareVersion(version);
                    }
                },
                ICCID, (req, value) -> {
                    if (nonEqual(wildCard, value)) {
                        req.setIccid(value);
                    }
                },
                IMSI, (req, value) -> {
                    if (nonEqual(wildCard, value)) {
                    req.setImsi(value);
                    }
                },
                METER_SERIAL_NUMBER, (req, meterSerial) -> {
                    if (nonEqual(wildCard, meterSerial)) {
                        req.setMeterSerialNumber(meterSerial);
                    }
                },
                METER_TYPE, (req, type) -> {
                    if (nonEqual(wildCard, type)) {
                        req.setMeterType(type);
                    }
                }
        );

        this.assertionFactory = Map.of(
                CHARGE_POINT_MODEL_REQUIRED, (expectedParams, actual) -> compareStringsIncludeWildCard(
                        expectedParams, actual.getChargePointModel(), CHARGE_POINT_MODEL_REQUIRED),

                CHARGE_POINT_VENDOR_REQUIRED, (expectedParams, actual) -> compareStringsIncludeWildCard(
                        expectedParams, actual.getChargePointVendor(), CHARGE_POINT_VENDOR_REQUIRED),

                CHARGE_BOX_SERIAL_NUMBER, (expectedParams, actual) -> compareStringsIncludeWildCard(
                        expectedParams, actual.getChargeBoxSerialNumber(),
                        CHARGE_BOX_SERIAL_NUMBER),

                CHARGE_POINT_SERIAL_NUMBER, (expectedParams, actual) -> compareStringsIncludeWildCard(
                        expectedParams, actual.getChargePointSerialNumber(),
                        CHARGE_POINT_SERIAL_NUMBER),

                FIRMWARE_VERSION, (expectedParams, actual) -> compareStringsIncludeWildCard(
                        expectedParams, actual.getFirmwareVersion(), FIRMWARE_VERSION),

                ICCID, (expectedParams, actual) -> compareStringsIncludeWildCard(
                        expectedParams, actual.getIccid(), ICCID),

                IMSI, (expectedParams, actual) -> compareStringsIncludeWildCard(
                        expectedParams, actual.getImsi(), IMSI),

                METER_SERIAL_NUMBER, (expectedParams, actual) -> compareStringsIncludeWildCard(
                        expectedParams, actual.getMeterSerialNumber(), METER_SERIAL_NUMBER),

                METER_TYPE, (expectedParams, actual) -> compareStringsIncludeWildCard(
                        expectedParams, actual.getMeterType(), METER_TYPE)
        );
    }

    @Override
    public void validateAndAssertFieldsWithParams(Map<String, String> params, BootNotificationRequest message) {
        super.validateParamsViaLibModel(params);
        super.assertParamsAndMessageFields(params, message);
    }

}
