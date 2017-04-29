package com.project.services;

import com.project.model.transfer.LayerDTO;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import java.util.List;

/**
 * Created by JORGE-HP on 25/4/2017.
 */
public interface SpatialLayerService {
    LayerDTO getLayerById(Long layerId);
    void createSpatialLayer(LayerDTO layerDTO);
    void updateSpatialLayer(LayerDTO layerDTO);
    void deleteSpatialLayer(LayerDTO layerDTO);
    List<LayerDTO> list();
    void persistLayerFeatures(LayerDTO layerDTO, FeatureCollection<SimpleFeatureType, SimpleFeature> collection);
    FeatureCollection<SimpleFeatureType, SimpleFeature> getLayerInfo(Long layerId);
}
