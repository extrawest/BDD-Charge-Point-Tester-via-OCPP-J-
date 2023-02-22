Feature:

  Scenario: The Central System receiving 'DataTransfer.req' message from Charge Point
    Given the Central System is started on "localhost"
    Given the Charge Point is connected
    When the Central System must receives "DataTransfer.req" with given data
      | vendorId  | any |
      | messageId | any |
      | data      | any |
    Then the Central System must sends confirmation response with given data
      | status | any |
