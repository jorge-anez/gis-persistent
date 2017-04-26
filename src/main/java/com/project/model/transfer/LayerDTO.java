package com.project.model.transfer;

/**
 * Created by JORGE-HP on 25/4/2017.
 */
public class LayerDTO {
    private Long layerId;
    private String name;
    private Integer epsgCode;

    public Long getLayerId() {
        return layerId;
    }

    public void setLayerId(Long layerId) {
        this.layerId = layerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getEpsgCode() {
        return epsgCode;
    }

    public void setEpsgCode(Integer epsgCode) {
        this.epsgCode = epsgCode;
    }
}
