package com.project.model.transfer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.json.simple.JSONObject;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 5/4/2017.
 */
@XmlRootElement
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SpatialLayerRequest {
    private ArrayList<AttributeDTO> attributes;
    private JSONObject geojson;

    public ArrayList<AttributeDTO> getAttributes() {
        return attributes;
    }

    public void setAttributes(ArrayList<AttributeDTO> attributes) {
        this.attributes = attributes;
    }

    public JSONObject getGeojson() {
        return geojson;
    }

    public void setGeojson(JSONObject geojson) {
        this.geojson = geojson;
    }
}
