package com.ibm.biginsights.ambari;



import junit.framework.Assert;

import org.json.JSONException;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.ibm.biginsights.maven.Common;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Header;
/*
 * @author,swetha
 */
public class AddComponent {

	static Header header= new Header("X-Requested-By:", "ambari");
    static String response = null;
    String content=null;
   
	@Test
	@Parameters({"ambarihost","username", "password",
		"clientservice","clientcomponent","clienthost"
		})
	public static void installComp(String ambarihost,String username, String password,
			String clientservice, String clientcomponent,
			String clienthost) throws InterruptedException, JSONException {
		
		ambarihost=Common.assignPOMValues("BIambarihost", ambarihost);
		username=Common.assignPOMValues("BIusername", username);
		password=Common.assignPOMValues("BIpassword", password);
		/*ambariclustername=Common.assignPOMValues("BIambariclustername", ambariclustername);*/		
		clientservice=Common.assignPOMValues("BIclientservice", clientservice);
		clientcomponent=Common.assignPOMValues("BIcomponent", clientcomponent);
		clienthost= Common.assignPOMValues("BIclienthost", clienthost);
		 /*ambariport=Common.assignPOMValues("BIambariport", ambariport);*/
		
    	System.out.println("Ambari Host: "+ambarihost);
     	/*System.out.println("Ambari port: "+ambariport);*/
     	System.out.println("Ambari user: "+username);
     	System.out.println("Ambari pw: "+password);
     	/*System.out.println("Cluster name: "+ambariclustername);*/
     	
     	

		/*
		 * Add master component	
		 * curl --user admin:admin -i -X POST http://AMBARI_SERVER_HOST:8080/api/v1/clusters/CLUSTER_NAME/hosts/NEW_HOST_ADDED/host_components/DATANODE
		 curl --user admin:admin -i -X POST http://AMBARI_SERVER_HOST:8080/api/v1/clusters/CLUSTER_NAME/hosts/NEW_HOST_ADDED/host_components/GANGLIA_MONITOR
		 */
		System.out.println("Adding component..");


		
		response = RestAssured.given().keystore("data/certs/BigInsights.jks","passw0rd").auth().preemptive().
				basic(username, password).header(header).
				post(AmbariUtil.getAmbariClusterURL(ambarihost, username, password)+
						"/hosts/"+clienthost+"/host_components/"+clientcomponent).getStatusLine();
		
		if(response.contains("404")){
			System.out.println("Add component response was not sucessful, host is not added "+response);
			Assert.assertFalse(true);
			
		}
		
		//Check for 201 response
		System.out.println("Add component response was "+response);
		Assert.assertEquals("HTTP/1.1 201 Created", response);
		

		/*commented until ganglia&nagios are available on ODP
		response = RestAssured.given().auth().preemptive().
				basic(username, password).header(header).
				post("http://"+ambarihost+":"+ambariport+"/api/v1/clusters/"+ambariclustername+
						"/hosts/"+clienthost+"/host_components/GANGLIA_MONITOR").getStatusLine();
		

		//Check for 201 response
		System.out.println("Add component response was "+response);
		Assert.assertEquals("HTTP/1.1 201 Created", response);*/
		

		//StartService.startService(ambarihost,ambariport,username,password,ambariclustername,clientservice);
		//commented until ganglia&nagios are available on ODP
		//StartService.startService(ahost,aport,user,pw,clustername,"GANGLIA_MONITOR");     	
		
		String content = new String("{\"HostRoles\": {\"state\" : \"INSTALLED\"}}");
		/*		response = RestAssured.given().auth().preemptive().
				basic(username, password).header(header).body(content).
				put("http://"+ambarihost+":"+ambariport+"/api/v1/clusters/"+ambariclustername+
						"/hosts/"+clienthost+"/host_components/GANGLIA_MONITOR").getStatusLine();

		//Check for 202 response
		System.out.println("Install response was "+response);
		Assert.assertEquals("HTTP/1.1 202 Accepted", response);
		*/
		
		response = RestAssured.given().keystore("data/certs/BigInsights.jks","passw0rd").auth().preemptive().
				basic(username, password).header(header).body(content).
				put(AmbariUtil.getAmbariClusterURL(ambarihost, username, password)+
						"/hosts/"+clienthost+"/host_components/"+clientcomponent).getStatusLine();
		
		//Check for 202 response
		System.out.println("Install client response was "+response);
		Assert.assertEquals("HTTP/1.1 202 Accepted", response);
		
		//Start components
		if(!clientcomponent.contains("CLIENT")){
			System.out.println("Starting components "+clientcomponent);
			content = new String("{\"HostRoles\": {\"state\" : \"STARTED\"}}");
			/*response = RestAssured.given().auth().preemptive().
				basic(username, password).header(header).body(content).
				put("http://"+ambarihost+":"+ambariport+"/api/v1/clusters/"+ambariclustername+
						"/hosts/"+clienthost+"/host_components/GANGLIA_MONITOR").getStatusLine();

			//Check for 202 response
			System.out.println("Install response was "+response);
			Assert.assertEquals("HTTP/1.1 202 Accepted", response);*/
		do{
			response = RestAssured.given().keystore("data/certs/BigInsights.jks","passw0rd").auth().preemptive().
				basic(username, password).header(header).body(content).
				put(AmbariUtil.getAmbariClusterURL(ambarihost, username, password)+
						"/hosts/"+clienthost+"/host_components/"+clientcomponent).getStatusLine();
		}while(response.contains("500"));	
		if(response.contains("200")|| response.contains("202")){
        	Assert.assertTrue("Service started", true);
        }else{
        	Assert.assertFalse("Service not started", true);
        }
			

		}		 
		/*
         * Restart nagios and ganglia
         
		StopService.stopService(ambarihost,ambariport,username,password,ambariclustername,"NAGIOS");
		StopService.stopService(ambarihost,ambariport,username,password,ambariclustername,"GANGLIA");
		
		StartService.startService(ambarihost,ambariport,username,password,ambariclustername,"NAGIOS");
		StartService.startService(ambarihost,ambariport,username,password,ambariclustername,"GANGLIA");
		*/
	}

}
