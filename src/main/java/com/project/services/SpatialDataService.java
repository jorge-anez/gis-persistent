package com.project.services;

import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

/**
 * Created by JORGE-HP on 18/4/2017.
 */
public interface SpatialDataService {
    void persistFeatures(FeatureIterator<SimpleFeature> featureIterator);
}
