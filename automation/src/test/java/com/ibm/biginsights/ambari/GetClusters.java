package com.ibm.biginsights.ambari;

import org.json.JSONException;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.ibm.biginsights.maven.Common;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Response;

public class GetClusters {

	
	@Test
	@Parameters({"ambarihost", "username", "password"})
	public void getBlueprints(String ambarihost, String username, String password) throws JSONException {
		
		ambarihost = Common.assignPOMValues("BIambarihost", ambarihost);
		username = Common.assignPOMValues("BIusername", username);
		password = Common.assignPOMValues("BIpassword", password);
		
		Response response = RestAssured.given().keystore("data/certs/BigInsights.jks","passw0rd").header("X-Requested-By:", "ambari").auth().preemptive().basic(username, password).get(AmbariUtil.getAmbariHostURL(ambarihost)+"/api/v1/clusters");
		
		System.out.println(response.asString());
	}
}
