package com.ibm.biginsights.ambari;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.ibm.biginsights.maven.Common;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Header;
import com.jayway.restassured.response.Response;

public class StopAllComponents
{
	static Header header = new Header("X-Requested-By:", "ambari");
	private static Response stopAllComponentsResponse = null;
	private static String content = "{\"RequestInfo\":{\"context\":\"Stop Component\"},\"Body\":{\"HostRoles\":{\"state\":\"INSTALLED\"}}}";
	
	@Test
	@Parameters({"ambarihost", "username", "password", "hostname"})
	public static void stopComponents(String ambarihost, String username, String password, String hostname) throws JSONException, InterruptedException 
	{		System.out.println("Stop components");
		/*ambariclustername = Common.assignPOMValues("BIambariclustername", ambariclustername);*/
		ambarihost = Common.assignPOMValues("BIambarihost", ambarihost);
		/*ambariport = Common.assignPOMValues("BIambariport", ambariport);*/
		username = Common.assignPOMValues("BIusername", username);
		password = Common.assignPOMValues("BIpassword", password);
		hostname = Common.assignPOMValues("BInewhost", hostname);
		long startTime = 0;
		long stopTime = 0;
		
		String response = RestAssured.given().keystore("data/certs/BigInsights.jks","passw0rd").header(header).auth().preemptive().
				basic(username, password).
				get(AmbariUtil.getAmbariClusterURL(ambarihost, username, password)+
						"/hosts/"+hostname).asString();
		JSONObject components = new JSONObject(response);
		//System.out.println("response in stop components "+response);
		JSONArray hostComponents = components.getJSONArray("host_components");
				
		for(int i = 0; i < hostComponents.length() ; i++ )
		{
			//get the component name from the response and stop the component
			if (hostComponents.getJSONObject(i).has("href"))
			{
				String status = null;
				String componentName = hostComponents.getJSONObject(i).getString("href");
				System.out.println("Stopping service for component "+componentName);
				//Stop comp needs to be skipped for clients
				if(!componentName.contains("_CLIENT")){
				do{
					startTime = System.currentTimeMillis();
					stopAllComponentsResponse = RestAssured.given().keystore("data/certs/BigInsights.jks","passw0rd").header(header).auth().preemptive().
						basic(username, password).body(content).
						put(componentName);
					
					
					String r = RestAssured.given().keystore("data/certs/BigInsights.jks","passw0rd").header(header).auth().preemptive().
							basic(username, password).get(componentName).asString();
					JSONObject jObject = new JSONObject(r);
					System.out.println("response : "+r);
					status = Common.readJsonObj(jObject, "HostRoles", "state");
					System.out.println("status is : "+status);
					
					stopTime = System.currentTimeMillis();
					
				}while(!(status.contains("INSTALLED"))||((stopTime - startTime)==300000));
				if(status.contains("INSTALLED") ){
					System.out.println(stopAllComponentsResponse.getStatusLine());
					
		        	Assert.assertTrue(true);
		        }else{
		        	System.out.println(stopAllComponentsResponse.getStatusLine());
					
		        	Assert.assertFalse(true);
		        }

				if((stopTime - startTime) == 300000){
		        	System.out.println("ERROR: Timeout of 300 seconds reached. Service could not be stopped.");
		        	Assert.assertFalse(true);
		        }
				}				}
		}
		
		
	}
}
