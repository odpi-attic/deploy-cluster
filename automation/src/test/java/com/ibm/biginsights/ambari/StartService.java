package com.ibm.biginsights.ambari;



import junit.framework.Assert;

import org.json.JSONException;
import org.json.JSONObject;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.ibm.biginsights.maven.Common;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Header;
import com.jayway.restassured.response.Response;

/*
 * @author,Swetha
 */
public class StartService {

	static Header header= new Header("X-Requested-By:", "ambari");
    static String response = null;
    static String content=null;
    
    
	@Test
	@Parameters({"hostname", "username", "password",
		"service"
		})
	public static void startService(String ambarihost,String username, String password,
			String service
			
			) throws InterruptedException, JSONException {
		
		ambarihost = Common.assignPOMValues("BIambarihost", ambarihost);
		
		/*ambariPort = Common.assignPOMValues("BIambariPort", ambariPort);*/
		username = Common.assignPOMValues("BIusername", username);
		password = Common.assignPOMValues("BIpassword", password);
		/*clusterName = Common.assignPOMValues("BIambariclustername", clusterName);*/
		service = Common.assignPOMValues("BIservice", service);
		

     	/*
     	 * Start a given service
     	 */
     	System.out.println("Ambari Host: "+ambarihost);
     	/*System.out.println("Ambari port: "+ambariPort);*/
     	System.out.println("Ambari user: "+username);
     	System.out.println("Ambari pw: "+password);
     	/*System.out.println("Cluster name: "+clusterName);*/
     	System.out.println("Service: "+service);
 
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
		int timeout = 0;
		do {

			Response r = RestAssured.given().keystore("data/certs/BigInsights.jks","passw0rd").auth().preemptive().
  				basic(username, password).header(header).get(AmbariUtil.getAmbariClusterURL(ambarihost, username, password)+"/services/"+service);
			
			JSONObject jObject = new JSONObject(r.getBody().asString());
			status = Common.readJsonObj(jObject, "ServiceInfo", "state");
			System.out.println("status of "+service+".. "+status);

			Thread.sleep(4000);
			timeout=timeout+4000;

		}while((!status.contains("STARTED"))&& (timeout!=300000));
		if(timeout == 300000){
			System.out.println("ERROR: Timeout of 5 minutes reached. Service could not be started.");
        	Assert.assertFalse("Could not start service.", true);
        }

		}
}
