package com.ibm.biginsights.ambari;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.ibm.biginsights.maven.Common;

public class HostMapping {

	@Test
	@Parameters({"ambarihost", "username", "password", "masternodelist", "slavenodelist", "authtype"})
	public void hostMapping(String ambarihost, String username, String password, String masternodelist, String slavenodelist, String authtype) throws JSONException{
		
		String blueprint;
		ambarihost = Common.assignPOMValues("BIambarihost", ambarihost);
		username = Common.assignPOMValues("BIusername", username);
		password = Common.assignPOMValues("BIpassword", password);
		masternodelist = Common.assignPOMValues("BImasternodelist", masternodelist);
		slavenodelist = Common.assignPOMValues("BIslavenodelist", slavenodelist);
		authtype = Common.assignPOMValues("BIauthtype", authtype);
		
		List<String> masterList = Arrays.asList(masternodelist.split("\\s*,\\s*"));
		List<String> slaveList = Arrays.asList(slavenodelist.split("\\s*,\\s*"));
		
		if(masterList.size() > 3){
			System.out.println("ERROR: Master node list cannot be greater than 3.");
			Assert.assertTrue(false, "Master node list cannot be greater than 3.");
		}
		if(masternodelist.isEmpty()){
			System.out.println("ERROR: Master node list cannot be empty.");
			Assert.assertTrue(false, "Master node list cannot be empty.");
		}
		blueprint = AmbariUtil.getGenBlueprintName(slavenodelist, masterList);
		
		JSONObject object = new JSONObject();
		JSONArray configArray = new JSONArray();
		
		if(authtype.equalsIgnoreCase("LDAP")){	
			JSONObject clusterEnv = new JSONObject();
			JSONObject groupUsers = new JSONObject();
			groupUsers.put("ignore_groupsusers_create", "true");
			clusterEnv.put("cluster-env", groupUsers);
			configArray.put(0,clusterEnv);
			
		}
		
		if(configArray.length() != 0){
			object.put("configurations", configArray);
		}
		
		object.put("blueprint", blueprint);
		object.put("default_password", "passw0rd");
		
		JSONArray hostGroup = new JSONArray();
		
		
		for(int i = 0; i < masterList.size(); i++){
			JSONObject groupObject = new JSONObject();
			groupObject.put("name", "master_group_"+(i+1));
			JSONArray hostArray = new JSONArray();
			JSONObject hostObject = new JSONObject();
			hostObject.put("fqdn", masterList.get(i));
			hostArray.put(hostObject);
			groupObject.put("hosts", hostArray);
			hostGroup.put(groupObject);
		}
		
		if(!slavenodelist.isEmpty()){
		JSONObject groupObject = new JSONObject();
		groupObject.put("name", "slave_group");
		JSONArray hostArray = new JSONArray();
		
		
		for(int i = 0; i < slaveList.size(); i++){
			
			JSONObject hostObject = new JSONObject();
			hostObject.put("fqdn", slaveList.get(i));
			hostArray.put(i, hostObject);
			
		}
		groupObject.put("hosts", hostArray);
		hostGroup.put(groupObject);
		}
		object.put("host_groups", hostGroup);
		
		try {
			 
			FileWriter file = new FileWriter("data/ambari/hostmapping.json");
			file.write(object.toString());
			file.flush();
			file.close();
	 
		} catch (IOException e) {
			e.printStackTrace();
		}
		String nl = System.lineSeparator();
		System.out.println("Host mapping JSON file created:" + nl);
		System.out.print(object);
		System.out.println(nl);
				
	}
	
}
