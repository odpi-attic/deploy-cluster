package com.ibm.biginsights.ambari;

import java.util.List;
import junit.framework.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import com.ibm.biginsights.selenium.WebElementHelper;

public class RestartService {

	public static Boolean findAndClickService(WebDriver driver, String serviceToFind) 
	{
		Boolean serviceFoundAndClicked = false;
		
		Boolean isServicesBar = WebElementHelper.findByXpath(driver, "//div[contains(@class,'services-menu')]", 30);
		Assert.assertEquals(isServicesBar, new Boolean(true));
		
		WebElement servicesMenu = driver.findElement(By.xpath("//div[contains(@class,'services-menu')]"));
		List<WebElement> services = servicesMenu.findElements(By.xpath("ul/li"));
		
		int servicePosition=0;
		
		for(int i=0;i<services.size();i++)
		{
			WebElement serviceA = services.get(i).findElement(By.xpath("a"));
			//String serviceHref = serviceA.getAttribute("href"); //TEXTANALYTICS
			
			List<WebElement> serviceSpan = serviceA.findElements(By.xpath("span"));
			String serviceText = serviceSpan.get((serviceSpan.size())-1).getText();//BigInsights - Text Analytics
			
			if(serviceText.contains(serviceToFind))
			{
				servicePosition = i;
				serviceFoundAndClicked = true;
			}
			
			//serviceText = serviceText.replaceAll("\\s+",""); //remove all white spaces --> BigInsights-TextAnalytics
			//serviceText = serviceText.toUpperCase(); //uppercae --> BIGINSIGHTS-TEXTANALYTICS
			
			//Compare two strings 
		}
		
		services.get(servicePosition).findElement(By.xpath("a")).click();
		
		return serviceFoundAndClicked;
		/*Boolean serviceFoundAndClicked = false;
		
		Boolean isServicesBar = WebElementHelper.findByXpath(driver, "//div[contains(@class,'services-menu')]", 30);
		Assert.assertEquals(isServicesBar, new Boolean(true));
		
		WebElement servicesMenu = driver.findElement(By.xpath("//div[contains(@class,'services-menu')]"));
		List<WebElement> services = servicesMenu.findElements(By.xpath("ul/li"));
		
		int servicePosition=0;
		
		for(int i=0;i<services.size();i++)
		{
			String service = services.get(i).findElement(By.xpath("a")).getAttribute("href");
			
			if(service.contains(serviceToFind))
			{
				servicePosition = i;
				serviceFoundAndClicked = true;
			}
		}
		
		services.get(servicePosition).findElement(By.xpath("a")).click();
		
		return serviceFoundAndClicked;*/
	}

	public static void loginToCluster(WebDriver driver, String username, String password) 
	{
		WebElementHelper.findAndTypeByXpath(driver, username, "//div[contains(@class,'login')]/input[1]", 30, "username ");
		WebElementHelper.findAndTypeByXpath(driver, password, "//div[contains(@class,'login')]/input[2]", 30, "password ");
		WebElementHelper.findAndClickByXpath(driver, "//button[contains(@class,'btn-success')]", 30, "login button ");
	}
}