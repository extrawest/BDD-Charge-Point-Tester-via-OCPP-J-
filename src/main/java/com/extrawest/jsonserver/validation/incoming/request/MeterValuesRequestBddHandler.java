package com.extrawest.jsonserver.validation.incoming.request;

import java.util.Map;
import java.util.Objects;
import com.extrawest.jsonserver.validation.incoming.IncomingMessageFactory;
import com.extrawest.jsonserver.validation.incoming.IncomingMessageFieldsFactory;
import eu.chargetime.ocpp.model.core.MeterValue;
import eu.chargetime.ocpp.model.core.MeterValuesRequest;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MeterValuesRequestBddHandler
        extends IncomingMessageFieldsFactory<MeterValuesRequest>
        implements IncomingMessageFactory<MeterValuesRequest> {

    public static final String CONNECTOR_ID_REQUIRED = "connectorId";
    public static final String METER_VALUES_REQUIRED = "meterValue";
    public static final String TRANSACTION_ID = "transactionId";

    @PostConstruct
    private void init() {
        this.requiredFieldsSetup = Map.of(
                CONNECTOR_ID_REQUIRED, (req, connectorId) -> {
                    if (nonEqual(wildCard, connectorId)) {
                        req.setConnectorId(Integer.parseInt(connectorId));
                    }
                },
                METER_VALUES_REQUIRED, (req, meterValues) -> {
                    if (nonEqual(wildCard, meterValues)) {
                        req.setMeterValue(parseModelsFromJson(meterValues, METER_VALUES_REQUIRED, MeterValue.class));
                    }
                }
        );

        this.optionalFieldsSetup = Map.of(
                TRANSACTION_ID, (req, transactionId) -> {
                    if (nonEqual(wildCard, transactionId)) {
                        req.setTransactionId(Integer.parseInt(transactionId));
                    }
                }
        );

        this.assertionFactory = Map.of(
                CONNECTOR_ID_REQUIRED, (expectedParams, actual) -> compareStringsIncludeWildCard(
                        expectedParams, String.valueOf(actual.getConnectorId()), CONNECTOR_ID_REQUIRED),

                METER_VALUES_REQUIRED, (expectedParams, actual) ->
                        compareStringsIgnoringTimestampFieldsIncludeWildCard(expectedParams,
                                parseModelsToString(actual.getMeterValue(), METER_VALUES_REQUIRED),
                                METER_VALUES_REQUIRED),

                TRANSACTION_ID, (expectedParams, actual) -> compareStringsIncludeWildCard(
                        expectedParams, String.valueOf(actual.getTransactionId()), TRANSACTION_ID)
        );
    }

    @Override
    public void validateAndAssertFieldsWithParams(Map<String, String> params, MeterValuesRequest message) {
        if (Objects.equals(params.size(), 1) && params.containsKey(wildCard)) {
            return;
        }
        super.validateParamsViaLibModel(params);
        super.assertParamsAndMessageFields(params, message);
    }

}
