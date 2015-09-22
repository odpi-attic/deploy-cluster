package com.ibm.biginsights.ambari;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Ambari Service Resources API
 * Includes:
 *	-List services
 *	-View service information
 * 	@see com.ibm.biginsights.ambari.GetInstallStatus for additional implementaion
 *  -Create service
 *  @see com.ibm.biginsights.ambari.AddService for additional implementaion
 *  @see com.ibm.biginsights.ambari.AddClient for additional implementaion
 *  -Update services
 *  -Update service
 */
public class ServiceResources {

	public class ServiceState {
		public static final String INIT = "INIT";
		public static final String INSTALLING = "INSTALLING";
		public static final String INSTALL_FAILED = "INSTALL_FAILED";
		public static final String INSTALLED = "INSTALLED";
		public static final String STARTING = "STARTING";
		public static final String STARTED = "STARTED";
		public static final String STOPPING = "STOPPING";
		public static final String UNINSTALLING = "UNINSTALLING";
		public static final String UNINSTALLED = "UNINSTALLED";
		public static final String WIPING_OUT = "WIPING_OUT";
		public static final String UPGRADING = "UPGRADING";
		public static final String MAINTENANCE = "MAINTENANCE";
		public static final String UNKNOWN = "UNKNOWN";
	}

	public static String serviceStateJson = "{" +
												"\"ServiceInfo\": " +
												"{" +
													"\"state\" : \"SERVICE_STATE\"" +
												"}" +
											"}";

	/**
	 * Update the state value in json content for request
	 * @param state the state to update to
	 * @return json string with state updated
	 */
	public static String serviceStateContent(String state) {
		JSONObject stateObj = null;
		try {
			stateObj = new JSONObject(serviceStateJson);
			stateObj.put("ServiceInfo", new JSONObject().put("state", state.toUpperCase()));
		}
		catch(JSONException e) {
			e.printStackTrace();
		}

		return JSONObject.quote(stateObj.toString());
	}

	/**
	 * POST /clusters/<cluster name>/services/<service name>
	 * Create/add service to cluster
	 * @param requestSpec specification of REST request
	 * @param clusterUrl ambari server cluster url
	 * @param serviceName service name
	 * @return REST request response
	 */
	public static Response createService(RequestSpecification requestSpec, String clusterUrl, String serviceName) {
		String requestPath = clusterUrl + "/services/" + serviceName.toUpperCase();
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
	 * PUT /clusters/<cluster name>/services/<service name>
	 * Update a service state, this is how start/stop is achieved.
	 * @param requestSpec specification of REST request
	 * @param clusterUrl ambari server cluster url
	 * @param serviceName service name
	 * @param newState new state to set for service
	 * @return REST request response
	 */
	public static Response updateService(RequestSpecification requestSpec, String clusterUrl, String serviceName,
										 	String newState) {
		String requestPath = clusterUrl + "/services/" + serviceName.toUpperCase();
		requestSpec.body(serviceStateContent(newState));
		/*return RestAssured.given().
							log().all().
							spec(requestSpec).
						expect().
							log().all().
						when().
							put(requestPath);*/
		return RestAssured.given().spec(requestSpec).put(requestPath);
	}

	/**
	 * PUT /clusters/<cluster name>/services?ServiceInfo/state=<current state>
	 * Update all services that are in current state, this is how start/stop all is achieved.
	 * @param requestSpec specification of REST request
	 * @param clusterUrl ambari server cluster url
	 * @param currentState current state of services
	 * @param newState new state to set for services
	 * @return REST request response
	 */
	public static Response updateServices(RequestSpecification requestSpec, String clusterUrl,
										  	String currentState, String newState) {
		String requestPath = clusterUrl + "/services?ServiceInfo/state=" + currentState.toUpperCase();
		requestSpec.body(serviceStateContent(newState));
		/*return RestAssured.given().
							log().all().
							spec(requestSpec).
						expect().
							log().all().
						when().
							put(requestPath);*/
		return RestAssured.given().spec(requestSpec).put(requestPath);
	}

	/**
	 * DELETE /clusters/<cluster name>/services/<service name>
	 * Remove service. Service must be stopped before removal
	 * @param requestSpec specification of REST request
	 * @param clusterUrl ambari server cluster url
	 * @param serviceName service name
	 * @return REST request response
	 */
	public static Response deleteService(RequestSpecification requestSpec, String clusterUrl, String serviceName) {
		String requestPath = clusterUrl + "/services/" + serviceName.toUpperCase();
		/*return RestAssured.given().
							log().all().
							spec(requestSpec).
						expect().
							log().all().
						when().
							delete(requestPath);*/
		return RestAssured.given().spec(requestSpec).delete(requestPath);
	}

	/**
	 * GET /clusters/<cluster name>/services/<service name>
	 * View service info
	 * @param requestSpec specification of REST request
	 * @param clusterUrl ambari server cluster url
	 * @param serviceName service name
	 * @return REST request response
	 */
	public static Response viewServiceInfo(RequestSpecification requestSpec, String clusterUrl, String serviceName) {
		String requestPath = clusterUrl + "/services/" + serviceName.toUpperCase();
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
	 GET /clusters/<cluster name>/services/<service name>?fields=<field>
	 * View specified fields of service info
	 * @param requestSpec specification of REST request
	 * @param clusterUrl ambari server cluster url
	 * @param serviceName service name
	 * @param field query field for request
	 * @return REST request response
	 */
	public static Response viewServiceInfo(RequestSpecification requestSpec, String clusterUrl, String serviceName, String field) {
		String requestPath = clusterUrl + "/services/" + serviceName.toUpperCase() + "?fields=" + field;
		/*return RestAssured.given().
							log().all().
							spec(requestSpec).
							queryParam("fields", field).
						expect().
							log().all().
						when().
							get(requestPath);*/
		return RestAssured.given().spec(requestSpec).get(requestPath);
	}

	/**
	 * GET /clusters/<cluster name>/services
	 * List services
	 * @param requestSpec specification of REST request
	 * @param clusterUrl ambari server cluster url
	 * @return REST request response
	 */
	public static Response listServices(RequestSpecification requestSpec, String clusterUrl) {
		String requestPath = clusterUrl + "/services";
		/*return RestAssured.given().
							log().all().
							spec(requestSpec).
						expect().
							log().all().
							when().
						get(requestPath);*/
		return RestAssured.given().spec(requestSpec).get(requestPath);
	}

}
