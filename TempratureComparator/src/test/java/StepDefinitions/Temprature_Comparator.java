package StepDefinitions;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;

import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;

import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;
import io.restassured.response.Response;

public class Temprature_Comparator {
	
	WebDriver driver;
	private static String uI_Real_Temprature,uI_feelslike_Temprature;

	@Before
    public void beforeScenario(){
		System.setProperty("webdriver.chrome.driver","D:\\ImportantStuff\\code\\Chrome\\chromedriver.exe");
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--incognito");
		DesiredCapabilities capabilities = DesiredCapabilities.chrome();
		capabilities.setCapability(ChromeOptions.CAPABILITY, options);
		driver= new ChromeDriver();
		driver.manage().window().maximize();
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
    }	
	
	@Given("user is on Accu Weather page")
	public void user_is_on_accu_weather_page() {		
		driver.get("https://www.accuweather.com/");

	}

	@And("^user enters \"([^\"]*)\" in searchbox$")
	public void user_enters_in_searchbox(String city) throws InterruptedException {		
		driver.findElement(By.cssSelector(".search-input")).sendKeys(city);
		driver.findElement(By.cssSelector(".search-input")).sendKeys(Keys.ENTER);		
	}

	@And("user captures the temprature details on the UI page")
	public void user_captures_the_temprature_details_on_the_ui_page() {
		uI_Real_Temprature=driver.findElement(By.cssSelector(".cur-con-weather-card__panel .temp-container > div.temp")).getText().replaceAll("[^0-9]", "");
		uI_feelslike_Temprature=driver.findElement(By.cssSelector(".cur-con-weather-card__panel .temp-container > div.real-feel")).getText().trim().replaceAll("[^0-9]", "");	    
	}	
	
	@And("it should be approximately match with the API temprature details")
	public void it_should_be_approximately_match_with_the_API_temprature_details() {
		
		baseURI = "api.openweathermap.org/data/2.5/weather?q=San Jose&appid=7fe67bf08c80ded756e598d6f8fedaea";
		given().
		get("weather?q=San Jose&appid=7fe67bf08c80ded756e598d6f8fedaea").
		then().
		statusCode(200).
		body("main.temp", greaterThan(uI_feelslike_Temprature) , lessThan(uI_Real_Temprature));
		
		System.out.println();
					    
	}
	

}

