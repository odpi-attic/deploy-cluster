package com.ibm.biginsights.ambari;

import java.text.DecimalFormat;

import junit.framework.Assert;

import org.json.JSONException;
import org.json.JSONObject;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.ibm.biginsights.maven.Common;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Header;
import com.jayway.restassured.response.Response;

public class StopAllServices {
	
	Header header = new Header("X-Requested-By:", "ambari");
	String content  =  "{\"RequestInfo\":{\"context\":\"Stop Service\"},\"Body\":{\"ServiceInfo\":{\"state\":\"INSTALLED\"}}}";
	
	@Test
	@Parameters({"ambarihost", "username", "password"})
	public void stopAllServices(String ambarihost, String username, String password) throws JSONException, InterruptedException {
		
		ambarihost = Common.assignPOMValues("BIambarihost", ambarihost);
		username = Common.assignPOMValues("BIusername", username);
		password = Common.assignPOMValues("BIpassword", password);
		
		String clustername = AmbariUtil.getClustername(ambarihost, username, password);
		String clusterURL = AmbariUtil.getAmbariClusterURL(ambarihost, username, password);
		String content = "{\"RequestInfo\":{\"context\":\"Stop all services\",\"operation_level\":{\"level\":\"CLUSTER\",\"cluster_name\":\""+clustername+"\"}},\"Body\":{\"ServiceInfo\":{\"state\":\"INSTALLED\"}}}";
		
		Response response = RestAssured.given().keystore("data/certs/BigInsights.jks","passw0rd").header(header).auth().preemptive().basic(username, password).body(content).put(clusterURL+"/services");
		
		String body = response.asString();
		String stopStatus = response.getStatusLine();
		String nl = System.lineSeparator();
		
		System.out.println(body + nl);
		
		System.out.println(stopStatus + nl);
		
		if(stopStatus.contains("202")){
			System.out.println("Stop all services request on "+clustername+" accepted!");
		} else {
			System.out.println("ERROR: Request to stop all services on "+clustername+" failed!");
		}
		
		Assert.assertEquals("HTTP/1.1 202 Accepted", stopStatus);
		
		JSONObject obj = new JSONObject(body);
		String url = (String) obj.get("href");
		
		String status = RestAssured.given().keystore("data/certs/BigInsights.jks","passw0rd").header("X-Requested-By:", "ambari").auth().preemptive().basic(username, password).get(url).asString();
		
		while(true){
			if(status.contains("\"request_status\" : \"COMPLETED\",")){
				System.out.println(status + nl);
				System.out.println("Start all services was successful!");
				Assert.assertTrue(true);
				break;
			} else if (status.contains("\"request_status\" : \"FAILED\",")){
				System.out.println(status + nl);
				System.out.println("Start all services failed!");
				Assert.assertTrue(false);
				break;
			} else if (status.contains("\"request_status\" : \"ABORTED\",")){
				System.out.println(status + nl);
				System.out.println("Start all services Aborted!");
				Assert.assertTrue(false);
				break;
			}
			Thread.sleep(5000);
			status = RestAssured.given().keystore("data/certs/BigInsights.jks","passw0rd").header("X-Requested-By:", "ambari").auth().preemptive().basic(username, password).get(url).asString();
			JSONObject jsonObj = new JSONObject(status);
			JSONObject requests = jsonObj.getJSONObject("Requests");
			Double percent = Double.parseDouble(requests.getString("progress_percent"));
			DecimalFormat df = new DecimalFormat("0.##");
			System.out.println(df.format(percent)+"%");
		}
				
	}
	
}
