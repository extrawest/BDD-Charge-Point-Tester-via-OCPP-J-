Feature:
  Scenario: Charging, initiator CP
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
    Given data for charging session test
      |idTag|idTag-BDD-CP-0001|
      |connectorId|5005|
      |meterStart|500|
      |stop transaction reason|Local|
    Given the Central System is started and must hold one charging session
    Given the Charge Point is connected
    Then the Central System must hold one charging session with given data

