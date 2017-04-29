package com.project.model.transfer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by JORGE-HP on 29/4/2017.
 */
@XmlRootElement
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseResponse {
    private Boolean success;
    private Integer errorCode;
    private String errorMessage;

    public BaseResponse() {
        this.success = Boolean.TRUE;
        this.errorCode = null;
        this.errorMessage = null;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
