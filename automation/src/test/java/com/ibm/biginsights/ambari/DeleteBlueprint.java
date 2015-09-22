package com.ibm.biginsights.ambari;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.ibm.biginsights.maven.Common;
import com.jayway.restassured.RestAssured;

public class DeleteBlueprint {
	
	@Test
	@Parameters({"ambarihost", "username", "password", "masternodelist", "slavenodelist"})
	public void deleteBlueprint(String ambarihost, String username, String password, String masternodelist, String slavenodelist) throws IOException{
		
		String blueprint;
		ambarihost = Common.assignPOMValues("BIambarihost", ambarihost);
		username = Common.assignPOMValues("BIusername", username);
		password = Common.assignPOMValues("BIpassword", password);
		masternodelist = Common.assignPOMValues("BImasternodelist", masternodelist);
		slavenodelist = Common.assignPOMValues("BIslavenodelist", slavenodelist);
		
		List<String> masterList = Arrays.asList(masternodelist.split("\\s*,\\s*"));
		
		blueprint = AmbariUtil.getGenBlueprintName(slavenodelist, masterList);
		
		String response = RestAssured.given().keystore("data/certs/BigInsights.jks","passw0rd").header("X-Requested-By:", "ambari").auth().preemptive().basic(username, password).delete(AmbariUtil.getAmbariHostURL(ambarihost)+"/api/v1/blueprints/"+blueprint).getStatusLine();
		
		String nl = System.lineSeparator();
		System.out.println(response + nl);
		
		if(response.contains("200")){
			System.out.println("Blueprint "+blueprint+" deleted!");
		} else {
			System.out.println("ERROR: Cound not delete blueprint "+blueprint+"!");
		}
		Assert.assertEquals("HTTP/1.1 200 OK", response);
	}

}
