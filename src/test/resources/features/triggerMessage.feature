Feature:
  Scenario: The Central System sends message request to Charge Point
    Given the Central System is started
    Given the Charge Point is connected
    When the Central System sends "TriggerMessage.req" request to the Charge Point and receives confirmation
      | requestedMessage | BootNotification |
      | connectorId      | any              |
      | status           | any              |
    Then the Central System must receive requested message with given data
      | chargePointModel       |  BDD-model                 |
      | chargePointVendor      |  BDD-vendor                |
      | chargePointSerialNumber|  BDD-serial-number         |
      | firmwareVersion        |  BDD-version               |
      | iccid                  |  BDD-iccid                 |
      | imsi                   |  BDD-imsi                  |
      | meterSerialNumber      |  BDD-meter-serial          |
      | meterType              |  BDD-meter-type            |