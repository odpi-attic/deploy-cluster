package com.ibm.biginsights.ambari;


import junit.framework.Assert;

import org.json.JSONException;
//import org.json.JSONObject;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;



import com.ibm.biginsights.maven.Common;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Header;
//import com.jayway.restassured.response.Response;

/*
 * @author,Swetha
 */
public class DeleteClientorService {

	Header header= new Header("X-Requested-By:", "ambari");
    String response = null;
    String content=null;
 
    
	@Test
	@Parameters({"ambarihost","username", "password",
		
		"service"})
	public void uninstallService(String ambarihost, String username, String password,
			String service) throws InterruptedException, JSONException {
		
		ambarihost=Common.assignPOMValues("BIambarihost", ambarihost);
		/*ambariport=Common.assignPOMValues("BIambariport", ambariport);*/
		username=Common.assignPOMValues("BIusername", username);
		password=Common.assignPOMValues("BIpassword", password);
		/*ambariclustername=Common.assignPOMValues("BIambariclustername", ambariclustername);*/
		
		Common.assignPOMValues("BIservice", service);
     	
		content = "{\"RequestInfo\":{\"context\":\"Stop Service\"},\"Body\":{\"ServiceInfo\":{\"state\":\"INSTALLED\"}}}";	  
		response = RestAssured.given().keystore("data/certs/BigInsights.jks","passw0rd").auth().preemptive().
  				basic(username, password).header(header).body(content).
  				put(AmbariUtil.getAmbariClusterURL(ambarihost, username, password)+"/services/"+service).getStatusLine();
			System.out.println("\nStopped "+service+response);
			//Thread.sleep(2000);
			 			//Check for 202 response
	        System.out.println("Stop service response was "+response);    
	        if(response.contains("200")|| response.contains("202")){
	        	Assert.assertTrue("Service stopped", true);
	        }else{
	        	Assert.assertFalse("Service stopped", true);
	        }
	     	/*
	     	 * wait until the service is stopped, re-try delete call  
	     	 */
	        do{
	    		response = RestAssured.given().keystore("data/certs/BigInsights.jks","passw0rd").auth().preemptive().
	    				basic(username, password).header(header).
	    				delete(AmbariUtil.getAmbariClusterURL(ambarihost, username, password)+"/services/"+service).getStatusLine();
	    		System.out.println(response);

	            try {
	                    Thread.sleep(3000); 
	            } catch (InterruptedException e) {}
	        }while(response.contains("500"));
	        System.out.println("\nDeleted "+service+response);
	        
	        /*
	         * Restart nagios and ganglia
	         */
			StopService.stopService(ambarihost,username,password,"NAGIOS");
			StopService.stopService(ambarihost,username,password,"GANGLIA");
			
			StartService.startService(ambarihost,username,password,"NAGIOS");
			StartService.startService(ambarihost,username,password,"GANGLIA");
	       
	}
}
