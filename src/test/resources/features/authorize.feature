Feature:

  Scenario: The Central System receiving 'BootNotification.req' message from Charge Point
    Given the Central System is started on "localhost"
    Given the Charge Point is connected
    When the Central System must receives "Authorize.req" with given data
      | idTag | any |
    Then the Central System must sends confirmation response with given data
      | idTagInfo | any |
