package com.ibm.biginsights.infra;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ibm.biginsights.maven.Common;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Response;

public class GetServiceAlerts {
	
	public static boolean displayAlerts(JSONObject jObject) throws JSONException  {
		
		boolean found = false;
		
     	
		if(jObject.equals(null)){
			found = false;
			
		}else{
			JSONArray obj1 = jObject.getJSONArray("detail");
			for(int i=0;i<obj1.length();i++){	
			
				JSONObject metric = obj1.getJSONObject(i);
				if(metric.has("description")&&metric.has("output")){
					String desc =  metric.getString("description");
					String output = metric.getString("output");
					System.out.println(desc+": "+output);			
					found = true;
				}else{
					found = false;
					break;
				}
			}
		}
		
		return found;
	}
	
}
