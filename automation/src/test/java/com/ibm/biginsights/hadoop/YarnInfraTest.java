package com.ibm.biginsights.hadoop;




import junit.framework.Assert;

import org.json.JSONException;
import org.json.JSONObject;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/*import com.ibm.biginsights.ambari.AmbariUtil;*/
import com.ibm.biginsights.ambari.RunServiceCheck;
import com.ibm.biginsights.infra.GetComponentMetrics;
import com.ibm.biginsights.infra.GetComponentState;
/*import com.ibm.biginsights.infra.GetServiceAlerts;*/
import com.ibm.biginsights.infra.GetServiceInfo;
import com.ibm.biginsights.maven.Common;
import com.jayway.restassured.response.Header;

/*
 * @author,Swetha
 */
public class YarnInfraTest {

	Header header= new Header("X-Requested-By:", "ambari");
    String response ;
    String content;
	String state ;
	private static JSONObject services ;
	private static JSONObject allMetrics ;

    private static String ahost;
 	/*private static String aport;*/
 	private static  String user;
 	private static String pw ;
 	/*private static  String clustername ;*/
 	private static String service = "YARN";
 	
    @BeforeClass
	@Parameters({"ambarihost", "ambariport","username", "password","ambariclustername"
		})
	public void yarnInfraSetup(String ambarihost, String ambariport,String username, String password,
			String ambariclustername) throws InterruptedException, JSONException {
		
		ahost= Common.assignPOMValues("BIambarihost", ambarihost);		
		/*aport=Common.assignPOMValues("BIambariport", ambariport);*/
		user=Common.assignPOMValues("BIusername", username);
		pw=Common.assignPOMValues("BIpassword", password);
		/*clustername=Common.assignPOMValues("BIambariclustername", ambariclustername);	*/	

     	System.out.println("\n==============This test checks status of Yarn and it's components===========");
     	System.out.println("============Displays the Alerts and some of metrics associated with yarn============== ") ;        	
		
    }
    
    @Test	
    /*
     * Test Yarn status, maintenance mode
     */
	public void yarnStatus() throws InterruptedException{
    	
    	//Get serviceInfo
     	services = GetServiceInfo.details(ahost, user, pw, service);
    
     	//get service status
     	System.out.println("\nChecking the status of Yarn..");
     	System.out.println("Status is expected to be started..");
    
     	String status = null;
		try {
			status = GetComponentState.state((JSONObject)services.get("1"));
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
     	System.out.println("\nChecking the maintenance state of Yarn..");
     	System.out.println("Maintenance state is expected to be turned off..");
     	
     	try {
			status = GetComponentState.maintState( (JSONObject) services.get("1"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Assert.assertFalse(true);
		}
     	
     	System.out.println(service+" maintenance state was: "+status);
     	if(status.contains("OFF")){
     		Assert.assertTrue("Status was expected to be OFF",true);
     	}else{
     		Assert.assertFalse("Status was expected to be OFF",true);
     	}
     	
    /* 	
   	try {
   		if(AmbariUtil.getServiceStatus(ahost, aport, user,  pw, clustername).contains("STARTED")){
		if(services.has("2")){
			GetServiceAlerts.displayAlerts((JSONObject)services.get("2"));
		 	Assert.assertTrue(true);
		 }else{
		     	Assert.assertFalse("Alerts were not found",true);
		 }
   		}
	} catch (JSONException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		Assert.assertFalse(true);
	}*/
    }
    
    @Test	
	public void yarnHealthCheck()throws InterruptedException {
		try {
			RunServiceCheck.healthChk(ahost, user,  pw, service);
		} catch (JSONException e) {
			Assert.assertFalse("Yarn healthcheck failed",true);
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
    
    //@Test	
    /*
     * Test Yarn alerts
     
   	public void yarnAlerts()throws InterruptedException, JSONException {
     	
     	System.out.println("\nDisplaying the alerts associated with Yarn..");
     	System.out.println("\nAlerts are expected ..");
     	
     	if(GetServiceAlerts.displayAlerts((JSONObject)services.get("2"))==true){
     		Assert.assertTrue(true);
     	}else{
         	Assert.assertFalse("Alerts were not found",true);
        }
    } */	
	
    @Test(enabled=false)	
    /*
     * Test Yarn metrics
     */
   	public void yarnMetrics()throws InterruptedException, JSONException {
		try{	
			// get NODEMANAGER state		
			System.out.println("\nChecking the status of NODEMANAGER..");
	     	allMetrics = GetComponentMetrics.AllMetrics(ahost, user, pw, service, "NODEMANAGER");     	
	     	System.out.println("Status is expected to be started..");
			state = Common.readJsonObj(allMetrics, "ServiceComponentInfo", "state");
			System.out.println("NODEMANAGER state is: "+state);
			
			if(state.contains("STARTED")){
	     		Assert.assertTrue("Status was expected to be started",true);
	     	}else{
	     		Assert.assertFalse("Status was expected to be started",true);
	     	}
			
			// get APP_TIMELINE_SERVER state			
			System.out.println("\nChecking the status of APP_TIMELINE_SERVER..");
			System.out.println("Status is expected to be started..");
	     	allMetrics = GetComponentMetrics.AllMetrics(ahost, user, pw, service, "APP_TIMELINE_SERVER");     	

			state = Common.readJsonObj(allMetrics, "ServiceComponentInfo", "state");
			System.out.println("APP_TIMELINE_SERVER state is: "+state);
			if(state.contains("STARTED")){
	     		Assert.assertTrue("Status was expected to be started",true);
	     	}else{
	     		Assert.assertFalse("Status was expected to be started",true);
	     	}
			
			// get YARN_CLIENT state			
			System.out.println("\nChecking the status of YARN_CLIENT..");
			System.out.println("Status is expected to be INSTALLED..");
	     	allMetrics = GetComponentMetrics.AllMetrics(ahost, user, pw, service, "YARN_CLIENT");     	

			state = Common.readJsonObj(allMetrics, "ServiceComponentInfo", "state");
			if(state.contains("INSTALLED")){
	     		Assert.assertTrue("Status was expected to be installed",true);
	     	}else{
	     		Assert.assertFalse("Status was expected to be installed",true);
	     	}
			System.out.println("YARN_CLIENT state is: "+state);

	     	//get metrics for RESOURCEMANAGER
			System.out.println("\nChecking the status of RESOURCEMANAGER..");
			System.out.println("Status is expected to be started..");
	     	allMetrics = GetComponentMetrics.AllMetrics(ahost, user, pw, service, "RESOURCEMANAGER");     	

			state = Common.readJsonObj(allMetrics, "ServiceComponentInfo", "state");
			if(state.contains("STARTED")){
	     		Assert.assertTrue("Status was expected to be started",true);
	     	}else{
	     		Assert.assertFalse("Status was expected to be started",true);
	     	}
			System.out.println("RESOURCEMANAGER state is: "+state);
			System.out.println("\nChecking RESOURCEMANAGER metrics..");
			System.out.println("RESOURCEMANAGER metrics need to be a positive integer..");
			
			extractYarnQueueMetrics(allMetrics, "AppsFailed");
			extractYarnQueueMetrics(allMetrics, "AppsKilled");
			extractYarnQueueMetrics(allMetrics, "AppsRunning");
			extractYarnQueueMetrics(allMetrics, "AppsSubmitted");
			extractYarnQueueMetrics(allMetrics, "PendingContainers");
			extractYarnQueueMetrics(allMetrics, "AllocatedContainers");
			extractYarnQueueMetrics(allMetrics, "ReservedContainers");
			extractYarnQueueMetrics(allMetrics, "AllocatedMB");
			extractYarnQueueMetrics(allMetrics, "AvailableVCores");
			
			//Node manager metrics
			System.out.println("\nChecking NODEMANAGER status metrics.. ");
			System.out.println("NODEMANAGER metrics need to be a positive integer..");
			extractYarnClusterMetrics(allMetrics, "NumActiveNMs");
			extractYarnClusterMetrics(allMetrics, "NumDecommissionedNMs");
			extractYarnClusterMetrics(allMetrics, "NumUnhealthyNMs");
			
		}catch(JSONException e) {	
			Assert.assertFalse("Metrics not found",true);
			e.printStackTrace();
			
		}
    
	}

	
public boolean extractYarnQueueMetrics(JSONObject allMetrics, String metricName){
	String value = null;
	boolean found = false;	
	try{	
		JSONObject jObject = Common.returnJsonObj(allMetrics, "metrics");
		JSONObject obj1 = null;
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
		Assert.assertFalse("Yarn metric "+ metricName+"not found",true);
		e.printStackTrace();
		
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
			obj1 = jObject.getJSONObject("yarn").getJSONObject("ClusterMetrics");
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
		Assert.assertFalse("Yarn metric "+ metricName+"not found",true);
		e.printStackTrace();
		
	}
	return found;
}   	
 }
