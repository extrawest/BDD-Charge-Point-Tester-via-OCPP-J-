package com.extrawest.jsonserver.validation.request;

import java.time.ZonedDateTime;
import java.util.Map;
import com.extrawest.jsonserver.validation.RequestFactory;
import com.extrawest.jsonserver.validation.ValidationAndAssertionRequestFieldsFactory;
import eu.chargetime.ocpp.model.core.StartTransactionRequest;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StartTransactionRequestBddHandler
        extends ValidationAndAssertionRequestFieldsFactory<StartTransactionRequest>
        implements RequestFactory<StartTransactionRequest> {
    public static final String CONNECTOR_ID_REQUIRED = "connectorId";
    public static final String ID_TAG_REQUIRED = "idTag";
    public static final String METER_START_REQUIRED = "meterStart";
    public static final String TIMESTAMP_REQUIRED = "timestamp";
    public static final String RESERVATION_ID = "reservationId";

    @PostConstruct
    private void init() {
        this.requiredFieldsSetup = Map.of(
                CONNECTOR_ID_REQUIRED, (req, connectorId) -> {
                    if (nonEqual(wildCard, connectorId)) {
                        req.setConnectorId(Integer.parseInt(connectorId));
                    }
                },
                ID_TAG_REQUIRED, (req, idTag) -> {
                    if (nonEqual(wildCard, idTag)) {
                        req.setIdTag(idTag);
                    }
                },
                METER_START_REQUIRED, (req, meterStart) -> {
                    if (nonEqual(wildCard, meterStart)) {
                        req.setMeterStart(Integer.parseInt(meterStart));
                    }
                },
                TIMESTAMP_REQUIRED, (req, timestamp) -> {
                    if (nonEqual(wildCard, timestamp)) {
                        req.setTimestamp(ZonedDateTime.parse(timestamp));
                    }
                }
        );

        this.optionalFieldsSetup = Map.of(
                RESERVATION_ID, (req, reservationId) -> {
                    if (nonEqual(wildCard, reservationId)) {
                        req.setReservationId(Integer.parseInt(reservationId));
                    }
                }
        );

        this.assertionFactory = Map.of(
                CONNECTOR_ID_REQUIRED, (expectedParams, actual) -> compareIntegerIncludeWildCard(
                        expectedParams, actual.getConnectorId(), CONNECTOR_ID_REQUIRED),
                ID_TAG_REQUIRED, (expectedParams, actual) -> compareStringsIncludeWildCard(
                        expectedParams, actual.getIdTag(), ID_TAG_REQUIRED),
                METER_START_REQUIRED, (expectedParams, actual) -> compareIntegerIncludeWildCard(
                        expectedParams, actual.getMeterStart(), METER_START_REQUIRED),
                TIMESTAMP_REQUIRED, (expectedParams, actual) -> compareTimestampIncludeWildCard(
                        expectedParams, actual.getTimestamp(), TIMESTAMP_REQUIRED),
                RESERVATION_ID, (expectedParams, actual) -> compareIntegerIncludeWildCard(
                        expectedParams, actual.getReservationId(), RESERVATION_ID)
        );
    }

    @Override
    public boolean validateFields(Map<String, String> params, StartTransactionRequest actualRequest) {
        return super.validateRequestFields(params, actualRequest);
    }

}
