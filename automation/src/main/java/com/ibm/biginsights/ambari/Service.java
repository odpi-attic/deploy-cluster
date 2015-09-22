package com.ibm.biginsights.ambari;

import java.util.concurrent.TimeUnit;

import junit.framework.Assert;

import org.json.JSONException;
import org.json.JSONObject;

import com.ibm.biginsights.maven.Common;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.path.json.JsonPath;

import com.jayway.restassured.response.Header;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.response.ResponseBody;

public class Service {
	static Header header= new Header("X-Requested-By:", "ambari");
    static String response = null;
    static String content=null;
    
    
	
	public static void startService(String ambarihost,String username, String password,
			String service
			
			) throws InterruptedException, JSONException {
		
		ambarihost = Common.assignPOMValues("BIambarihost", ambarihost);
		username = Common.assignPOMValues("BIusername", username);
		password = Common.assignPOMValues("BIpassword", password);
		service = Common.assignPOMValues("BIservice", service);
		

     	/*
     	 * Start a given service
     	 */
     	
		content = "{\"RequestInfo\":{\"context\":\"Start Service\"},\"Body\":{\"ServiceInfo\":{\"state\":\"STARTED\"}}}";
		do{
			response = RestAssured.given().keystore("data/certs/BigInsights.jks","passw0rd").auth().preemptive().
  				basic(username, password).header(header).body(content).
  				put(AmbariUtil.getAmbariClusterURL(ambarihost, username, password)+"/services/"+service).getStatusLine();
			System.out.println("\nAttempting to start  "+service+" .. "+response);
		}while(response.contains("500"));	
		if(response.contains("200")|| response.contains("202")){
        	Assert.assertTrue("Service started", true);
        }else{
        	Assert.assertFalse("Service not started", true);
        }
		
		/*
		 * Check if the service was started,
		 * get status until a time out
		 */
		
		String status = null;
		long start = System.currentTimeMillis();
		long end = start + 10*60*1000;
		do {
			Response r = RestAssured.given().keystore("data/certs/BigInsights.jks","passw0rd").auth().preemptive().
  				basic(username, password).header(header).get(AmbariUtil.getAmbariClusterURL(ambarihost, username, password)+"/services/"+service);
			
			JSONObject jObject = new JSONObject(r.getBody().asString());
			status = Common.readJsonObj(jObject, "ServiceInfo", "state");
			System.out.println("Status of "+service+": "+status);
			Thread.sleep(30000);
			System.out.println("Time elapsed is: "+String.format("%d min, %d sec", 
				    TimeUnit.MILLISECONDS.toMinutes((System.currentTimeMillis() - start)),
				    TimeUnit.MILLISECONDS.toSeconds((System.currentTimeMillis() - start)) - 
				    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((System.currentTimeMillis() - start)))
				));	
		}while((!status.contains("STARTED"))&&(System.currentTimeMillis() < end));
		//TODO Need to compensate for the different states.
		if(System.currentTimeMillis() > end){
        	System.out.println("ERROR: Timeout of 5 minutes reached. Service could not be started.");
        	Assert.assertFalse("Could not start service.", true);
        }
	}
	

public static void stopService(String ambarihost,String username, String password, String service
	) throws InterruptedException, JSONException {

	ambarihost = Common.assignPOMValues("BIambarihost", ambarihost);
	username = Common.assignPOMValues("BIusername", username);
	password = Common.assignPOMValues("BIpassword", password);
	service = Common.assignPOMValues("BIservice", service);
	
/*
 * Stop a given service
 */
	content = "{\"RequestInfo\":{\"context\":\"Stop Service\"},\"Body\":{\"ServiceInfo\":{\"state\":\"INSTALLED\"}}}";
	response = RestAssured.given().keystore("data/certs/BigInsights.jks","passw0rd").auth().preemptive().
			basic(username, password).header(header).body(content).
			put(AmbariUtil.getAmbariClusterURL(ambarihost, username, password)+"/services/"+service).getStatusLine();
	System.out.println("\nStopping "+service+" .. "+response);
	if(response.contains("200")|| response.contains("202")){
		Assert.assertTrue("Service stopped", true);
	}else{
		Assert.assertFalse("Service stopped", true);
	}
	/*
	 * 	Check if the service was stopped,
	 * get status until a time out
	 */
	
	String status = null;
	long start = System.currentTimeMillis();
	long end = start + 5*60*1000;
	
	do {
		
		Response r = RestAssured.given().keystore("data/certs/BigInsights.jks","passw0rd").auth().preemptive().
			basic(username, password).header(header).get(AmbariUtil.getAmbariClusterURL(ambarihost, username, password)+"/services/"+service);
	
		JSONObject jObject = new JSONObject(r.getBody().asString());
		status = Common.readJsonObj(jObject, "ServiceInfo", "state");
		System.out.println("status of "+service+".. "+status);
		Thread.sleep(4000);

		System.out.println("Time elapsed is .."+(System.currentTimeMillis() - start));
	}while((!status.contains("INSTALLED"))||(System.currentTimeMillis() < end));
	//TODO Need to compensate for the different states.
	if(System.currentTimeMillis() < end){
		System.out.println("ERROR: Timeout of 300 seconds reached. Service could not be stopped.");
		Assert.assertFalse("Could not stop service.", true);
	}

}

public static int healthChk(String ambarihost,String username, String password,
		String service, String servicename) throws InterruptedException, JSONException {
	ResponseBody response = null;
     String content=null;
	
	
	int failedCnt = 0;
	String jobCompleted = null;
    
    
	
	System.out.println("\nRunning service check for "+service+".. ");
	ambarihost = Common.assignPOMValues("BIambarihost", ambarihost);
	username = Common.assignPOMValues("BIusername", username);
	password = Common.assignPOMValues("BIpassword", password);
	service = Common.assignPOMValues("BIservice", service);

		content = "{\"RequestInfo\" :{\"context\":\""+service +" SERVICE CHECK\",\"command\":\""+servicename+"\" }," +
			" \"Requests/resource_filters\": [{\"service_name\":\""+service+"\" }]}";	  
 		response = RestAssured.given().keystore("data/certs/BigInsights.jks","passw0rd").auth().preemptive().
				basic(username, password).header(header).body(content).
				post(AmbariUtil.getAmbariClusterURL(ambarihost, username, password)+"/requests").getBody();//getStatusLine();
 	
	if(response==null){
		Assert.assertFalse("Service check failed.", true);
	}
		JsonPath responseId = response.jsonPath();
		String path = responseId.getString("href");
		System.out.println("\nRunning service check(healthcheck) for.. "+service+" with response "+path);
		failedCnt = AmbariUtil.CheckRequest(username,password,path,300000);
		Thread.sleep(24000);
	
		if(failedCnt!=0){
			System.out.println("Healthcheck "+ jobCompleted + " with failed count of "+ failedCnt);
			Assert.assertFalse("Service check failed.", true);
		
		}
		return failedCnt;
	}


	public static void maintOn(String ambarihost, String username, String password, String component
	        ) throws InterruptedException, JSONException {

	                ambarihost = Common.assignPOMValues("BIambarihost", ambarihost);
	                username = Common.assignPOMValues("BIusername", username);
	                password = Common.assignPOMValues("BIpassword", password);
	                String mycluster = AmbariUtil.getAmbariClusterURL(ambarihost,username,password);
	                String[] component_host = AmbariUtil.getComponentHosts(mycluster,component,username,password);


	                String content = "{\"RequestInfo\":{\"context\":\"Turn on Maintenance mode\"},\"Body\":{\"HostRoles\":{\"maintenance_state\":\"ON\"}}}";
	                Response response = RestAssured.given().keystore("data/certs/BigInsights.jks","passw0rd").auth().preemptive().
	                         basic(username, password).header(header).body(content).
	                         put(mycluster+"/hosts/"+component_host[0]+"/host_components/"+component);

	                System.out.println("\nAttempting to start maintenance mode for "+component+" .. "+response.getStatusLine());

	                if(response.getStatusLine().contains("200")|| response.getStatusLine().contains("202")){
	                        Assert.assertTrue("Service in maintenance mode", true);
	                }else{
	                        Assert.assertFalse("Service not in maintenance mode", true);
	                }

	}
}
