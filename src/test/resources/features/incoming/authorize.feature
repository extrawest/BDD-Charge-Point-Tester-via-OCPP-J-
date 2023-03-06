Feature:

  Scenario: The Central System receiving 'Authorize.req' message from Charge Point
    Given the Central System is started on "localhost"
    Given the Charge Point is connected
    When the Central System must receive "Authorize.req" with given data
      | idTag | any |
    Then the Central System must send confirmation response with given data
      | idTagInfo | any |
