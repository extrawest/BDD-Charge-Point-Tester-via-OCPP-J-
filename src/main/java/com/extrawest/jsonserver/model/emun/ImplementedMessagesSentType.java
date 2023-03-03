package com.extrawest.jsonserver.model.emun;

import static com.extrawest.jsonserver.util.EnumUtil.findByField;

import java.util.Arrays;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ImplementedMessagesSentType {
    TRIGGER_MESSAGE("TriggerMessage"),
    RESET("Reset");

    private final String value;

    @Override
    public String toString() {
        return this.value;
    }

    public static boolean contains(String value) {
        return Arrays.stream(ImplementedMessagesSentType.values())
                .anyMatch(e -> Objects.equals(e.value, value));
    }

    public static ImplementedMessagesSentType fromValue(String value) {
        return findByField(
                ImplementedMessagesSentType.class,
                ImplementedMessagesSentType::getValue,
                value
        );
    }

}
