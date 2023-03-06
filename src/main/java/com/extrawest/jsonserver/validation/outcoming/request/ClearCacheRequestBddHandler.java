package com.extrawest.jsonserver.validation.outcoming.request;

import com.extrawest.jsonserver.validation.outcoming.OutcomingMessageFieldsValidationFactory;
import com.extrawest.jsonserver.validation.outcoming.OutgoingMessageFactory;
import eu.chargetime.ocpp.model.core.ClearCacheRequest;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@NoArgsConstructor
public class ClearCacheRequestBddHandler extends OutcomingMessageFieldsValidationFactory<ClearCacheRequest>
        implements OutgoingMessageFactory<ClearCacheRequest> {

    @Override
    public ClearCacheRequest createMessageWithValidatedParams(Map<String, String> params) {
        return new ClearCacheRequest();
    }
}
