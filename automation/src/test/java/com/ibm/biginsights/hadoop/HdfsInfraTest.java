package com.ibm.biginsights.hadoop;




import junit.framework.Assert;

import org.json.JSONException;
import org.json.JSONObject;
import org.testng.annotations.BeforeClass;

import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.ibm.biginsights.ambari.RunServiceCheck;
import com.ibm.biginsights.infra.GetComponentMetrics;
import com.ibm.biginsights.infra.GetComponentState;
import com.ibm.biginsights.infra.GetServiceInfo;
import com.ibm.biginsights.maven.Common;
import com.jayway.restassured.response.Header;

/*
 * @author,Swetha
 */
public class HdfsInfraTest {

	Header header= new Header("X-Requested-By:", "ambari");
    String response = null;
    String content=null;
	String state = null;
 	JSONObject services = null;
 	JSONObject allMetrics = null;
 	private static String ahost;
 	/*private static String aport;*/
 	private static  String user;
 	private static String pw ;
 	/*private static  String clustername ;*/
 	private static String service = "HDFS";

	@BeforeClass
	@Parameters({"ambarihost", "ambariport","username", "password","ambariclustername"
		})
	public void hdfsInfraSetup(String ambarihost,String ambariport, String username, String password, String ambariclustername) {
		

		ahost= Common.assignPOMValues("BIambarihost", ambarihost);		
		/*aport=Common.assignPOMValues("BIambariport", ambariport);*/
		user=Common.assignPOMValues("BIusername", username);
		pw=Common.assignPOMValues("BIpassword", password);
		/*clustername=Common.assignPOMValues("BIambariclustername", ambariclustername);	*/	

		System.out.println("\n==============This test checks status of HDFS and it's components===========");
     	System.out.println("============Displays the Alerts and some of metrics associated with HDFS============== ") ; 
	}
	
	
	@Test
	public void hdfsStatus() {
     	services = GetServiceInfo.details(ahost,user, pw,service);
 
     	try {
			RunServiceCheck.healthChk(ahost,user,pw,service);
		} catch (InterruptedException e1) {
			Assert.assertFalse(true);
			e1.printStackTrace();
			
		} catch (JSONException e1) {
			Assert.assertFalse(true);
			e1.printStackTrace();
		}

     	//get service status
     	System.out.println("\nChecking the status of HDFS..");
     	System.out.println("Status is expected to be started..");
     	String status = null;
		try {
			status = GetComponentState.state( (JSONObject) services.get("1"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			Assert.assertFalse(true);
			e.printStackTrace();
		}
     	System.out.println(service+" state was: "+status);
     	if(status.contains("STARTED")){
     		Assert.assertTrue("Status was expected to be started",true);
     		
     	}else{
     		Assert.assertFalse("Status was expected to be started",true);
     	}
     	try {
			status = GetComponentState.maintState( (JSONObject) services.get("1"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			Assert.assertFalse(false);
			e.printStackTrace();
		}
     	System.out.println(service+" maintenance state was: "+status);
     	if(status.contains("OFF")){
     		Assert.assertTrue("Status was expected to be OFF",true);
     	}else{
     		Assert.assertFalse("Status was expected to be OFF",true);
     	}
     	/*
     	System.out.println("\nDisplaying the alerts associated with HDFS..");
     	System.out.println("\nAlerts are expected ..");
     	try {
			if(AmbariUtil.getServiceStatus(ahost, aport, user,  pw, clustername).contains("STARTED")){
				if(services.has("2")){
					try {
						GetServiceAlerts.displayAlerts((JSONObject) services.get("2"));
					} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Assert.assertFalse(true);
				}
					Assert.assertTrue(true);
				}else{
					Assert.assertFalse(true);
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
     	
	}
	
	@Test(dependsOnMethods = { "hdfsStatus" }, enabled=false)		
	public void hdfsMetrics()throws InterruptedException, JSONException {
		
		try{	
			//get metrics for SEC NAMENODE
			System.out.println("\nChecking the status of SECONDARY_NAMENODE..");
			System.out.println("Status is expected to be started..");
	     	allMetrics = GetComponentMetrics.AllMetrics(ahost,user, pw, service, "SECONDARY_NAMENODE");     	

			state = Common.readJsonObj(allMetrics, "ServiceComponentInfo", "state");
			if(state.contains("STARTED")){
	     		Assert.assertTrue("Status was expected to be started",true);
	     	}else{
	     		Assert.assertFalse("Status was expected to be started",true);
	     	}
			System.out.println("SECONDARY_NAMENODE state is: "+state);
			
			// get HDFS_CLIENT state			
			System.out.println("\nChecking the status of HDFS_CLIENT..");
			System.out.println("Status is expected to be installed..");
			allMetrics = GetComponentMetrics.AllMetrics(ahost, user, pw, service, "HDFS_CLIENT");     	

			state = Common.readJsonObj(allMetrics, "ServiceComponentInfo", "state");
			System.out.println("HDFS_CLIENT state is: "+state);
			if(state.contains("INSTALLED")){
				Assert.assertTrue("Status was expected to be started",true);
			}else{
				Assert.assertFalse("Status was expected to be started",true);
			}

			// get DATANODE state		
			System.out.println("\nChecking the status of DATANODE..");
	     	allMetrics = GetComponentMetrics.AllMetrics(ahost, user, pw, service, "DATANODE");     	
	     	System.out.println("Status is expected to be started..");
			state = Common.readJsonObj(allMetrics, "ServiceComponentInfo", "state");
			System.out.println("DATANODE state is: "+state);
			if(state.contains("STARTED")){
	     		Assert.assertTrue("Status was expected to be started",true);
	     	}else{
	     		Assert.assertFalse("Status was expected to be started",true);
	     	}
			
			System.out.println("\nChecking DATAMANAGER metrics..");
			System.out.println("DATAMANAGER metrics need to be a positive integer..");
			extractDfsMetrics(allMetrics, "blockReports_avg_time");
			extractDfsMetrics(allMetrics, "blocks_read");
			extractDfsMetrics(allMetrics, "heartBeats_avg_time");
			
			System.out.println("Number of data nodes started: "+Common.readJsonObj(allMetrics, "ServiceComponentInfo","started_count"));
			
			extractDfsDiskMetrics(allMetrics,"disk_free");
			extractDfsDiskMetrics(allMetrics,"disk_total");
			extractDfsDiskMetrics(allMetrics,"part_max_used");
			
					
			// get NAMENODE state			
			System.out.println("\nChecking the status of NAMENODE..");
			System.out.println("Status is expected to be STARTED..");
	     	allMetrics = GetComponentMetrics.AllMetrics(ahost, user, pw, service, "NAMENODE");     	

			state = Common.readJsonObj(allMetrics, "ServiceComponentInfo", "state");
			if(state.contains("STARTED")){
	     		Assert.assertTrue("Status was expected to be installed",true);
	     	}else{
	     		Assert.assertFalse("Status was expected to be installed",true);
	     	}
			System.out.println("NAMENODE state is: "+state);
			
			System.out.println("Some of the NAMENODE metrics below ");
			extractNNSvcMetrics(allMetrics,"HeapMemoryMax");
			extractNNSvcMetrics(allMetrics,"HeapMemoryUsed");
			extractNNSvcMetrics(allMetrics,"NonDfsUsedSpace");
			extractNNDfsMetrics(allMetrics,"FilesTotal");
			extractNNDfsMetrics(allMetrics,"BlocksTotal");
	     	
			

		}catch(JSONException e) {	
			e.printStackTrace();
			Assert.assertFalse(false);
		}
	}
	
	
private void extractNNSvcMetrics(JSONObject allMetrics, String metricName) {
		// TODO Auto-generated method stub
	String value = null;
	
	try{	
		JSONObject obj1 = Common.returnJsonObj(allMetrics, "ServiceComponentInfo");
		
				
		if(obj1.has(metricName)){
			value = obj1.getString(metricName);
			
			System.out.println("NameNode "+metricName+": "+value);
						
			/*int res = Integer.parseInt(value);
			if(res>=0){
				Assert.assertTrue(true);
			}*/	
	     	
		}else{
		
     		Assert.assertFalse("Metric was supposed to return a value",true);
	     	
		}
	}catch(JSONException e) {
		Assert.assertFalse(false);
		e.printStackTrace();
		
	}
	
		
	}

public boolean extractDfsMetrics(JSONObject allMetrics, String metricName){
	String value = null;
	boolean found = false;	
	try{	
		JSONObject obj1=null;
		JSONObject jObject = Common.returnJsonObj(allMetrics, "metrics");
		if(jObject!=null){
			obj1 = jObject.getJSONObject("dfs").getJSONObject("datanode");
		}else{
			Assert.assertFalse("Metric was supposed to return a value",true);
		}
		if(obj1.has(metricName)){
			value = obj1.getString(metricName);
			
			System.out.println("DFS "+metricName+": "+value);
			found = true;			
			/*int res = Integer.parseInt(value);
			if(res>=0){
				Assert.assertTrue(true);
			}*/	
	     	
		}else{
			found = false;
     		Assert.assertFalse("Metric was supposed to return a value",true);
	     	
		}
	}catch(JSONException e) {	
		e.printStackTrace();
		Assert.assertFalse("DataNode metric "+ metricName+"not found",true);
		
	}
	return found;
}

public void extractNNDfsMetrics(JSONObject allMetrics, String metricName){
	String value = null;
	
	try{	
		JSONObject obj1=null;
		JSONObject jObject = Common.returnJsonObj(allMetrics, "metrics");
		if(jObject!=null){
			obj1 = jObject.getJSONObject("dfs").getJSONObject("FSNamesystem");
		}else{
			Assert.assertFalse("Metric was supposed to return a value",true);
		}	
		if(obj1.has(metricName)){
			value = obj1.getString(metricName);
			
			System.out.println(metricName+": "+value);
					
			int res = Integer.parseInt(value);
			if(res>=0){
				Assert.assertTrue(true);
			}	
	     	
		}else{
			
     		Assert.assertFalse("Metric was supposed to return a value",true);
	     	
		}
	}catch(JSONException e) {	
		e.printStackTrace();
		Assert.assertFalse("Namenode metric "+ metricName+"not found",true);
	}
}	
public boolean extractDfsDiskMetrics(JSONObject allMetrics, String metricName){
	String value = null;
	boolean found = false;	
	try{	
		
		JSONObject obj1 = null;
		JSONObject jObject = Common.returnJsonObj(allMetrics, "metrics");
		if(jObject!=null){
			obj1 = jObject.getJSONObject("disk");
		}	
		else{
			Assert.assertFalse("Metric was supposed to return a value",true);
		}		
		if(obj1.has(metricName)){
			value = obj1.getString(metricName);
			
			System.out.println("DFS "+ metricName+": "+value);
			found = true;
			/*int res = Integer.parseInt(value);
			if(res>=0){
				Assert.assertTrue(true);
			}*/	
		}else{
			found = false;
			Assert.assertFalse("Metric was supposed to return a value",true);
		}
	}catch(JSONException e) {	
		Assert.assertFalse("Disc metric "+ metricName+"not found",true);
		e.printStackTrace();
		
	}
	return found;
}

 }
