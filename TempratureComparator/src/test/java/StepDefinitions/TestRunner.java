package StepDefinitions;

import org.junit.runner.RunWith;
import io.cucumber.junit.CucumberOptions;
import io.cucumber.junit.Cucumber;

@RunWith(Cucumber.class)
@CucumberOptions(features="src/test/resources/Features",glue= {"StepDefinitions"},
plugin={"pretty",
		"html:target/cucumber-report/cucumber.html",
		"json:target/cucumber-report/cucumber.json",
		"junit:target/cucumber-report/cucumber.xml"},
monochrome=true,
tags="@smoke"
)
public class TestRunner {

}
