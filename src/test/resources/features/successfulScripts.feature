Feature:
  Scenario: The Central System sends message request to Charge Point
    Given Charge Point data
      | chargePointId          |  BDD-CP-0001  |
      | chargePointModel       |  BDD-model                 |
      | chargePointVendor      |  BDD-vendor                |
      | chargePointSerialNumber|  BDD-serial-number         |
      | firmwareVersion        |  BDD-version               |
      | iccid                  |  BDD-iccid                 |
      | imsi                   |  BDD-imsi                  |
      | meterSerialNumber      |  BDD-meter-serial          |
      | meterType              |  BDD-meter-type            |
    Given the Central System is started
    Given the Charge Point is connected
    When the Central System sends "TriggerMessage" request on "BootNotification" and receives confirmation
    Then the Central System must receive requested message with given data

  Scenario: The Central System sends RESET request to Charge Point
    Given Charge Point data
      | chargePointId          |  BDD-CP-0001       |
      | chargePointModel       |  BDD-model         |
      | chargePointVendor      |  BDD-vendor        |
      | chargePointSerialNumber|  BDD-serial-number |
      | firmwareVersion        |  BDD-version       |
      | iccid                  |  BDD-iccid         |
      | imsi                   |  BDD-imsi          |
      | meterSerialNumber      |  BDD-meter-serial  |
      | meterType              |  BDD-meter-type    |
    Given the Central System is started
    Given the Charge Point is connected
    Then the Central System sends "Reset.req" to Charge Point and receives confirmation

  Scenario: The Central System receiving message from Charge Point
    Given Charge Point data
      | chargePointId          |  BDD-CP-0001       |
      | chargePointModel       |  BDD-model         |
      | chargePointVendor      |  BDD-vendor        |
      | chargePointSerialNumber|  BDD-serial-number |
      | firmwareVersion        |  BDD-version       |
      | iccid                  |  BDD-iccid         |
      | imsi                   |  BDD-imsi          |
      | meterSerialNumber      |  BDD-meter-serial  |
      | meterType              |  BDD-meter-type    |
    Given advanced data in receiving messages
      | messageType            | Authorize   |
      | idTag                  | idTag-BDD-CP-0001  |
    Then the Central System must receive message with given data
