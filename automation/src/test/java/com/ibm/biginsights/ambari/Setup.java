package com.ibm.biginsights.ambari;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.authentication.PreemptiveBasicAuthScheme;
import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.config.LogConfig;
import com.jayway.restassured.config.RestAssuredConfig;
import com.jayway.restassured.http.ContentType;
import org.testng.ITestContext;
import org.testng.xml.XmlSuite;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


/**
 * Initialize common test fixtures for ambari test suites including POM value assignment,
 * logging, data providers for base request specification
 */
public class Setup {

	/**
	 * Create base request spec with configurations common to all tests
	 * @param username ambari server admin username
	 * @param password ambari server admin password
	 * @return a base request specification that can be extended & built for tests
	 */
	public static RequestSpecBuilder createBaseSpec(String username, String password) {
		PreemptiveBasicAuthScheme basicAuth = new PreemptiveBasicAuthScheme();
		basicAuth.setUserName(username);
		basicAuth.setPassword(password);

		return new RequestSpecBuilder().
				setAuth(basicAuth).
				setKeystore("data/certs/BigInsights.jks","passw0rd").
				setContentType(ContentType.JSON).
				addHeader("X-Requested-By", "ambari");
	}

	/**
	 * Initialize logging to trace REST request/response input/ouput for tests
	 * Call in @BeforeClass method for tests
	 * @param testContext of current test/suite
	 */
	public static void initLogging(ITestContext testContext) {
		XmlSuite currentSuite = testContext.getCurrentXmlTest().getSuite();
		String suiteName = currentSuite.getName();
		String service = currentSuite.getParameter("service");
		PrintStream defaultLogStream = null;
		Path outDirPath;

		try {
			if(service != null)
				outDirPath = Files.createDirectories(Paths.get(System.getProperty("java.io.tmpdir"), "test-output", service));
			else
				outDirPath = Files.createDirectories(Paths.get(System.getProperty("java.io.tmpdir"), "test-output"));
			Path logFilePath = Files.createTempFile(outDirPath, suiteName, ".out");
			defaultLogStream = new PrintStream(logFilePath.toFile());
		}
		catch(IOException e) {
			e.printStackTrace();
		}

		LogConfig testLogConfig = new LogConfig(defaultLogStream, true);
		RestAssured.config = RestAssuredConfig.config().logConfig(testLogConfig);
	}
}
