package com.ibm.biginsights.ambari;

import org.json.JSONException;
import org.json.JSONObject;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Response;

public class AddService {
	
	public static String AddServiceToCluster(String cluster_url, String service, String username, String password) {
	// register service to cluster defined by cluster url
		
		String content = "{\"ServiceInfo\":{\"service_name\":\""+service+"\"}}";

		System.out.println("Add service: " + service);	
		
		Response response = RestAssured.given().keystore("data/certs/BigInsights.jks","passw0rd").header("X-Requested-By:", "ambari").
		auth().preemptive().basic(username, password).body(content).post(cluster_url+"/services");
		
		return response.getStatusLine();
	}

	public static String AddServiceComponent(String cluster_url, String service, String component, String username, String password) {
	//register component under service for a cluster defined by cluster_url

		System.out.println("Add component: "+component+" to service: "+service);	
		
		Response response = RestAssured.given().keystore("data/certs/BigInsights.jks","passw0rd").header("X-Requested-By:", "ambari").
		auth().preemptive().basic(username, password).post(cluster_url+"/services/"+service+"/components/"+component);
		
		return response.getStatusLine();
	}
	
	public static String AddServiceMapHost(String cluster_url, String component, String host, String username, String password) {
	// Map a component to a host on cluster defined by cluster url
		
		String content = "{\"host_components\" : [{\"HostRoles\":{\"component_name\":\""+component+"\"}}] }";

		System.out.println("\nMap component: "+component+" on host: "+ " "+host);

		Response response = RestAssured.given().keystore("data/certs/BigInsights.jks","passw0rd").header("X-Requested-By:", "ambari").
		auth().preemptive().basic(username, password).body(content).post(cluster_url+"/hosts?Hosts/host_name="+host);
		
		return response.getStatusLine();
	}
	
	public static String AddServiceCreateConfig(String cluster_url,String config, String username, String password) {
	// Create configuration info for service to be installed
	// example of configuration: {"type": "bigsql-users", "tag": "1", "properties" : { "bigsql_password" : "bigsql"}}
	
		Response response = RestAssured.given().keystore("data/certs/BigInsights.jks","passw0rd").header("X-Requested-By:", "ambari").
		auth().preemptive().basic(username, password).body(config).post(cluster_url+"/configurations/");
		
		if(!response.getStatusLine().contains("HTTP/1.1 201 Created")){
			System.out.println(response.asString());
		}
		return response.getStatusLine();
	}
	
	public static String AddServiceApplyConfig(String cluster_url,String config, String username, String password) {
    // Apply configuration to cluster
    // example of configuration: { "Clusters" : {"desired_configs": {"type": "bigsql-users", "tag" : "1" }}}
		
		Response response = RestAssured.given().keystore("data/certs/BigInsights.jks","passw0rd").header("X-Requested-By:", "ambari").
		auth().preemptive().basic(username, password).body(config).put(cluster_url);
		
		if(!response.getStatusLine().contains("HTTP/1.1 200 OK")){
			System.out.println(response.asString());
		}
		return response.getStatusLine();
	}
	
	public static String AddServiceInstall(String cluster_url, String service, String username, String password) {
	// Start install of service, return href request. href item can be used with CheckRequest
		
		String href = null;
	    System.out.println("Install service: "+service);
	    
	    String content = "{\"RequestInfo\": {\"context\" :\"Install "+service+" service\"}, \"Body\": {\"ServiceInfo\": {\"state\" : \"INSTALLED\"}}}";
	    
	    Response install_response = RestAssured.given().keystore("data/certs/BigInsights.jks","passw0rd").header("X-Requested-By:", "ambari").
		auth().preemptive().basic(username, password).body(content).put(cluster_url+"/services/"+service);
	    
		System.out.println("Install service response was "+install_response.getStatusLine());
	    
	    try {
			JSONObject jObject = new JSONObject(install_response.getBody().asString());
			href = jObject.getString("href");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
		return href;
	}
	
	public static String AddServiceDelete(String cluster_url,String service,String username,String password) {
	// Delete service from Ambari. Needs to be stopped prior to delete.
		
		Response response = RestAssured.given().keystore("data/certs/BigInsights.jks","passw0rd").header("X-Requested-By:", "ambari").
				auth().preemptive().basic(username, password).delete(cluster_url+"/services/"+service);
		
		return response.getStatusLine();

	}
}
