Feature:
  Scenario: The Central System sends message request to Charge Point
    Given the Central System is started
    Given the Charge Point is connected
    Then the Central System sends "ChangeConfiguration.req" request to the Charge Point with given data
      | key    | any |
      | value  | any |
    And the Central System receives confirmation with given data
      | status | any |