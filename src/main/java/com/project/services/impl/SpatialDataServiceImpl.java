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
        List<SpatialData> spatialDataList = spatialDataDAO.getNamedQuery("getSpatialDataAndAttribs").list();
        SimpleFeatureType featureType = createFeatureType(spatialDataList.get(0).getSpatialDataAttributes());

        DefaultFeatureCollection featureCollection = new DefaultFeatureCollection("internal", featureType);
        //Collection<SimpleFeature> featureCollection = new MemoryFeatureCollection(featureType);

        SimpleFeatureBuilder builder = new SimpleFeatureBuilder(featureType);
        for (SpatialData e: spatialDataList) {
            for (SpatialDataAttribute a: e.getSpatialDataAttributes()) {
                builder.set(a.getAttribute().getAttributeName(), a.getValue());
            }
            builder.set("the_geom", e.getTheGeom());
            SimpleFeature feature = builder.buildFeature("fid." + e.getSpatialDataId());
            featureCollection.add(feature);
            builder.reset();
        }
        return featureCollection;
    }

    public List<SpatialData> getSpatialDatasByLayer(Long layerId) {
        Query query = spatialDataDAO.getNamedQuery("getSpatialDataForLayer");
        query.setParameter("spatialLayerId", layerId);
        List<SpatialData> spatialDatas = query.list();
        return spatialDatas;
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
