package com.project.services.impl;

import com.project.dao.GenericDAOImpl;
import com.project.model.domain.Attribute;
import com.project.model.domain.SpatialData;
import com.project.model.domain.SpatialDataAttribute;
import com.project.model.domain.SpatialLayer;
import com.project.model.transfer.AttributeDTO;
import com.project.model.transfer.LayerDTO;
import com.project.services.AttributeService;
import com.project.services.SpatialDataAttributeService;
import com.project.services.SpatialDataService;
import com.project.services.SpatialLayerService;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Polygon;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.referencing.factory.AbstractAuthorityFactory;
import org.hibernate.SessionFactory;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.GeographicCRS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by JORGE-HP on 25/4/2017.
 */
@Service
public class SpatialLayerServiceImpl implements SpatialLayerService {
    @Autowired
    private AttributeService attributeService;
    @Autowired
    private SpatialDataService spatialDataService;
    @Autowired
    private SpatialDataAttributeService spatialDataAttributeService;

    @Autowired
    private SessionFactory sessionFactory;
    private GenericDAOImpl<SpatialLayer, Long> spatialLayerDAO;

    @PostConstruct
    public void init() {
        spatialLayerDAO = new GenericDAOImpl<SpatialLayer, Long>(sessionFactory, SpatialLayer.class);
    }

    @Transactional
    public LayerDTO getLayerById(Long layerId) {
        SpatialLayer spatialLayer = spatialLayerDAO.find(layerId);
        if(spatialLayer == null) return null;
        LayerDTO layerDTO= new LayerDTO();
        layerDTO.setEpsgCode(spatialLayer.getEpsgCode());
        layerDTO.setName(spatialLayer.getLayerName());
        layerDTO.setLayerId(spatialLayer.getSpatialLayerId());
        return layerDTO;
    }

    @Transactional
    public void createSpatialLayer(LayerDTO layerDTO) {
        SpatialLayer spatialLayer = new SpatialLayer();
        spatialLayer.setSpatialLayerId(layerDTO.getLayerId());
        spatialLayer.setEpsgCode(layerDTO.getEpsgCode());
        spatialLayer.setLayerName(layerDTO.getName());
        spatialLayerDAO.save(spatialLayer);
        layerDTO.setLayerId(spatialLayer.getSpatialLayerId());
    }

    @Transactional
    public List<LayerDTO> list() {
        List<LayerDTO> result = new ArrayList<LayerDTO>();
        List<SpatialLayer> layers = spatialLayerDAO.findAll();
        for(SpatialLayer e: layers) {
            LayerDTO layerDTO = new LayerDTO();
            layerDTO.setLayerId(e.getSpatialLayerId());
            layerDTO.setName(e.getLayerName());
            layerDTO.setEpsgCode(e.getEpsgCode());
            result.add(layerDTO);
        }
        return result;
    }

    @Transactional
    public void persistLayerFeatures(LayerDTO layerDTO, FeatureCollection<SimpleFeatureType, SimpleFeature> collection) {
        createSpatialLayer(layerDTO);
        List<AttributeDTO> attributes = getAttributes(collection.getSchema().getAttributeDescriptors());
        for (AttributeDTO e: attributes) {
           attributeService.create(e, layerDTO.getLayerId());
        }
        FeatureIterator<SimpleFeature> featureIterator = collection.features();
        while (featureIterator.hasNext()) {
            SimpleFeature feature = featureIterator.next();
            SpatialData spatialData = new SpatialData();
            spatialData.setTheGeom((Geometry)feature.getAttribute("the_geom"));
            spatialData.setSource("shape file");
            spatialDataService.persistFeatures(spatialData, layerDTO.getLayerId());
            for (AttributeDTO e: attributes) {
                spatialDataAttributeService.create(feature.getAttribute(e.getAttributeName()).toString(), e, spatialData);
            }
        }
    }

    @Transactional
    public FeatureCollection<SimpleFeatureType, SimpleFeature> getLayerInfo(Long layerId) {
        LayerDTO layerDTO = getLayerById(layerId);
        List<AttributeDTO> dtos = attributeService.getLayerAttribs(layerId);
        SimpleFeatureType featureType = null;
        try {
            featureType = createFeatureType(dtos, layerDTO.getEpsgCode());
        } catch (FactoryException e) {
            e.printStackTrace();
        }
        DefaultFeatureCollection featureCollection = new DefaultFeatureCollection(null, featureType);
        List<SpatialData> spatialDataList = spatialDataService.getSpatialDatasByLayer(layerId);
        SimpleFeatureBuilder builder = new SimpleFeatureBuilder(featureType);
        for (SpatialData e: spatialDataList) {
            builder.set("the_geom", e.getTheGeom());
            for (SpatialDataAttribute a: e.getSpatialDataAttributes()) {
                builder.set(a.getAttribute().getAttributeName(), a.getValue());
            }
            SimpleFeature feature = builder.buildFeature(String.valueOf(e.getSpatialDataId()));
            System.out.println(feature.getDefaultGeometry().getClass());

            featureCollection.add(feature);
            builder.reset();
        }
        return featureCollection;
    }

    private SimpleFeatureType createFeatureType(List<AttributeDTO> dtos, Integer epsgCode) throws FactoryException {
        SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
        builder.setName("Location");
        builder.setCRS(CRS.decode("EPSG:"+epsgCode));
        builder.add("the_geom", Polygon.class);
        for (AttributeDTO e: dtos)
            builder.add(e.getAttributeName(), String.class);
        final SimpleFeatureType featureType = builder.buildFeatureType();
        return featureType;
    }

    private List<AttributeDTO> getAttributes(List<AttributeDescriptor> descriptors) {
        List<AttributeDTO> list = new ArrayList<AttributeDTO>(descriptors.size());
        for (AttributeDescriptor e: descriptors) {
            if("the_geom".equals(e.getName().toString())) continue;
            AttributeDTO dto = new AttributeDTO();
            dto.setAttributeName(e.getName().toString());
            dto.setClassType(e.getType().getBinding().getName());
            list.add(dto);
        }
        return list;
    }
}
