Feature:
  Scenario: The Central System sends message request to Charge Point
    Given the Central System is started
    Given the Charge Point is connected
    Then the Central System sends "GetDiagnostics.req" request to the Charge Point with given data
      | location      | any |
      | retries       | any |
      | retryInterval | any |
      | startTime     | any |
      | stopTime      | any |
    And the Central System receives confirmation with given data
      | fileName      | any |