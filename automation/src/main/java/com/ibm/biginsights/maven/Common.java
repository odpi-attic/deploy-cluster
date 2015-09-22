package com.ibm.biginsights.maven;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class Common {

	/**
	 * @info: Assign pom values from pom.xml file
	 * @param POMProperty
	 * @param POMValue
	 */
	public static String assignPOMValues(String POMProperty, String POMValue) {

		if (System.getProperty(POMProperty) != null) {
			POMValue=System.getProperty(POMProperty);
		}
		return POMValue;
	}
	public static List<String> setSlaveNodes(String slave_nodes){
		if(System.getProperty("BIslavehost") != null){
			String systemNodes = System.getProperty("BIslavehost");
			List<String> nodes = Arrays.asList(systemNodes.split("\\s*,\\s*"));
			return nodes;
		}else{
			List<String> nodes = Arrays.asList(slave_nodes.split("\\s*,\\s*"));
			return nodes;
		}
	}
	public static List<String> setClientNodes(String client_nodes){
		if(System.getProperty("BIclienthost") != null){
			System.out.println("not null");
			String systemNodes = System.getProperty("BIclienthost");
			List<String> nodes = Arrays.asList(systemNodes.split("\\s*,\\s*"));
			return nodes;
		}else{
			System.out.println("null");
			List<String> nodes = Arrays.asList(client_nodes.split("\\s*,\\s*"));
			return nodes;
		}
	}
	public static List<String> setMasterNodes(String master_nodes){
		if(System.getProperty("BImasterhost") != null){
			String systemNodes = System.getProperty("BImasterhost");
			List<String> nodes = Arrays.asList(systemNodes.split("\\s*,\\s*"));
			return nodes;
		}else{
			List<String> nodes = Arrays.asList(master_nodes.split("\\s*,\\s*"));
			return nodes;
		}

	}

	/*
	   * Pass the JSON response "rsp" to be parsed
	   * Pass the array "jAry" and search string thats needed "key"
	   * example: if "state" is the key, jAry=ServiceInfo
	   *
	   * "href" : "http://hdtest102.svl.ibm.com:8080/api/v1/clusters/cluster1m2d/services/NAGIOS",
  "ServiceInfo" : {
	"cluster_name" : "cluster1m2d",
	"maintenance_state" : "OFF",
	"service_name" : "NAGIOS",
	"state" : "STARTED"
  },
  "alerts" : {
	"detail" : [
	  {
		"description" : "Nagios status log freshness",
		"host_name" : "hdtest103.svl.ibm.com",
		"last_status" : "OK",
		"last_status_time" : 1423785091,
		"service_name" : "NAGIOS",
		"status" : "OK",
		"status_time" : 1424202481,
		"output" : "NAGIOS OK: 8 processes, status log updated 0 seconds ago",
		"actual_status" : "OK"
	  }
	   *
	   */
	public static String readJsonObj(JSONObject jObject, String jAry, String key) throws JSONException{
		JSONObject info = null;
		String result = null;

		if(jObject.has(jAry)){

			info = jObject.getJSONObject(jAry);
			if(info.has(key)){
				result = info.getString(key);
			}
		}
		return result;

	}

	public static JSONObject returnJsonObj(JSONObject jObject, String jAry) throws JSONException{
		JSONObject info = null;
		if(jObject.has(jAry)){
			info = jObject.getJSONObject(jAry);
		}
		return info;

	}

	/**
	 * Read JSON from file into JSONObject
	 * @param filePath path to JSON file (relative to location in classpath)
	 * @return JSONObject with content from JSON file
	 */
	public static JSONObject JSONObjectFromFile(String filePath) {
		JSONObject jsonObject = null;
		
			Path fullPath = Paths.get(filePath);
			String jsonContent;
			try {
				jsonContent = new String(Files.readAllBytes(fullPath), StandardCharsets.UTF_8);
				jsonObject = new JSONObject(jsonContent);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		
		return jsonObject;
	}
	
	public static String readFile(String path, Charset encoding) 
  		  throws IOException 
  	{
  	  byte[] encoded = Files.readAllBytes(Paths.get(path));
  	  return new String(encoded, encoding);
  	}

}
