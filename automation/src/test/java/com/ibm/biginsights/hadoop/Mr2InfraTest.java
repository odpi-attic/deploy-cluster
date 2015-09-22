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
public class Mr2InfraTest {

	Header header= new Header("X-Requested-By:", "ambari");
    String response = null;
    String content=null;
    private static String ahost;
 	/*private static String aport;*/
 	private static  String user;
 	private static String pw ;
 	/*private static  String clustername ;*/
 	private static String service = "MAPREDUCE2";
 	private static String state = null;
 	private static JSONObject services = null;
 	private static JSONObject allMetrics = null;
 	
 	
    @BeforeClass
	@Parameters({"ambarihost", "ambariport","username", "password","ambariclustername"
		})
	public void mr2InfraSetup(String ambarihost, String ambariport,String username, String password,
			String ambariclustername		
			) {
		
    	ahost= Common.assignPOMValues("BIambarihost", ambarihost);		
		/*aport=Common.assignPOMValues("BIambariport", ambariport);*/
		user=Common.assignPOMValues("BIusername", username);
		pw=Common.assignPOMValues("BIpassword", password);
		/*clustername=Common.assignPOMValues("BIambariclustername", ambariclustername);	*/
	
     	System.out.println("\n==============This test checks status of MR2 and it's components===========");
     	System.out.println("============Displays the Alerts and some of metrics associated with it============== ") ; 
	}     
    
		
     	@Test	
    	public void mr2Status() {
     	services = GetServiceInfo.details(ahost,user, pw,service);
    
     	//get service status
     	System.out.println("\nChecking the status of MAPREDUCE2..");
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
     	
     	//get service maintenance status
     	System.out.println("\nChecking the maintenance state of MAPREDUCE2..");
     	System.out.println("Maintenance state is expected to be turned off..");
     	try {
			status = GetComponentState.maintState( (JSONObject) services.get("1"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			Assert.assertFalse(true);
			e.printStackTrace();
		}
     	System.out.println(service+" maintenance state was: "+status);
     	if(status.contains("OFF")){
     		Assert.assertTrue("Status was expected to be OFF",true);
     	}else{
     		Assert.assertFalse("Status was expected to be OFF",true);
     	}  
     	/*
     	System.out.println("\nDisplaying the alerts associated with MAPREDUCE2..");
     	
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
				Assert.assertFalse("Alerts were not found",true);
			}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
 	*/
     	}
     	
     	
     	@Test	
    	public void mr2Metrics() {
		try{	
			// get MAPREDUCE2_CLIENT state		
			System.out.println("\nChecking the status of MAPREDUCE2_CLIENT..");
	     	allMetrics = GetComponentMetrics.AllMetrics(ahost, user, pw, service, "MAPREDUCE2_CLIENT");     	
	     	System.out.println("Status is expected to be installed..");
			state = Common.readJsonObj(allMetrics, "ServiceComponentInfo", "state");
			System.out.println("MAPREDUCE2_CLIENT state is: "+state);
			if(state.contains("INSTALLED")){
	     		Assert.assertTrue("Status was expected to be installed",true);
	     	}else{
	     		Assert.assertFalse("Status was expected to be installed",true);
	     	}
			System.out.println("Number of MAPREDUCE2_CLIENT installed: "+Common.readJsonObj(allMetrics, "ServiceComponentInfo", "installed_count"));
			// get HISTORYSERVER state			
			System.out.println("\nChecking the status of HISTORYSERVER..");
			System.out.println("Status is expected to be started..");
	     	allMetrics = GetComponentMetrics.AllMetrics(ahost, user, pw, service, "HISTORYSERVER");     	

			state = Common.readJsonObj(allMetrics, "ServiceComponentInfo", "state");
			System.out.println("HISTORYSERVER state is: "+state);
			if(state.contains("STARTED")){
	     		Assert.assertTrue("Status was expected to be started",true);
	     	}else{
	     		Assert.assertFalse("Status was expected to be started",true);
	     	}
			
			System.out.println("Number of History servers started: "+Common.readJsonObj(allMetrics, "ServiceComponentInfo","started_count"));
			
			
		}catch(JSONException e) {	
			e.printStackTrace();
			Assert.assertFalse("MR2 test failed",true);
		}
    
	}
     	@Test	
    	public void mr2Healthcheck()throws InterruptedException, JSONException {

			RunServiceCheck.healthChk(ahost, user, pw, service);
     	}
    	/*
public boolean extractYarnQueueMetrics(JSONObject allMetrics, String metricName){
	String value = null;
	boolean found = false;	
	try{	
		JSONObject obj1= null;
		JSONObject jObject = Common.returnJsonObj(allMetrics, "metrics");
		if(jObject!=null){
			obj1 = jObject.getJSONObject("yarn").getJSONObject("Queue").
				getJSONObject("root");
		}else{
			Assert.assertFalse("Metric was supposed to return a value",true);
		}
		if(obj1.has(metricName)){
			value = obj1.getString(metricName);
			
			System.out.println(metricName+": "+value);
			found = true;			
			int res = Integer.parseInt(value);
			if(res>=0){
				Assert.assertTrue(true);
			}	
	     	
		}else{
			found = false;
     		Assert.assertFalse("Metric was supposed to return a value",true);
	     	
		}
	}catch(JSONException e) {	
		e.printStackTrace();
		Assert.assertFalse("MR2 metric "+ metricName+"not found",true);
	}
	return found;
}
public boolean extractYarnClusterMetrics(JSONObject allMetrics, String metricName){
	String value = null;
	boolean found = false;	
	try{	
		
		
		JSONObject jObject = Common.returnJsonObj(allMetrics, "metrics");
		JSONObject obj1 = null;
		if(jObject!=null){
		 obj1  = jObject.getJSONObject("yarn").getJSONObject("ClusterMetrics");
		}else{
			Assert.assertFalse("Metric was supposed to return a value",true);
		}		
		if(obj1.has(metricName)){
			value = obj1.getString(metricName);
			
			System.out.println(metricName+": "+value);
			found = true;
			int res = Integer.parseInt(value);
			if(res>=0){
				Assert.assertTrue(true);
			}	
		}else{
			found = false;
			Assert.assertFalse("Metric was supposed to return a value",true);
		}
	}catch(JSONException e) {	
		e.printStackTrace();
		Assert.assertFalse("MR2 metric "+ metricName+"not found",true);
		
	}
	return found;
}  */ 	
 }
