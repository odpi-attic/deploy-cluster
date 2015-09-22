package com.ibm.biginsights.ambari;

import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.ibm.biginsights.maven.Common;

public class BigInsightsEnv {
	
	static String clusterURL;
	static String username;
	static String password;
	static String[] components;
	
	@BeforeClass
	@Parameters({"ambarihost", "username", "password"})
	public void initializeVariables(String ambarihost, String username, String password)
	{
		ambarihost = Common.assignPOMValues("BIambarihost", ambarihost);
		setUsername(Common.assignPOMValues("BIusername", username));
		setPassword(Common.assignPOMValues("BIpassword", password));
		clusterURL = AmbariUtil.getAmbariClusterURL(ambarihost, username, password);
	}
	
	@Test
	public static void getComponents() 
	{
		components = AmbariUtil.getComponents(clusterURL, getUsername(), getPassword());
	}
	
	@Test(dependsOnMethods="getComponents")
	public static void getComponentHosts() 
	{
		for (String s: components) {        
			String [] hosts = AmbariUtil.getComponentHosts(clusterURL, s, getUsername(), getPassword());
			System.out.println(s+"_HOSTS="+StringUtils.join(hosts, ','));	  	    
		}
	}
	
	@Test(dependsOnMethods="getComponentHosts")
	public static void getClusterHosts()
	{
		System.out.println("CLUSTER_HOSTS="+StringUtils.join(AmbariUtil.getClusterHosts(clusterURL, username, password), ','));
	}
	
	@Test(dependsOnMethods="getClusterHosts")
	public static void getClusterName()
	{
		int index=clusterURL.lastIndexOf('/');
		System.out.println("CLUSTER_NAME="+clusterURL.substring(index+1,clusterURL.length()));
	}
	
	@Test(dependsOnMethods="getClusterName")
	public static void getAmbariPort()
	{
		int first = clusterURL.indexOf(':', clusterURL.indexOf(':')+1);
		int last = clusterURL.indexOf('/', clusterURL.indexOf("//")+2);
		System.out.println("AMBARI_PORT="+clusterURL.substring(first+1, last));
	}
	
	public static String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		BigInsightsEnv.username = username;
	}

	public static String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		BigInsightsEnv.password = password;
	}

}
