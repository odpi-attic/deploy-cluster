package com.ibm.biginsights.selenium;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

public class WebElementHelper {


	/**
	 * @info: Method that finds and clicks any element by its Xpath 
	 * @param driver - Provide the web driver
	 * @param xpath - Provide xpath of the element
	 * @param seconds - Provide number of seconds to wait
	 */
	public static void findAndClickByXpath(WebDriver driver,String xpath,int seconds,String desc){		    
		Boolean actual = findByXpath(driver,xpath,seconds);
		System.out.println(desc+" found ?? " + actual);
		Assert.assertEquals(actual, new Boolean(true));
		clickByXpath(driver,xpath);
	}

	/**
	 * @info: Method that finds and clicks any element by its ID
	 * @param driver - Provide the web driver
	 * @param id - Provide the id of the element
	 * @param seconds - Provide number of seconds to wait
	 */
	public static void findAndClickByID(WebDriver driver,String id,int seconds,String desc){		    
		Boolean actual = findByID(driver,id,seconds);
		System.out.println(desc+" found ?? " + actual);
		Assert.assertEquals(actual, new Boolean(true));		
		clickByID(driver,id);
	}

	/**
	 * @info: Method that finds and clicks any element by its name
	 * @param driver - Provide the web driver
	 * @param name - Provide the name of the element
	 * @param seconds - Provide number of seconds to wait
	 */
	public static void findAndClickByName(WebDriver driver,String name,int seconds,String desc){		    
		Boolean actual = findByName(driver,name,seconds);
		System.out.println(desc+" found ?? " + actual);
		Assert.assertEquals(actual, new Boolean(true));
		clickByName(driver,name);
	}
	
	/**
	 * @info: Method that finds and clicks any element by its name
	 * @param driver - Provide the web driver
	 * @param name - Provide the name of the element
	 * @param seconds - Provide number of seconds to wait
	 */
	public static void findAndClickByPartialLinktext(WebDriver driver,String partialLinkText,int seconds,String desc){		    
		Boolean actual = findByPartialLinkText(driver,partialLinkText,seconds);
		System.out.println(desc+" found ?? " + actual);
		Assert.assertEquals(actual, new Boolean(true));
		clickByPartialLinkText(driver,partialLinkText);
	}
	
	/**
	 * @info: Method that finds and clicks any element by its name
	 * @param driver - Provide the web driver
	 * @param name - Provide the name of the element
	 * @param seconds - Provide number of seconds to wait
	 */
	public static void findAndClickByLinktext(WebDriver driver,String linkText,int seconds,String desc){		    
		Boolean actual = findByLinkText(driver,linkText,seconds);
		System.out.println(desc+" found ?? " + actual);
		Assert.assertEquals(actual, new Boolean(true));
		clickByLinkText(driver,linkText);
	}	

	/**
	 * @info: Method that finds and types in to a form by its Xpath
	 * @param driver - Provide the web driver
	 * @param sendKeys - Provide the text that needs to be entered into the form
	 * @param xpath - Provide xpath of the element
	 * @param seconds - Provide number of seconds to wait
	 */
	public static void findAndTypeByXpath(WebDriver driver,String sendKeys,String xpath,int seconds,String desc){		    
		Boolean actual = findByXpath(driver,xpath,seconds);
		System.out.println(desc+" found ?? " + actual);
		Assert.assertEquals(actual, new Boolean(true));		
		typeByXpath(driver,sendKeys,xpath);
	}

	/**
	 * @info: Method that finds and types in to a form by its ID
	 * @param driver - Provide the web driver
	 * @param sendKeys - Provide the text that needs to be entered into the form
	 * @param id - Provide the id of the element
	 * @param seconds - Provide number of seconds to wait
	 */
	public static void findAndTypeByID(WebDriver driver,String sendKeys,String id,int seconds,String desc){		    
		Boolean actual = findByID(driver,id,seconds);
		System.out.println(desc+" found ?? " + actual);
		Assert.assertEquals(actual, new Boolean(true));
		typeByID(driver,sendKeys,id);
	}

	/**
	 * @info: Method that finds and types in to a form by its name
	 * @param driver - Provide the web driver
	 * @param sendKeys - Provide the text that needs to be entered into the form
	 * @param name - Provide the name of the element
	 * @param seconds - Provide number of seconds to wait
	 */
	public static void findAndTypeByName(WebDriver driver,String sendKeys,String name,int seconds,String desc){		    
		Boolean actual = findByName(driver,name,seconds);
		System.out.println(desc+" found ?? " + actual);
		Assert.assertEquals(actual, new Boolean(true));
		typeByName(driver,sendKeys,name);
	}
	
	/**
	 * @info: Method that finds and types in to a form by its class name
	 * @param driver - Provide the web driver
	 * @param sendKeys - Provide the text that needs to be entered into the form
	 * @param name - Provide the name of the element
	 * @param seconds - Provide number of seconds to wait
	 */
	public static void findAndTypeByClassName(WebDriver driver,String sendKeys,String className,int seconds){		    
		Boolean actual = findByClassName(driver,className,seconds);
		Assert.assertEquals(actual, new Boolean(true));
		typeByName(driver,sendKeys,className);
	}

	/**
	 * @info: Method that finds and selects an element by its Xpath
	 * @param driver - Provide the web driver
	 * @param selectByVisibleText - Provide the text of the option that should be selected from the dropdown list
	 * @param xpath - Provide xpath of the element
	 * @param seconds - Provide number of seconds to wait
	 */
	public static void findAndSelectByXpath(WebDriver driver,String selectByVisibleText,String xpath,int seconds,String desc){		    
		Boolean actual = findByXpath(driver,xpath,seconds);
		System.out.println(desc+" found ?? " + actual);
		Assert.assertEquals(actual, new Boolean(true));
		selectByXpath(driver,selectByVisibleText,xpath);
	}

	/**
	 * @info: Method that finds and selects an element by its ID
	 * @param driver - Provide the web driver
	 * @param selectByVisibleText - Provide the text of the option that should be selected from the dropdown list
	 * @param id - Provide the id of the element
	 * @param seconds - Provide number of seconds to wait
	 */
	public static void findAndSelectByID(WebDriver driver,String selectByVisibleText,String id,int seconds,String desc){		    
		Boolean actual = findByID(driver,id,seconds);
		System.out.println(desc+" found ?? " + actual);
		Assert.assertEquals(actual, new Boolean(true));
		selectByID(driver,selectByVisibleText,id);
	}

	/**
	 * @info: Method that finds and selects an element by its name
	 * @param driver - Provide the web driver
	 * @param selectByVisibleText - Provide the text of the option that should be selected from the dropdown list
	 * @param name - Provide the name of the element
	 * @param seconds - Provide number of seconds to wait
	 */
	public static void findAndSelectByName(WebDriver driver,String selectByVisibleText,String name,int seconds,String desc){		    
		Boolean actual = findByName(driver,name,seconds);
		System.out.println(desc+" found ?? " + actual);
		Assert.assertEquals(actual, new Boolean(true));
		selectByName(driver,selectByVisibleText,name);
	}
	
	
	private static Boolean findByLinkText(WebDriver driver, final String linkText,
			int seconds) {
		
		return (new WebDriverWait(driver, seconds)).until(new ExpectedCondition<Boolean>() 
				{
			public Boolean apply(WebDriver d)
			{ 
				return d.findElement(By.linkText(linkText)).isDisplayed();
			}
				});
	}

	/**
	 * @info: Method that finds an element by its Xpath
	 * @param driver - Provide the web driver
	 * @param xpath - Provide xpath of the element
	 * @param seconds - Provide number of seconds to wait
	 * @return
	 */
	public static Boolean findByXpath(WebDriver driver, final String xpath, int seconds) 
	{
		return (new WebDriverWait(driver, seconds)).until(new ExpectedCondition<Boolean>() 
				{
			public Boolean apply(WebDriver d)
			{ 
				return d.findElement(By.xpath(xpath)).isDisplayed();
			}
				});
	}
	
	/**
	 * @info: Method that finds an element by its ID
	 * @param driver - Provide the web driver
	 * @param id - Provide the id of the element
	 * @param seconds - Provide number of seconds to wait
	 * @return
	 */
	public static Boolean findByID(WebDriver driver, final String id, int seconds) 
	{
		return (new WebDriverWait(driver, seconds)).until(new ExpectedCondition<Boolean>() 
				{
			public Boolean apply(WebDriver d)
			{ 
				return d.findElement(By.id(id)).isDisplayed();
			}
				});
	}

	/**
	 * @info: Method that finds an element by its name
	 * @param driver - Provide the web driver
	 * @param name - Provide the name of the element
	 * @param seconds - Provide number of seconds to wait
	 * @return
	 */
	public static Boolean findByName(WebDriver driver, final String name, int seconds) 
	{
		return (new WebDriverWait(driver, seconds)).until(new ExpectedCondition<Boolean>() 
				{
			public Boolean apply(WebDriver d)
			{ 
				return d.findElement(By.name(name)).isDisplayed();
			}
				});
	}
	
	/**
	 * @info: Method that finds an element by its class name
	 * @param driver - Provide the web driver
	 * @param name - Provide the name of the element
	 * @param seconds - Provide number of seconds to wait
	 * @return
	 */
	public static Boolean findByClassName(WebDriver driver, final String className, int seconds) 
	{
		return (new WebDriverWait(driver, seconds)).until(new ExpectedCondition<Boolean>() 
				{
			public Boolean apply(WebDriver d)
			{ 
				return d.findElement(By.className(className)).isDisplayed();
			}
				});
	}
	
	/**
	 * @info: Method that finds an element by its name
	 * @param driver - Provide the web driver
	 * @param partialLinkText - Provide the partial link text of the a element
	 * @param seconds - Provide number of seconds to wait
	 * @return
	 */
	public static Boolean findByPartialLinkText(WebDriver driver, final String partialLinkText, int seconds) 
	{
		return (new WebDriverWait(driver, seconds)).until(new ExpectedCondition<Boolean>() 
				{
			public Boolean apply(WebDriver d)
			{ 
				return d.findElement(By.partialLinkText(partialLinkText)).isDisplayed();
			}
				});
	}
	
	/**
	 * @info: Method that finds an element by its Xpath
	 * @param driver - Provide the web driver
	 * @param xpath - Provide xpath of the element
	 * @param seconds - Provide number of seconds to wait until element is enabled
	 * @return
	 */
	public static Boolean waitUntilEnabledByXpath(WebDriver driver, final String xpath, int seconds) 
	{
		return (new WebDriverWait(driver, seconds)).until(new ExpectedCondition<Boolean>() 
				{
			public Boolean apply(WebDriver d)
			{ 
				return d.findElement(By.xpath(xpath)).isEnabled();
			}
				});
	}

	/**
	 * @info: Method that clicks an element by its Xpath
	 * @param driver - Provide the web driver
	 * @param xpath - Provide xpath of the element
	 */
	public static void clickByXpath(WebDriver driver, String xpath) 
	{	
		WebElement element = driver.findElement(By.xpath(xpath));
		element.click();
	}

	/**
	 * @info: Method that clicks an element by its ID
	 * @param driver - Provide the web driver
	 * @param id - Provide the id of the element
	 */
	public static void clickByID(WebDriver driver, String id) 
	{	
		WebElement element = driver.findElement(By.id(id));
		element.click();
	}

	/**
	 * @info: Method that clicks an element by its name
	 * @param driver - Provide the web driver
	 * @param name - Provide the name of the element
	 */
	public static void clickByName(WebDriver driver, String name) 
	{	
		WebElement element = driver.findElement(By.name(name));
		element.click();
	}
	
	/**
	 * @info: Method that clicks an element by its name
	 * @param driver - Provide the web driver
	 * @param partialLinkText - Provide the partial link text of the a element
	 */
	public static void clickByPartialLinkText(WebDriver driver, String partialLinkText) 
	{	
		WebElement element = driver.findElement(By.partialLinkText(partialLinkText));
		element.click();
	}
	
	/**
	 * @info: Method that clicks an element by its name
	 * @param driver - Provide the web driver
	 * @param partialLinkText - Provide the partial link text of the a element
	 */
	public static void clickByLinkText(WebDriver driver, String linkText) 
	{	
		WebElement element = driver.findElement(By.linkText(linkText));
		element.click();
	}

	/**
	 * @info: Method that types in to a form by its xpath
	 * @param driver - Provide the web driver
	 * @param sendKeys - Provide the text that needs to be entered into the form
	 * @param xpath - Provide xpath of the element
	 */
	public static void typeByXpath(WebDriver driver, String sendKeys, String xpath) 
	{
		WebElement element = driver.findElement(By.xpath(xpath));
		element.clear();
		element.sendKeys(sendKeys);
	}

	/**
	 * @info: Method that types in to a form by its ID
	 * @param driver - Provide the web driver
	 * @param sendKeys - Provide the text that needs to be entered into the form
	 * @param id - Provide the id of the element
	 */
	public static void typeByID(WebDriver driver, String sendKeys, String id) 
	{
		WebElement element = driver.findElement(By.id(id));
		element.clear();
		element.sendKeys(sendKeys);
	}

	/**
	 * @info: Method that types in to a form by its name
	 * @param driver - Provide the web driver
	 * @param sendKeys - Provide the text that needs to be entered into the form
	 * @param name - Provide the name of the element
	 */
	public static void typeByName(WebDriver driver, String sendKeys, String name) 
	{
		WebElement element = driver.findElement(By.name(name));
		element.clear();
		element.sendKeys(sendKeys);
	}

	/**
	 * @info: Method that selects an element by its Xpath
	 * @param driver - Provide the web driver
	 * @param selectByVisibleText - Provide the text of the option that should be selected from the dropdown list
	 * @param xpath - Provide xpath of the element
	 */
	public static void selectByXpath(WebDriver driver, String selectByVisibleText, String xpath) 
	{
		Select select = new Select(driver.findElement(By.xpath(xpath)));
		select.selectByVisibleText(selectByVisibleText);		
	}

	/**
	 * @info: Method that selects an element by its ID
	 * @param driver - Provide the web driver
	 * @param selectByVisibleText - Provide the text of the option that should be selected from the dropdown list
	 * @param id - Provide the id of the element
	 */
	public static void selectByID(WebDriver driver, String selectByVisibleText, String id) 
	{
		Select select = new Select(driver.findElement(By.id(id)));
		select.selectByVisibleText(selectByVisibleText);		
	}

	/**
	 * @info: Method that selects an element by its name
	 * @param driver - Provide the web driver
	 * @param selectByVisibleText - Provide the text of the option that should be selected from the dropdown list
	 * @param name - Provide the name of the element
	 */
	public static void selectByName(WebDriver driver, String selectByVisibleText, String name) 
	{
		Select select = new Select(driver.findElement(By.name(name)));
		select.selectByVisibleText(selectByVisibleText);		
	}
	
	/**
	 * @info: Method that finds an elements by its Xpath
	 * @param driver - Provide the web driver
	 * @param xpath - Provide xpath of the element
	 * @param seconds - Provide number of seconds to wait
	 * @return list of WebElements
	 */
	public static List<WebElement> getElementsByXpath(WebDriver driver, final String xpath) 
	{	
		return driver.findElements(By.xpath(xpath));
	}
}
