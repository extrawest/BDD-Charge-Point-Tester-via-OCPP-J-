package com.extrawest.jsonserver.validation.incoming.request;

import com.extrawest.jsonserver.validation.incoming.IncomingMessageFactory;
import com.extrawest.jsonserver.validation.incoming.IncomingMessageFieldsAssertionFactory;
import eu.chargetime.ocpp.model.core.ChargePointErrorCode;
import eu.chargetime.ocpp.model.core.ChargePointStatus;
import eu.chargetime.ocpp.model.core.StatusNotificationRequest;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class StatusNotificationRequestBddHandler
        extends IncomingMessageFieldsAssertionFactory<StatusNotificationRequest>
        implements IncomingMessageFactory<StatusNotificationRequest> {

    public static final String CONNECTOR_ID_REQUIRED = "connectorId";
    public static final String ERROR_CODE_REQUIRED = "errorCode";
    public static final String INFO = "info";
    public static final String STATUS_REQUIRED = "status";
    public static final String TIMESTAMP = "timestamp";
    public static final String VENDOR_ID = "vendorId";
    public static final String VENDOR_ERROR_CODE = "vendorErrorCode";

    @PostConstruct
    private void init() {
        this.requiredFieldsSetup = Map.of(
                CONNECTOR_ID_REQUIRED, (req, connectorId) -> {
                    if (nonEqual(wildCard, connectorId)) {
                        req.setConnectorId(Integer.parseInt(connectorId));
                    }
                },
                ERROR_CODE_REQUIRED, (req, errorCode) -> {
                    if (nonEqual(wildCard, errorCode)) {
                        req.setErrorCode(ChargePointErrorCode.valueOf(errorCode));
                    }
                },
                STATUS_REQUIRED, (req, status) -> {
                    if (nonEqual(wildCard, status)) {
                        req.setStatus(ChargePointStatus.valueOf(status));
                    }
                }
        );

        this.optionalFieldsSetup = Map.of(
                INFO, (req, info) -> {
                    if (nonEqual(wildCard, info)) {
                        req.setInfo(info);
                    }
                },
                TIMESTAMP, (req, timestamp) -> {
                    if (nonEqual(wildCard, timestamp)) {
                        req.setTimestamp(ZonedDateTime.parse(timestamp));
                    }
                },
                VENDOR_ID, (req, vendorId) -> {
                    if (nonEqual(wildCard, vendorId)) {
                        req.setVendorId(vendorId);
                    }
                },
                VENDOR_ERROR_CODE, (req, vendorErrorCode) -> {
                    if (nonEqual(wildCard, vendorErrorCode)) {
                        req.setVendorErrorCode(vendorErrorCode);
                    }
                }
        );

        this.assertionFactory = Map.of(
                CONNECTOR_ID_REQUIRED, (expectedParams, actual) -> compareStringsIncludeWildCard(
                        expectedParams, String.valueOf(actual.getConnectorId()), CONNECTOR_ID_REQUIRED),

                ERROR_CODE_REQUIRED, (expectedParams, actual) -> compareStringsIncludeWildCard(
                        expectedParams, String.valueOf(actual.getErrorCode()), ERROR_CODE_REQUIRED),

                INFO, (expectedParams, actual) -> compareStringsIncludeWildCard(
                        expectedParams, actual.getInfo(), INFO),

                STATUS_REQUIRED, (expectedParams, actual) -> compareStringsIncludeWildCard(
                        expectedParams, actual.getStatus().toString(), STATUS_REQUIRED),

                TIMESTAMP, (expectedParams, actual) -> compareTimestampIncludeWildCard(
                        expectedParams, actual.getTimestamp(), TIMESTAMP),

                VENDOR_ID, (expectedParams, actual) -> compareStringsIncludeWildCard(
                        expectedParams, actual.getVendorId(), VENDOR_ID),

                VENDOR_ERROR_CODE, (expectedParams, actual) -> compareStringsIncludeWildCard(
                        expectedParams, actual.getVendorErrorCode(), VENDOR_ERROR_CODE)
        );
    }

    @Override
    public void validateAndAssertFieldsWithParams(Map<String, String> params, StatusNotificationRequest message) {
        super.validateParamsViaLibModel(params);
        super.assertParamsAndMessageFields(params, message);
    }

}
