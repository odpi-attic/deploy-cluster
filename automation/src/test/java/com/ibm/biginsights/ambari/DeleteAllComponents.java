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

/*
 * Deleting Component involves the following steps
 * 1. Stop all services
 * 2. Stop the components
 * 3. Delete the components
 */
public class DeleteAllComponents
{
	static Header header = new Header("X-Requested-By:", "ambari");
	private static Response deleteEachComponentResponse = null;
	
	@Test
	@Parameters({"ambarihost", "username", "password", "hostname"})
	public static void deleteComponents(String ambarihost, 
			String username, String password, String hostname) throws JSONException, InterruptedException 
	{		
		/*ambariclustername = Common.assignPOMValues("BIambariclustername", ambariclustername);*/
		ambarihost = Common.assignPOMValues("BIambarihost", ambarihost);
		/*ambariport = Common.assignPOMValues("BIambariport", ambariport);*/
		username = Common.assignPOMValues("BIusername", username);
		password = Common.assignPOMValues("BIpassword", password);
		hostname = Common.assignPOMValues("BInewhost", hostname);
		
		
		//Stop all components
		StopAllComponents.stopComponents(ambarihost, username, password,  hostname);

		String response = RestAssured.given().keystore("data/certs/BigInsights.jks","passw0rd").header(header).auth().preemptive().basic(username, password).
				get(AmbariUtil.getAmbariClusterURL(ambarihost, username, password)+"/hosts/"+hostname).asString();
		JSONObject components = new JSONObject(response);
		JSONArray hostComponents = components.getJSONArray("host_components");
		for(int i = 0; i < hostComponents.length() ; i++ )
		{
			//get the href from the response and delete the component
			if(hostComponents.getJSONObject(i).has("href"))
			{
			String componentURL = (String) hostComponents.getJSONObject(i).get("href");				
			System.out.println("Deleting component : "+componentURL);									
			
				do{
					deleteEachComponentResponse = RestAssured.given().keystore("data/certs/BigInsights.jks","passw0rd").header(header).auth().preemptive().
							basic(username, password).delete(componentURL);		
					//System.out.println("Deleting Component: "+deleteEachComponentResponse.getStatusLine());
					
					
				}while((deleteEachComponentResponse.getStatusLine().contains("500")));
				System.out.println("Status line .."+deleteEachComponentResponse.getStatusLine());
				System.out.println("Status code.."+deleteEachComponentResponse.getStatusLine());
				
				if(deleteEachComponentResponse.getStatusCode()==200 || deleteEachComponentResponse.getStatusCode()==202 ){
					System.out.println(deleteEachComponentResponse.getStatusLine());
					
		        	Assert.assertTrue(true);
		        }else{
		        	System.out.println(deleteEachComponentResponse.getStatusLine());
					
		        	Assert.assertFalse(true);
		        }
				System.out.println("All components deleted");
			}
		}
		
	}
}