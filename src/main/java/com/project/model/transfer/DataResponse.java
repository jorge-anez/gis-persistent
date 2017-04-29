package com.project.model.transfer;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by JORGE-HP on 29/4/2017.
 */
public class DataResponse<T> extends BaseResponse {
    T data;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
