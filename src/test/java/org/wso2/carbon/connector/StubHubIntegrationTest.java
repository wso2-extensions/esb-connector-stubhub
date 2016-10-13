/*
*  Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
package org.wso2.carbon.connector;

import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.connector.integration.test.base.ConnectorIntegrationTestBase;
import org.wso2.connector.integration.test.base.RestResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * StubHub Connector Integration test
 * <p/>
 * Advise to the tester. If you have the stubHub Bronze tire you can able to send only 10 request per minute
 * Please run this integration test partly.
 */
public class StubHubIntegrationTest extends ConnectorIntegrationTestBase {
    /**
     * Advise to the tester : If you have the stubHub Bronze tire you can able to send only 10 request per minute
     * Please run this integration test partly. Consider while partitioning the test cases focus on createPriceAlert
     * and deletePriceAlert in the same partitions.
     */
    private Map<String, String> esbRequestHeadersMap = new HashMap<String, String>();
    private Map<String, String> apiRequestHeadersMap = new HashMap<String, String>();

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        init("stubHub-connector-1.0.1-SNAPSHOT");
        esbRequestHeadersMap.put("Content-Type", "application/json");
        esbRequestHeadersMap.put("Accept", "application/json");
        apiRequestHeadersMap.put("Accept-Charset", "UTF-8");
        apiRequestHeadersMap.put("Accept", "application/json");
        apiRequestHeadersMap.put("Content-Type", "application/json");
    }

    /**
     * Get information about a venue.
     *
     * @throws Exception
     */
    @Test(enabled = true, groups = {"wso2.esb"},
            description = "StubHub {getVenueDetails with mandatory params}.")
    public void testGetVenuesDetailsWithMandatoryParameters() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:getVenueDetails");
        final String appToken = connectorProperties.getProperty("appToken");
        apiRequestHeadersMap.put("Authorization", "Bearer " + appToken);
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/catalog/venues/v2/"
                + connectorProperties.getProperty("venueId");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getVenueDetails_mandatory.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().get("id"), apiRestResponse.getBody().get("id"));
        Assert.assertEquals(esbRestResponse.getBody().get("name"), apiRestResponse.getBody().get("name"));
        Assert.assertEquals(esbRestResponse.getBody().get("description"), apiRestResponse.getBody().get("description"));
    }

    /**
     * Negative case on getting information about a venue.
     *
     * @throws Exception
     */
    @Test(enabled = true, groups = {"wso2.esb"},
            description = "StubHub {getVenueDetails with negative params}.")
    public void testGetVenuesDetailsNegativeTestCase() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:getVenueDetails");
        final String appToken = connectorProperties.getProperty("appToken");
        apiRequestHeadersMap.put("Authorization", "Bearer " + appToken);
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/catalog/venues/v2/invalid";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getVenueDetails_negative.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("errors").
                        getJSONArray("error").getJSONObject(0).get("errorTypeId"),
                apiRestResponse.getBody().getJSONObject("errors").
                        getJSONArray("error").getJSONObject(0).get("errorTypeId"));
    }

    /**
     * Get information about a venue with optional parameters.
     *
     * @throws Exception
     */
    @Test(enabled = true, groups = {"wso2.esb"},
            description = "StubHub {getVenueDetails with optional params}")
    public void testGetVenuesDetailsWithOptionalParameters() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:getVenueDetails");
        final String appToken = connectorProperties.getProperty("appToken");
        apiRequestHeadersMap.put("Authorization", "Bearer " + appToken);
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/catalog/venues/v2/"
                + connectorProperties.getProperty("venueId")
                + "?locale=" + connectorProperties.getProperty("locale");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getVenueDetails_optional.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().get("id"), apiRestResponse.getBody().get("id"));
        Assert.assertEquals(esbRestResponse.getBody().get("name"), apiRestResponse.getBody().get("name"));
        Assert.assertEquals(esbRestResponse.getBody().get("description"), apiRestResponse.getBody().get("description"));
    }

    /**
     * Create a notification for an event according to the set of criteria.
     *
     * @throws Exception
     */
    @Test(enabled = true, groups = {"wso2.esb"},
            description = "StubHub {createPriceAlert with mandatory params}.")
    public void testCreatePriceAlert() throws Exception {
        final String accessToken = connectorProperties.getProperty("accessToken");
        apiRequestHeadersMap.put("Authorization", "Bearer " + accessToken);
        esbRequestHeadersMap.put("Action", "urn:createPriceAlert");
        RestResponse<JSONObject> esbRestResponseCreatePriceAlert =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createPriceAlert.json");
        Assert.assertEquals(esbRestResponseCreatePriceAlert.getHttpStatusCode(), 201);
        String priceAlertRequestId = esbRestResponseCreatePriceAlert.getBody().getJSONObject("priceAlert")
                .getJSONObject("priceAlert")
                .get("priceAlertId").toString();
        connectorProperties.setProperty("priceAlertRequestId", priceAlertRequestId);
    }

    /**
     * Retrieves information about one of the user price alert requests the detail of a particular price alert
     *
     * @throws Exception
     */
    @Test(dependsOnMethods = {"testCreatePriceAlert"}, enabled = true, groups = {"wso2.esb"},
            description = "StubHub {GetPriceAlertRequest with mandatory params}.")
    public void testGetPriceAlertRequest() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:getPriceAlertRequest");
        final String accessToken = connectorProperties.getProperty("accessToken");
        apiRequestHeadersMap.put("Authorization", "Bearer " + accessToken);
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/user/customers/v1/"
                + connectorProperties.getProperty("userId")
                + "/pricealerts/" + connectorProperties.get("priceAlertRequestId");
        RestResponse<JSONObject> apiRestResponseGetPriceAlert =
                sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        RestResponse<JSONObject> esbRestResponseGetPriceAlert =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getPriceAlertRequest_manadatory.json");
        Assert.assertEquals(esbRestResponseGetPriceAlert.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponseGetPriceAlert.getBody().getJSONObject("priceAlert")
                        .getJSONObject("priceAlert").get("priceAlertId"),
                apiRestResponseGetPriceAlert.getBody().getJSONObject("priceAlert")
                        .getJSONObject("priceAlert").get("priceAlertId"));
        Assert.assertEquals(esbRestResponseGetPriceAlert.getBody().getJSONObject("priceAlert")
                        .getJSONObject("priceAlert").get("eventId"),
                apiRestResponseGetPriceAlert.getBody().getJSONObject("priceAlert")
                        .getJSONObject("priceAlert").get("eventId"));
        Assert.assertEquals(esbRestResponseGetPriceAlert.getBody().getJSONObject("priceAlert")
                        .getJSONObject("priceAlert").get("priceAlertId"),
                apiRestResponseGetPriceAlert.getBody().getJSONObject("priceAlert")
                        .getJSONObject("priceAlert").get("priceAlertId"));
    }

    /**
     * Get all information about price alerts create by user.
     *
     * @throws Exception
     */
    @Test(dependsOnMethods = {"testCreatePriceAlert"}, enabled = true, groups = {"wso2.esb"},
            description = "StubHub {GetPriceAlertRequests with mandatory params}.")
    public void testGetPriceAlertRequests() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:getPriceAlertRequests");
        final String accessToken = connectorProperties.getProperty("accessToken");
        apiRequestHeadersMap.put("Authorization", "Bearer " + accessToken);
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/user/customers/v1/"
                + connectorProperties.getProperty("userId")
                + "/pricealerts";
        RestResponse<JSONObject> apiRestResponseGetPriceAlerts =
                sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        RestResponse<JSONObject> esbRestResponseGetPriceAlerts =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getPriceAlertRequests_mandatory.json");
        Assert.assertEquals(esbRestResponseGetPriceAlerts.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponseGetPriceAlerts.getBody().getJSONObject("priceAlert")
                        .getJSONArray("priceAlert").getJSONObject(0).get("eventId"),
                apiRestResponseGetPriceAlerts.getBody().getJSONObject("priceAlert")
                        .getJSONArray("priceAlert").getJSONObject(0).get("eventId"));
    }

    /**
     * Get all information about a particular event price alert with optional parameter.
     *
     * @throws Exception
     */
    @Test(dependsOnMethods = {"testCreatePriceAlert"}, enabled = true, groups = {"wso2.esb"},
            description = "StubHub {GetPriceAlertRequest with optional params}.")
    public void testGetPriceAlertRequestsOptional() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:getPriceAlertRequests");
        final String accessToken = connectorProperties.getProperty("accessToken");
        apiRequestHeadersMap.put("Authorization", "Bearer " + accessToken);
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/user/customers/v1/"
                + connectorProperties.getProperty("userId")
                + "/pricealerts?eventId=" + connectorProperties.getProperty("eventId");
        RestResponse<JSONObject> apiRestResponseGetPriceAlerts =
                sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        RestResponse<JSONObject> esbRestResponseGetPriceAlerts =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getPriceAlertRequests_optional.json");
        Assert.assertEquals(esbRestResponseGetPriceAlerts.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponseGetPriceAlerts.getBody().getJSONObject("priceAlert")
                        .getJSONObject("priceAlert").get("eventId"),
                apiRestResponseGetPriceAlerts.getBody().getJSONObject("priceAlert")
                        .getJSONObject("priceAlert").get("eventId"));
    }

    /**
     * Get all information about a particular event price alert with optional parameter.
     *
     * @throws Exception
     */
    @Test(enabled = true, groups = {"wso2.esb"},
            description = "StubHub {GetPriceAlertRequest with negative params}.")
    public void testGetPriceAlertRequestsNegative() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:getPriceAlertRequests");
        final String accessToken = connectorProperties.getProperty("accessToken");
        apiRequestHeadersMap.put("Authorization", "Bearer " + accessToken);
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/user/customers/v1/"
                + connectorProperties.getProperty("userId")
                + "/pricealerts?eventId=invalid";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getPriceAlertRequests_negative.json");
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("priceAlert").getJSONObject("errors").
                getJSONArray("error").getJSONObject(0).get("errorTypeId"), apiRestResponse.getBody()
                .getJSONObject("priceAlert").getJSONObject("errors").
                        getJSONArray("error").getJSONObject(0).get("errorTypeId"));

    }

    /**
     * Pause or Resume a price alert request.
     *
     * @throws Exception
     */
    @Test(dependsOnMethods = {"testCreatePriceAlert"}, enabled = true, groups = {"wso2.esb"},
            description = "StubHub {Pause/Resume price alert with mandatory params}.")
    public void testPauseResumePriceAlertRequests() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:pauseMyPriceAlertRequest");
        RestResponse<JSONObject> esbRestResponseGetPriceAlerts =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                        "esb_pausePriceAlertRequest_mandatory.json");
        final String accessToken = connectorProperties.getProperty("accessToken");
        apiRequestHeadersMap.put("Authorization", "Bearer " + accessToken);
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/user/customers/v1/"
                + connectorProperties.getProperty("userId")
                + "/pricealerts/"
                + connectorProperties.getProperty("priceAlertRequestId");
        RestResponse<JSONObject> apiRestResponseGetPriceAlerts = sendJsonRestRequest(apiEndPoint, "GET",
                apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponseGetPriceAlerts.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponseGetPriceAlerts.getBody().getJSONObject("priceAlert")
                        .getJSONObject("priceAlert").get("pauseInd"),
                apiRestResponseGetPriceAlerts.getBody().getJSONObject("priceAlert").getJSONObject("priceAlert")
                        .get("pauseInd"));
        Assert.assertEquals(esbRestResponseGetPriceAlerts.getBody().getJSONObject("priceAlert")
                        .getJSONObject("priceAlert").get("priceAlertId"),
                apiRestResponseGetPriceAlerts.getBody().getJSONObject("priceAlert")
                        .getJSONObject("priceAlert").get("priceAlertId"));
    }

    /**
     * Pause or Resume a price alert negative case.
     *
     * @throws Exception
     */
    @Test(enabled = true, groups = {"wso2.esb"},
            description = "StubHub {Pause/Resume price alert with negative params}.")
    public void testPauseResumePriceAlertRequestsNegative() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:pauseMyPriceAlertRequest");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_pausePriceAlertRequest_negative.json");
        final String accessToken = connectorProperties.getProperty("accessToken");
        apiRequestHeadersMap.put("Authorization", "Bearer " + accessToken);
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/user/customers/v1/"
                + connectorProperties.getProperty("userId")
                + "/pricealerts/invalid";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET",
                apiRequestHeadersMap, "api_pausePriceAlertRequest_negative.json");
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("priceAlert").getJSONObject("errors").
                        getJSONArray("error").getJSONObject(0).get("errorTypeId"),
                apiRestResponse.getBody().getJSONObject("priceAlert").getJSONObject("errors").
                        getJSONArray("error").getJSONObject(0).get("errorTypeId"));
    }

    /**
     * update a particular price alert request.
     *
     * @throws Exception
     */
    @Test(dependsOnMethods = {"testPauseResumePriceAlertRequests"}, enabled = true, groups = {"wso2.esb"},
            description = "StubHub {Update a particular price alert with mandatory params}.")
    public void testUpdatePriceAlertRequests() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:updateMyPriceAlertRequest");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updatePriceAlertRequest.json");
        final String accessToken = connectorProperties.getProperty("accessToken");
        apiRequestHeadersMap.put("Authorization", "Bearer " + accessToken);
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/user/customers/v1/"
                + connectorProperties.getProperty("userId")
                + "/pricealerts/"
                + connectorProperties.getProperty("priceAlertRequestId");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET",
                apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("priceAlert")
                        .getJSONObject("priceAlert").get("pauseInd"),
                apiRestResponse.getBody().getJSONObject("priceAlert").getJSONObject("priceAlert").get("pauseInd"));
    }

    /**
     * Delete a price alert request
     *
     * @throws Exception
     */
    @Test(dependsOnMethods = {"testUpdatePriceAlertRequests"}, enabled = true, groups = {"wso2.esb"},
            description = "StubHub {Delete a price alert with mandatory params}.")
    public void testDeletePriceAlert() throws Exception {
        final String accessToken = connectorProperties.getProperty("accessToken");
        apiRequestHeadersMap.put("Authorization", "Bearer " + accessToken);
        esbRequestHeadersMap.put("Action", "urn:deletePriceAlertRequest");
        RestResponse<JSONObject> esbRestResponseDeletePriceAlert =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deletePriceAlertRequest.json");
        Assert.assertEquals(esbRestResponseDeletePriceAlert.getHttpStatusCode(), 200);
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/user/customers/v1/"
                + connectorProperties.getProperty("userId")
                + "/pricealerts?eventId=" + connectorProperties.getProperty("eventId");
        RestResponse<JSONObject> apiRestResponseGetPriceAlert =
                sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(apiRestResponseGetPriceAlert.getHttpStatusCode(), 404);
        Assert.assertEquals(apiRestResponseGetPriceAlert.getBody().getJSONObject("priceAlert").getJSONObject("errors")
                .getJSONArray("error").getJSONObject(0).get("errorTypeId"), "101");
    }

    /**
     * Search for ticket listings for an event
     *
     * @throws Exception
     */
    @Test(enabled = true, groups = {"wso2.esb"},
            description = "StubHub {searchInventory with mandatory params}")
    public void testSearchInventoryWithMandatoryParameters() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:searchInventory");
        final String appToken = connectorProperties.getProperty("appToken");
        apiRequestHeadersMap.put("Authorization", "Bearer " + appToken);
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/search/inventory/v1?eventId="
                        + connectorProperties.getProperty("eventId");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_searchInventory_mandatory.json");
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().get("totalListings"), apiRestResponse.getBody()
                .get("totalListings"));
        Assert.assertEquals(esbRestResponse.getBody().get("start"), apiRestResponse.getBody().get("start"));
        Assert.assertEquals(esbRestResponse.getBody().get("rows"), apiRestResponse.getBody().get("rows"));
    }

    /**
     * Search for ticket listings for an event - Negative Case
     *
     * @throws Exception
     */
    @Test(enabled = true, groups = {"wso2.esb"},
            description = "StubHub {searchInventory Negative Case}.")
    public void testSearchInventoryWithMandatoryParametersNegativeCase() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:searchInventory");
        final String appToken = connectorProperties.getProperty("appToken");
        apiRequestHeadersMap.put("Authorization", "Bearer " + appToken);
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/search/inventory/v1?eventId=invalid";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_searchInventory_negative.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().get("code"), apiRestResponse.getBody().get("code"));
    }

    /**
     * Search for ticket listings for an event with optional parameters
     *
     * @throws Exception
     */
    @Test(enabled = true, groups = {"wso2.esb"},
            description = "StubHub {searchInventory with optional params}.")
    public void testSearchInventoryWithOptionalParameters() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:searchInventory");
        final String appToken = connectorProperties.getProperty("appToken");
        apiRequestHeadersMap.put("Authorization", "Bearer " + appToken);
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/search/inventory/v1?eventId="
                        + connectorProperties.getProperty("eventId")
                        + "&zoneidlist=" + connectorProperties.getProperty("zoneIdList")
                        + "&sectionidlist=" + connectorProperties.getProperty("sectionIdList")
                        + "&quantity=" + connectorProperties.getProperty("quantity")
                        + "&pricemin=" + connectorProperties.getProperty("priceMin")
                        + "&pricemax=" + connectorProperties.getProperty("priceMax")
                        + "&listingattributelist=" + connectorProperties.getProperty("listingAttributeList")
                        + "&listingattributecategorylist=" + connectorProperties.getProperty("listingAttributeCategoryList")
                        + "&deliverytypelist=" + connectorProperties.getProperty("deliveryTypeList")
                        + "&zonestats=" + connectorProperties.getProperty("zoneStats")
                        + "&sectionstats=" + connectorProperties.getProperty("sectionStats")
                        + "&pricingsummary=" + connectorProperties.getProperty("pricingSummary");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_searchInventory_optional.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().get("totalListings"), apiRestResponse.getBody()
                .get("totalListings"));
        Assert.assertEquals(esbRestResponse.getBody().get("start"), apiRestResponse.getBody().get("start"));
        Assert.assertEquals(esbRestResponse.getBody().get("rows"), apiRestResponse.getBody().get("rows"));
    }

    /**
     * Get information about an event
     *
     * @throws Exception
     */
    @Test(enabled = true, groups = {"wso2.esb"},
            description = "StubHub {getEventDetails with mandatory params}.")
    public void testGetEventDetailsWithMandatoryParameters() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:getEventDetails");
        final String appToken = connectorProperties.getProperty("appToken");
        apiRequestHeadersMap.put("Authorization", "Bearer " + appToken);
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/catalog/events/v2/"
                + connectorProperties.getProperty("eventId");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getEventDetails_mandatory.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().get("id"), apiRestResponse.getBody().get("id"));
        Assert.assertEquals(esbRestResponse.getBody().get("eventUrl")
                , apiRestResponse.getBody().get("eventUrl"));
        Assert.assertEquals(esbRestResponse.getBody().get("eventDateUTC")
                , apiRestResponse.getBody().get("eventDateUTC"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("venue").get("id")
                , apiRestResponse.getBody().getJSONObject("venue").get("id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("eventMeta").get("seoDescription")
                , apiRestResponse.getBody().getJSONObject("eventMeta").get("seoDescription"));
    }

    /**
     * Get information about an event with negative case
     *
     * @throws Exception
     */
    @Test(enabled = true, groups = {"wso2.esb"},
            description = "StubHub {getEventDetails with negative case}.")
    public void testGetEventDetailsWithMandatoryParametersNegativeCase() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:getEventDetails");
        final String appToken = connectorProperties.getProperty("appToken");
        apiRequestHeadersMap.put("Authorization", "Bearer " + appToken);
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/catalog/events/v2/invalid";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getEventDetails_negative.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("errors")
                        .getJSONArray("error").getJSONObject(0).get("errorTypeId"),
                apiRestResponse.getBody().getJSONObject("errors")
                        .getJSONArray("error").getJSONObject(0).get("errorTypeId"));
    }
}
