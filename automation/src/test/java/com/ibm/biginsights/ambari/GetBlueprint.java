package com.ibm.biginsights.ambari;

import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.ibm.biginsights.maven.Common;
import com.jayway.restassured.RestAssured;

public class GetBlueprint {
	
	@Test
	@Parameters({"ambarihost", "username", "password"})
	public void getBlueprint(String ambarihost, String username, String password) {
		
		ambarihost = Common.assignPOMValues("BIambarihost", ambarihost);
		/*ambariclustername = Common.assignPOMValues("BIambariclustername", ambariclustername);*/
		username = Common.assignPOMValues("BIusername", username);
		password = Common.assignPOMValues("BIpassword", password);
		
		String response = RestAssured.given().keystore("data/certs/BigInsights.jks","passw0rd").header("X-Requested-By:", "ambari").auth().preemptive().basic(username, password).get(AmbariUtil.getAmbariClusterURL(ambarihost, username, password)+"?format=blueprint").asString();
		
		
		System.out.println(response);
	}

}
