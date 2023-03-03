package com.extrawest.jsonserver.cucumberglue;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import com.extrawest.jsonserver.service.StepsSupporterService;
import com.extrawest.jsonserver.model.emun.ImplementedMessageType;
import com.extrawest.jsonserver.model.exception.BddTestingException;
import com.extrawest.jsonserver.model.ChargePoint;
import com.extrawest.jsonserver.service.MessagingService;
import com.extrawest.jsonserver.repository.BddDataRepository;
import com.extrawest.jsonserver.repository.ServerSessionRepository;
import eu.chargetime.ocpp.model.Confirmation;
import eu.chargetime.ocpp.model.Request;
import eu.chargetime.ocpp.model.core.AuthorizeConfirmation;
import eu.chargetime.ocpp.model.core.BootNotificationConfirmation;
import eu.chargetime.ocpp.model.core.DataTransferConfirmation;
import eu.chargetime.ocpp.model.core.HeartbeatConfirmation;
import eu.chargetime.ocpp.model.core.MeterValuesConfirmation;
import eu.chargetime.ocpp.model.core.StartTransactionConfirmation;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeStep;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import static com.extrawest.jsonserver.util.TimeUtil.waitOneSecond;
import static java.util.Objects.isNull;

/**
 * All time variables in seconds
 */

@Slf4j
@RequiredArgsConstructor
public class MyStepsTest extends SpringIntegrationTest {
    private final BddDataRepository storage;
    private final ServerSessionRepository sessionRepository;

    private final MessagingService messagingService;
    private final StepsSupporterService stepsSupporterService;

    private final ChargePoint chargePoint = new ChargePoint();
    private final int connectionWaitingTime = 300; // in seconds
    private final int messageWaitingTime = 120; // in seconds

    private int scenarioId;
    private String scenarioName;
    private int stepNumber;

    private UUID sessionIndex;
    private ImplementedMessageType waitingMessageType;
    private ImplementedMessageType requestedMessageType;
    private ImplementedMessageType sendingMessageType;

    @Value("${wildcard:any}")
    private String wildCard;


    @Before
    public void scenarioIncrease(Scenario scenario) {
        scenarioId = scenario.getLine();
        scenarioName = scenario.getName();
        log.info("\nNew Scenario: " + scenarioName);
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

    @Given("the Charge Point is connected")
    public void chargePointIsConnected() {
        UUID sessionIndex = null;
        LocalDateTime finishTime = LocalDateTime.now().plusSeconds(connectionWaitingTime);
        log.info(String.format("Scenario №%s, STEP %s: waiting for any connection up to %s seconds...",
                scenarioId, stepNumber, connectionWaitingTime));
        while (isNull(sessionIndex) && LocalDateTime.now().isBefore(finishTime)) {
            try {
                sessionIndex = sessionRepository.getSessionForWildCard();
                String chargerPointId = sessionRepository.getChargerIdBySession(sessionIndex);
                chargePoint.setChargePointId(chargerPointId);
                log.info(String.format("Scenario №%s, STEP %s: Charge point %s is connected!",
                        scenarioId, stepNumber, chargerPointId));
            } catch (Exception e) {
                waitOneSecond();
            }
        }
        if (isNull(sessionIndex)) {
            throw new BddTestingException(String.format("Scenario №%s, STEP %s: %s didn't connect.",
                    scenarioId, stepNumber, chargePoint.getChargePointId()));
        }
        this.sessionIndex = sessionIndex;
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
        while (isNull(sessionIndex) && LocalDateTime.now().isBefore(finishTime)) {
            try {
                sessionIndex = sessionRepository.getSessionByChargerId(chargePointId);
                log.info(String.format("Scenario №%s, STEP %s: Charge point %s is connected!",
                        scenarioId, stepNumber, chargePoint.getChargePointId()));
            } catch (Exception e) {
                waitOneSecond();
                stepsSupporterService.closeAllSessionsExceptGiven(chargePointId);
            }
        }
        if (isNull(sessionIndex)) {
            throw new BddTestingException(String.format("Scenario №%s, STEP %s: %s didn't connect.",
                    scenarioId, stepNumber, chargePoint.getChargePointId()));
        }
        this.sessionIndex = sessionIndex;
    }

    @Given("the Central System sends {string} request to the Charge Point")
    public void csSendsMessageRequestAndGetsConfirmation(String messageType, DataTable table) {
        Map<String, String> parameters = isNull(table) || table.isEmpty() ? Collections.emptyMap() : table.asMap();
        String requestType = messageType.replace(".req", "").replace(".conf", "");
        if (ImplementedMessageType.contains(requestType)) {
            waitingMessageType = ImplementedMessageType.fromValue(requestType);
            requestedMessageType = messagingService.sendRequest(chargePoint.getChargePointId(), waitingMessageType,
                    parameters);
            log.info(String.format("Scenario №%s, STEP %s: %s request sent. ", scenarioId, stepNumber, messageType));
        } else {
            throw new BddTestingException(
                    String.format("Scenario №%s, STEP %s: wrong message request type or %s is not implemented.",
                            scenarioId, stepNumber, messageType));
        }
    }

    @And("the Central System receives confirmation")
    public void theCentralSystemReceivesConfirmation(DataTable table) {
        Map<String, String> parameters = isNull(table) || table.isEmpty() ? Collections.emptyMap() : table.asMap();
        handlingConfirmationResponse(parameters);
    }


    private void handlingConfirmationResponse(Map<String, String> parameters) {
        log.info(String.format("Scenario №%s, STEP %s: Waiting for confirmation response...",
                scenarioId, stepNumber));
        Optional<CompletableFuture<Confirmation>> future =
                messagingService.waitForSuccessfulResponse(sessionIndex, messageWaitingTime, parameters);
        if (future.isEmpty()) {
            throw new BddTestingException(
                    String.format("Scenario №%s, STEP %s: response to the trigger message is not valid.",
                            scenarioId, stepNumber));
        }
        CompletableFuture<Confirmation> completableFuture = future.get();
        if (completableFuture.isDone()) {
            messagingService.assertConfirmationMessage(parameters, completableFuture);
            log.info(String.format("Scenario №%s, STEP %s: handled. Response is valid. ",
                    scenarioId, stepNumber));
            return;
        }
        if (completableFuture.isCompletedExceptionally()) {
            throw new BddTestingException(
                    String.format("Scenario №%s, STEP %s: handled. Response is not valid. ",
                            scenarioId, stepNumber));
        }
        if (completableFuture.isCancelled()) {
            throw new BddTestingException(
                    String.format("Scenario №%s, STEP %s: awaiting response was cancelled by CS.",
                            scenarioId, stepNumber));
        }
    }

    @Then("the Central System must receive requested message")
    public void csMustReceiveRequestedMessage() {
        csMustReceiveRequestedMessageWithGivenData(DataTable.emptyDataTable());
    }

    @Then("the Central System must receive requested message with given data")
    public void csMustReceiveRequestedMessageWithGivenData(DataTable table) {
        Map<String, String> parameters = isNull(table) || table.isEmpty() ? Collections.emptyMap() : table.asMap();
        log.info(String.format("Scenario №%s, STEP %s: Waiting for request up to %s...",
                scenarioId, stepNumber, messageWaitingTime));
        Optional<Request> request = messagingService.waitForRequestedMessage(chargePoint, messageWaitingTime,
                requestedMessageType);
        if (request.isEmpty()) {
            throw new BddTestingException(
                    String.format("Scenario №%s, STEP %s: requested %s message is not handled or invalid.",
                            scenarioId, stepNumber, requestedMessageType));
        }
        sendingMessageType = messagingService.validateRequest(parameters, request.get());
        log.info(String.format("Scenario №%s, STEP %s: handled. Requested %s message is valid: %s ",
                scenarioId, stepNumber, requestedMessageType, request.get()));
    }

    @When("the Central System must receives {string}")
    public void theCentralSystemMustReceivesMessageWithGivenData(String messageType) {
        Map<String, String> parameters = Collections.emptyMap();
        theCSReceivesMessageWithGivenData(messageType, parameters);
    }

    @When("the Central System must receives {string} with given data")
    public void theCentralSystemMustReceivesMessageWithGivenData(String messageType, DataTable table) {
        Map<String, String> parameters = isNull(table) || table.isEmpty() ? Collections.emptyMap() : table.asMap();
        theCSReceivesMessageWithGivenData(messageType, parameters);
    }

    private void theCSReceivesMessageWithGivenData(String messageType, Map<String, String> parameters) {
        ImplementedMessageType type = ImplementedMessageType
                .fromValue(messageType.replace(".req", "").replace(".conf", ""));
        waitingMessageType = type;
        sendingMessageType = type;
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

    @Then("the Central System must send confirmation response")
    public void theCentralSystemMustSendConfirmationResponseWithGivenData() {
        DataTable table = DataTable.emptyDataTable();
        theCentralSystemMustSendConfirmationResponseWithGivenData(table);
    }

    @Then("the Central System must send confirmation response with given data")
    public void theCentralSystemMustSendConfirmationResponseWithGivenData(DataTable table) {
        Map<String, String> parameters = isNull(table) || table.isEmpty() ? Collections.emptyMap() : table.asMap();
        Confirmation response;
        switch (sendingMessageType) {
            case BOOT_NOTIFICATION -> response = new BootNotificationConfirmation();
            case AUTHORIZE ->  response = new AuthorizeConfirmation();
            case DATA_TRANSFER -> response = new DataTransferConfirmation();
            case HEARTBEAT -> response = new HeartbeatConfirmation();
            case METER_VALUES -> response = new MeterValuesConfirmation();
            case START_TRANSACTION -> response = new StartTransactionConfirmation();
            default -> throw new BddTestingException("Message type is unavailable");
        }
        Confirmation confirmation = messagingService.sendConfirmationResponse(parameters, response);

        log.info(String.format("Scenario №%s, STEP %s: %s confirmation message sent: \n%s ",
                scenarioId, stepNumber, sendingMessageType, confirmation));
    }

}
