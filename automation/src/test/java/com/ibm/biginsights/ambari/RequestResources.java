package com.ibm.biginsights.ambari;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;

/**
 * Ambari Request Resources API
 * Includes:
 *  -List cluster requests
 *  -List request info
 *  -Run command
 */
public class RequestResources {

	public static String customCommandJson = "{" +
												"\"RequestInfo\" : " +
												"{" +
													"\"context\": \"Service Check\"," +
													"\"command\": \"SERVICE_CHECK\"" +
												"}," +
												"\"Requests/resource_filters\" : " +
												"[{" +
													"\"service_name\": \"SERVICE\"" +
												"}]" +
											"}";

	/**
	 * Update the command context using customCommandJson template
	 * @param context context of custom command
	 * @param command the custom command
	 * @param serviceName name of the service
	 * @return string with updated command context
	 */
	public static String commandContent(String context, String command, String serviceName) {

		LinkedHashMap<String, String> commandContext = new LinkedHashMap<String, String>();
		commandContext.put("context", context);
		commandContext.put("command", command.toUpperCase());

		JSONObject commandObj = null;
		try {
			commandObj = new JSONObject(customCommandJson);
			JSONObject service = new JSONObject().put("service_name", serviceName.toUpperCase());
			JSONArray resourceFilters = new JSONArray().put(service);
			commandObj.put("RequestInfo", commandContext);
			commandObj.put("Requests/resource_filters", resourceFilters);
		}
		catch(JSONException e) {
			e.printStackTrace();
		}

		return JSONObject.quote(commandObj.toString());
	}

	/**
	 * POST /clusters/<cluster name>/requests
	 * @param requestSpec specification of REST request
	 * @param clusterUrl ambari server cluster url
	 * @param serviceName name of the service
	 * @param context context of custom command
	 * @param command the custom command
	 * @return REST request response
	 */
	public static Response runCustomCommand(RequestSpecification requestSpec, String clusterUrl, String serviceName,
												String context, String command) {
		String requestPath = clusterUrl + "/requests";
		requestSpec.body(commandContent(context, command, serviceName));
		/*return RestAssured.given().
								log().all().
								spec(requestSpec).
							expect().
								log().all().
							when().
								post(requestPath);*/
		return RestAssured.given().spec(requestSpec).post(requestPath);
	}

	/**
	 * GET /clusters/<cluster name>/requests
	 * View cluster requests
	 * @param requestSpec specification of REST request
	 * @param clusterUrl ambari server cluster url
	 * @return REST request response
	 */
	public static Response viewRequests(RequestSpecification requestSpec, String clusterUrl) {
		String requestPath = clusterUrl + "/requests";
		/*return RestAssured.given().
								log().all().
								spec(requestSpec).
							expect().
								log().all().
							when().
								get(requestPath);*/
		return RestAssured.given().spec(requestSpec).get(requestPath);
	}

	/**
	 * GET /clusters/<cluster name>/requests?fields=Requests/*
	 * View cluster request info
	 * @param requestSpec specification of REST request
	 * @param clusterUrl ambari server cluster url
	 * @return REST request response
	 */
	public static Response viewRequestsInfo(RequestSpecification requestSpec, String clusterUrl) {
		String requestPath = clusterUrl + "/requests?fields=Requests/*";
		/*return RestAssured.given().
								log().all().
								spec(requestSpec).
								queryParam("fields", "Requests/*").
							expect().
								log().all().
							when().
								get(requestPath);*/
		return RestAssured.given().spec(requestSpec).get(requestPath);

	}
}
