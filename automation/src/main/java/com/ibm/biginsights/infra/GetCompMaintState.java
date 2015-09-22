package com.ibm.biginsights.infra;


import org.json.JSONException;
import org.json.JSONObject;

import com.ibm.biginsights.ambari.AmbariUtil;
import com.ibm.biginsights.maven.Common;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Response;

public class GetCompMaintState {
	
	
	public static String maintState(String ambarihost, String username, String password, String component
			) throws InterruptedException, JSONException {
		
     	
     	/*
         * Check component maintenance status
         */
			
		
		Response response = RestAssured.given().keystore("data/certs/BigInsights.jks","passw0rd").header("X-Requested-By:", "ambari").auth().preemptive().basic(username, password).
		get(AmbariUtil.getAmbariClusterURL(ambarihost, username, password)+"/hosts/"+ambarihost+"/host_components/"+component);
		JSONObject jObject = new JSONObject(response.getBody().asString());
		String state = Common.readJsonObj(jObject, "HostRoles", "maintenance_state");
		System.out.println("Maintenance mode for component " +component+" was: "+state);
		
		return state;

	}

	
	
}
