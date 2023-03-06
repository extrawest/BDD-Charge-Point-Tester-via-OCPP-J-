Feature:

  Scenario: The Central System receiving 'StatusNotification.req' message from Charge Point
    Given the Central System is started on "localhost"
    Given the Charge Point is connected
    When the Central System must receives "StatusNotification.req" with given data
      | connectorId     |  any  |
      | errorCode       |  any  |
      | info            |  any  |
      | status          |  any  |
      | timestamp       |  any  |
      | vendorId        |  any  |
      | vendorErrorCode |  any  |
    Then the Central System must sends confirmation response