package com.extrawest.jsonserver.validation.outgoing.request;

import java.util.Collections;
import java.util.Map;
import com.extrawest.jsonserver.validation.outgoing.OutgoingMessageFactory;
import com.extrawest.jsonserver.validation.outgoing.OutgoingMessageFieldsFactory;
import eu.chargetime.ocpp.model.core.ResetRequest;
import eu.chargetime.ocpp.model.core.ResetType;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ResetRequestBddHandler extends OutgoingMessageFieldsFactory<ResetRequest>
    implements OutgoingMessageFactory<ResetRequest> {

    public static final String TYPE_REQUIRED = "type";

    @Value("${Reset.request.requestedMessage:Soft}")
    private String defaultType;

    @PostConstruct
    private void init() {
        this.defaultValues = Map.of(
                TYPE_REQUIRED, defaultType
        );

        this.requiredFieldsSetup = Map.of(
                TYPE_REQUIRED, (req, type) -> req.setType(
                        getValidatedEnumValueOrThrow(ResetType.class, type, defaultType, TYPE_REQUIRED))
        );

        this.optionalFieldsSetup = Collections.emptyMap();
    }

    @Override
    public ResetRequest createMessageWithValidatedParams(Map<String, String> params) {
        ResetRequest request = super.createMessageWithValidatedParamsViaLibModel(params);
        log.debug(getParameterizeClassName() + ": " + request);
        return request;
    }

}
