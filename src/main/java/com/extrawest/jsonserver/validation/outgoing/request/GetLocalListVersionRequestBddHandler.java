package com.extrawest.jsonserver.validation.outcoming.request;

import com.extrawest.jsonserver.validation.outcoming.OutcomingMessageFieldsValidationFactory;
import com.extrawest.jsonserver.validation.outcoming.OutgoingMessageFactory;
import eu.chargetime.ocpp.model.localauthlist.GetLocalListVersionRequest;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@NoArgsConstructor
public class GetLocalListVersionRequestBddHandler extends OutcomingMessageFieldsValidationFactory<GetLocalListVersionRequest>
        implements OutgoingMessageFactory<GetLocalListVersionRequest> {
    @Override
    public GetLocalListVersionRequest createMessageWithValidatedParams(Map<String, String> params) {
        return new GetLocalListVersionRequest();
    }
}
