package com.ibm.biginsights.ambari;

import java.util.Arrays;

import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.ibm.biginsights.maven.Common;

public class GetComponentHosts {

	@Test
	@Parameters({"ambarihost", "username", "password"})
	public void getHosts(String ambarihost, String username, String password) {
		
		ambarihost = Common.assignPOMValues("BIambarihost", ambarihost);
		username = Common.assignPOMValues("BIusername", username);
		password = Common.assignPOMValues("BIpassword", password);
		
		String clusterURL = AmbariUtil.getAmbariClusterURL(ambarihost, username, password);
		String [] hosts = AmbariUtil.getComponentHosts(clusterURL, "DATANODE", username, password);
		
		System.out.println(Arrays.toString(hosts));
	}
}
