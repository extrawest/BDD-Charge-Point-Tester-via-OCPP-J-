Feature:

  Scenario: The Central System receives "BootNotification.req", confirms it, and then receives "MeterValues.req" from Charge Point
    Given the Central System is started on "localhost"
    Given the Charge Point is connected
    Given the Central System must receive "BootNotification.req" with given data
      | chargePointModel        |  any         |
      | chargePointVendor       |  any         |
      | chargePointSerialNumber |  any         |
      | firmwareVersion         |  any         |
      | iccid                   |  any         |
      | imsi                    |  any         |
      | meterSerialNumber       |  any         |
      | meterType               |  any         |
    Given the Central System must send confirmation response with given data
      | currentTime | any |
      | interval | 60 |
      | status | Accepted |
    When the Central System must receive "MeterValues.req" with given data
      | connectorId | any |
      | meterValue | [{"timestamp":"2023-02-23T10:01:08Z","sampledValue":[{"value":"1000","context":"Transaction.Begin","format":"Raw","measurand":"Energy.Active.Import.Register","phase":null,"location":"Outlet","unit":"Wh"}]}] |
      | transactionId | any |
    Then the Central System must send confirmation response
