package com.ibm.biginsights.selenium;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;


public class WebDriverHelper {


	/**
	 * @param driver
	 * @param String browser
	 * @param String url
	 * @param String security
	 * @param String username
	 * @param String password
	 * @return the instantiated WebDriver after it is invoked it, goes to the base web console url and logs into the web console if there is security.
	 */

	public static WebDriver instantiateDriver(WebDriver driver, String browser, String url) throws InterruptedException {

		if(browser.equalsIgnoreCase("firefox"))
		{
			FirefoxProfile profile = new FirefoxProfile();  
			profile.setPreference("capability.policy.default.Window.QueryInterface", "allAccess");  
			profile.setPreference("capability.policy.default.Window.frameElement.get","allAccess");
			profile.setPreference("capability.policy.default.HTMLDocument.readyState","allAccess");
			profile.setPreference("capability.policy.default.HTMLDocument.compatMode","allAccess");
			profile.setPreference("capability.policy.default.Document.compatMode","allAccess");
			profile.setPreference("capability.policy.default.Location.href","allAccess");
			profile.setPreference("capability.policy.default.Window.pageXOffset","allAccess");
			profile.setPreference("capability.policy.default.Window.pageYOffset","allAccess");
			profile.setPreference("capability.policy.default.Window.frameElement","allAccess");
			profile.setPreference("capability.policy.default.Window.frameElement.get","allAccess");
			profile.setPreference("capability.policy.default.Window.QueryInterface","allAccess");
			profile.setPreference("capability.policy.default.Window.mozInnerScreenY","allAccess");
			profile.setPreference("capability.policy.default.Window.mozInnerScreenX","allAccess");
			driver = new FirefoxDriver(profile);
			driver.manage().window().maximize();
		}
		else if(browser.equalsIgnoreCase("internetExplorer"))
		{
			DesiredCapabilities capabilities = DesiredCapabilities.internetExplorer();
			capabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
			driver = new InternetExplorerDriver(capabilities);
		}
		else if(browser.equalsIgnoreCase("chrome"))
		{
			DesiredCapabilities capabilities = DesiredCapabilities.chrome();
			capabilities.setCapability("chrome.switches",Arrays.asList("--disable-popup-blocking"));
			ChromeOptions options = new ChromeOptions();
			options.addArguments("test-type");
			capabilities.setCapability(ChromeOptions.CAPABILITY, options);
			System.setProperty("webdriver.chrome.driver","src/test/java/com/ibm/biginsights/data/chromedriver.exe"); 
			driver = new ChromeDriver(capabilities);
			driver.manage().window().maximize();
		}
		driver.manage().timeouts().pageLoadTimeout(240, TimeUnit.SECONDS);
		driver.get(url);
		System.out.println("driver is"+driver);


		/*Allows time for the whole page to load before clicking on an object.*/
		Thread.sleep(1000);

		return driver;

	}
	
public static String getIOPWebConsoleURL(String hostname, String https,String ambariPort ) {
		
		if(System.getProperty("BIhostname") != null && System.getProperty("BIhttps") != null)
		{
			if(System.getProperty("BIhttps").equalsIgnoreCase("true")){				
			   
				   return "https://"+System.getProperty("BIhostname")+":"+System.getProperty("BIambariport");
			   }
			  
			else{
				
					return "http://"+System.getProperty("BIhostname")+":"+System.getProperty("BIambariport");
				}
				
				
			
		}else{
			if(https.equalsIgnoreCase("true")){
				
				//return "https://"+hostname+":"+ambariPort+"/data/html/index.html";
				return "https://"+hostname+":"+ambariPort;
			}else{
				return "http://"+hostname+":"+ambariPort;
			}
	}
}


	public static String generateURL(String hostname, String https, String URLpath) {

		if (System.getProperty("BIhostname") != null
				&& System.getProperty("BIhttps") != null) {
			if (System.getProperty("BIhttps").equalsIgnoreCase("true")) {

				if (System.getProperty("BIambariport") != null) {
					return "https://" + System.getProperty("BIhostname") + ":"
							+ System.getProperty("BIambariport") + URLpath;
				} else {
					return "http://" + System.getProperty("BIhostname") + ":"
							+ System.getProperty("BIambariport") + URLpath;
				}

			}

		}

		return "http://" + hostname + ":8080" + URLpath;

	}	
	
	public static String generateValuePacksWebURL(String hostname, String knoxPort,String vpLoginName,String vpLoginPassword ,String vpService) {
		
		 if(System.getProperty("BIhostname") != null && System.getProperty("BIknoxPort") != null)
      {
			 
	          return "https://"+System.getProperty("BIhostname")+ ":" + System.getProperty("BIknoxPort") + "/gateway/default/BigInsightsWeb/index.html#/welcome?component="+vpService;
	          
              }else{
            	  
            	  System.out.println("https://"+vpLoginName+":"+vpLoginPassword+"@"+hostname+":"+knoxPort+"/gateway/default/BigInsightsWeb/index.html#/welcome?component="+vpService);
                      return "https://"+vpLoginName+":"+vpLoginPassword+"@"+hostname+":"+knoxPort+"/gateway/default/BigInsightsWeb/index.html#/welcome?component="+vpService;
                 

                    }
                          
             }      
	
}
        
