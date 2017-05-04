package com.project.model.transfer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.json.simple.JSONObject;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 5/4/2017.
 */
@XmlRootElement
@JsonIgnoreProperties(ignoreUnknown = true)
public class SpatialLayerRequest {
    private ArrayList<String> attributeNames;
    private JSONObject geojson;

    public ArrayList<String> getAttributeNames() {
        return attributeNames;
    }

    public void setAttributeNames(ArrayList<String> attributeNames) {
        this.attributeNames = attributeNames;
    }

    public JSONObject getGeojson() {
        return geojson;
    }

    public void setGeojson(JSONObject geojson) {
        this.geojson = geojson;
    }
}
