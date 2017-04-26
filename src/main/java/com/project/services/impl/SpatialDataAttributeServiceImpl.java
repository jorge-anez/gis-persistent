package com.project.services.impl;

import com.project.dao.GenericDAOImpl;
import com.project.model.domain.Attribute;
import com.project.model.domain.SpatialData;
import com.project.model.domain.SpatialDataAttribute;
import com.project.model.transfer.AttributeDTO;
import com.project.services.SpatialDataAttributeService;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;

/**
 * Created by JORGE-HP on 18/4/2017.
 */
@Service
public class SpatialDataAttributeServiceImpl implements SpatialDataAttributeService {
        @Autowired
    private SessionFactory sessionFactory;
    private GenericDAOImpl<SpatialDataAttribute, Long> spatialDataAttributeDAO;

    @PostConstruct
    public void init() {
        spatialDataAttributeDAO = new GenericDAOImpl<SpatialDataAttribute, Long>(sessionFactory, SpatialDataAttribute.class);
    }

    @Transactional
    public void create(String value, AttributeDTO attributeDTO, SpatialData spatialData) {
        SpatialDataAttribute dataAttribute = new SpatialDataAttribute();
        Attribute attribute = new Attribute();
        attribute.setAttributeId(attributeDTO.getAttributeId());
        dataAttribute.setValue(value);
        dataAttribute.setAttribute(attribute);
        dataAttribute.setSpatialData(spatialData);
        spatialDataAttributeDAO.save(dataAttribute);
    }
}
