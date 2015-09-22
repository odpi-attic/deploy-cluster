package com.ibm.biginsights.ambari;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.ibm.biginsights.maven.Common;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Response;

public class CreateBlueprint {
	
	@Test
	@Parameters({"ambarihost", "username", "password", "masternodelist", "slavenodelist","stackname"})
	public void createBlueprint(String ambarihost, String username, String password, String masternodelist, String slavenodelist, String stackname) throws IOException{
		
		String blueprint;
		ambarihost = Common.assignPOMValues("BIambarihost", ambarihost);
		username = Common.assignPOMValues("BIusername", username);
		password = Common.assignPOMValues("BIpassword", password);
		masternodelist = Common.assignPOMValues("BImasternodelist", masternodelist);
		slavenodelist = Common.assignPOMValues("BIslavenodelist", slavenodelist);
		stackname = Common.assignPOMValues("BIstackname", stackname);
		
		List<String> masterList = Arrays.asList(masternodelist.split("\\s*,\\s*"));
		
		blueprint = AmbariUtil.getGenBlueprintName(slavenodelist, masterList);
		
		Path path = Paths.get("data/ambari/"+stackname+"_"+blueprint+".json"); 
		//TODO This is a temporary fix for blueprint issue on ppc64le
		/*if (System.getProperty("BIosarch") != null && System.getProperty("BIosarch").contains("ppc64le")) {
			path = Paths.get("data/ambari/"+stackname+"_"+blueprint+"ppc64le.json");
			System.out.println("Using ppc64le blueprint.");
		}else{
			path = Paths.get("data/ambari/"+stackname+"_"+blueprint+".json");
		}*/
		
		Charset charset = StandardCharsets.UTF_8;

		String content = new String(Files.readAllBytes(path), charset);
		/*content = content.replaceAll("localhost", ambarihost);*/
		
		Response response = RestAssured.given().keystore("data/certs/BigInsights.jks","passw0rd").header("X-Requested-By:", "ambari").auth().preemptive().basic(username, password).body(content).post(AmbariUtil.getAmbariHostURL(ambarihost)+"/api/v1/blueprints/"+blueprint+"?validate_topology=false");
		
		String nl = System.lineSeparator();
		System.out.println(response.getStatusLine() + nl);
		
		if(response.getStatusLine().contains("201")){
			System.out.println("Blueprint "+blueprint+" created!");
		} else {
			System.out.println(response.asString() + nl);
			System.out.println("ERROR: Cound not create blueprint "+blueprint+"!");			
		}
		
		Assert.assertEquals("HTTP/1.1 201 Created", response.getStatusLine());
	}

}
