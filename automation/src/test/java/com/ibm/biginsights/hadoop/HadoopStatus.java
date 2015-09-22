package com.ibm.biginsights.hadoop;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.ibm.biginsights.ambari.AmbariUtil;
import com.ibm.biginsights.maven.Common;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Header;

public class HadoopStatus {
	
	Header header = new Header("X-Requested-By:", "ambari");
	String ambarihost;
	String username;
	String password;
	String[] hostList = null;
	String clusterURL;
	
	@BeforeClass
	@Parameters({"ambarihost", "username", "password"})
	public void setup(String _ambarihost, String _username, String _password) throws JSONException {
		
		ambarihost = Common.assignPOMValues("BIambarihost", _ambarihost);
		username = Common.assignPOMValues("BIusername", _username);
		password = Common.assignPOMValues("BIpassword", _password);
		clusterURL = AmbariUtil.getAmbariClusterURL(ambarihost, username, password);
		hostList = AmbariUtil.getClusterHosts(clusterURL, username, password);
		
	}
	
	@Test
	public void nameNodeCheck() throws JSONException, InterruptedException {
		int timeout = 0;
		
		for(int i = 0; i < hostList.length ; i++ ){	
			JSONObject host_components = new JSONObject(RestAssured.given().keystore("data/certs/BigInsights.jks","passw0rd").header("X-Requested-By:", "ambari").auth().preemptive().basic(username, password).get(clusterURL+"/hosts/"+hostList[i]+"/host_components").asString());
			JSONArray items = host_components.getJSONArray("items");
			for(int j = 0; j < items.length() ; j++ ){
				if(items.getJSONObject(j).has("href")){
					String componentURL = (String) items.getJSONObject(j).get("href");
					if(componentURL.contains("/NAMENODE")){
						JSONObject component = new JSONObject(RestAssured.given().keystore("data/certs/BigInsights.jks","passw0rd").header("X-Requested-By:", "ambari").auth().preemptive().basic(username, password).get(componentURL).asString());						
						String state = Common.readJsonObj(component, "HostRoles", "state");
						System.out.println("Component NAMENODE on "+hostList[i]+" state was: "+state);
						if (state.equals("UNKNOWN")){
							while(timeout != 60){
								timeout = timeout + 1;
								Thread.sleep(5000);
								component = new JSONObject(RestAssured.given().keystore("data/certs/BigInsights.jks","passw0rd").header("X-Requested-By:", "ambari").auth().preemptive().basic(username, password).get(componentURL).asString());
								state = Common.readJsonObj(component, "HostRoles", "state");
								System.out.println("Component NAMENODE on "+hostList[i]+" state was: "+state);
								if (state.equals("STARTED")){
									Assert.assertEquals(state, "STARTED");
									break;
								}
							}
						}else{
							Assert.assertEquals(state, "STARTED");
							
						}
											
					}
				}
			}
		}
		if(timeout == 60){
			Assert.assertFalse(true, "Timeout of 5 minutes reached.");
		}
		
	}
	
	@Test
	public void secondaryNameNodeCheck() throws JSONException, InterruptedException {
		int timeout = 0;
		for(int i = 0; i < hostList.length ; i++ ){	
			JSONObject host_components = new JSONObject(RestAssured.given().keystore("data/certs/BigInsights.jks","passw0rd").header("X-Requested-By:", "ambari").auth().preemptive().basic(username, password).get(clusterURL+"/hosts/"+hostList[i]+"/host_components").asString());
			JSONArray items = host_components.getJSONArray("items");
			for(int j = 0; j < items.length() ; j++ ){
				if(items.getJSONObject(j).has("href")){
					String componentURL = (String) items.getJSONObject(j).get("href");
					if(componentURL.contains("/SECONDARY_NAMENODE")){
						JSONObject component = new JSONObject(RestAssured.given().keystore("data/certs/BigInsights.jks","passw0rd").header("X-Requested-By:", "ambari").auth().preemptive().basic(username, password).get(componentURL).asString());						
						String state = Common.readJsonObj(component, "HostRoles", "state");
						System.out.println("Component SECONDARY_NAMENODE on "+hostList[i]+" state was: "+state);
						if (state.equals("UNKNOWN")){
							while(timeout != 60){
								timeout = timeout + 1;
								Thread.sleep(5000);
								component = new JSONObject(RestAssured.given().keystore("data/certs/BigInsights.jks","passw0rd").header("X-Requested-By:", "ambari").auth().preemptive().basic(username, password).get(componentURL).asString());
								state = Common.readJsonObj(component, "HostRoles", "state");
								System.out.println("Component SECONDARY_NAMENODE on "+hostList[i]+" state was: "+state);
								if (state.equals("STARTED")){
									Assert.assertEquals(state, "STARTED");
									break;
								}
							}
						}else{
							Assert.assertEquals(state, "STARTED");
							break;
						}			
					}
				}
			}
		}
		if(timeout == 60){
			Assert.assertFalse(true, "Timeout of 5 minutes reached.");
		}
	}
	
	@Test
	public void dataNodeCheck() throws JSONException, InterruptedException {
		int timeout = 0;
		for(int i = 0; i < hostList.length ; i++ ){	
			JSONObject host_components = new JSONObject(RestAssured.given().keystore("data/certs/BigInsights.jks","passw0rd").header("X-Requested-By:", "ambari").auth().preemptive().basic(username, password).get(clusterURL+"/hosts/"+hostList[i]+"/host_components").asString());
			JSONArray items = host_components.getJSONArray("items");
			for(int j = 0; j < items.length() ; j++ ){
				if(items.getJSONObject(j).has("href")){
					String componentURL = (String) items.getJSONObject(j).get("href");
					if(componentURL.contains("/DATANODE")){
						JSONObject component = new JSONObject(RestAssured.given().keystore("data/certs/BigInsights.jks","passw0rd").header("X-Requested-By:", "ambari").auth().preemptive().basic(username, password).get(componentURL).asString());						
						String state = Common.readJsonObj(component, "HostRoles", "state");
						System.out.println("Component DATANODE on "+hostList[i]+" state was: "+state);
						if (state.equals("UNKNOWN")){
							while(timeout != 60){
								timeout = timeout + 1;
								Thread.sleep(5000);
								component = new JSONObject(RestAssured.given().keystore("data/certs/BigInsights.jks","passw0rd").header("X-Requested-By:", "ambari").auth().preemptive().basic(username, password).get(componentURL).asString());
								state = Common.readJsonObj(component, "HostRoles", "state");
								System.out.println("Component DATANODE on "+hostList[i]+" state was: "+state);
								if (state.equals("STARTED")){
									Assert.assertEquals(state, "STARTED");									
									break;
								}
							}
						}else{
							Assert.assertEquals(state, "STARTED");
							break;
						}					
					}
				}
			}
		}
		if(timeout == 60){
			Assert.assertFalse(true, "Timeout of 5 minutes reached.");
		}
	}
	
	@Test
	public void hdfsClientCheck() throws JSONException {
		
		for(int i = 0; i < hostList.length ; i++ ){	
			JSONObject host_components = new JSONObject(RestAssured.given().keystore("data/certs/BigInsights.jks","passw0rd").header("X-Requested-By:", "ambari").auth().preemptive().basic(username, password).get(clusterURL+"/hosts/"+hostList[i]+"/host_components").asString());
			JSONArray items = host_components.getJSONArray("items");
			for(int j = 0; j < items.length() ; j++ ){
				if(items.getJSONObject(j).has("href")){
					String componentURL = (String) items.getJSONObject(j).get("href");
					if(componentURL.contains("/HDFS_CLIENT")){
						JSONObject component = new JSONObject(RestAssured.given().keystore("data/certs/BigInsights.jks","passw0rd").header("X-Requested-By:", "ambari").auth().preemptive().basic(username, password).get(componentURL).asString());						
						String state = Common.readJsonObj(component, "HostRoles", "state");
						System.out.println("Component HDFS_CLIENT on "+hostList[i]+" state was: "+state);
						Assert.assertEquals(state, "INSTALLED");					
					}
				}
			}
		}
		
	}
	

}
