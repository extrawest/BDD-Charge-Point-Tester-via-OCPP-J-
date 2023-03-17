Feature:

  Scenario: A charge session initiated by Charge Point
    Given the Central System is started on "localhost"
    Given the Charge Point is connected
    When the Central System must receive "BootNotification.req" with given data
      | chargePointModel        |  any         |
      | chargePointVendor       |  any         |
      | chargePointSerialNumber |  any         |
      | firmwareVersion         |  any         |
      | iccid                   |  any         |
      | imsi                    |  any         |
      | meterSerialNumber       |  any         |
      | meterType               |  any         |
    And the Central System must send confirmation response with given data
      | currentTime | any |
      | interval | 60 |
      | status | Accepted |
    And the Central System must receive "Authorize.req" with given data
      | idTag | any |
    And the Central System must send confirmation response with given data
      | idTagInfo | any |
    And the Central System must receive "StartTransaction.req" with given data
      | idTag           |  any   |
      | meterStart      |  any   |
      | timestamp       |  any   |
      | connectorId     |  any   |
    And the Central System must send confirmation response with given data
      | idTagInfo       |  any   |
      | transactionId   |  any   |
    Then the Central System must receive "MeterValues.req" with given data
      | connectorId   | any |
      | meterValue    | [{"timestamp":"2023-02-23T10:01:08Z","sampledValue":[{"value":"1000","context":"Transaction.Begin","format":"Raw","measurand":"Energy.Active.Import.Register","phase":null,"location":"Outlet","unit":"Wh"}]}] |
      | transactionId | any |
    And the Central System must send confirmation response
    Then the Central System must receive "MeterValues.req" with given data
      | connectorId   | any |
      | meterValue    | any |
      | transactionId | any |
    And the Central System must send confirmation response
    Then the Central System must receive "MeterValues.req" with given data
      | connectorId   | any |
      | meterValue    | any |
      | transactionId | any |
    And the Central System must send confirmation response
    Then the Central System must receive "MeterValues.req" with given data
      | connectorId   | any |
      | meterValue    | any |
      | transactionId | any |
    And the Central System must send confirmation response
    And the Central System must receive "MeterValues.req" with given data
      | connectorId   | any |
      | meterValue    | any |
      | transactionId | any |
    And the Central System must send confirmation response
    Then the Central System must receive "StopTransaction.req" with given data
      | idTag           |  any   |
      | meterStop       |  any   |
      | timestamp       |  any   |
      | transactionId   |  any   |
      | reason          |  any   |
      | transactionData |  any   |
    And the Central System must send confirmation response with given data
      | idTagInfo       |  any   |