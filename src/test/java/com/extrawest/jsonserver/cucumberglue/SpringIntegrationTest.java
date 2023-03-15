package com.extrawest.jsonserver.cucumberglue;

import com.extrawest.jsonserver.config.JsonServerConfig;
import com.extrawest.jsonserver.cucumber.StepsDefinitionTest;
import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;

@ComponentScan(value = "com.extrawest.jsonserver")
@ContextConfiguration(classes = {JsonServerConfig.class})
@SpringBootTest(classes = StepsDefinitionTest.class)
@RunWith(Cucumber.class)
@CucumberOptions(features = "src/test/resources", glue = "com.extrawest.jsonserver.cucumber.StepsDefinitionTest")
public class SpringIntegrationTest {

}
