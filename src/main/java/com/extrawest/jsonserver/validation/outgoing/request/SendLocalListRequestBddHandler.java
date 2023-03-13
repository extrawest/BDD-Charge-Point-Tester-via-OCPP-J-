package com.extrawest.jsonserver.validation.outgoing.request;

import java.util.Map;
import com.extrawest.jsonserver.validation.outgoing.OutgoingMessageFactory;
import com.extrawest.jsonserver.validation.outgoing.OutgoingMessageFieldsFactory;
import eu.chargetime.ocpp.model.localauthlist.SendLocalListRequest;
import eu.chargetime.ocpp.model.localauthlist.UpdateType;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SendLocalListRequestBddHandler extends OutgoingMessageFieldsFactory<SendLocalListRequest>
        implements OutgoingMessageFactory<SendLocalListRequest> {

    public static final String LIST_VERSION_REQUIRED = "listVersion";
    public static final String UPDATE_TYPE_REQUIRED = "updateType";
    public static final String LOCAL_AUTHORIZATION_LIST = "localAuthorizationList";

    @Value("${SendLocalList.request.listVersion:1}")
    private String defaultListVersion;
    @Value("${SendLocalList.request.updateType:Full}")
    private String defaultUpdateType;
    @Value("${SendLocalList.request.localAuthorizationList:}")
    private String defaultLocalAuthorizationList;

    @PostConstruct
    private void init() {
        this.defaultValues = Map.of(
                LIST_VERSION_REQUIRED, defaultListVersion,
                UPDATE_TYPE_REQUIRED, defaultUpdateType,
                LOCAL_AUTHORIZATION_LIST, defaultLocalAuthorizationList
        );

        this.requiredFieldsSetup = Map.of(
                LIST_VERSION_REQUIRED, (req, type) -> req.setListVersion(
                        getValidatedIntegerOrThrow(type, defaultListVersion, LIST_VERSION_REQUIRED)),
                UPDATE_TYPE_REQUIRED, (req, type) -> req.setUpdateType(
                        getValidatedEnumValueOrThrow(UpdateType.class, type, defaultUpdateType, UPDATE_TYPE_REQUIRED))

        );

        this.optionalFieldsSetup = Map.of(
                LOCAL_AUTHORIZATION_LIST, (req, type) -> req.setLocalAuthorizationList(
                        getValidatedLocalAuthorizationList(type, defaultLocalAuthorizationList,
                                LOCAL_AUTHORIZATION_LIST))
        );
    }

    @Override
    public SendLocalListRequest createMessageWithValidatedParams(Map<String, String> params) {
        SendLocalListRequest request = super.createMessageWithValidatedParamsViaLibModel(params);
        log.debug(getParameterizeClassName() + ": " + request);
        return request;
    }

}
