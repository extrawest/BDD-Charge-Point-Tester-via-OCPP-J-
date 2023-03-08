package com.extrawest.jsonserver.validation.outcoming.request;

import com.extrawest.jsonserver.validation.outcoming.OutcomingMessageFieldsValidationFactory;
import com.extrawest.jsonserver.validation.outcoming.OutgoingMessageFactory;
import eu.chargetime.ocpp.model.core.GetConfigurationRequest;
import jakarta.annotation.PostConstruct;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

@Slf4j
@Component
@NoArgsConstructor
public class GetConfigurationRequestBddHandler extends OutcomingMessageFieldsValidationFactory<GetConfigurationRequest>
        implements OutgoingMessageFactory<GetConfigurationRequest> {
    public static final String KEY = "key";

    @Value("${triggerMessage.request.key:Key}")
    private String defaultKey;

    @PostConstruct
    private void init() {
        this.defaultValues = Collections.emptyMap();

        this.requiredFieldsSetup = Collections.emptyMap();

        this.optionalFieldsSetup = Map.of(
                KEY, (req, key) -> req.setKey(
                        new String[]{getValidatedStringValueOrThrow(key, defaultKey)}
                )
        );
    }

    @Override
    public GetConfigurationRequest createMessageWithValidatedParams(Map<String, String> params) {
        GetConfigurationRequest request = super.createMessageWithValidatedParamsViaLibModel(params);
        log.debug(getParameterizeClassName() + ": " + request);
        return request;
    }
}
