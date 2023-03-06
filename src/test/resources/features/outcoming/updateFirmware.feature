Feature:
  Scenario: The Central System sends message request to Charge Point
    Given the Central System is started
    Given the Charge Point is connected
    Then the Central System sends "UpdateFirmware.req" request to the Charge Point with given data
      | location      | any |
      | retrieveDate  | any |
      | retries       | any |
      | retryInterval | any |
    And the Central System receives confirmation with given data