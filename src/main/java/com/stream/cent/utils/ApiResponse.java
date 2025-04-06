package com.stream.cent.utils;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.ToString;

import java.util.Date;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
public class ApiResponse {

    /**
     * This field represents the project name
     */
    private String applicationName;

    /**
     * This field represents module name (GIACT, GLVDB...etc)
     */
    private String moduleName;

    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss", timezone="Asia/Kolkata")
    private Date date;

    /**
     * This field represents the Http Response Status Code
     */
    private int status;

    /**
     * This field represents keyword as success/failure about current executing API
     */
    private String keyword;

    /**
     * This `uiMsgKey` field represents success or error message in form of key on UI page.
     *
     * e.g.,'uiMsgKey' : 'vendor.registration.success' (or) 'vendor.registration.failed'
     *
     * Based on `uiMsgKey` field, UI-developer will display the `success` or `error` message
     * on UI page in different language (say - English, Spanish, portuguese ...etc). So we
     * need to include `uiMsgKey` for each & every API response.
     *
     * @implNote `uiMsgKey` - we are storing `uiMsgKey` into TRANSLATION table for success/error msg
     *           with Translated value/msg into multiple language (say - English, Spanish, portuguese ...etc)
     *           as well as we are maintaing into "UiMsgKeyConstants" interface {@link UiMsgKeyConstants}
     *
     */
    private String uiMsgKey;

    /**
     * This field represents human readable msg about current executing API.
     */
    private String message;

    /**
     * This field stores actual response data about current executing API.
     */
    private Map<String, Object> data;

    /**
     * This field represent response error msg about current executing API.
     */
    private String error;

    /**
     * This field represent exception msg if any problem occoures while API executing.
     */
    private String exception;

    /**
     * This field represent resource path or endpoint uri of current executing API.
     */
    private String path;

    public ApiResponse() {

        this.keyword ="success";

    }

    public ApiResponse(int status) {

        if (status == 200 || status == 201) {
            this.status = status;
            this.keyword = "success";
            this.error = "";
            this.exception = "";

        } else if(status == 500){
            this.date = new Date();
            this.status = status;
            this.keyword = "failure";
            this.message = "Some Problem occours..! Please Try After Some time";
        } else {
            this.date = new Date();
            this.status = status;
            this.keyword = "failure";
        }
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getUiMsgKey() {
        return uiMsgKey;
    }

    public void setUiMsgKey(String uiMsgKey) {
        this.uiMsgKey = uiMsgKey;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

}
