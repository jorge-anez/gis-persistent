package com.project.services;

import com.project.model.domain.Attribute;
import com.project.model.domain.SpatialData;
import com.project.model.domain.SpatialDataAttribute;
import com.project.model.transfer.AttributeDTO;

import java.util.List;

/**
 * Created by JORGE-HP on 18/4/2017.
 */
public interface SpatialDataAttributeService {
    void create(String value, AttributeDTO attributeId, SpatialData spatialData);
    void deleteAttributesValuesForLayer(Long layerId);
}
