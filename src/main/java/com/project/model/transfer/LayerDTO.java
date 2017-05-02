package com.project.model.transfer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by JORGE-HP on 25/4/2017.
 */
@XmlRootElement(name="Response")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class LayerDTO {
    private Long layerId;
    private String layerName;
    private Integer epsgCode;

    public Long getLayerId() {
        return layerId;
    }

    public void setLayerId(Long layerId) {
        this.layerId = layerId;
    }

    public String getLayerName() {
        return layerName;
    }

    public void setLayerName(String layerName) {
        this.layerName = layerName;
    }

    public Integer getEpsgCode() {
        return epsgCode;
    }

    public void setEpsgCode(Integer epsgCode) {
        this.epsgCode = epsgCode;
    }
}
