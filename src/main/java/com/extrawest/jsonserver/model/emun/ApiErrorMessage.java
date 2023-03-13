package com.extrawest.jsonserver.model.emun;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ApiErrorMessage {
    INVALID_REQUIRED_PARAM("Invalid required '%s' parameter for '%s' message. "),
    INVALID_FIELD_VALUE("'%s' has invalid '%s' field value. %s. "),
    REDUNDANT_EXPECTED_PARAM("Redundant '%s' expected parameter for '%s' message. "),
    NON_MATCH_FIELDS("%s has non match field(s): %s"),
    CONNECTION_LIMIT_EXCEEDED("Connection limit exceeded"),
    ONLY_ONE_CONNECTION_ALLOWED("Only one connection allowed"),
    BUG_CREATING_INSTANCE("Unexpected bug: can't create instance of %s. "),
    BUG_PARSING_MODEL("Unexpected bug: can't parse model %s to string. "),
    WRONG_INSTANCE_OF("Requested method available only for %s inherit. "),
    UNEXPECTED_MESSAGE_RECEIVED("Unexpected %s message received: %s. "),
    INVALID_INCOMING_FACTORY("%s message doesn't have incoming factory. "),
    INVALID_OUTGOING_FACTORY("%s message doesn't have outgoing factory. "),
    INVALID_REQUEST_TYPE("Type is not implemented. Request: "),
    INVALID_CONFIRMATION_TYPE("Type is not implemented. Confirmation: ");

    private final String value;

}
