package StepDefinitions;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class Temprature_Comparator {
	
	WebDriver driver;
	private static String uI_Real_Temprature;
	private static float api_temp_max,api_temp_min,api_Real_Temprature;
	private static String ui_url;
	FileReader dataReader,webElementReader;
	Properties dataProperties,webElementsProperties;
	private static String currentDir= System.getProperty("user.dir"); 
	
	public Temprature_Comparator() throws IOException {
		currentDir= System.getProperty("user.dir");	
		// All the required urls are mentioned in data.properties file
		dataReader=new FileReader(currentDir+"\\src\\test\\resources\\Properties\\data.properties");
		dataProperties=new Properties(); 
		dataProperties.load(dataReader); 
		// All the required webelements are mentioned in webElements.properties file
		webElementReader=new FileReader(currentDir+"\\src\\test\\resources\\Properties\\webElements.properties");	   
	    webElementsProperties=new Properties(); 	   
	    webElementsProperties.load(webElementReader);
	}
	
	@Before
    public void beforeScenario(){		
		System.setProperty("webdriver.chrome.driver",currentDir+"\\src\\test\\resources\\Drivers\\chromedriver.exe");
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
		// Launch the Chrome browser
		driver.get(dataProperties.getProperty("UI_URL"));

	}

	@And("^user enters \"([^\"]*)\" in searchbox$")
	public void user_enters_in_searchbox(String city) throws InterruptedException {	
		// Enter the city details in the search box
		driver.findElement(By.cssSelector(webElementsProperties.getProperty("searchbox"))).sendKeys(city);
		driver.findElement(By.cssSelector(webElementsProperties.getProperty("searchbox"))).sendKeys(Keys.ENTER);		
	}

	@And("user captures the temprature details on the UI page")
	public void user_captures_the_temprature_details_on_the_ui_page() {
		//Gets the temperatures from UI and stores in a uI_Real_Temprature variable
		uI_Real_Temprature=driver.findElement(By.cssSelector(webElementsProperties.getProperty("ui_real_temp"))).getText().replaceAll("[^0-9]", "");			    
	}	
	
	@And("^it should be approximately match with the API \"([^\"]*)\" temprature details$")
	public void it_should_be_approximately_match_with_the_API_temprature_details(String city) throws InterruptedException, ApiUiTempratureMismatchException {		
		baseURI =dataProperties.getProperty("API_BASE_URL");  
		String pathParam="/weather";
		String queryParam="?q="+city+"&appid=7fe67bf08c80ded756e598d6f8fedaea&units=metric";
		String url=baseURI+pathParam+queryParam;
		//Get the real,minimum and maximum temperature from API
		api_Real_Temprature = when().get(url).then().extract().path("main.temp");
		api_temp_max = when().get(url).then().extract().path("main.temp_max");
		api_temp_min = when().get(url).then().extract().path("main.temp_min");
		
		/** Variance logic: Here we are subtracting max_temp - min_temp  from API to get min_max_difference 
		 * Once we get the difference we calculate the minCalculatedTemperature by subtracting the min_max_difference
		 * and calculate maxCalculatedTemperature by adding the min_max_difference.
		 * The logic here is the UI temperature should not be less than minCalculatedTemperature 
		 * and should not be grater than maxCalculatedTemperature.
		 * If any of the above logic fails we will throw the custom exception ApiUiTempratureMismatchException
		 */
		float min_max_diff=api_temp_max-api_temp_min;		
		float uIrealTemp=Float.parseFloat(uI_Real_Temprature);		
		float minCalculatedTemp=uIrealTemp-min_max_diff;
		float maxCalculatedTemp=uIrealTemp+min_max_diff;
		
		if(uIrealTemp<minCalculatedTemp||uIrealTemp>maxCalculatedTemp) {
			throw new ApiUiTempratureMismatchException("The temprature of UI and API is not in matching range");    
		}
		
		//validate the status code and greater than temperature logic
		given().when().get(url).then().assertThat()
		.statusCode(200)
		.body("main.temp", greaterThan(minCalculatedTemp));
		
		//validate  less than temperature logic
		given().when().get(url).then().assertThat()
		.statusCode(200)
		.body("main.temp", lessThan(maxCalculatedTemp));
	}	
	
	@After
    public void afterScenario(){
		//Close all browsers
		driver.quit();
    }
	

}

