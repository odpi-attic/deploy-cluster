package com.ibm.biginsights.ambari;

import java.io.IOException;

import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.testng.Assert;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.ibm.biginsights.maven.Common;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Response;

/* 
 * Delete Host involves the following steps
 * 1. Stop all services
 * 2. Stop and delete all components
 * 3. Delete the host
 * To prevent data loss, nodes need to be decommissioned. In ambari decommissioning is a manual process and this test case will not handle decommissioning
 */
public class DeleteHost {
	
	@Test
	@Parameters({"ambarihost", "username", "password", "hostname"})
	public static void deleteHost(String ambarihost, String username, String password, String hostname) throws IOException{
		
		/*ambariclustername = Common.assignPOMValues("BIambariclustername", ambariclustername);*/
		ambarihost =	Common.assignPOMValues("BIambarihost", ambarihost);
		/*ambariport = Common.assignPOMValues("BIambariport", ambariport);*/
		username = Common.assignPOMValues("BIusername", username);
		password = Common.assignPOMValues("BIpassword", password);		
		hostname = Common.assignPOMValues("BInewhost", hostname);
		
		try
		{
			DeleteAllComponents.deleteComponents(ambarihost, username, password, hostname);
		} 
		catch (JSONException e)
		{
			System.out.println("Unable to delete all the components in the host");
			e.printStackTrace();
		} 
		catch (InterruptedException e)
		{
			System.out.println("Unable to delete all the components in the host");
			e.printStackTrace();
		}
		Response response = RestAssured.given().keystore("data/certs/BigInsights.jks","passw0rd").header("X-Requested-By:", "ambari").auth().preemptive().basic(username, password).delete(AmbariUtil.getAmbariClusterURL(ambarihost, username, password)+"/hosts/"+hostname);
		System.out.println("Response"+response.asString());
				
		if(HttpStatus.SC_OK != response.getStatusCode())
		{
			System.out.println(response.getStatusLine());
			Assert.fail();
		}		
		System.out.println(response.getStatusLine());
		System.out.println("Host Deleted");	
	}

}

