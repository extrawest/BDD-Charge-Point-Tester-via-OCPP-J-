Feature:

  Scenario: The Central System receiving 'Heartbeat.req' message from Charge Point
    Given the Central System is started on "localhost"
    Given the Charge Point is connected
    When the Central System must receive "Heartbeat.req"
    Then the Central System must send confirmation response with given data
      | currentTime | any |
