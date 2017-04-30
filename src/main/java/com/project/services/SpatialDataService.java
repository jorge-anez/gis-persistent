package com.project.services;

import com.project.model.domain.SpatialData;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import java.util.Collection;
import java.util.List;

/**
 * Created by JORGE-HP on 18/4/2017.
 */
public interface SpatialDataService {
    void persistFeatures(SpatialData spatialData, Long layerId);
    FeatureCollection getSpacialData();
    List<SpatialData> getSpatialDatasByLayer(Long layerId);
    void deleteSpatialDataForLayer(Long layerId);
}
