Feature:

  Scenario: The Central System receiving 'StopTransaction.req' message from Charge Point
    Given the Central System is started on "localhost"
    Given the Charge Point is connected
    When the Central System must receives "StopTransaction.req" with given data
      | idTag           |  any   |
      | meterStop       |  any   |
      | timestamp       |  any   |
      | transactionId   |  any   |
      | reason          |  any   |
      | transactionData |  any   |
    Then the Central System must sends confirmation response with given data
      | idTagInfo       |  any   |