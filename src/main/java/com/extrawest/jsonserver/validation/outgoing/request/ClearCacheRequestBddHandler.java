package com.extrawest.jsonserver.validation.outgoing.request;

import com.extrawest.jsonserver.validation.outgoing.OutgoingMessageFactory;
import com.extrawest.jsonserver.validation.outgoing.OutgoingMessageFieldsFactory;
import eu.chargetime.ocpp.model.core.ClearCacheRequest;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@NoArgsConstructor
public class ClearCacheRequestBddHandler extends OutgoingMessageFieldsFactory<ClearCacheRequest>
        implements OutgoingMessageFactory<ClearCacheRequest> {

    @Override
    public ClearCacheRequest createMessageWithValidatedParams(Map<String, String> params) {
        ClearCacheRequest message = super.createMessageWithValidatedParamsViaLibModel(params);
        log.debug(getParameterizeClassName() + ": " + message);
        return message;
    }

}
