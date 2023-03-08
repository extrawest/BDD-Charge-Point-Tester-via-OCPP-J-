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
- [Java 17 or higher](https://www.oracle.com/java/)
- [Maven 3.6 or higher](https://maven.apache.org/)
## Features
Operations Initiated by Charge Point:
- Authorize
- Boot Notification
- Data Transfer
- Diagnostics Status Notification
- Firmware Status Notification
- Heartbeat
- Meter Values
- Start Transaction
- Status Notification
- Stop Transaction

Operations Initiated by Central System:
- Cancel Reservation
- Change Availability
- Change Configuration
- Clear Cache
- Reset
- Send Local List
- Set Charging Profile
- Trigger Message
- Unlock Connector
- Update Firmware
## Usage

To use this application, you can modify the Cucumber scenarios in the 'src/test/resources/features' directory to fit your testing needs. You can also add new steps in the 'src/test/java/com/extrawest/jsonserver/cucumberglue' directory.




## Running Tests
#### Mandatory sequence of steps:
- [Starting the Central System(CS)](#Starting-the-CS)
- [Waiting for a Charge Point(CP) connection](#Waiting-for-connection)
- [Operation test](#operation-test)


###### All text in steps is case-sensitive.

### Starting the CS

###### You can run CS by next steps:
```gherkin
    Given the Central System is started
    Given the Central System is started on "129.39.32.0"
```
###### In the first case CS will start on 'localhost'.
###### In the second - CS will start on specified IP.

### Waiting for connection

###### CS can wait connection with any CP(the first case) or with only specified CP(the second case). 


```gherkin
    Given the Charge Point is connected
    Given the Charge Point "ChargePointId" is connected 
```

### Operation test
###### According to the OCPP documentation, operations are divided into two parts: 
- operations initiated by CP 
- operations initiated by CS 
###### Also every sent request must receive confirmation response.
###### The next steps combination is used to test operation, regardless of what data will be received or sent:
initiated by CP:
```gherkin 
    When the Central System must receive "ClearCache.req"
    Then the Central System must send confirmation response
```
initiated by CS:
```gherkin 
    When the Central System sends "Reset.req" request to the Charge Point
    Then the Central System receives confirmation
```
######  The sending messages will be created with the [default values](#default-values):

#### Message's parameters
###### According to OCPP documentation messages can have required and optional parameters.

###### The next steps combination is used to test operation, with specified parameters:
initiated by CP:
```gherkin
    When the Central System must receive "BootNotification.req" with given data
      | chargePointModel  | CurrentModel  |
      | chargePointVendor | CurrentVendor |
    Then the Central System must send confirmation response with given data
      | currentTime | any |
      | interval    | 60 |
      | status      | Accepted |
``````
initiated by CS:
```gherkin 
    When the Central System sends "Reset.req" request to the Charge Point with given data
      | type   | Hard |
    Then the Central System receives confirmation with given data
      | status | any  |
```
###### All required fields must be specified, optional fields - optional.

#### Validation
###### A receiving message and the specified parameters will be validated according to OCPP documentation.
#### Assertion
###### If parameters are specified then all received data in message will be asserted to these parameters.

#### Wildcard
###### Parameters can be specified as wildcard(by default 'any').
```gherkin
      | currentTime | any |
```
###### For the receiving message this means that current field must have any value(can't be Null).
###### For the sending message - current field value will be set to [default value](#default-values).



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

### Default values
###### All messages have the default values for every field. These values can be set in application.properties file. If field value is a complicated type(IdTagInfo for example) then you can specify it via JSON string.  