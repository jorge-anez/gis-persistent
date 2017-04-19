package com.project.services;

import com.project.dao.GenericDAOImpl;
import com.project.model.Attribute;
import com.project.model.SpatialData;
import com.project.model.SpatialDataAttribute;
import com.vividsolutions.jts.geom.Geometry;
import org.geotools.feature.FeatureIterator;
import org.hibernate.SessionFactory;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collection;

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
    public SpatialDataAttribute create(String value, Attribute attribute, SpatialData spatialData) {
        SpatialDataAttribute dataAttribute = new SpatialDataAttribute();
        dataAttribute.setValue(value);
        dataAttribute.setAttribute(attribute);
        dataAttribute.setSpatialData(spatialData);
        spatialDataAttributeDAO.save(dataAttribute);
        return null;
    }
}
