package com.extrawest.jsonserver.validation.outgoing.request;

import com.extrawest.jsonserver.validation.outgoing.OutgoingMessageFieldsFactory;
import com.extrawest.jsonserver.validation.outgoing.OutgoingMessageFactory;
import eu.chargetime.ocpp.model.reservation.ReserveNowRequest;
import jakarta.annotation.PostConstruct;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@NoArgsConstructor
public class ReserveNowRequestBddHandler extends OutgoingMessageFieldsFactory<ReserveNowRequest>
        implements OutgoingMessageFactory<ReserveNowRequest> {

    public static final String CONNECTOR_ID_REQUIRED = "connectorId";
    public static final String EXPIRY_DATE_REQUIRED = "expiryDate";
    public static final String ID_TAG_REQUIRED = "idTag";
    public static final String PARENT_ID_TAG = "parentIdTag";
    public static final String RESERVATION_ID_REQUIRED = "reservationId";

    @Value("${ReserveNow.request.connectorId:1111}")
    private String defaultConnectorId;
    @Value("${ReserveNow.request.expiryDate:2024-03-23T16:43:32.010069453}")
    private String defaultExpiryDate;
    @Value("${ReserveNow.request.idTag:idToken}")
    private String defaultIdTag;
    @Value("${ReserveNow.request.parentIdTag:parentIdToken}")
    private String defaultParentIdTag;
    @Value("${ReserveNow.request.reservationId:1111}")
    private String defaultReservationId;

    @PostConstruct
    private void init() {
        this.defaultValues = Map.of(
                CONNECTOR_ID_REQUIRED, defaultConnectorId,
                EXPIRY_DATE_REQUIRED, defaultExpiryDate,
                ID_TAG_REQUIRED, defaultIdTag,
                PARENT_ID_TAG, defaultParentIdTag,
                RESERVATION_ID_REQUIRED, defaultReservationId
        );

        this.requiredFieldsSetup = Map.of(
                CONNECTOR_ID_REQUIRED, (req, idTag) -> req.setConnectorId(
                        getValidatedIntegerOrThrow(idTag, defaultConnectorId, CONNECTOR_ID_REQUIRED)),
                EXPIRY_DATE_REQUIRED, (req, expiryDate) -> req.setExpiryDate(
                        getValidatedZonedDateTimeOrCurrentTimeIfEmptyOrThrow(expiryDate, defaultExpiryDate, EXPIRY_DATE_REQUIRED)),
                ID_TAG_REQUIRED, (req, idTag) -> req.setIdTag(
                        getValidatedStringValueOrThrow(idTag, defaultIdTag)),
                RESERVATION_ID_REQUIRED, (req, reservationId) -> req.setReservationId(
                        getValidatedIntegerOrThrow(reservationId, defaultReservationId, RESERVATION_ID_REQUIRED))

        );

        this.optionalFieldsSetup = Map.of(
                PARENT_ID_TAG, (req, parentIdTag) -> req.setParentIdTag(
                        getValidatedStringValueOrThrow(parentIdTag, defaultParentIdTag)
                )
        );
    }

    @Override
    public ReserveNowRequest createMessageWithValidatedParams(Map<String, String> params) {
        ReserveNowRequest request = super.createMessageWithValidatedParamsViaLibModel(params);
        log.debug(getParameterizeClassName() + ": " + request);
        return request;
    }

}
