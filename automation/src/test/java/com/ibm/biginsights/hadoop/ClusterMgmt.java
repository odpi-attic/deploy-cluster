package com.ibm.biginsights.hadoop;




import java.io.IOException;

import junit.framework.Assert;

import org.json.JSONException;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;


import com.ibm.biginsights.ambari.AddComponent;
import com.ibm.biginsights.ambari.AddHost;
import com.ibm.biginsights.ambari.DeleteAllComponents;
import com.ibm.biginsights.ambari.DeleteHost;
import com.ibm.biginsights.maven.Common;
import com.jayway.restassured.response.Header;

/*
 * @author,Swetha
 */
public class ClusterMgmt {

	Header header= new Header("X-Requested-By:", "ambari");
    String response ;
    String content;
	String state ;
	
    private static String ahost;
 	/*private static String aport;*/
 	private static  String user;
 	private static String pw ;
 	/*private static  String clustername ;*/
 	private static String newHost ;
 	
    //@BeforeClass
 	@Test
	@Parameters({"ambarihost", "username", "password", "newhost"
		})
	public void yarnInfraSetup(String ambarihost,String username, String password,
			String newhost) throws InterruptedException, JSONException {
		
		ahost= Common.assignPOMValues("BIambarihost", ambarihost);		
		/*aport=Common.assignPOMValues("BIambariport", ambariport);*/
		user=Common.assignPOMValues("BIusername", username);
		pw=Common.assignPOMValues("BIpassword", password);
		/*clustername=Common.assignPOMValues("BIambariclustername", ambariclustername);	*/	
		newHost = Common.assignPOMValues("BInewhost", newhost);	
     	
		System.out.println("\n==============This test adds a host and hadoop services ===========");
     	System.out.println("===================Deletes services and hosts============== ") ; 
     	
    	
    	Thread.sleep(1000);
     	System.out.println("\nAdding host "+ahost+"..");
    	AddHost.addHost(ahost, user,pw, newHost);

    	System.out.println("\nInstalling component YARN_CLIENT..");
    	AddComponent.installComp(ahost, user,pw,"YARN","YARN_CLIENT",newHost);
    	
    	Thread.sleep(1000);
    	System.out.println("\nInstalling component DATANODE..");
     	AddComponent.installComp(ahost, user,pw,"HDFS","DATANODE",newHost);
     	Thread.sleep(1000);
     	
     	try {
    		System.out.println("\nDeleting components from "+ahost+" ..");
     		DeleteAllComponents.deleteComponents( ahost, user,pw, newHost);
     		System.out.println("\nDeleting host"+ahost+" ..");
			DeleteHost.deleteHost( ahost, user,pw, newHost);
		} catch (IOException e) {
			System.out.println(e);
			Assert.assertFalse("Test failed due to exception.",true);
		}
		
    }
    
    
 }
