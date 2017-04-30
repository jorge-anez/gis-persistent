package com.project.services.impl;

import com.project.dao.GenericDAOImpl;
import com.project.model.domain.Attribute;
import com.project.model.domain.SpatialData;
import com.project.model.domain.SpatialDataAttribute;
import com.project.model.domain.SpatialLayer;
import com.project.services.AttributeService;
import com.project.services.SpatialDataAttributeService;
import com.project.services.SpatialDataService;
import com.project.services.SpatialLayerService;
import com.vividsolutions.jts.geom.Geometry;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.List;

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
    SpatialLayerService spatialLayerService;

    @Autowired
    private SessionFactory sessionFactory;
    private GenericDAOImpl<SpatialData, Long> spatialDataDAO;

    @PostConstruct
    public void init() {
        spatialDataDAO = new GenericDAOImpl<SpatialData, Long>(sessionFactory, SpatialData.class);
    }

    @Transactional
    public void persistFeatures(SpatialData spatialData, Long layerId) {
        SpatialLayer spatialLayer = new SpatialLayer();
        spatialLayer.setSpatialLayerId(layerId);
        spatialData.setSpatialLayer(spatialLayer);
        spatialDataDAO.save(spatialData);
    }

    @Transactional
    public FeatureCollection getSpacialData() {

        return null;
    }

    @Transactional
    public List<SpatialData> getSpatialDatasByLayer(Long layerId) {
        Query query = spatialDataDAO.getNamedQuery("getSpatialDataAndAttribs");
        query.setParameter("layerId", layerId);
        List<SpatialData> spatialDatas = query.list();
        return spatialDatas;
    }

    @Transactional
    public void deleteSpatialDataForLayer(Long layerId) {
        String sql = String.format("DELETE FROM t_spatial_data WHERE spatial_layer_id = %d", layerId);
        sessionFactory.getCurrentSession().createSQLQuery(sql).executeUpdate();
    }

    private SimpleFeatureType createFeatureType(Collection<SpatialDataAttribute> dataAttributes) {
        SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
        builder.setName("Location");
        builder.setCRS(DefaultGeographicCRS.WGS84); // <- Coordinate reference system
        // add attributes in order
        builder.add("the_geom", Geometry.class);
        for (SpatialDataAttribute e: dataAttributes)
            builder.add(e.getAttribute().getAttributeName(), String.class); // <- 15 chars width for name field
        // build the type

        final SimpleFeatureType featureType = builder.buildFeatureType();
        return featureType;
    }
}
