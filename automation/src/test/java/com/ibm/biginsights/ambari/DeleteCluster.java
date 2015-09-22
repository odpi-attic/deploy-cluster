package com.ibm.biginsights.ambari;

import java.io.IOException;
import junit.framework.Assert;

import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.ibm.biginsights.maven.Common;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Response;

public class DeleteCluster {
	
	@Test
	@Parameters({"ambarihost", "username", "password", "masternodelist", "slavenodelist"})
	public void deleteCluster(String ambarihost, String username, String password, String masternodelist, String slavenodelist) throws IOException{
		
		String ambariclustername;
		ambarihost = Common.assignPOMValues("BIambarihost", ambarihost);
		username = Common.assignPOMValues("BIusername", username);
		password = Common.assignPOMValues("BIpassword", password);
		masternodelist = Common.assignPOMValues("BImasternodelist", masternodelist);
		slavenodelist = Common.assignPOMValues("BIslavenodelist", slavenodelist);
		
		ambariclustername = AmbariUtil.getClustername(ambarihost, username, password);
		
		Response response = RestAssured.given().keystore("data/certs/BigInsights.jks","passw0rd").header("X-Requested-By:", "ambari").auth().preemptive().basic(username, password).delete(AmbariUtil.getAmbariHostURL(ambarihost)+"/api/v1/clusters/"+ambariclustername);
		
		String body = response.asString();
		String status = response.getStatusLine();
		String nl = System.lineSeparator();
		
		System.out.println(body + nl);
		
		System.out.println(status + nl);
		
		if(status.contains("200")){
			System.out.println("Cluster "+ambariclustername+" deleted!");
			Assert.assertEquals("HTTP/1.1 200 OK", status);
		} else {
			Assert.assertEquals("HTTP/1.1 404 Not Found", status);
			System.out.println("ERROR: Cound not delete cluster "+ambariclustername+"!");
		}
		
	}

}
