package com.extrawest.jsonserver.validation.incoming.confirmation;

import com.extrawest.jsonserver.validation.incoming.IncomingMessageFactory;
import com.extrawest.jsonserver.validation.incoming.IncomingMessageFieldsFactory;
import eu.chargetime.ocpp.model.core.GetConfigurationConfirmation;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class GetConfigurationConfirmationBddHandler extends IncomingMessageFieldsFactory<GetConfigurationConfirmation>
        implements IncomingMessageFactory<GetConfigurationConfirmation> {

    public static final String CONFIGURATION_KEY = "configurationKey";
    public static final String UNKNOWN_KEY = "unknownKey";

    @PostConstruct
    private void init() {
        this.requiredFieldsSetup = Collections.emptyMap();

        this.optionalFieldsSetup = Map.of(
                CONFIGURATION_KEY, (conf, configurationKey) -> {
                    if (nonEqual(wildCard, configurationKey)) {
                        conf.setConfigurationKey(getValidatedKeyValueType(configurationKey, CONFIGURATION_KEY));
                    }
                },
                UNKNOWN_KEY, (conf, unknownKey) -> {
                    if (nonEqual(wildCard, unknownKey)) {
                        conf.setUnknownKey(new String[]{unknownKey});
                    }
                }
        );
        this.assertionFactory = Map.of(
                CONFIGURATION_KEY, (expectedParams, actual) -> compareStringsIncludeWildCard(
                        expectedParams, Arrays.toString(actual.getConfigurationKey()), CONFIGURATION_KEY),
                UNKNOWN_KEY, (expectedParams, actual) -> compareStringsIncludeWildCard(
                        expectedParams, Arrays.toString(actual.getUnknownKey()), UNKNOWN_KEY
                )
        );
    }

    @Override
    public void validateAndAssertFieldsWithParams(Map<String, String> params, GetConfigurationConfirmation message) {
        if (Objects.equals(params.size(), 1) && params.containsKey(wildCard)) {
            return;
        }
        super.validateParamsViaLibModel(params);
        super.assertParamsAndMessageFields(params, message);
    }

}
