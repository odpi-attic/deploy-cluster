package com.ibm.biginsights.zookeeper;

import org.json.JSONException;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.ibm.biginsights.ambari.StopService;
import com.ibm.biginsights.ambari.StartService;
import com.ibm.biginsights.ambari.RunServiceCheck;
//import com.ibm.biginsights.ambari.DeleteClientorService;
import com.ibm.biginsights.maven.Common;



public class ZookeeperTests 
{
	
	static String zookeeper_name = "ZOOKEEPER_QUORUM";
	static String hostname;
	static String username;
	static String password;
	static String service;
	@Test
	@Parameters({"hostname", "username", "password","service"})
	public static void stopZookeeperService  (
			                                   String host, 
			                                   String user, 
			                                   String pass,
	                                           String serv 
	                                           ) throws InterruptedException, JSONException 
	{
		hostname= Common.assignPOMValues("BIambarihost", host);		
		username=Common.assignPOMValues("BIusername", user);
		password=Common.assignPOMValues("BIpassword", pass);
		service=Common.assignPOMValues("BIservice", serv);
	    StopService.stopService(hostname, username, password, service);
			
	}
	
	@Test(dependsOnMethods = { "stopZookeeperService" })
	public static void startZookeeperService() throws InterruptedException, JSONException 
	{
		
		//Start the zookeeper service
		StartService.startService(hostname, username, password, service);
		
	}
	
	
	
	@Test(dependsOnMethods = { "stopZookeeperService", "startZookeeperService" })
	public static void healthCheckZooKeeper() throws InterruptedException, JSONException
	{
		//Sleep for a while to allow start service to complete
		
		Thread.sleep(2000);
		RunServiceCheck.healthChk(hostname, username, password, zookeeper_name);
		
    } 
	
	/*
	@Test
	@Parameters({"hostname", "ambariPort","username", "password","clusterName",
		"service"
		})
	public static void removeZooKeeperService (
			                                     String hostname,
			                                     String ambariPort,
			                                     String username,
			                                     String password,
			                                     String clusterName,
			                                     String service
			                                ) throws InterruptedException, JSONException
	{
		DeleteClientorService obj = new DeleteClientorService();
		obj.uninstallService(hostname, ambariPort, username, password, clusterName, service);
		
		
    } 
	*/
	
				           

}
