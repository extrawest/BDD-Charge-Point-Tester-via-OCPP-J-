Feature:
  Scenario: The Central System sends message request to Charge Point
    Given the Central System is started
    Given the Charge Point is connected
    Then the Central System sends "Reset.req" request to the Charge Point
      | type | any |
    And the Central System receives confirmation
      | status | any |