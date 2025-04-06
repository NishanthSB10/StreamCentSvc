package com.stream.cent.utils;

import java.util.HashMap;
import java.util.Map;


public class EntityStatus {

    // ---------------- Api Response method for success message (2xx series:- 200/201/204) -----------------------------------------------

    Map<String, Object> dataMap = new HashMap<String, Object>();
    ApiResponse apiResponse = null;

    /**
     * @method no_content
     *
     * @param applicationName {of type String}
     * @param moduleName {of type String}
     * @param uri {of type String}
     * @param uiMsgKey {of type String} - used to display success or error msg on ui page in multiple language
     * @param message {of type String}
     *
     * @return apiResponse object
     * @see ApiResponse class
     */
    public ApiResponse no_content(String applicationName, String moduleName, String uri,String uiMsgKey,String message) {
        apiResponse = new ApiResponse(204);
        apiResponse.setApplicationName(applicationName);
        apiResponse.setModuleName(moduleName);
        apiResponse.setData(dataMap);
        apiResponse.setUiMsgKey(uiMsgKey);
        apiResponse.setMessage(message);
        apiResponse.setError("No Content");
        apiResponse.setPath(uri);
        return apiResponse;

    }

    public ApiResponse success(String applicationName, String moduleName, Map<String, Object> dataMap,String uiMsgKey,String message) {
        apiResponse = new ApiResponse(200);
        apiResponse.setApplicationName(applicationName);
        apiResponse.setModuleName(moduleName);
        apiResponse.setUiMsgKey(uiMsgKey);
        apiResponse.setMessage(message);
        apiResponse.setData(dataMap);
        return apiResponse;
    }


    public ApiResponse created(String applicationName, String moduleName, Map<String, Object> dataMap,String uiMsgKey, String message) {
        apiResponse = new ApiResponse(201);
        apiResponse.setApplicationName(applicationName);
        apiResponse.setModuleName(moduleName);
        apiResponse.setUiMsgKey(uiMsgKey);
        apiResponse.setMessage(message);
        apiResponse.setData(dataMap);
        return apiResponse;
    }

    // ---- Api Response method for failue message(i.e, client error --> 4xx series:- 400/404) ---------------------------------


    public ApiResponse bad_request(String applicationName,String moduleName,String uri,String uiMsgKey,String message) {
        apiResponse = new ApiResponse(400);
        apiResponse.setApplicationName(applicationName);
        apiResponse.setModuleName(moduleName);
        apiResponse.setUiMsgKey(uiMsgKey);
        apiResponse.setMessage(message);
        apiResponse.setData(dataMap);
        apiResponse.setError("Bad Request");
        return apiResponse;
    }

    public ApiResponse not_found(String applicationName,String moduleName,String uri,String uiMsgKey,String message) {
        apiResponse = new ApiResponse(404);
        apiResponse.setApplicationName(applicationName);
        apiResponse.setModuleName(moduleName);
        apiResponse.setUiMsgKey(uiMsgKey);
        apiResponse.setMessage(message);
        apiResponse.setData(dataMap);
        apiResponse.setError("Not found");
        apiResponse.setPath(uri);
        return apiResponse;
    }


    public ApiResponse method_not_allowed(String applicationName,String moduleName,String uri,String uiMsgKey,String message) {
        apiResponse = new ApiResponse(405);
        apiResponse.setApplicationName(applicationName);
        apiResponse.setModuleName(moduleName);
        apiResponse.setUiMsgKey(uiMsgKey);
        apiResponse.setMessage(message);
        apiResponse.setData(dataMap);
        apiResponse.setError("Method Not Allowed");
        apiResponse.setPath(uri);
        return apiResponse;
    }

    public ApiResponse conflict(String applicationName,String moduleName,String uri,String uiMsgKey,String message) {
        apiResponse = new ApiResponse(409);
        apiResponse.setApplicationName(applicationName);
        apiResponse.setModuleName(moduleName);
        apiResponse.setUiMsgKey(uiMsgKey);
        apiResponse.setMessage(message);
        apiResponse.setData(dataMap);
        apiResponse.setError("Conflict");
        return apiResponse;
    }


    public ApiResponse unauthorized(String applicationName, String moduleName, String uri, String uiMsgKey, String message) {
        ApiResponse apiResponse = new ApiResponse(401); // Set status code to 401 for unauthorized access
        apiResponse.setApplicationName(applicationName);
        apiResponse.setModuleName(moduleName);
        apiResponse.setUiMsgKey(uiMsgKey);
        apiResponse.setMessage(message);
        apiResponse.setData(dataMap);
        apiResponse.setError("Unauthorized");
        apiResponse.setPath(uri);
        return apiResponse;
    }


    public ApiResponse forbidden(String applicationName,String moduleName,String uri,String uiMsgKey,String message) {
        apiResponse = new ApiResponse(403);
        apiResponse.setApplicationName(applicationName);
        apiResponse.setModuleName(moduleName);
        apiResponse.setUiMsgKey(uiMsgKey);
        apiResponse.setMessage(message);
        apiResponse.setData(dataMap);
        apiResponse.setError("Forbidden");
        apiResponse.setPath(uri);
        return apiResponse;
    }

    public ApiResponse expectation_failed(String applicationName,String moduleName,String uri,String uiMsgKey,String message) {
        apiResponse = new ApiResponse(417);
        apiResponse.setApplicationName(applicationName);
        apiResponse.setModuleName(moduleName);
        apiResponse.setUiMsgKey(uiMsgKey);
        apiResponse.setMessage(message);
        apiResponse.setData(dataMap);
        apiResponse.setError("expectation_failed");
        apiResponse.setPath(uri);
        return apiResponse;
    }

    public ApiResponse unprocessable_entity(String applicationName,String moduleName,String uri,String uiMsgKey,String message) {
        apiResponse = new ApiResponse(422);
        apiResponse.setApplicationName(applicationName);
        apiResponse.setModuleName(moduleName);
        apiResponse.setUiMsgKey(uiMsgKey);
        apiResponse.setMessage(message);
        apiResponse.setData(dataMap);
        apiResponse.setError("Unprocessable Entity");
        apiResponse.setPath(uri);
        return apiResponse;
    }

    //----- Api Response method for exception message (i.e, Internal server error --> 5xx series:- 500) ------------------------


//    public ApiResponse server_error(String applicationName,String moduleName,String uiMsgKey,String uri,Exception e) {
//        CoreException cex = null;
//        apiResponse = new ApiResponse(500);
//        apiResponse.setApplicationName(applicationName);
//        apiResponse.setModuleName(moduleName);
//        apiResponse.setData(dataMap);
//        apiResponse.setError("Internal Server Error");
//        if(e instanceof CoreException) {
//            cex = (CoreException)e;
//            apiResponse.setException(cex.getErrorMsg());
//        } else {
//            apiResponse.setException(ExceptionUtils.getStackTrace(e));
//        }
//        apiResponse.setUiMsgKey(uiMsgKey);
//        apiResponse.setPath(uri);
//        return apiResponse;
//    }

    public ApiResponse serviceUnavailable(String applicationName,String moduleName,String uri,String uiMsgKey,String message) {
        apiResponse = new ApiResponse(503);
        apiResponse.setApplicationName(applicationName);
        apiResponse.setModuleName(moduleName);
        apiResponse.setUiMsgKey(uiMsgKey);
        apiResponse.setMessage(message);
        apiResponse.setData(dataMap);
        apiResponse.setError("Service Unavailable");
        apiResponse.setPath(uri);
        return apiResponse;
    }
}

