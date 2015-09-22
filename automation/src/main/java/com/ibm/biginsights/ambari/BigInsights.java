package com.ibm.biginsights.ambari;

import java.util.Hashtable;

import org.apache.commons.lang3.StringUtils;

public class BigInsights {
	
	public String hostname;
	public String user;
	public String pass;
	public String clusterURL;
	public Hashtable<String, String> componentHosts;
	public String clusterHosts;
	
	public BigInsights(String ambarihost, String username, String password) {
		setAmbarihost(ambarihost);
		setUsername(username);
		setPassword(password);
		setClusterURL(ambarihost, username, password);
		setComponentHosts(getClusterURL(),username, password);
		setClusterHosts(getClusterURL(),username, password);		
	}
	
	public Hashtable<String, String> getComponentHosts() {
		return componentHosts;
	}

	public void setComponentHosts(String clusterURL, String username, String password) {
		this.componentHosts = AmbariUtil.getComponentsHosts(clusterURL, username, password);
	}

	public String getAmbarihost() {
		return hostname;
	}

	public void setAmbarihost(String ambarihost) {
		this.hostname = ambarihost;
	}

	public String getUsername() {
		return user;
	}

	public void setUsername(String username) {
		this.user = username;
	}

	public String getPassword() {
		return pass;
	}

	public void setPassword(String password) {
		this.pass = password;
	}

	public String getClusterURL() {
		return clusterURL;
	}

	public void setClusterURL(String ambarihost, String username, String password) {
		this.clusterURL = AmbariUtil.getAmbariClusterURL(ambarihost, username, password);
	}

	public String getClusterHosts() {
		return clusterHosts;
	}

	public void setClusterHosts(String clusterURL, String username, String password) {
		this.clusterHosts = StringUtils.join(AmbariUtil.getClusterHosts(clusterURL, username, password), ',');
	}
	
	
	
	

}
