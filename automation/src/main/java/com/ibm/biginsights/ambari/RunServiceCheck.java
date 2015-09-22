package com.ibm.biginsights.ambari;





import junit.framework.Assert;

import org.json.JSONException;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Header;
import com.jayway.restassured.response.ResponseBody;


public class RunServiceCheck {

	static Header header= new Header("X-Requested-By:", "ambari");
    static ResponseBody response = null;
    static String content=null;
	static long startTime = 0;
	static long stopTime = 0;
	static String progress = null;
	static int failedCnt = 0;
	static String jobCompleted = null;
    static String path ;
    
	public static void healthChk(String ambarihost,String username, String password,
			String service) throws InterruptedException, JSONException {
		
		
		System.out.println("\nRunning service check for "+service+".. ");
     	if(!service.contains("ZOOKEEPER")){
     		content = "{\"RequestInfo\" :{\"context\":\""+service +" SERVICE CHECK\",\"command\":\""+service+"_SERVICE_CHECK\" }," +
				" \"Requests/resource_filters\": [{\"service_name\":\""+service+"\" }]}";	  
     		response = RestAssured.given().keystore("data/certs/BigInsights.jks","passw0rd").auth().preemptive().
  				basic(username, password).header(header).body(content).
  				post(AmbariUtil.getAmbariClusterURL(ambarihost, username, password)+"/requests").getBody();//getStatusLine();
     	}else{
     		content = "{\"RequestInfo\" :{\"context\":\""+service +" SERVICE CHECK\",\"command\":\"ZOOKEEPER_QUORUM_SERVICE_CHECK\" }," +
    				" \"Requests/resource_filters\": [{\"service_name\":\""+"ZOOKEEPER"+"\" }]}";	  
         		response = RestAssured.given().keystore("data/certs/BigInsights.jks","passw0rd").auth().preemptive().
      				basic(username, password).header(header).body(content).
      				post(AmbariUtil.getAmbariClusterURL(ambarihost, username, password)+"/requests").getBody();
     	}
		if(response==null){
			Assert.assertFalse("Service check failed.", true);
		}
			JsonPath responseId = response.jsonPath();
			String path = responseId.getString("href");
			System.out.println("\nRunning service check(healthcheck) for.. "+service+" with response "+path);
			failedCnt = AmbariUtil.CheckRequest(username,password,path,300000);
			Thread.sleep(5000);
		
			if(failedCnt!=0){
				System.out.println("Healthcheck "+ jobCompleted + " with failed count of "+ failedCnt);
				Assert.assertFalse("Service check failed.", true);
			
			}
		}


}
