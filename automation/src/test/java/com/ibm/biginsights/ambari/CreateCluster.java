package com.ibm.biginsights.ambari;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.json.JSONException;
import org.json.JSONObject;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.ibm.biginsights.maven.Common;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Response;

public class CreateCluster {
	
	String url;
	
	@Test
	@Parameters({"ambarihost", "username", "password", "masternodelist", "slavenodelist"})
	public void createCluster(String ambarihost,  String username, String password, String masternodelist, String slavenodelist) throws IOException, JSONException{
		
		String ambariclustername;
		ambarihost = Common.assignPOMValues("BIambarihost", ambarihost);
		username = Common.assignPOMValues("BIusername", username);
		password = Common.assignPOMValues("BIpassword", password);
		masternodelist = Common.assignPOMValues("BImasternodelist", masternodelist);
		slavenodelist = Common.assignPOMValues("BIslavenodelist", slavenodelist);
		
		List<String> masterList = Arrays.asList(masternodelist.split("\\s*,\\s*"));
		List<String> slaveList = Arrays.asList(slavenodelist.split("\\s*,\\s*"));
		
		ambariclustername = AmbariUtil.getGenClustername(slavenodelist, masterList, slaveList);
		
		Path path = Paths.get("data/ambari/hostmapping.json");
		Charset charset = StandardCharsets.UTF_8;

		String content = new String(Files.readAllBytes(path), charset);
		
		Response response = RestAssured.given().keystore("data/certs/BigInsights.jks","passw0rd").header("X-Requested-By:", "ambari").auth().preemptive().basic(username, password).body(content).post(AmbariUtil.getAmbariHostURL(ambarihost)+"/api/v1/clusters/"+ambariclustername);
		
		String body = response.asString();
		String status = response.getStatusLine();
		String nl = System.lineSeparator();
		
		System.out.println(body + nl);
		
		System.out.println(status + nl);
		
		if(status.contains("202")){
			System.out.println("Cluster "+ambariclustername+" install started!");
		} else {
			System.out.println("ERROR: Cound not start cluster "+ambariclustername+" install!");
		}
		
		Assert.assertEquals("HTTP/1.1 202 Accepted", status);
		
		JSONObject obj = new JSONObject(body);
		url = (String) obj.get("href");
		
	}
	
	@Test(dependsOnMethods = { "createCluster" })
	@Parameters({"username", "password"})
	public void getInstallStatus(String username, String password) throws InterruptedException, JSONException {
		
		username = Common.assignPOMValues("BIusername", username);
		password = Common.assignPOMValues("BIpassword", password);
		
		String response = RestAssured.given().keystore("data/certs/BigInsights.jks","passw0rd").header("X-Requested-By:", "ambari").auth().preemptive().basic(username, password).get(url).asString();
		String nl = System.lineSeparator();
		
		while(true){
			if(response.contains("\"request_status\" : \"COMPLETED\",")){
				System.out.println(response + nl);
				System.out.println("HDP Install was successful!");
				Assert.assertTrue(true);
				break;
			} else if (response.contains("\"request_status\" : \"FAILED\",")){
				System.out.println(response + nl);
				System.out.println("HDP Install failed!");
				Assert.assertTrue(false);
				break;
			} else if (response.contains("\"request_status\" : \"ABORTED\",")){
				System.out.println(response + nl);
				System.out.println("HDP Install Aborted!");
				Assert.assertTrue(false);
				break;
			}
			Thread.sleep(5000);
			response = RestAssured.given().keystore("data/certs/BigInsights.jks","passw0rd").header("X-Requested-By:", "ambari").auth().preemptive().basic(username, password).get(url).asString();
			JSONObject jsonObj = new JSONObject(response);
			JSONObject requests = jsonObj.getJSONObject("Requests");
			Double percent = Double.parseDouble(requests.getString("progress_percent"));
			DecimalFormat df = new DecimalFormat("0.##");
			System.out.println(df.format(percent)+"%");
		}
	}
	
}
