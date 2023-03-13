package com.extrawest.jsonserver.cucumber;

import static com.extrawest.jsonserver.util.TimeUtil.waitOneSecond;
import static java.util.Objects.isNull;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import com.extrawest.jsonserver.model.emun.ImplementedMessageType;
import com.extrawest.jsonserver.model.exception.BddTestingException;
import com.extrawest.jsonserver.repository.BddDataRepository;
import com.extrawest.jsonserver.repository.ServerSessionRepository;
import com.extrawest.jsonserver.service.MessagingService;
import com.extrawest.jsonserver.service.StepsSupporterService;
import eu.chargetime.ocpp.model.Confirmation;
import eu.chargetime.ocpp.model.Request;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeStep;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import io.cucumber.spring.CucumberContextConfiguration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * All time variables in seconds
 */

@Slf4j
@RequiredArgsConstructor
@CucumberContextConfiguration
@SpringBootTest
@RunWith(Cucumber.class)
@CucumberOptions(features = "src/test/resources")
public class StepsDefinitionTest {
    private final BddDataRepository storage;
    private final ServerSessionRepository sessionRepository;

    private final MessagingService messagingService;
    private final StepsSupporterService stepsSupporterService;

    private final int connectionWaitingTime = 300; // in seconds
    private final int messageWaitingTime = 120; // in seconds

    private int scenarioId;
    private String scenarioName;
    private int stepNumber;

    private UUID sessionIndex;
    private String chargePointId = "";
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
        storage.testFinished(chargePointId);
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
                String chargerId = sessionRepository.getChargerIdBySession(sessionIndex);
                this.chargePointId = chargerId;
                log.info(String.format("Scenario №%s, STEP %s: Charge point %s is connected!",
                        scenarioId, stepNumber, chargerId));
            } catch (Exception e) {
                waitOneSecond();
            }
        }
        if (isNull(sessionIndex)) {
            throw new BddTestingException(String.format("Scenario №%s, STEP %s: %s didn't connect.",
                    scenarioId, stepNumber, chargePointId));
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
                        scenarioId, stepNumber, chargePointId));
            } catch (Exception e) {
                waitOneSecond();
                stepsSupporterService.closeAllSessionsExceptGiven(chargePointId);
            }
        }
        if (isNull(sessionIndex)) {
            throw new BddTestingException(String.format("Scenario №%s, STEP %s: %s didn't connect.",
                    scenarioId, stepNumber, chargePointId));
        }
        this.sessionIndex = sessionIndex;
    }

    @Given("the Central System sends {string} request to the Charge Point")
    public void csSendsMessageRequest(String messageType) {
        csSendsMessageRequestWithData(messageType, Map.of(wildCard, wildCard));
    }

    @Given("the Central System sends {string} request to the Charge Point with given data")
    public void csSendsMessageRequest(String messageType, DataTable table) {
        Map<String, String> parameters = isNull(table) || table.isEmpty() ? Collections.emptyMap() : table.asMap();
        csSendsMessageRequestWithData(messageType, parameters);
    }

    private void csSendsMessageRequestWithData(String messageType, Map<String, String> parameters) {
        String requestType = messageType.replace(".req", "").replace(".conf", "");
        if (ImplementedMessageType.contains(requestType)) {
            waitingMessageType = ImplementedMessageType.fromValue(requestType);
            requestedMessageType = messagingService.sendRequest(chargePointId, waitingMessageType,
                    parameters);
            log.info(String.format("Scenario №%s, STEP %s: %s request sent. ", scenarioId, stepNumber, messageType));
        } else {
            throw new BddTestingException(
                    String.format("Scenario №%s, STEP %s: wrong message request type or %s is not implemented.",
                            scenarioId, stepNumber, messageType));
        }
    }

    @And("the Central System receives confirmation")
    public void theCentralSystemReceivesConfirmationWithAnyData() {
        handlingConfirmationResponseWithData(Map.of(wildCard, wildCard));
    }

    @And("the Central System receives confirmation with given data")
    public void theCentralSystemReceivesConfirmationWithGivenData(DataTable table) {
        Map<String, String> parameters = isNull(table) || table.isEmpty() ? Collections.emptyMap() : table.asMap();
        handlingConfirmationResponseWithData(parameters);
    }

    private void handlingConfirmationResponseWithData(Map<String, String> parameters) {
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
    public void csMustReceiveRequestedMessageAnyData() {
        csMustReceiveRequestedMessageWithData(Map.of(wildCard, wildCard));
    }

    @Then("the Central System must receive requested message with given data")
    public void csMustReceiveRequestedMessageWithGivenData(DataTable table) {
        Map<String, String> parameters = isNull(table) || table.isEmpty() ? Collections.emptyMap() : table.asMap();
        csMustReceiveRequestedMessageWithData(parameters);
    }

    private void csMustReceiveRequestedMessageWithData(Map<String, String> parameters) {
        log.info(String.format("Scenario №%s, STEP %s: Waiting for request up to %s...",
                scenarioId, stepNumber, messageWaitingTime));
        Optional<Request> request = messagingService.waitForRequestedMessage(chargePointId, messageWaitingTime,
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

    @When("the Central System must receive {string}")
    public void theCentralSystemMustReceivesMessageWithAnyData(String messageType) {
        Map<String, String> parameters = Map.of(wildCard, wildCard);
        theCSReceivesMessageWithData(messageType, parameters);
    }

    @When("the Central System must receive {string} with given data")
    public void theCentralSystemMustReceivesMessageWithGivenData(String messageType, DataTable table) {
        Map<String, String> parameters = isNull(table) || table.isEmpty() ? Collections.emptyMap() : table.asMap();
        theCSReceivesMessageWithData(messageType, parameters);
    }

    private void theCSReceivesMessageWithData(String messageType, Map<String, String> parameters) {
        ImplementedMessageType type = ImplementedMessageType
                .fromValue(messageType.replace(".req", "").replace(".conf", ""));
        waitingMessageType = type;
        sendingMessageType = type;
        storage.addRequestedMessageType(chargePointId, type);
        Optional<Request> request = messagingService.waitForRequestedMessage(chargePointId, messageWaitingTime, type);
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
    public void theCentralSystemMustSendConfirmationResponseWithAnyData() {
        theCentralSystemMustSendConfirmationResponseWithData(Map.of(wildCard, wildCard));
    }

    @Then("the Central System must send confirmation response with given data")
    public void theCentralSystemMustSendConfirmationResponseWithGivenData(DataTable table) {
        Map<String, String> parameters = isNull(table) || table.isEmpty() ? Collections.emptyMap() : table.asMap();

        theCentralSystemMustSendConfirmationResponseWithData(parameters);
    }

    private void theCentralSystemMustSendConfirmationResponseWithData(Map<String, String> parameters) {
        Confirmation confirmation = messagingService.sendConfirmationResponse(parameters, sendingMessageType);

        log.info(String.format("Scenario №%s, STEP %s: %s confirmation message sent: \n%s ",
                scenarioId, stepNumber, sendingMessageType, confirmation));
    }

}
