package com.ibm.biginsights.ambari;

import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.ibm.biginsights.maven.Common;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Response;

public class GetHosts {

	@Test
	@Parameters({"ambarihost", "username", "password"})
	public void getHosts(String ambarihost, String username, String password) {
		
		Response response;
		ambarihost = Common.assignPOMValues("BIambarihost", ambarihost);
		username = Common.assignPOMValues("BIusername", username);
		password = Common.assignPOMValues("BIpassword", password);
		
		response = RestAssured.given().keystore("data/certs/BigInsights.jks","passw0rd").header("X-Requested-By:", "ambari").auth().preemptive().basic(username, password).get(AmbariUtil.getAmbariHostURL(ambarihost)+"/api/v1/hosts");
			
		System.out.println(response.getBody().asString());
	}
}
