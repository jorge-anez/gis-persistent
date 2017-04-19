package com.project.services;

import com.project.dao.GenericDAOImpl;
import com.project.model.Attribute;
import com.project.model.SpatialData;
import com.project.model.SpatialDataAttribute;
import com.vividsolutions.jts.geom.Geometry;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.hibernate.SessionFactory;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
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
public class SpatialDataServiceImpl implements SpatialDataService {
    @Autowired
    AttributeService attributeService;
    @Autowired
    SpatialDataAttributeService spatialDataAttributeService;

    @Autowired
    private SessionFactory sessionFactory;
    private GenericDAOImpl<SpatialData, Long> spatialDataDAO;

    @PostConstruct
    public void init() {
        spatialDataDAO = new GenericDAOImpl<SpatialData, Long>(sessionFactory, SpatialData.class);
    }

    @Transactional
    public void persistFeatures(FeatureIterator<SimpleFeature> featureIterator) {
        while (featureIterator.hasNext()) {
            SimpleFeature feature = featureIterator.next();
            SpatialData spatialData = new SpatialData();
            spatialData.setTheGeom((Geometry)feature.getAttribute("the_geom"));
            spatialData.setSource("shape file");
            spatialDataDAO.save(spatialData);

            for (Property e: feature.getProperties()){
                if(e.getName().toString().equals("the_geom"))
                    continue;
                Attribute attribute = attributeService.findByName(e.getName().toString());
                if(attribute == null ){
                    attribute = attributeService.create(e.getName().toString());
                }
                spatialDataAttributeService.create(e.getValue().toString(), attribute, spatialData);
            }
        }
    }
}
