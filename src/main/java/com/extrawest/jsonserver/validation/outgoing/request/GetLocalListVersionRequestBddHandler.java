package com.extrawest.jsonserver.validation.outgoing.request;

import com.extrawest.jsonserver.validation.outgoing.OutgoingMessageFactory;
import com.extrawest.jsonserver.validation.outgoing.OutgoingMessageFieldsFactory;
import eu.chargetime.ocpp.model.localauthlist.GetLocalListVersionRequest;
import jakarta.annotation.PostConstruct;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

@Slf4j
@Component
@NoArgsConstructor
public class GetLocalListVersionRequestBddHandler extends OutgoingMessageFieldsFactory<GetLocalListVersionRequest>
        implements OutgoingMessageFactory<GetLocalListVersionRequest> {

    @PostConstruct
    private void init() {
        this.defaultValues = Collections.emptyMap();
        this.requiredFieldsSetup = Collections.emptyMap();
        this.optionalFieldsSetup = Collections.emptyMap();
    }

    @Override
    public GetLocalListVersionRequest createMessageWithValidatedParams(Map<String, String> params) {
        GetLocalListVersionRequest request = super.createMessageWithValidatedParamsViaLibModel(params);
        log.debug(getParameterizeClassName() + ": " + request);
        return request;
    }

}
