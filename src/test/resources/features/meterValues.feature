Feature:

  Scenario: The Central System receiving 'MeterValues.req' message from Charge Point
    Given the Central System is started on "localhost"
    Given the Charge Point is connected
    When the Central System must receives "MeterValues.req" with given data
      | connectorId | any |
      | meterValue | [{"timestamp":"2023-02-23T10:01:08Z","sampledValue":[{"value":"1000","context":"Transaction.Begin","format":"Raw","measurand":"Energy.Active.Import.Register","phase":null,"location":"Outlet","unit":"Wh"}]}] |
      | transactionId | any |
    Then the Central System must sends confirmation response
