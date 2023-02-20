package com.extrawest.jsonserver.model;

import java.util.Objects;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class RequiredChargingData implements ValidateOcppData {
    private String messageType;
    private String idTag; // 20
    private Integer connectorId; // > 0
    private Integer meterStart;

    @Override
    public boolean validate() {
        return Objects.nonNull(idTag) && !idTag.isBlank() && idTag.length() <= 20
                && connectorId > 0;
    }
}
