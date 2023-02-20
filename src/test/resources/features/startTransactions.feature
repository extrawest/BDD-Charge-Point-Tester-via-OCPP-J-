Feature:
  @StartTransaction
  Scenario: Charging, initiator CP
    Given Charge Point data
      | chargePointId           |  BDD-CP-0001       |
      | chargePointModel        |  BDD-model         |
      | chargePointVendor       |  BDD-vendor        |
      | chargePointSerialNumber |  BDD-serial-number |
      | firmwareVersion         |  BDD-version       |
    Given data for charging session test
      | idTag                   | idTag-BDD-CP-0001  |
      | connectorId             | 1001               |
      | meterStart              | 1000               |
    Given the Central System is started and must hold one charging session
    Given the Charge Point is connected
    Then the Central System must hold one charging session with given data

  @RemoteStartTransaction
  Scenario: Charging, initiator CS
    Given Charge Point data
      | chargePointId           |  BDD-CP-0001       |
      | chargePointModel        |  BDD-model         |
      | chargePointVendor       |  BDD-vendor        |
      | chargePointSerialNumber |  BDD-serial-number |
      | firmwareVersion         |  BDD-version       |
      | iccid                   |  BDD-iccid         |
      | imsi                    |  BDD-imsi          |
      | meterSerialNumber       |  BDD-meter-serial  |
      | meterType               |  BDD-meter-type    |
    Given data for charging session test
      |idTag|idTag-BDD-CP-0001|
      |connectorId|1001|
      |meterStart|1000|
    Given the Central System is started and must initiate one charging session
    Given the Charge Point is connected
    Then the Central System must hold one charging session with given data
