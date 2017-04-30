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
import com.vividsolutions.jts.geom.*;
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
import java.util.*;

/**
 * Created by JORGE-HP on 25/4/2017.
 */
@Service
public class SpatialLayerServiceImpl implements SpatialLayerService {
    private static Map<String, Class> SUPPORT_GEOMETRIES = new HashMap<String, Class>();
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
        SUPPORT_GEOMETRIES.put("LineString", LineString.class);
        SUPPORT_GEOMETRIES.put("MultiLineString", MultiLineString.class);
        SUPPORT_GEOMETRIES.put("Polygon", Polygon.class);
        SUPPORT_GEOMETRIES.put("MultiPolygon", MultiPolygon.class);
        SUPPORT_GEOMETRIES.put("Point", Point.class);
        SUPPORT_GEOMETRIES.put("MultiPoint", MultiPoint.class);
    }

    @Transactional
    public LayerDTO getLayerById(Long layerId) {
        SpatialLayer spatialLayer = spatialLayerDAO.find(layerId);
        if(spatialLayer == null) return null;
        LayerDTO layerDTO= new LayerDTO();
        layerDTO.setEpsgCode(spatialLayer.getEpsgCode());
        layerDTO.setLayerName(spatialLayer.getLayerName());
        layerDTO.setLayerId(spatialLayer.getSpatialLayerId());
        return layerDTO;
    }

    @Transactional
    public void createSpatialLayer(LayerDTO layerDTO) {
        SpatialLayer spatialLayer = new SpatialLayer();
        spatialLayer.setEpsgCode(layerDTO.getEpsgCode());
        spatialLayer.setLayerName(layerDTO.getLayerName());
        spatialLayerDAO.save(spatialLayer);
        layerDTO.setLayerId(spatialLayer.getSpatialLayerId());
    }

    @Transactional
    public void updateSpatialLayer(LayerDTO layerDTO) {
        SpatialLayer spatialLayer = new SpatialLayer();
        spatialLayer.setSpatialLayerId(layerDTO.getLayerId());
        spatialLayer.setEpsgCode(layerDTO.getEpsgCode());
        spatialLayer.setLayerName(layerDTO.getLayerName());
        spatialLayerDAO.update(spatialLayer);
    }

    @Transactional
    public void deleteSpatialLayer(LayerDTO layerDTO) {
        SpatialLayer spatialLayer = new SpatialLayer();
        spatialLayer.setSpatialLayerId(layerDTO.getLayerId());
        spatialLayer.setEpsgCode(layerDTO.getEpsgCode());
        spatialLayer.setLayerName(layerDTO.getLayerName());
        spatialLayerDAO.remove(spatialLayer);
    }

    @Transactional
    public List<LayerDTO> list() {
        List<LayerDTO> result = new ArrayList<LayerDTO>();
        List<SpatialLayer> layers = spatialLayerDAO.findAll();
        for(SpatialLayer e: layers) {
            LayerDTO layerDTO = new LayerDTO();
            layerDTO.setLayerId(e.getSpatialLayerId());
            layerDTO.setLayerName(e.getLayerName());
            layerDTO.setEpsgCode(e.getEpsgCode());
            result.add(layerDTO);
        }
        return result;
    }

    @Transactional
    public void createLayerFeatures(LayerDTO layerDTO, FeatureCollection<SimpleFeatureType, SimpleFeature> collection) {
        SpatialLayer spatialLayer = spatialLayerDAO.find(layerDTO.getLayerId());
        if(spatialLayer == null) {
            createSpatialLayer(layerDTO);
        }
        else {
            spatialDataAttributeService.deleteAttributesValuesForLayer(layerDTO.getLayerId());
            attributeService.deleteAttributesForLayer(layerDTO.getLayerId());
            spatialDataService.deleteSpatialDataForLayer(layerDTO.getLayerId());
        }

        List<AttributeDTO> attributes = getAttributes(collection.getSchema().getAttributeDescriptors());
        for (AttributeDTO e: attributes) {
           attributeService.create(e, layerDTO.getLayerId());
        }
        FeatureIterator<SimpleFeature> featureIterator = collection.features();
        while (featureIterator.hasNext()) {
            SimpleFeature feature = featureIterator.next();
            SpatialData spatialData = new SpatialData();
            Geometry geometry = (Geometry)feature.getDefaultGeometry();
            spatialData.setTheGeom(geometry);
            spatialData.setSource("geoJson");
            spatialData.setGeometryType(geometry.getGeometryType());
            spatialDataService.persistFeatures(spatialData, layerDTO.getLayerId());
            for (AttributeDTO e: attributes) {
                spatialDataAttributeService.create(feature.getAttribute(e.getAttributeName()).toString(), e, spatialData);
            }
        }
    }

    @Transactional
    public void updateLayerFeatures(LayerDTO layerDTO, FeatureCollection<SimpleFeatureType, SimpleFeature> collection) {
        //TODO
    }

    @Transactional
    public FeatureCollection<SimpleFeatureType, SimpleFeature> getLayerInfo(Long layerId) throws Exception {
        LayerDTO layerDTO = getLayerById(layerId);
        List<AttributeDTO> dtos = attributeService.getLayerAttribs(layerId);
        Map<String, SimpleFeatureType> featureTypes = new HashMap<String, SimpleFeatureType>();
            featureTypes.put("LineString", createFeatureType(dtos, SUPPORT_GEOMETRIES.get("LineString"), layerDTO.getEpsgCode()));
            featureTypes.put("MultiLineString", createFeatureType(dtos, SUPPORT_GEOMETRIES.get("MultiLineString"), layerDTO.getEpsgCode()));
            featureTypes.put("Polygon", createFeatureType(dtos, SUPPORT_GEOMETRIES.get("Polygon"), layerDTO.getEpsgCode()));
            featureTypes.put("MultiPolygon", createFeatureType(dtos, SUPPORT_GEOMETRIES.get("MultiPolygon"), layerDTO.getEpsgCode()));
            featureTypes.put("Point", createFeatureType(dtos, SUPPORT_GEOMETRIES.get("Point"), layerDTO.getEpsgCode()));
            featureTypes.put("MultiPoint", createFeatureType(dtos, SUPPORT_GEOMETRIES.get("MultiPoint"), layerDTO.getEpsgCode()));
        DefaultFeatureCollection featureCollection = new DefaultFeatureCollection();
        List<SpatialData> spatialDataList = spatialDataService.getSpatialDatasByLayer(layerId);
        SimpleFeatureBuilder builder;
        for (SpatialData e: spatialDataList) {
            builder = new SimpleFeatureBuilder(featureTypes.get(e.getGeometryType()));
            builder.set("geometry", e.getTheGeom());
            for (SpatialDataAttribute a: e.getSpatialDataAttributes()) {
                builder.set(a.getAttribute().getAttributeName(), a.getValue());
            }
            SimpleFeature feature = builder.buildFeature(String.valueOf(e.getSpatialDataId()));
            featureCollection.add(feature);
            builder.reset();
        }
        return featureCollection;
    }


    private List<AttributeDTO> getAttributes(List<AttributeDescriptor> descriptors) {
        List<AttributeDTO> list = new ArrayList<AttributeDTO>(descriptors.size());
        for (AttributeDescriptor e: descriptors) {
            if("geometry".equals(e.getName().toString())) continue;
            AttributeDTO dto = new AttributeDTO();
            dto.setAttributeName(e.getName().toString());
            dto.setClassType(e.getType().getBinding().getName());
            list.add(dto);
        }
        return list;
    }
    private SimpleFeatureType createFeatureType(List<AttributeDTO> dtos, Class geometryType, Integer epsgCode) throws FactoryException {
        SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
        builder.setName("Location");
        builder.setCRS(CRS.decode("EPSG:" + epsgCode));
        builder.add("geometry", geometryType);
        for (AttributeDTO e: dtos)
            builder.add(e.getAttributeName(), String.class);
        final SimpleFeatureType featureType = builder.buildFeatureType();
        return featureType;
    }
}
