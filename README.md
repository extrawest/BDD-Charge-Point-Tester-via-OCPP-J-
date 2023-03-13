<a href="https://www.extrawest.com/"><img src="https://drive.google.com/uc?export=view&id=1kXfNj5WfW2oSMzQR82xYBI6Bw_W8-LpK" width="20%"></a>

# BDD Charge Point Tester via OCPP J

OCPP Charge Point Tester on Cucumber is a software tool that is designed to simulate a charge point in the Open Charge Point Protocol (OCPP) 1.6 specification using the Cucumber testing framework.

Cucumber is a behavior-driven development (BDD) testing framework that uses the Gherkin language to write test cases in a human-readable format. With the OCPP Charge Point Simulator on Cucumber, test cases can be written in Gherkin and automated using Cucumber's testing framework.

The simulator provides a set of predefined OCPP messages that can be sent and received by the charge point. These messages include basic operations such as boot notification, status notification.


## Badges
![release version](https://img.shields.io/github/v/release/extrawest/BDD-Charge-Point-Tester-via-OCPP-J-?style=for-the-badge)
![release maven](https://img.shields.io/github/actions/workflow/status/extrawest/BDD-Charge-Point-Tester-via-OCPP-J-/maven-publish.yml?event=push&label=RELEASE%20VERSION%20BUILD&style=for-the-badge)
![snap](https://img.shields.io/github/actions/workflow/status/extrawest/BDD-Charge-Point-Tester-via-OCPP-J-/snapshot-publish.yml?branch=dev&label=SNAPSHOT%20VERSION%20BUILD&style=for-the-badge)
![contr](https://img.shields.io/github/contributors/extrawest/BDD-Charge-Point-Tester-via-OCPP-J-?style=for-the-badge)
![commits](https://img.shields.io/github/commit-activity/w/extrawest/BDD-Charge-Point-Tester-via-OCPP-J-?style=for-the-badge)
![OCPP](https://img.shields.io/badge/OCPP-1.6-brightgreen?style=for-the-badge)
![JDK](https://img.shields.io/badge/JDK-17-yellow?style=for-the-badge)
![social](https://img.shields.io/github/forks/extrawest/BDD-Charge-Point-Tester-via-OCPP-J-?style=for-the-badge)


## Publication
You can find the latest version of the release in the [Maven Central repository](https://mvnrepository.com/artifact/io.github.extrawest/bdd-charge-point-tester-via-ocpp-j)

## Technologies used
- [Spring Boot](https://spring.io/projects/spring-boot)
- [Cucumber for Java](https://cucumber.io/docs/cucumber/api/?lang=java)
- [OCPP library EuChargetime](https://github.com/ChargeTimeEU/Java-OCA-OCPP)

## Requirements
- [Java 17 or higher](https://www.oracle.com/java/)
- [Maven 3.6 or higher](https://maven.apache.org/)
- [Spring boot 3.0.2 or higher](https://spring.io/)

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
- Clear Charging Profile
- Data Transfer
- Get Composite Schedule
- Get Configuration
- Get Diagnostics
- Get Local List Version
- Remote Start Transaction
- Remote Stop Transaction
- Reset
- Send Local List
- Set Charging Profile
- Trigger Message
- Unlock Connector
- Update Firmware

## Usage

To use this application, you can modify the Cucumber scenarios in the 'src/test/resources/features' directory to fit your testing needs. You can also add new steps in the 'src/test/java/com/extrawest/jsonserver/cucumberglue' directory.

## Installation

Install 1/2: Add this to pom.xml:

```bash
<dependency>
    <groupId>io.github.extrawest</groupId>
    <artifactId>bdd-charge-point-tester-via-ocpp-j</artifactId>
    <version>0.1.1</version>
</dependency>
```

Install 2/2: Run via command line
```bash
mvn install
```


## Running Tests

1. You need to create a feature file. The file should be located in ['src/test/resources/features'](src/test/resources/features).
2. You need to write a test scenario. [Mandatory sequence of the steps](#Mandatory-sequence-of-the-steps) is REQUIRED.
3. Run the scenario.

Or you can copy scenario script from [the folder](https://github.com/extrawest/BDD-Charge-Point-Tester-via-OCPP-J-/tree/main/src/test/resources/features). Just change data and run

#### Mandatory sequence of the steps
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
#### initiated by CP without parameters
```gherkin 
    When the Central System must receive "ClearCache.req"
    Then the Central System must send confirmation response
```
#### initiated by CS without parameters
```gherkin 
    When the Central System sends "Reset.req" request to the Charge Point
    Then the Central System receives confirmation
```
######  The sending messages will be created with the [default values](#default-values):

#### Message's parameters
###### According to OCPP documentation messages can have required and optional parameters.

###### The next steps combination is used to test operation, with specified parameters:
#### initiated by CP with parameters
```gherkin
    When the Central System must receive "BootNotification.req" with given data
      | chargePointModel  | CurrentModel  |
      | chargePointVendor | CurrentVendor |
    Then the Central System must send confirmation response with given data
      | currentTime | any |
      | interval    | 60 |
      | status      | Accepted |
``````
#### initiated by CS with parameters
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

### List of available steps:
- [the Central System is started](#Starting-the-CS)
- [the Central System is started on "specified domain"](#Starting-the-CS)
- [the Charge Point is connected](#Waiting-for-connection)
- [the Charge Point "specified Charge Point Id" is connected](#Waiting-for-connection)
- [the Central System must receive "OCPPMessageType.req"](#initiated-by-CP-without-parameters)
- [the Central System must receive "OCPPMessageType.req" with given data](#initiated-by-CP-with-parameters)
- [the Central System must send confirmation response](#initiated-by-CP-without-parameters)
- [the Central System must send confirmation response with given data](#initiated-by-CP-with-parameters)
- [the Central System sends "OCPPMessageType.req" request to the Charge Point](#initiated-by-CS-without-parameters)
- [the Central System sends "OCPPMessageType.req" request to the Charge Point with given data](#initiated-by-CS-with-parameters)
- [the Central System receives confirmation](#initiated-by-CS-without-parameters)
- [the Central System receives confirmation with given data](#initiated-by-CS-with-parameters)
- the Central System must receive requested message
- the Central System must receive requested message with given data

### Default values
###### All messages have the default values for every field. These values can be set in application.properties file. If field value is a complicated type(IdTagInfo for example) then you can specify it via JSON string.  

#### Scenario example:
* [Authorize](https://github.com/extrawest/BDD-Charge-Point-Tester-via-OCPP-J-/blob/main/src/test/resources/features/incoming/authorize.feature) message
* [Boot Notification](https://github.com/extrawest/BDD-Charge-Point-Tester-via-OCPP-J-/blob/main/src/test/resources/features/incoming/bootNotification.feature) message
* [Data Transfer](https://github.com/extrawest/BDD-Charge-Point-Tester-via-OCPP-J-/blob/main/src/test/resources/features/incoming/dataTransfer.feature) message
* [Meter Values](https://github.com/extrawest/BDD-Charge-Point-Tester-via-OCPP-J-/blob/main/src/test/resources/features/incoming/meterValues.feature) message
* [Status Notification](https://github.com/extrawest/BDD-Charge-Point-Tester-via-OCPP-J-/blob/main/src/test/resources/features/incoming/statusNotification.feature) message
* [Change Availability](https://github.com/extrawest/BDD-Charge-Point-Tester-via-OCPP-J-/blob/main/src/test/resources/features/outcoming/changeAvailability.feature) message
* [Clear Cache](https://github.com/extrawest/BDD-Charge-Point-Tester-via-OCPP-J-/blob/main/src/test/resources/features/outcoming/clearCache.feature) message
* [Get Configuration](https://github.com/extrawest/BDD-Charge-Point-Tester-via-OCPP-J-/blob/main/src/test/resources/features/outcoming/getConfiguration.feature) message
* [Reset](https://github.com/extrawest/BDD-Charge-Point-Tester-via-OCPP-J-/blob/main/src/test/resources/features/outcoming/reset.feature) message
* [Trigger Message](https://github.com/extrawest/BDD-Charge-Point-Tester-via-OCPP-J-/blob/main/src/test/resources/features/outcoming/triggerMessage.feature) message