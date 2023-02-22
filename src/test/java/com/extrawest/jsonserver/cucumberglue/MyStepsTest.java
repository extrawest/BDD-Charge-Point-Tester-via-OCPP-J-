package com.extrawest.jsonserver.cucumberglue;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import com.extrawest.jsonserver.model.exception.ValidationException;
import com.extrawest.jsonserver.service.StepsSupporterService;
import com.extrawest.jsonserver.model.emun.ImplementedReceivedMessageType;
import com.extrawest.jsonserver.model.exception.BddTestingException;
import com.extrawest.jsonserver.model.ChargePoint;
import com.extrawest.jsonserver.model.RequiredChargingData;
import com.extrawest.jsonserver.service.MessagingService;
import com.extrawest.jsonserver.repository.BddDataRepository;
import com.extrawest.jsonserver.repository.ServerSessionRepository;
import eu.chargetime.ocpp.model.Confirmation;
import eu.chargetime.ocpp.model.Request;
import eu.chargetime.ocpp.model.core.ResetType;
import eu.chargetime.ocpp.model.remotetrigger.TriggerMessageRequestType;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeStep;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

/**
 * All time variables in seconds
 */

@Slf4j
@RequiredArgsConstructor
public class MyStepsTest extends SpringIntegrationTest {
    private final ServerSessionRepository sessionRepository;

    private final MessagingService messagingService;
    private final StepsSupporterService stepsSupporterService;
    private final BddDataRepository storage;

    private final ChargePoint chargePoint = new ChargePoint();
    private final int connectionWaitingTime = 300; // in seconds
    private final int messageWaitingTime = 120; // in seconds
    private final RequiredChargingData requiredData = new RequiredChargingData();

    private Boolean initiateCharging;
    private int scenarioId;
    private String scenarioName;
    private UUID sessionIndex;
    private int stepNumber;

    @Value("${wildcard:any}")
    private String wildCard;


    @Before
    public void scenarioIncrease(Scenario scenario) {
        scenarioId = scenario.getLine();
        scenarioName = scenario.getName();
        log.info("\nNew Scenario: " + scenarioName);
        initiateCharging = null;
        stepNumber = 0;
    }

    @BeforeStep
    public void stepIncrease() {
        stepNumber++;
    }

    @After
    public void endingScenario() {
        storage.testFinished(chargePoint.getChargePointId());
    }

    @Given("Charge Point data")
    public void settingChargePointData(DataTable table) {
        log.info(String.format("Scenario №%s, STEP %s: setting Charge Point data", scenarioId, stepNumber));
        Map<String, String> map = table.asMap(String.class, String.class);
        chargePoint.setChargePointId(map.get("chargePointId")); // required

        if (storage.isContainsTestingChargePoint(chargePoint.getChargePointId())) {
            throw new BddTestingException(chargePoint.getChargePointId() + " already testing. ");
        }
        storage.addTestingChargePoint(chargePoint.getChargePointId());

        chargePoint.setChargePointModel(map.get("chargePointModel")); // required
        chargePoint.setChargePointVendor(map.get("chargePointVendor")); // required
        chargePoint.setChargePointSerialNumber(map.getOrDefault("chargePointSerialNumber", null));
        chargePoint.setFirmwareVersion(map.getOrDefault("firmwareVersion", null));
        chargePoint.setIccid(map.getOrDefault("iccid", null));
        chargePoint.setImsi(map.getOrDefault("imsi", null));
        chargePoint.setMeterSerialNumber(map.getOrDefault("meterSerialNumber", null));
        chargePoint.setMeterType(map.getOrDefault("meterType", null));
        chargePoint.validate();
    }

    @Given("the Central System is started")
    public void csIsStarted() {
        String hostAddress = stepsSupporterService.startCS();
        log.info(String.format("Scenario №%s, STEP %s: Server started on %s ", scenarioId, stepNumber, hostAddress));
    }

    @Given("the Central System is started on {string}")
    public void csIsStarted(String host) {
        String hostAddress = stepsSupporterService.startCS(host);
        log.info(String.format("Scenario №%s, STEP %s: Server started on %s ", scenarioId, stepNumber, hostAddress));
    }

    @Given("the Central System is started and must hold one charging session")
    public void theCentralSystemIsStartedAndMustHoldOneChargingSession() {
        initiateCharging = false;
        if (Objects.isNull(chargePoint.getChargePointId()) || chargePoint.getChargePointId().isBlank()) {
            throw new ValidationException("You must set charge point id before running this step. Use 'Given Charge Point data'");
        }
        String hostAddress = stepsSupporterService.startCS();
        storage.addChargingChargePoint(chargePoint.getChargePointId());
        storage.addRequestedMessageType(chargePoint.getChargePointId(), ImplementedReceivedMessageType.BootNotification);
        storage.addRequestedMessageType(chargePoint.getChargePointId(), ImplementedReceivedMessageType.Authorize);
        log.info(String.format("Scenario №%s, STEP %s: Server for charging session started on %s ",
                scenarioId, stepNumber, hostAddress));
    }

    @Given("the Central System is started and must initiate one charging session")
    public void theCentralSystemIsStartedAndMustInitiateOneChargingSession() {
        initiateCharging = true;
        if (Objects.isNull(chargePoint.getChargePointId()) || chargePoint.getChargePointId().isBlank()) {
            throw new ValidationException("You must set charge point id before running this step. Use 'Given Charge Point data'");
        }
        String hostAddress = stepsSupporterService.startCS();
        storage.addChargingChargePoint(chargePoint.getChargePointId());
        storage.addRequestedMessageType(chargePoint.getChargePointId(), ImplementedReceivedMessageType.BootNotification);
        storage.addRequestedMessageType(chargePoint.getChargePointId(), ImplementedReceivedMessageType.Authorize);
        log.info(String.format("Scenario №%s, STEP %s: Server for charging session started on %s ",
                scenarioId, stepNumber, hostAddress));
    }

    @Given("the Charge Point is connected")
    public void chargePointIsConnected() {
        UUID sessionIndex = null;
        LocalDateTime finishTime = LocalDateTime.now().plusSeconds(connectionWaitingTime);
        log.info(String.format("Scenario №%s, STEP %s: waiting for any connection up to %s seconds...",
                scenarioId, stepNumber, connectionWaitingTime));
        while (Objects.isNull(sessionIndex) && LocalDateTime.now().isBefore(finishTime)) {
            try {
                sessionIndex = sessionRepository.getSessionForWildCard();
                String chargerPointId = sessionRepository.getChargerIdBySession(sessionIndex);
                chargePoint.setChargePointId(chargerPointId);
                log.info(String.format("Scenario №%s, STEP %s: Charge point %s is connected!",
                        scenarioId, stepNumber, chargerPointId));
            } catch (Exception e) {
                messagingService.sleep(1000L);
            }
        }
        if (Objects.isNull(sessionIndex)) {
            throw new BddTestingException(String.format("Scenario №%s, STEP %s: %s didn't connect.",
                    scenarioId, stepNumber, chargePoint.getChargePointId()));
        }
        this.sessionIndex = sessionIndex;
        if (Objects.nonNull(initiateCharging) && initiateCharging) {
            messagingService.sendRemoteStartTransaction(chargePoint, sessionIndex, requiredData.getIdTag());
        }
    }

    @Given("the Charge Point {string} is connected")
    public void chargePointIsConnected(String chargePointId) {
        if (Objects.equals(chargePointId, wildCard)) {
            chargePointIsConnected();
            return;
        }
        UUID sessionIndex = null;
        LocalDateTime finishTime = LocalDateTime.now().plusSeconds(connectionWaitingTime);
        log.info(String.format("Scenario №%s, STEP %s: waiting for %s connection up to %s seconds...",
                scenarioId, stepNumber, chargePointId, connectionWaitingTime));
        while (Objects.isNull(sessionIndex) && LocalDateTime.now().isBefore(finishTime)) {
            try {
                sessionIndex = sessionRepository.getSessionByChargerId(chargePointId);
                log.info(String.format("Scenario №%s, STEP %s: Charge point %s is connected!",
                        scenarioId, stepNumber, chargePoint.getChargePointId()));
            } catch (Exception e) {
                messagingService.sleep(1000L);
                stepsSupporterService.closeAllSessionsExceptGiven(chargePointId);
            }
        }
        if (Objects.isNull(sessionIndex)) {
            throw new BddTestingException(String.format("Scenario №%s, STEP %s: %s didn't connect.",
                    scenarioId, stepNumber, chargePoint.getChargePointId()));
        }
        this.sessionIndex = sessionIndex;
        if (Objects.nonNull(initiateCharging) && initiateCharging) {
            messagingService.sendRemoteStartTransaction(chargePoint, sessionIndex, requiredData.getIdTag());
        }
    }

    @When("the Central System sends {string} request on {string} and receives confirmation")
    public void csSendsTriggerMessageRequestAndGetsConfirmation(String messageType, String requestedMessageType) {
        requiredData.setMessageType(requestedMessageType);
        if (Objects.equals("TriggerMessage", messageType)) {
            messagingService.sendTriggerMessage(
                    chargePoint.getChargePointId(), TriggerMessageRequestType.valueOf(requestedMessageType));
            log.info(String.format("Scenario №%s, STEP %s: Trigger message request sent. ",
                    scenarioId, stepNumber));
            handlingConfirmationResponse();
        } else {
            throw new BddTestingException(String.format("Scenario №%s, STEP %s: %s is not implemented.",
                    scenarioId, stepNumber, messageType));
        }
    }

    @When("the Central System sends {string} to Charge Point and receives confirmation")
    public void csSendsMessageRequestAndGetsConfirmation(String messageType) {
        if (Objects.equals("Reset.req", messageType)) {
            messagingService.sendResetMessage(chargePoint.getChargePointId(), ResetType.Soft);
            log.info(String.format("Scenario №%s, STEP %s: %s request sent. ",
                    scenarioId, stepNumber, messageType));
            handlingConfirmationResponse();
        } else {
            throw new BddTestingException(
                    String.format("Scenario №%s, STEP %s: wrong message request type or %s is not implemented.",
                            scenarioId, stepNumber, messageType));
        }
    }

    private void handlingConfirmationResponse() {
        log.info(String.format("Scenario №%s, STEP %s: Waiting for confirmation response...",
                scenarioId, stepNumber));
        Optional<CompletableFuture<Confirmation>> future =
                messagingService.waitForSuccessfulResponse(sessionIndex, messageWaitingTime);
        if (future.isEmpty()) {
            throw new BddTestingException(
                    String.format("Scenario №%s, STEP %s: response to the trigger message is not valid.",
                            scenarioId, stepNumber));
        }
        if (future.get().isDone()) {
            log.info(String.format("Scenario №%s, STEP %s: handled. Response is valid. ",
                    scenarioId, stepNumber));
            return;
        }
        if (future.get().isCompletedExceptionally()) {
            throw new BddTestingException(
                    String.format("Scenario №%s, STEP %s: handled. Response is not valid. ",
                            scenarioId, stepNumber));
        }
        if (future.get().isCancelled()) {
            throw new BddTestingException(
                    String.format("Scenario №%s, STEP %s: awaiting response was cancelled by CS.",
                            scenarioId, stepNumber));
        }
    }

    @Then("the Central System must receive requested message with given data")
    public void csMustReceiveMessageRequestAndCompareData() {
        log.info(String.format("Scenario №%s, STEP %s: Waiting for request up to %s...",
                scenarioId, stepNumber, messageWaitingTime));
        ImplementedReceivedMessageType type =
                ImplementedReceivedMessageType.valueOf(requiredData.getMessageType());
        Optional<Request> request = messagingService.waitForRequestedMessage(chargePoint, messageWaitingTime, type);
        if (request.isEmpty()) {
            throw new BddTestingException(
                    String.format("Scenario №%s, STEP %s: requested %s message is not handled or invalid.",
                            scenarioId, stepNumber, type));
        }
        messagingService.validateReceivedMessageOrThrow(chargePoint, requiredData, request.get());
        log.info(String.format("Scenario №%s, STEP %s: handled. Requested %s message is valid: %s ",
                scenarioId, stepNumber, type, request.get()));
    }

    @Given("data for charging session test")
    public void settingChargingSessionData(DataTable table) {
        log.info(String.format("Scenario №%s, STEP %s: setting charging session data", scenarioId, stepNumber));
        setRequiredDate(table);
    }

    private void setRequiredDate(DataTable table) {
        Map<String, String> map = table.asMap(String.class, String.class);
        requiredData.setMessageType(map.getOrDefault("messageType", null));
        requiredData.setIdTag(map.getOrDefault("idTag", null));
        String connectorId = map.getOrDefault("connectorId", null);
        requiredData.setConnectorId(Objects.isNull(connectorId) ? null : Integer.parseInt(connectorId));
        String meterStart = map.getOrDefault("meterStart", null);
        requiredData.setMeterStart(Objects.isNull(meterStart) ? null : Integer.parseInt(meterStart));
    }

    @Then("the Central System must hold one charging session with given data")
    public void theCSMustHoldOneChargingSessionWithGivenData() {
        log.info(String.format("Scenario №%s, STEP %s: Charging process started: ",
                scenarioId, stepNumber));
        if (!messagingService.holdChargingSessionWithComparingData(chargePoint, requiredData)) {
            storage.removeChargePointFromChargingList(chargePoint.getChargePointId());
            throw new BddTestingException("Charging session finished, but some of received data were different from testing");
        }
        storage.removeChargePointFromChargingList(chargePoint.getChargePointId());
        log.info(String.format("Scenario №%s, STEP %s: Charging session finished succeed. ",
                scenarioId, stepNumber));
    }

    @Given("advanced data in receiving messages")
    public void dataForReceivingMessages(DataTable table) {
        log.info(String.format("Scenario №%s, STEP %s: Setting data for receive messages ",
                scenarioId, stepNumber));
        setRequiredDate(table);
        storage.addRequestedMessageType(chargePoint.getChargePointId(),
                ImplementedReceivedMessageType.valueOf(requiredData.getMessageType()));
        System.out.println("Awaiting message type: " + ImplementedReceivedMessageType.valueOf(requiredData.getMessageType()));
    }

    @When("the Central System must receives {string} with given data")
    public void theCentralSystemMustReceivesMessageWithGivenData(String messageType, DataTable table) {
        Map<String, String> parameters = table.asMap();
        ImplementedReceivedMessageType type = ImplementedReceivedMessageType
                .valueOf(messageType.replace(".req", "").replace(".conf", ""));
        storage.addRequestedMessageType(chargePoint.getChargePointId(), type);
        Optional<Request> request = messagingService.waitForRequestedMessage(chargePoint, messageWaitingTime, type);
        if (request.isEmpty()) {
            throw new BddTestingException(
                    String.format("Scenario №%s, STEP %s: %s message is not handled or invalid.",
                            scenarioId, stepNumber, type));
        }
        messagingService.validateRequest(parameters, request.get());
        log.info(String.format("Scenario №%s, STEP %s: handled. %s message is valid: \n%s ",
                scenarioId, stepNumber, type, request.get()));
    }

}
