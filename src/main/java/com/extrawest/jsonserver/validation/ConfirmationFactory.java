package com.extrawest.jsonserver.validation;

import java.util.Map;
import eu.chargetime.ocpp.model.Confirmation;

public interface ConfirmationFactory<T extends Confirmation> {

    boolean validateFields(Map<String, String> params, T response);

    T createValidatedConfirmation(Map<String, String> params, T response);

}
