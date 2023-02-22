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
    BUG_CREATING_INSTANCE("Unexpected bug: can't create instance of %s. ");

    private final String value;
}
