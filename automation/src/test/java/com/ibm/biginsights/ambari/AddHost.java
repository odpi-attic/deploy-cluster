package com.ibm.biginsights.ambari;

import java.util.ArrayList;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Header;
import com.jayway.restassured.response.Response;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ibm.biginsights.maven.Common;

/*
 * Adding new host to ambari cluster involves the following steps-
 * 1. Install ambari agent to the new host and edit the /etc/ambari-agent/conf/ambari-agent.ini file to register the host to ambari server - Done through Jenkins job Env-Register-Node
 * 2. Ensure the host has been registered through RestAPI call
 * 3. Add the registered host to the ambari cluster
 */
public class AddHost 
{   
	 private static Header header = new Header("X-Requested-By:", "ambari");
	 private static Response addResponse = null;
	 private static String ensureHostIsRegistered = null;
	 private static Response ensureHostIsAdded = null;
	 private static String newHost;
	 private static List<String> hostHref = new ArrayList<String>();
	 private static List<String> listOfHosts = new ArrayList<String>();
	 int hrefList = 0;

	 @Test
	 @Parameters({"ambariHost", "username", "password",
			"newhost"})
    public static void addHost(String ambarihost,
   		 String username, String password, String newhost) throws InterruptedException, JSONException
    {
   	 /*ambariClustername = Common.assignPOMValues("BIambariclustername", ambariClustername);*/
       ambarihost = Common.assignPOMValues("BIambarihost", ambarihost);		
   	 /*ambariPort = Common.assignPOMValues("BIambariport", ambariPort);*/
   	 username = Common.assignPOMValues("BIusername", username);
   	 newHost = Common.assignPOMValues("BInewhost", newhost);
   	 
   	 /*
   	 System.out.println(ambariClustername);
   	 System.out.println(ambariHost);
   	 System.out.println(ambariPort);
   	 System.out.println(ambariBaseUrl);
   	 System.out.println(username);
   	 System.out.println(password);*/
   	 System.out.println("New hosts to be added: "+newHost);
   	 
   	 if (newHost == null || newHost.equals("") || newHost.isEmpty())
   	 {
   		 System.out.println("Host to be added is null. Please enter atleast one host");
   		 Assert.assertFalse(true, "Host to be added is null. Please enter atleast one host");   		 
   	 }
   	 
   	 int hostNumber = 1;
   	 for (String retHostList: newhost.split("\\s"))
   	 {
   		 System.out.println("Host "+hostNumber+" : "+retHostList);
   		 listOfHosts.add(retHostList);
   		 hostNumber++;
   	 }
   	  
   	 System.out.println("Number of hosts to be added: "+listOfHosts.size());
   	 System.out.println("Checking if the hosts to be added are registered with the ambari server...");

   	 // Ensure the host has been registered
   	 ensureHostIsRegistered = RestAssured.given().keystore("data/certs/BigInsights.jks","passw0rd").header(header).auth().preemptive().basic(username, password).get(AmbariUtil.getAmbariHostURL(ambarihost) + "/api/v1/hosts/").asString();
   	 System.out.println("List of hosts registered with the ambari server "+ensureHostIsRegistered);   	   		   	 	   
   	 JSONObject items = new JSONObject(ensureHostIsRegistered.toString());
 		 JSONArray hosts = items.getJSONArray("items");
 		
	 	 for(int itemList = 0; itemList < hosts.length() ; itemList++ )
	 	 {
 			if(hosts.getJSONObject(itemList).has("href"))
 			{
 				String hostURL = (String) hosts.getJSONObject(itemList).get("href");
 				System.out.println("HostURL registered in Ambari Server >>>>> "+hostURL);
 				hostHref.add(hostURL);
 			}
 			else
 			{
 				System.out.println("Could not find href element in the response"); 
 				Assert.assertFalse(true, "Href element is not found in the response ");
 			}
	 			
		 }
	 	 
	 	int counter = 0;	
	 	int failureCount = 0;
	 
	 	for (int hostList = 0; hostList < listOfHosts.size(); hostList++)
	 	{
	 		String newHost = listOfHosts.get(hostList);
	 		System.out.println("New Host: "+newHost);
	 		
	 		for (int hrefList = 0; hrefList < hostHref.size(); hrefList++)
	 		{	 			
	 			if (hostHref.get(hrefList).contains(newHost))
	 			{
	 				counter++;
		 			addResponse = RestAssured.given().keystore("data/certs/BigInsights.jks","passw0rd").header(header).auth().preemptive().basic(username, password).
							post(AmbariUtil.getAmbariClusterURL(ambarihost, username, password) + "/hosts/" + newHost); 		 			
		   	 
					System.out.println("Response for new host added: "+ addResponse.getStatusLine());

					if((HttpStatus.SC_CREATED != addResponse.getStatusCode()) )
					{   			
						System.out.println("--------> Host "+newHost+" could not be added to the ambari cluster: "+ addResponse.getStatusLine());  
						failureCount++;						
						//Assert.assertFalse(true);						
					}
					else
					{
						Thread.sleep(300000);
						ensureHostIsAdded = RestAssured.given().keystore("data/certs/BigInsights.jks","passw0rd").header(header).auth().preemptive().basic(username, password).get(AmbariUtil.getAmbariClusterURL(ambarihost, username, password)+"/hosts/"+newHost);
						System.out.println("ensureHostIsAdded "+ensureHostIsAdded.getStatusLine());
						if((HttpStatus.SC_CREATED != ensureHostIsAdded.getStatusCode()) && (HttpStatus.SC_OK != ensureHostIsAdded.getStatusCode()))
						{
							System.out.println("--------> Host "+newHost+" was not added or added incorrectly: "+ensureHostIsAdded.getStatusLine());
							failureCount++;
							//Assert.assertFalse(true);
						}
						else
						{
							System.out.println("--------> Host "+newHost+" was added successfully: "+ensureHostIsAdded.getStatusLine());
							//System.out.println("Installing NAGIOS and GANGLIA_MONITOR>> Commented until the dev code for this is ready");
							//Install ganglia and nagios - commented until these are available					
						}
					}
					break;
	 			}	 			
			}
	 	}
 		 if(counter == 0)	 			 
 		 {	 		 				 							
 			System.out.println("The node is not registered properly with Ambari server");
 			Assert.assertFalse(true);				
 		 }
 		 
 		 if(failureCount == 0)
 		 {
 			 Assert.assertTrue(true, "All hosts were added to the cluster successfully");
 		 }
 		 else
 		 {
 			 Assert.assertFalse(true, "Failed to add all hosts successfully" );
 		 }
    }
}
