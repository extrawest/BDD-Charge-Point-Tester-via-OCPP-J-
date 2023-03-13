package com.extrawest.jsonserver.validation.outgoing.request;

import com.extrawest.jsonserver.validation.outgoing.OutgoingMessageFactory;
import com.extrawest.jsonserver.validation.outgoing.OutgoingMessageFieldsFactory;
import eu.chargetime.ocpp.model.reservation.CancelReservationRequest;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class CancelReservationRequestBddHandler extends OutgoingMessageFieldsFactory<CancelReservationRequest>
        implements OutgoingMessageFactory<CancelReservationRequest> {

    public static final String RESERVATION_ID_REQUIRED = "reservationId";
    @Value("${triggerMessage.request.reservationId:1}")
    private String defaultReservationId;

    @PostConstruct
    private void init() {
        this.defaultValues = Map.of(
                RESERVATION_ID_REQUIRED, defaultReservationId
        );

        this.requiredFieldsSetup = Map.of(
                RESERVATION_ID_REQUIRED, (req, reservationId) -> req.setReservationId(
                        getValidatedIntegerOrThrow( reservationId, defaultReservationId, RESERVATION_ID_REQUIRED))
        );

        this.optionalFieldsSetup = Collections.emptyMap();
    }

    @Override
    public CancelReservationRequest createMessageWithValidatedParams(Map<String, String> params) {
        CancelReservationRequest request = super.createMessageWithValidatedParamsViaLibModel(params);
        log.debug(getParameterizeClassName() + ": " + request);
        return request;
    }

}
