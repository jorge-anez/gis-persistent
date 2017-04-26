package com.project.services;


import com.project.model.domain.Attribute;
import com.project.model.transfer.AttributeDTO;

import java.util.List;

/**
 * Created by JORGE-HP on 18/4/2017.
 */
public interface AttributeService {
    void create(AttributeDTO attributeDTO, Long spatialLayerId);
    Attribute findByName(String name);
    List<AttributeDTO> getLayerAttribs(Long layerId);
}
