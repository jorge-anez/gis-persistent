package com.project.services;

import com.project.model.Attribute;
import com.project.model.SpatialData;
import com.project.model.SpatialDataAttribute;
import org.geotools.feature.FeatureIterator;
import org.opengis.feature.simple.SimpleFeature;

/**
 * Created by JORGE-HP on 18/4/2017.
 */
public interface SpatialDataAttributeService {
    public SpatialDataAttribute create(String value, Attribute attributeId, SpatialData spatialData);
}
