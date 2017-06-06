package com.project.services;

import com.project.model.domain.SpatialData;
import org.geotools.feature.FeatureCollection;

import java.util.List;
import java.util.Map;

/**
 * Created by JORGE-HP on 18/4/2017.
 */
public interface SpatialLayerStyleService {
    void persistStyle(Map<String, String> styles, Long layerId, String geometryType) throws Exception;
    Map<String, String> getSpatialLayerStyles(Long layerId, List<String> styleNames);
    String readBaseSLDStyle() throws Exception;
    String readSLDStyle(Long layerId) throws Exception;
    void deleteStyles(Long layerId);
}
