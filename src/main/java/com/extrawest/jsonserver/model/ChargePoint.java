package com.extrawest.jsonserver.model;

import java.util.Objects;
import com.extrawest.jsonserver.model.exception.BddTestingException;
import lombok.Data;

@Data
public class ChargePoint implements ValidateOcppData {
    private String chargePointId;
    private String chargePointModel; // REQUIRED max 20 length
    private String chargePointVendor; // REQUIRED max 20 length
    private String chargePointSerialNumber; // max 25 length
    private String firmwareVersion; // max 50 length
    private String iccid; // max 20 length
    private String imsi; // max 20 length
    private String meterSerialNumber;  // max 25 length
    private String meterType; // max 25 length


    @Override
    public boolean validate() {
        ifNullThrow(chargePointModel, "ChargePoint model must exist.");
        ifNullThrow(chargePointVendor, "ChargePoint vendor must exist.");
        ifNotNullValidateLengthOrThrow(chargePointModel, 20, "Model has wrong length.");
        ifNotNullValidateLengthOrThrow(chargePointVendor, 20, "Vendor has wrong length.");

        ifNotNullValidateLengthOrThrow(chargePointSerialNumber, 25, "ChargePoint serial number has wrong length.");
        ifNotNullValidateLengthOrThrow(firmwareVersion, 50, "Firmware version has wrong length.");
        ifNotNullValidateLengthOrThrow(iccid, 20, "Iccid has wrong length.");
        ifNotNullValidateLengthOrThrow(imsi, 20, "Imsi has wrong length.");
        ifNotNullValidateLengthOrThrow(meterSerialNumber, 25, "Meter serial number has wrong length.");
        ifNotNullValidateLengthOrThrow(meterType, 25, "Meter type has wrong length.");

        return true;
    }

    private void ifNullThrow(String data, String message) {
        if (Objects.isNull(data)) {
            throw new BddTestingException(message);
        }
    }

    private void ifNotNullValidateLengthOrThrow(String data, int length, String message) {
        if (Objects.nonNull(data) && data.length() > length) {
            throw new BddTestingException(message);
        }
    }
}
