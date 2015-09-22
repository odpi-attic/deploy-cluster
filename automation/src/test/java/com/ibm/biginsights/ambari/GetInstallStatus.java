package com.ibm.biginsights.ambari;

import java.io.IOException;

import org.json.JSONException;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.ibm.biginsights.maven.Common;
import com.jayway.restassured.RestAssured;

public class GetInstallStatus {
	
	@Test
	@Parameters({"ambarihost", "username", "password"})
	public void getInstallStatus(String ambarihost, String username, String password) throws IOException, InterruptedException, JSONException{
		
		ambarihost = Common.assignPOMValues("BIambarihost", ambarihost);
		/*ambariclustername = Common.assignPOMValues("BIambariclustername", ambariclustername);*/
		username = Common.assignPOMValues("BIusername", username);
		password = Common.assignPOMValues("BIpassword", password);
		
		String id = "24";
		
		String response = RestAssured.given().header("X-Requested-By:", "ambari").auth().preemptive().basic(username, password).get(AmbariUtil.getAmbariClusterURL(ambarihost, username, password)+"/requests/"+id).asString();
		
		System.out.println(response);
	}
}
