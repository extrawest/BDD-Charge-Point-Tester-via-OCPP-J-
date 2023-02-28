<a href="https://www.extrawest.com/"><img src="https://drive.google.com/uc?export=view&id=1kXfNj5WfW2oSMzQR82xYBI6Bw_W8-LpK" width="20%"></a>

# BDD Charge Point Tester via OCPP J

OCPP Charge Point Tester on Cucumber is a software tool that is designed to simulate a charge point in the Open Charge Point Protocol (OCPP) 1.6 specification using the Cucumber testing framework.

Cucumber is a behavior-driven development (BDD) testing framework that uses the Gherkin language to write test cases in a human-readable format. With the OCPP Charge Point Simulator on Cucumber, test cases can be written in Gherkin and automated using Cucumber's testing framework.

The simulator provides a set of predefined OCPP messages that can be sent and received by the charge point. These messages include basic operations such as boot notification, status notification.



## Badges
![contr](https://img.shields.io/github/contributors/extrawest/BDD-Charge-Point-Tester-via-OCPP-J-?style=flat-square)
![commits](https://img.shields.io/github/commit-activity/w/extrawest/BDD-Charge-Point-Tester-via-OCPP-J-?style=flat-square)
![OCPP](https://img.shields.io/badge/OCPP-1.6-brightgreen?style=flat-square)
![JDK](https://img.shields.io/badge/JDK-17-yellow?style=flat-square)
![social](https://img.shields.io/github/forks/extrawest/BDD-Charge-Point-Tester-via-OCPP-J-?style=social)


## Technologies used
- [Spring Boot](https://spring.io/projects/spring-boot)
- [Cucumber for Java](https://cucumber.io/docs/cucumber/api/?lang=java)
- [OCPP library EuChargetime](https://github.com/ChargeTimeEU/Java-OCA-OCPP)
## Requirements
- Java 17 or higher
- Maven 3.6 or higher
## Features
- Boot Notification
- Authorize
- Data Transfer
- Heart Beat
## Usage

To use this application, you can modify the Cucumber scenarios in the 'src/test/resources/features' directory to fit your testing needs. You can also add new scenarios and steps in the 'src/test/java' directory.




## Running Tests

### Starting Central System

###### You can run CS by next steps:
```gherkin
    Given the Central System is started
    Given the Central System is started on "129.39.32.0"
```
###### In the first case CS will start on 'localhost'.
###### In the second - CS will start on specified IP.

#### Waiting for connection

###### CS can wait connection with any CP(the first case) or with only specified CP(the second case)

```gherkin
    Given the Charge Point is connected
    Given the Charge Point "ChargePointId" is connected 
```

#### Message testing
According to OCPP documentation every sent request must receive confirmation response.
The next steps combination using for testing this flow.
```gherkin 
    When the Central System must receives "ClearCache.req"
    Then the Central System must sends confirmation response
```

#### Message's parameters
According to OCPP documentation messages can have required and optional parameters.
All required parameters MUST be specified. The optional parameters can be set or not.
```gherkin 
    When the Central System must receives "BootNotification.req" with given data
      | chargePointModel  | CurrentModel  |
      | chargePointVendor | CurrentVendor |
    Then the Central System must sends confirmation response with given data
      | currentTime | any |
      | interval    | 60 |
      | status      | Accepted |
```
If parameters specified for sent message then message will be created with these values

#### Validation
All specified parameters will be validated according documentation.

#### Assertion
All received values in message will be asserted with specified parameters.

#### Wildcard
Parameters can be specified as wildcard(by default 'any').

For received messages this means that current field must have any value(can't be equals to Null).

For sending messages - current field will be set from default values. Default values can be set in application.properties file.

```gherkin
      | currentTime | any |
```

#### Scenario example:
```gherkin
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
```

