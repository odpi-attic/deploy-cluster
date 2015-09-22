package com.ibm.biginsights.infra;


import org.json.JSONException;
import org.json.JSONObject;

import com.ibm.biginsights.ambari.AmbariUtil;
import com.ibm.biginsights.maven.Common;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Response;

public class GetComponentState {
	
	public static String state(JSONObject jObject){
		String status = null;
		try {
			status = jObject.getString("state");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
		return status;
		
	}
	public static String maintState(JSONObject jObject){
		String status = null;
		try {
			status = jObject.getString("maintenance_state");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
		return status;
		
	}
	
	
	public static String status(String ambarihost, String username, String password,
			String component
			) throws InterruptedException, JSONException {
		
		
     	
     	/*
         * Check component service status
         */
		System.out.println("Checking component status..");	
		
		
		
		Response response = RestAssured.given().keystore("data/certs/BigInsights.jks","passw0rd").header("X-Requested-By:", "ambari").auth().preemptive().
				basic(username, password).get(AmbariUtil.getAmbariClusterURL(ambarihost, username, password)+"/hosts/"+ambarihost+"/host_components/"+component);
		JSONObject jObject = new JSONObject(response.getBody().asString());
		String state = Common.readJsonObj(jObject, "HostRoles", "state");
		System.out.println("Component " +component+" state was: "+state);
		
		return state;

	}

	
}
