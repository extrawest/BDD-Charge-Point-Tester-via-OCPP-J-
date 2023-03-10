package com.extrawest.jsonserver.validation.incoming.confirmation;

import com.extrawest.jsonserver.validation.incoming.IncomingMessageFactory;
import com.extrawest.jsonserver.validation.incoming.IncomingMessageFieldsFactory;
import eu.chargetime.ocpp.model.core.DataTransferConfirmation;
import eu.chargetime.ocpp.model.core.DataTransferStatus;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataTransferIncomingConfirmationBddHandler extends IncomingMessageFieldsFactory<DataTransferConfirmation>
        implements IncomingMessageFactory<DataTransferConfirmation> {

    public static final String STATUS_REQUIRED = "status";
    public static final String DATA = "data";

    @PostConstruct
    private void init() {
        this.requiredFieldsSetup = Map.of(
                STATUS_REQUIRED, (conf, status) -> {
                    if (nonEqual(wildCard, status)) {
                        conf.setStatus(getValidatedEnumValueOrThrow(DataTransferStatus.class, status, STATUS_REQUIRED));
                    }
                }
        );

        this.optionalFieldsSetup = Map.of(
                DATA, (conf, data) -> {
                    if (nonEqual(wildCard, data)) {
                        conf.setData(data);
                    }
                }
        );
        this.assertionFactory = Map.of(
                STATUS_REQUIRED, (expectedParams, actual) -> compareStringsIncludeWildCard(
                        expectedParams, actual.getStatus().name(), STATUS_REQUIRED),
                DATA, (expectedParams, actual) -> compareStringsIncludeWildCard(
                        expectedParams, actual.getData(), DATA)
        );
    }

    @Override
    public void validateAndAssertFieldsWithParams(Map<String, String> params, DataTransferConfirmation message) {
        if (Objects.equals(params.size(), 1) && params.containsKey(wildCard)) {
            return;
        }
        super.validateParamsViaLibModel(params);
        super.assertParamsAndMessageFields(params, message);
    }

}
