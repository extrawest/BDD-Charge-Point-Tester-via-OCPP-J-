Feature:

  Scenario: The Central System receiving 'BootNotification.req' message from Charge Point
    Given the Central System is started on "localhost"
    Given the Charge Point is connected
    When the Central System must receives "BootNotification.req" with given data
      | chargePointModel        |  any         |
      | chargePointVendor       |  any         |
      | chargePointSerialNumber |  any         |
      | firmwareVersion         |  any         |
      | iccid                   |  any         |
      | imsi                    |  any         |
      | meterSerialNumber       |  any         |
      | meterType               |  any         |
    Then the Central System must sends confirmation response with given data
      | currentTime | any |
      | interval | 60 |
      | status | Accepted |
