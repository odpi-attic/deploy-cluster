package com.ibm.biginsights.infra;


import org.json.JSONException;
import org.json.JSONObject;

import com.ibm.biginsights.ambari.AmbariUtil;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Response;

public class GetComponentMetrics {

	public static JSONObject AllMetrics(String ambarihost, String username, String password,
			String service,	String component) {
		
		;
		
		Response response = RestAssured.given().keystore("data/certs/BigInsights.jks","passw0rd").header("X-Requested-By:", "ambari").auth().
				preemptive().basic(username, password).
		get(AmbariUtil.getAmbariClusterURL(ambarihost, username, password)+"/services/"+service+"/components/"+component);
		
		JSONObject allMetrics = null;
		
		
		try{	
			allMetrics = new JSONObject(response.getBody().asString());
			
		}catch(JSONException e) {	
			e.printStackTrace();
			
		}
		
		return allMetrics;
		
	}
	
	
}
