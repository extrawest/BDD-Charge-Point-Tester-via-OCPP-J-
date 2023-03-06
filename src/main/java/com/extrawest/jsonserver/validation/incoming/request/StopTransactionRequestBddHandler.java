package com.extrawest.jsonserver.validation.incoming.request;

import com.extrawest.jsonserver.validation.incoming.IncomingMessageFactory;
import com.extrawest.jsonserver.validation.incoming.IncomingMessageFieldsAssertionFactory;
import eu.chargetime.ocpp.model.core.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class StopTransactionRequestBddHandler
        extends IncomingMessageFieldsAssertionFactory<StopTransactionRequest>
        implements IncomingMessageFactory<StopTransactionRequest> {

    public static final String ID_TAG = "idTag";
    public static final String METER_STOP_REQUIRED = "meterStop";
    public static final String TIMESTAMP_REQUIRED = "timestamp";
    public static final String TRANSACTION_ID_REQUIRED = "transactionId";
    public static final String REASON = "reason";
    public static final String TRANSACTION_DATA = "transactionData";

    @PostConstruct
    private void init() {
        this.requiredFieldsSetup = Map.of(
                METER_STOP_REQUIRED, (req, meterStop) -> {
                    if (nonEqual(wildCard, meterStop)) {
                        req.setMeterStop(Integer.parseInt(meterStop));
                    }
                },
                TIMESTAMP_REQUIRED, (req, timestamp) -> {
                    if (nonEqual(wildCard, timestamp)) {
                        req.setTimestamp(ZonedDateTime.parse(timestamp));
                    }
                },
                TRANSACTION_ID_REQUIRED, (req, transactionId) -> {
                    if (nonEqual(wildCard, transactionId)) {
                        req.setTransactionId(Integer.parseInt(transactionId));
                    }
                }
        );

        this.optionalFieldsSetup = Map.of(
                ID_TAG, (req, idTag) -> {
                    if (nonEqual(wildCard, idTag)) {
                        req.setIdTag(idTag);
                    }
                },
                REASON, (req, reason) -> {
                    if (nonEqual(wildCard, reason)) {
                        req.setReason(Reason.valueOf(reason));
                    }
                },
                TRANSACTION_DATA, (req, transactionData) -> {
                    if (nonEqual(wildCard, transactionData)) {
                        req.setTransactionData(parseModelsFromJson(transactionData, TRANSACTION_DATA, MeterValue.class));
                    }
                }
        );

        this.assertionFactory = Map.of(
                ID_TAG, (expectedParams, actual) -> compareStringsIncludeWildCard(
                        expectedParams, String.valueOf(actual.getIdTag()), ID_TAG),

                METER_STOP_REQUIRED, (expectedParams, actual) -> compareStringsIncludeWildCard(
                        expectedParams, String.valueOf(actual.getMeterStop()), METER_STOP_REQUIRED),

                TIMESTAMP_REQUIRED, (expectedParams, actual) -> compareTimestampIncludeWildCard(
                        expectedParams, actual.getTimestamp(), TIMESTAMP_REQUIRED),

                TRANSACTION_ID_REQUIRED, (expectedParams, actual) -> compareStringsIncludeWildCard(
                        expectedParams, String.valueOf(actual.getTransactionId()), TRANSACTION_ID_REQUIRED),

                REASON, (expectedParams, actual) -> compareStringsIncludeWildCard(
                        expectedParams, actual.getReason().toString(), REASON),

                TRANSACTION_DATA, (expectedParams, actual) ->
                        compareStringsIgnoringTimestampFieldsIncludeWildCard(expectedParams,
                                parseModelsToString(actual.getTransactionData(), TRANSACTION_DATA),
                                TRANSACTION_DATA)
        );
    }

    @Override
    public void validateAndAssertFieldsWithParams(Map<String, String> params, StopTransactionRequest message) {
        super.validateParamsViaLibModel(params);
        super.assertParamsAndMessageFields(params, message);
    }

}
