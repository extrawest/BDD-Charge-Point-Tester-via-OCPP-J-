package com.extrawest.jsonserver.validation.outcoming.request;

import com.extrawest.jsonserver.validation.outcoming.OutcomingMessageFieldsValidationFactory;
import com.extrawest.jsonserver.validation.outcoming.OutgoingMessageFactory;
import eu.chargetime.ocpp.model.core.ChangeConfigurationRequest;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChangeConfigurationRequestBddHandler extends OutcomingMessageFieldsValidationFactory<ChangeConfigurationRequest>
        implements OutgoingMessageFactory<ChangeConfigurationRequest> {
    public static final String KEY_REQUIRED = "key";
    public static final String VALUE_REQUIRED = "value";

    @Value("${triggerMessage.request.key:Key}")
    private String defaultKey;
    @Value("${triggerMessage.request.value:Value}")
    private String defaultValue;

    @PostConstruct
    private void init() {
        this.defaultValues = Map.of(
                KEY_REQUIRED, defaultKey,
                VALUE_REQUIRED, defaultValue
        );

        this.requiredFieldsSetup = Map.of(
                KEY_REQUIRED, (req, key) -> req.setKey(
                        getValidatedStringValueOrThrow(key, defaultKey)),
                VALUE_REQUIRED, (req, value) -> req.setValue(
                        getValidatedStringValueOrThrow(value, defaultValue))
        );

        this.optionalFieldsSetup = Collections.emptyMap();
    }

    @Override
    public ChangeConfigurationRequest createMessageWithValidatedParams(Map<String, String> params) {
        ChangeConfigurationRequest request = super.createMessageWithValidatedParamsViaLibModel(params);
        log.debug(getParameterizeClassName() + ": " + request);
        return request;
    }
}
