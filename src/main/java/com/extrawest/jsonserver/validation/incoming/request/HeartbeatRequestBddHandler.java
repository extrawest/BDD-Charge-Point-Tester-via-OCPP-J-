package com.extrawest.jsonserver.validation.incoming.request;

import java.util.Collections;
import java.util.Map;
import com.extrawest.jsonserver.validation.incoming.IncomingMessageFactory;
import com.extrawest.jsonserver.validation.incoming.IncomingMessageFieldsAssertionFactory;
import eu.chargetime.ocpp.model.core.HeartbeatRequest;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class HeartbeatRequestBddHandler
        extends IncomingMessageFieldsAssertionFactory<HeartbeatRequest>
        implements IncomingMessageFactory<HeartbeatRequest> {

    @PostConstruct
    private void init() {
        this.requiredFieldsSetup = Collections.emptyMap();
        this.optionalFieldsSetup = Collections.emptyMap();
        this.assertionFactory = Collections.emptyMap();
    }

    @Override
    public boolean validateFields(Map<String, String> params, HeartbeatRequest actualMessage) {
        return super.validateRequestFields(params, actualMessage);
    }

}