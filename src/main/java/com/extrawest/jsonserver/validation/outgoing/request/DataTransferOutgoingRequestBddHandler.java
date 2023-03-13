package com.extrawest.jsonserver.validation.outgoing.request;

import com.extrawest.jsonserver.validation.outgoing.OutgoingMessageFactory;
import com.extrawest.jsonserver.validation.outgoing.OutgoingMessageFieldsFactory;
import eu.chargetime.ocpp.model.core.DataTransferRequest;
import jakarta.annotation.PostConstruct;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@NoArgsConstructor
public class DataTransferOutgoingRequestBddHandler extends OutgoingMessageFieldsFactory<DataTransferRequest>
        implements OutgoingMessageFactory<DataTransferRequest> {

    public static final String VENDOR_ID_REQUIRED = "vendorId";
    public static final String MESSAGE_ID = "messageId";
    public static final String DATA = "data";

    @Value("${SendLocalList.request.defaultVendorId:1}")
    private String defaultVendorId;
    @Value("${SendLocalList.request.defaultMessageId:1}")
    private String defaultMessageId;
    @Value("${SendLocalList.request.defaultData:Data}")
    private String defaultData;

    @PostConstruct
    private void init() {
        this.defaultValues = Map.of(
                VENDOR_ID_REQUIRED, defaultVendorId,
                MESSAGE_ID, defaultMessageId,
                DATA, defaultData
        );

        this.requiredFieldsSetup = Map.of(
                VENDOR_ID_REQUIRED, (req, vendorId) -> req.setVendorId(
                        getValidatedStringValueOrThrow(vendorId, defaultVendorId))
        );

        this.optionalFieldsSetup = Map.of(
                MESSAGE_ID, (req, messageId) -> req.setMessageId(
                        getValidatedStringValueOrThrow(messageId, defaultMessageId)),
                DATA, (req, data) -> req.setData(
                        getValidatedStringValueOrThrow(data, defaultData))
        );
    }

    @Override
    public DataTransferRequest createMessageWithValidatedParams(Map<String, String> params) {
        DataTransferRequest request = super.createMessageWithValidatedParamsViaLibModel(params);
        log.debug(getParameterizeClassName() + ": " + request);
        return request;
    }

}
