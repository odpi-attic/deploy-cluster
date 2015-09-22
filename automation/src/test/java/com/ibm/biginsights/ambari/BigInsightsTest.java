package com.ibm.biginsights.ambari;

import java.util.Enumeration;
import java.util.Hashtable;

public class BigInsightsTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		BigInsights test = new BigInsights("bdvs478.svl.ibm.com", "admin", "admin");
		
		System.out.println("Ambari host: "+test.getAmbarihost());
		System.out.println("Username: "+test.getUsername());
		System.out.println("Password: "+test.getPassword());
		System.out.println("ClusterURL: "+test.getClusterURL());
		System.out.println("Cluster Hosts: "+test.getClusterHosts());
		Hashtable<String, String> componentHosts = test.getComponentHosts();
		Enumeration<String> e = componentHosts.keys();
	    while (e.hasMoreElements()) {
	      String key = (String) e.nextElement();
	      System.out.println(key + "=" + componentHosts.get(key));
	    }
	    System.out.println("Getting datanode list via hashtable key: "+componentHosts.get("DATANODE_HOSTS"));

	}

}
