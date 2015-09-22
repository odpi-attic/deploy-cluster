package com.ibm.biginsights.ambari;

import java.util.Arrays;
import java.util.List;
import junit.framework.Assert;
import org.json.JSONException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.ibm.biginsights.ambari.RestartService;
import com.ibm.biginsights.maven.Common;
import com.ibm.biginsights.selenium.WebDriverHelper;
import com.ibm.biginsights.selenium.WebElementHelper;

public class RestartServices {
	
	private String url;
	private WebDriver driver;
	private String servicesToRestart;
	private Boolean isKnox;
	
	@BeforeClass
	@Parameters({"ambarihost", "ambariport", "browser", "https"})
	public void openBrowser(String ambarihost, String ambariport, String browser, String https) throws JSONException, InterruptedException
	{
		ambarihost = Common.assignPOMValues("BIambarihost", ambarihost);
		ambariport = Common.assignPOMValues("BIambariport", ambariport);
		https = Common.assignPOMValues("BIhttps", https);
		
		//url = "http://"+ambarihost+":"+ambariport;
		url = WebDriverHelper.getIOPWebConsoleURL(ambarihost, https, ambariport);
		driver = WebDriverHelper.instantiateDriver(driver, browser, url);
	}
	
	@Test
	/***
	 * Find and login
	 */
	@Parameters({"username", "password"})
	public void login(String username, String password)
	{
		username = Common.assignPOMValues("BIusername", username);
		password = Common.assignPOMValues("BIpassword", password);
		
		System.out.println("----- Logging Into Ambari Cluster -----");
		RestartService.loginToCluster(driver, username, password);
	}
	
	@Test(dependsOnMethods = {"login"}, enabled=true)
	/***
	 * Click restartServices
	 */
	@Parameters({"services"})
	public void restartServices(String services) throws InterruptedException
	{	
		isKnox =  false;
		
		services = Common.assignPOMValues("BIservice", services);
		
		System.out.println("----- Restarting Services -----");
		
		String[] restart = services.split(",");
		List<String> restartServices = Arrays.asList(restart);
		
		for(int i=0;i<restartServices.size();i++)
		{
			servicesToRestart = restartServices.get(i);
			Boolean serviceSelected = RestartService.findAndClickService(driver,  servicesToRestart);
			if(!serviceSelected)
			{
				System.out.println(servicesToRestart + " service not found, skipping...");
			}
			else
			{
				if(servicesToRestart.equalsIgnoreCase("Knox"))
				{
					isKnox = true;
				}
				
				System.out.println(servicesToRestart + " service found, restarting service...");
				Thread.sleep(2000);
				
				WebElementHelper.findAndClickByXpath(driver, "//span[text()='Service Actions']", 30, "Service Actions ");
				WebElementHelper.findAndClickByXpath(driver, "//*[text()='Restart All']", 30, "Restart All for Service ");	
				
				WebElementHelper.findAndClickByXpath(driver, "//button[text()='Confirm Restart All']", 30, "Confirm Restart All ");
				Thread.sleep(2000);
				
				/*if(servicesToRestart.equalsIgnoreCase("BigInsights Home"))
				{
					servicesToRestart = "WEBUIFRAMEWORK";
				}*/
				
				String restartText = "Restart all components for " + servicesToRestart;
				
				Boolean restartBar = WebElementHelper.findByXpath(driver, "//div[contains(@class,'operation-name-icon-wrap')]", 20);
				Assert.assertEquals(restartBar, new Boolean(true));
				List<WebElement> restartElem1 = driver.findElements(By.xpath("//div[contains(@class,'operation-name-icon-wrap')]"));
				WebElement restartElem = restartElem1.get(0).findElement(By.xpath("a"));
				
				System.out.println(restartElem.getText());
				if((restartElem.getText()).equalsIgnoreCase(restartText))
				{
					System.out.println("Correct Restart");
				}
				else
				{
					Boolean found = false;
					Assert.assertEquals(found, new Boolean(true));
				}
				
				String progressBarString = "continue";
				int timeCounter = 0;
				while(!(progressBarString.equalsIgnoreCase("100%")) && (timeCounter!=900))
				{
					
					WebElement progressBar = restartElem.findElement(By.xpath("../..")).findElement(By.className("host-progress-num"));
					progressBarString = progressBar.getText();
					timeCounter++;
					Thread.sleep(1000);
				}
				
				WebElement progressBarStatus = restartElem.findElement(By.xpath("../..")).findElement(By.className("progress-bar"));
				WebElement progressStatus = progressBarStatus.findElement(By.xpath("div"));
				
				String serviceStatus = progressStatus.getAttribute("class");
				
				if(serviceStatus.contains("success"))
				{
					System.out.println(servicesToRestart + " Service Restarted Successfully");
				}
				else
				{
					System.out.println(servicesToRestart + " Service Not Restarted Properly");
				}
				
				WebElementHelper.findAndClickByXpath(driver, "//button[text()='OK']", 30, "OK button ");
				Thread.sleep(2000);
			}
		}
	}
		
		/*for(int i=0;i<restartServices.size();i++)
		{
			servicesToRestart = restartServices.get(i);
			Boolean serviceSelected = RestartService.findAndClickService(driver, servicesToRestart);
			if(!serviceSelected)
			{
				System.out.println(servicesToRestart + " service not found, skipping...");
			}
			else
			{
				if(servicesToRestart.equalsIgnoreCase("KNOX"))
				{
					isKnox = true;
				}
				
				System.out.println(servicesToRestart + " service found, restarting service...");
				Thread.sleep(2000);
				
				WebElementHelper.findAndClickByXpath(driver, "//span[text()='Service Actions']", 30, "Service Actions ");
				WebElementHelper.findAndClickByXpath(driver, "//*[text()='Restart All']", 30, "Restart All for Service ");	
				
				WebElementHelper.findAndClickByXpath(driver, "//button[text()='Confirm Restart All']", 30, "Confirm Restart All ");
				Thread.sleep(2000);
				
				servicesToRestart = servicesToRestart.toUpperCase();
								
				String restartText = "Restart all components for " + servicesToRestart;
								
				Boolean restartBar = WebElementHelper.findByXpath(driver, "//div[contains(@class,'operation-name-icon-wrap')]", 20);
				Assert.assertEquals(restartBar, new Boolean(true));
				List<WebElement> restartElem1 = driver.findElements(By.xpath("//div[contains(@class,'operation-name-icon-wrap')]"));
				WebElement restartElem = restartElem1.get(0).findElement(By.xpath("a"));
				
				System.out.println(restartElem.getText());
				if((restartElem.getText()).equalsIgnoreCase(restartText))
				{
					System.out.println("Correct Restart");
				}
				else
				{
					Boolean found = false;
					Assert.assertEquals(found, new Boolean(true));
				}
				
				/*div[contains(@class,'operation-name-icon-wrap')]//child::a
				Boolean restartBar = WebElementHelper.findByXpath(driver, "//*[text()='Restart all components for MapReduce2']", 20);
				Boolean restartBar = WebElementHelper.findByXpath(driver, "//*[starts-with(text(),'Restart all components for')]", 20);
				
				driver.findElements(By.linkText("regexpi:Linktext"));
				
				Assert.assertEquals(restartBar, new Boolean(true));
				WebElement restartElem = driver.findElement(By.xpath("//*[text()='Restart all components for MapReduce2']"));
				WebElement restartElem = driver.findElement(By.xpath("//*[text()='Restart all components for "+servicesToRestart+"']"));*/
				
				/*System.out.println("Restarting Service: " + servicesToRestart);
				
				String progressBarString = "continue";
				int timeCounter = 0;
				while(!(progressBarString.equalsIgnoreCase("100%")) && (timeCounter!=900))
				{
					
					WebElement progressBar = restartElem.findElement(By.xpath("../..")).findElement(By.className("host-progress-num"));
					progressBarString = progressBar.getText();
					timeCounter++;
					Thread.sleep(1000);
				}
				
				WebElement progressBarStatus = restartElem.findElement(By.xpath("../..")).findElement(By.className("progress-bar"));
				WebElement progressStatus = progressBarStatus.findElement(By.xpath("div"));
				
				String serviceStatus = progressStatus.getAttribute("class");
				
				if(serviceStatus.contains("success"))
				{
					System.out.println(servicesToRestart + " Service Restarted Successfully");
				}
				else
				{
					System.out.println(servicesToRestart + " Service Not Restarted Properly");
				}
				
				WebElementHelper.findAndClickByXpath(driver, "//button[text()='OK']", 30, "OK button ");
				Thread.sleep(2000);
			}
		}
	}*/
	
	@Test(dependsOnMethods = {"restartServices"}, enabled=true)
	/***
	 * Click restartServices
	 */
	@Parameters({"services"})
	public void demoLdap(String services) throws InterruptedException
	{
		if(isKnox)
		{
			System.out.println("----- Starting Demo Ldap -----");
			Boolean serviceSelected = RestartService.findAndClickService(driver, "Knox");
			Assert.assertEquals(serviceSelected, new Boolean(true));
			
			System.out.println("Selected KNOX service");
			Thread.sleep(2000);
			
			WebElementHelper.findAndClickByXpath(driver, "//span[text()='Service Actions']", 30, "Service Actions ");
			WebElementHelper.findAndClickByXpath(driver, "//*[text()='Start Demo LDAP']", 30, "Start Demo LDAP ");	
			
			WebElementHelper.findAndClickByXpath(driver, "//button[text()='OK']", 30, "Confirm Start Demo LDAP ");
			Thread.sleep(2000);
						
			Boolean restartBar = WebElementHelper.findByXpath(driver, "//div[contains(@id,'service-info')]/div[1]/div", 20);
			Assert.assertEquals(restartBar, new Boolean(true));
			WebElement restartElem = driver.findElement(By.xpath("//div[contains(@id,'service-info')]/div[1]/div"));
			
			WebElement startDemoLdap = restartElem.findElement(By.className("operation-name-icon-wrap")).findElement(By.xpath("a"));
			String knoxDemoLdap = startDemoLdap.getText();
			System.out.println(knoxDemoLdap + " in progress...");
			
			System.out.println("Starting Demo LDAP");
			
			String progressBarString = "continue";
			int timeCounter = 0;
			while(!(progressBarString.equalsIgnoreCase("100%")) && (timeCounter!=900))
			{
				
				WebElement progressBar = restartElem.findElement(By.className("host-progress-num"));
				progressBarString = progressBar.getText();
				timeCounter++;
				Thread.sleep(1000);
			}
			
			WebElement progressBarStatus = restartElem.findElement(By.className("progress-bar"));
			WebElement progressStatus = progressBarStatus.findElement(By.xpath("div"));
			
			String serviceStatus = progressStatus.getAttribute("class");
			
			if(serviceStatus.contains("success"))
			{
				System.out.println("Demo LDAP Started Properly");
			}
			else
			{
				System.out.println("Demo LDAP Not Started Properly");
			}
			
			WebElementHelper.findAndClickByXpath(driver, "//button[text()='OK']", 30, "OK button ");
			Thread.sleep(2000);
		}
	}

	@AfterClass
	public void cleanup()
	{
		driver.quit();
	}

}
