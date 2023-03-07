Feature:
  Scenario: The Central System sends message request to Charge Point
    Given the Central System is started
    Given the Charge Point is connected
    Then the Central System sends "ClearChargingProfile.req" request to the Charge Point with given data
      | id                     | any |
      | connectorId            | any |
      | chargingProfilePurpose | any |
      | stackLevel             | any |
    And the Central System receives confirmation with given data
      | status                 | any |