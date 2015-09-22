package com.ibm.biginsights.infra;


import org.json.JSONException;
import org.json.JSONObject;

import com.ibm.biginsights.ambari.AmbariUtil;
import com.ibm.biginsights.maven.Common;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Response;

public class GetComponentNodes {

	public static String getInstalledCount(String ambarihost, String username, String password,
			String service, String component) {
		
		Response response = RestAssured.given().keystore("data/certs/BigInsights.jks","passw0rd").header("X-Requested-By:", "ambari").auth().preemptive().basic(username, password).
		get(AmbariUtil.getAmbariClusterURL(ambarihost, username, password)+"/services/"+service+"/components/"+component);
		
		JSONObject jObject = null;
		String count = null;
		
		try{	
			jObject = new JSONObject(response.getBody().asString());
			count = Common.readJsonObj(jObject, "ServiceComponentInfo", "installed_count");
			System.out.println("Number of nodes the component "+component+" is installed on: "+count);
			
		}catch(JSONException e) {	
			e.printStackTrace();
			
		}
		
		return count;
		
	}
	
}
