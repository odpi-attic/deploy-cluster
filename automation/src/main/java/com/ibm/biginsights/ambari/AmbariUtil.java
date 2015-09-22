package com.ibm.biginsights.ambari;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ibm.biginsights.maven.Common;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Header;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.response.ResponseBody;

public class AmbariUtil {
	
	public static String getClustername(String ambarihost, String username, String password) {
		
		String cluster_name = null;
		Response response = RestAssured.given().keystore("data/certs/BigInsights.jks","passw0rd").header("X-Requested-By:", "ambari").auth().preemptive().basic(username, password).get(AmbariUtil.getAmbariHostURL(ambarihost)+"/api/v1/clusters");
		
		try {
			JSONObject jObject = new JSONObject(response.getBody().asString());
			JSONArray items = jObject.getJSONArray("items");
			JSONObject clusters = (JSONObject) items.get(0);
			JSONObject clustername = clusters.getJSONObject("Clusters");
			cluster_name = clustername.getString("cluster_name");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return cluster_name;

	}
	
	public static String[] getAmbariHosts(String ambarihost, String username, String password) {
		
		JSONObject hosts;
		String[] hostList = null;
		String response = RestAssured.given().keystore("data/certs/BigInsights.jks","passw0rd").header("X-Requested-By:", "ambari").auth().preemptive().basic(username, password).get(AmbariUtil.getAmbariHostURL(ambarihost)+"/api/v1/hosts").asString();
					
		try {
		hosts = new JSONObject(response);
		
		JSONArray items = hosts.getJSONArray("items");
		hostList = new String[items.length()];  
		for(int i = 0; i < items.length() ; i++ ){
			if(items.getJSONObject(i).has("Hosts")){
				JSONObject host = items.getJSONObject(i);
				JSONObject hostObj = host.getJSONObject("Hosts");
				hostList[i] = hostObj.getString("host_name");
			}
		}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return hostList;

	}
	
	public static String[] getClusterHosts(String clusterURL, String username, String password) {
		
		JSONObject hosts;
		String[] hostList = null;
		String response = RestAssured.given().keystore("data/certs/BigInsights.jks","passw0rd").header("X-Requested-By:", "ambari").auth().preemptive().basic(username, password).get(clusterURL+"/hosts").asString();
					
		try {
		hosts = new JSONObject(response);
		
		JSONArray items = hosts.getJSONArray("items");
		hostList = new String[items.length()];  
		for(int i = 0; i < items.length() ; i++ ){
			if(items.getJSONObject(i).has("Hosts")){
				JSONObject host = items.getJSONObject(i);
				JSONObject hostObj = host.getJSONObject("Hosts");
				hostList[i] = hostObj.getString("host_name");
			}
		}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return hostList;

	}
	
	public static String getGenBlueprintName(String slavenodelist, List<String> masterList){
		String blueprint;
		if(slavenodelist.isEmpty() && masterList.size() == 1){
			blueprint = "blueprint1m";
		}else if (masterList.size() == 1){
			blueprint = "blueprint1mXd";
		}else if (masterList.size() == 2){
			blueprint = "blueprint2mXd";
		}else{
			blueprint = "blueprint3mXd";
		}
		return blueprint;	
	}
	
	public static String getGenClustername(String slavenodelist, List<String> masterList, List<String> slaveList){
		String ambariclustername;
		if(slavenodelist.isEmpty() && masterList.size() == 1){
			ambariclustername = "cluster1m";
		}else if (masterList.size() == 1){
			ambariclustername = "cluster1m"+slaveList.size()+"d";
		}else if (masterList.size() == 2){
			ambariclustername = "cluster2m"+slaveList.size()+"d";
		}else{
			ambariclustername = "cluster3m"+slaveList.size()+"d";
		}
		return ambariclustername;
	}	
	
	public static String getServiceStatus(String ambarihost,String username,String password) throws JSONException, InterruptedException{
		Header header= new Header("X-Requested-By:", "ambari");
		Response r = RestAssured.given().keystore("data/certs/BigInsights.jks","passw0rd").auth().preemptive().
				basic(username, password).header(header).get(AmbariUtil.getAmbariClusterURL(ambarihost, username, password)+"/services/NAGIOS");
		
		JSONObject jObject = null;
		jObject = new JSONObject(r.getBody().asString());
		String status = null;
		status = Common.readJsonObj(jObject, "ServiceInfo", "state");
		System.out.println("status of Nagios "+status);
		Thread.sleep(4000);
		
		return status;

	}
	
	/*Pass the username, password and the
	 * href path of the REST call submitted.
	 * 
	 * returns the failed count.
	 * Success => 0 failed count
	 */
public static int CheckRequest(String username, String password, String path, long timeout ){

	int res = 0;
	Header header= new Header("X-Requested-By:", "ambari");
	String jobCompleted = null;
	String failedCnt= null;
	String progress = null;
	long start = System.currentTimeMillis();
	long end = start + timeout;

	do{
		//Test the result of a REST call
		ResponseBody result = RestAssured.given().keystore("data/certs/BigInsights.jks","passw0rd").auth().preemptive().basic(username, password).header(header).get(path).getBody();
		Map<Object, Object> jObj = result.jsonPath().getMap("Requests");

		if(jObj!=null){
			failedCnt = jObj.get("failed_task_count").toString();
			jobCompleted = (String) jObj.get("request_status").toString();
			if(failedCnt.equalsIgnoreCase("0") && jobCompleted.equalsIgnoreCase("COMPLETED")){
				System.out.println("\nJob status: "+ jobCompleted + " with failed count: "+ failedCnt);
				Assert.assertTrue("Request check succeeded.", true);
				break;
			}
		}

		//Show current progress
		progress = (String) jObj.get("progress_percent").toString();
		System.out.println("\nProgress of the current job: "+progress);
		System.out.println("Time elapsed: "+String.format("%d min, %d sec", 
			    TimeUnit.MILLISECONDS.toMinutes((System.currentTimeMillis() - start)),
			    TimeUnit.MILLISECONDS.toSeconds((System.currentTimeMillis() - start)) - 
			    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((System.currentTimeMillis() - start)))
			));
		System.out.println("Status: "+jobCompleted);

		//Sleep for 45 seconds until next check
		try {
			Thread.sleep(45000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}while(!jobCompleted.equalsIgnoreCase("COMPLETED") && !jobCompleted.equalsIgnoreCase("FAILED") && (System.currentTimeMillis() < end));
	
	if(System.currentTimeMillis() > end){
		System.out.println("Timeout of "+TimeUnit.MILLISECONDS.toMinutes(timeout)+" minutes reached!");
		Assert.assertFalse("Timeout of "+TimeUnit.MILLISECONDS.toMinutes(timeout)+" minutes reached!", true);
		return 1;
	}else{
		res = Integer.parseInt(failedCnt);
		return res;
	}
	
} 

public static Properties getAmbariProperties(){
	Properties prop = new Properties();
	InputStream input = null;
	String fis;
	File f = new File("/etc/ambari-server/conf/ambari.properties");
	if(f.exists() && !f.isDirectory()) { 
		System.out.println("Ambari properties file found.");
		fis = "/etc/ambari-server/conf/ambari.properties";
	}else{
		System.out.println("Could NOT find Ambari properties file. Using default data/ambari/ambari.properties.");
		fis = "data/ambari/ambari.properties";
	}
	try {
 
		input = new FileInputStream(fis);
 
		// load a properties file
		prop.load(input);
 
	} catch (IOException ex) {
		ex.printStackTrace();
	} finally {
		if (input != null) {
			try {
				input.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	return prop;
}

public static String getClientAPIPort(Properties prop){
	if(prop.getProperty("client.api.port") != null){
		return prop.getProperty("client.api.port");
	}else{
		return "8080";
	}
}

public static String getAmbariHostURL(String ambarihost){
	Properties prop = AmbariUtil.getAmbariProperties();
	if(prop.getProperty("api.ssl") != null && prop.getProperty("api.ssl").equals("true")){
		return "https://"+ambarihost+":"+prop.getProperty("client.api.ssl.port");
	}else{
		return "http://"+ambarihost+":"+AmbariUtil.getClientAPIPort(prop);
	}
	
}

public static String getAmbariClusterURL(String ambarihost, String username, String password) {
	
	String href = null;
	Response response = RestAssured.given().keystore("data/certs/BigInsights.jks","passw0rd").header("X-Requested-By:", "ambari").auth().preemptive().basic(username, password).get(AmbariUtil.getAmbariHostURL(ambarihost)+"/api/v1/clusters");
	
	try {
		JSONObject jObject = new JSONObject(response.getBody().asString());
		JSONArray items = jObject.getJSONArray("items");
		JSONObject clusters = (JSONObject) items.get(0);
		href =  clusters.getString("href");
	} catch (JSONException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}

	return href;
}

public static String[] getComponentHosts(String clusterURL, String component, String username, String password) {
	// TODO Auto-generated method stub
	Response response = RestAssured.given().keystore("data/certs/BigInsights.jks","passw0rd").header("X-Requested-By:", "ambari").auth().preemptive().basic(username, password).get(clusterURL+"/hosts?host_components/HostRoles/component_name="+component);
	String[] hosts = null;
	try {
		JSONObject host_roles = new JSONObject(response.asString());
		JSONArray items = host_roles.getJSONArray("items");
		hosts = new String[items.length()];
		for(int i = 0; i < items.length() ; i++ ){
			if(items.getJSONObject(i).has("Hosts")){
			hosts[i] = Common.readJsonObj(items.getJSONObject(i), "Hosts", "host_name");				
			}
		}
		
	} catch (JSONException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	return hosts;
	
}

public static String[] getComponents(String clusterURL, String username, String password) {

	String[] components = null;
	Response response = RestAssured.given().keystore("data/certs/BigInsights.jks","passw0rd").header("X-Requested-By:", "ambari").auth().preemptive().basic(username, password).get(clusterURL+"/components");
	try {
		JSONObject host_roles = new JSONObject(response.asString());
		JSONArray items = host_roles.getJSONArray("items");
		components = new String[items.length()];
		for(int i = 0; i < items.length() ; i++ ){
			if(items.getJSONObject(i).has("ServiceComponentInfo")){
				components[i] = Common.readJsonObj(items.getJSONObject(i), "ServiceComponentInfo", "component_name");				
			}
		}
		
	} catch (JSONException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return components;
}

public static Hashtable<String, String> getComponentsHosts(String clusterURL, String username, String password){
	
	Hashtable<String, String> componentHosts =  new Hashtable<String, String>();
	String[] components = AmbariUtil.getComponents(clusterURL, username, password);
	for (String s: components) {        
		String [] hosts = AmbariUtil.getComponentHosts(clusterURL, s, username, password);
		componentHosts.put(s+"_HOSTS", StringUtils.join(hosts, ','));
	}
	return componentHosts;
	
}

}
