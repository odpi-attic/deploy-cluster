package com.ibm.biginsights.infra;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ibm.biginsights.ambari.AmbariUtil;
import com.ibm.biginsights.maven.Common;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Response;

public class GetServiceInfo {
	
	

	public static JSONObject details(String ambarihost, String username, String password,
			String service
			)  {
		
		
		JSONObject servicesAry = null;
     	/*
         * Return an array of JSON objects for a given service
         * that contains -
         * 1. JSON object containing service info 
         * 2. JSON object containing alerts
         * 3. JSON object containing associated components  
         */
			
		try{
			servicesAry = new JSONObject();
			Response response = RestAssured.given().keystore("data/certs/BigInsights.jks","passw0rd").header("X-Requested-By:", "ambari").auth().preemptive().basic(username, password).
				get(AmbariUtil.getAmbariClusterURL(ambarihost, username, password)+"/services/"+service);
			JSONObject jObject = new JSONObject(response.getBody().asString());
			servicesAry.put("1",Common.returnJsonObj(jObject, "ServiceInfo"));
			/*Removing alert object since we user ambari metrics now.*/
			/*servicesAry.put("2",Common.returnJsonObj(jObject, "alerts"));*/
			
			
	
		}catch (JSONException e){
			e.printStackTrace();
		}
		return servicesAry;
	}
	public static JSONArray serviceComponents(String ambarihost, String username, String password,
			String service
			)  {
		
		
		JSONArray servicesAry = null;
     	/*
         * Return an array of JSON objects for a given service
         * that contains -
         * 1. JSON object containing service info 
         * 2. JSON object containing alerts
         * 3. JSON object containing associated components  
         */
			
		try{
			servicesAry = null;
			Response response = RestAssured.given().keystore("data/certs/BigInsights.jks","passw0rd").header("X-Requested-By:", "ambari").auth().preemptive().basic(username, password).
				get(AmbariUtil.getAmbariClusterURL(ambarihost, username, password)+"/services/"+service);
			JSONObject jObject = new JSONObject(response.getBody().asString());
			servicesAry = jObject.getJSONArray("components");
			
			
	
		}catch (JSONException e){
			e.printStackTrace();
		}
		return servicesAry;
	}

}
