Feature:

  Scenario: The Central System receiving 'Heartbeat.req' message from Charge Point
    Given the Central System is started on "localhost"
    Given the Charge Point is connected
    When the Central System must receives "Heartbeat.req"
    Then the Central System must sends confirmation response with given data
      | currentTime | any |
