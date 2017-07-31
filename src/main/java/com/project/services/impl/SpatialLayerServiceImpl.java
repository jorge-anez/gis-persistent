package com.project.services.impl;

import com.project.dao.GenericDAOImpl;
import com.project.model.domain.*;
import com.project.model.transfer.AttributeDTO;
import com.project.model.transfer.LayerDTO;
import com.project.services.*;
import com.vividsolutions.jts.geom.*;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.referencing.factory.AbstractAuthorityFactory;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
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
import org.springframework.util.StringUtils;

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
    private SpatialLayerStyleService spatialLayerStyleService;

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
        layerDTO.setLayerType(spatialLayer.getLayerType());
        layerDTO.setLayerId(spatialLayer.getSpatialLayerId());
        return layerDTO;
    }

    @Transactional
    public List<AttributeDTO> listAttributeForLayer(Long layerId) {
        List<AttributeDTO> list = attributeService.getLayerAttribs(layerId);
        return list;
    }

    @Transactional
    public void createSpatialLayer(Long projectId, LayerDTO layerDTO) {
        SpatialLayer spatialLayer = new SpatialLayer();
        if(projectId != null) {
            Project project = new Project();
            project.setProjectId(projectId);
            spatialLayer.setProject(project);
        }
        spatialLayer.setEpsgCode(layerDTO.getEpsgCode());
        spatialLayer.setLayerName(layerDTO.getLayerName());
        spatialLayer.setLayerType(layerDTO.getLayerType());
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
    public void deleteSpatialLayer(Long layerId) {
        spatialDataAttributeService.deleteAttributesValuesForLayer(layerId);
        attributeService.deleteAttributesForLayer(layerId);
        spatialDataService.deleteSpatialDataForLayer(layerId);
        spatialLayerStyleService.deleteStyles(layerId);
        SpatialLayer spatialLayer = new SpatialLayer();
        spatialLayer.setSpatialLayerId(layerId);
        spatialLayerDAO.remove(spatialLayer);
    }

    @Transactional
    public LayerDTO getBaseLayer() {
        Criterion criterion = Restrictions.eq("layerType", "BASE_LAYER");
        List<SpatialLayer> layers = spatialLayerDAO.findByCriteria(criterion);
        if(layers != null && layers.size() > 0) {
            LayerDTO layerDTO = new LayerDTO();
            SpatialLayer layer = layers.get(0);
            layerDTO.setLayerId(layer.getSpatialLayerId());
            layerDTO.setLayerName(layer.getLayerName());
            layerDTO.setEpsgCode(layer.getEpsgCode());
            return layerDTO;
        }

        return null;
    }

    @Transactional
    public LayerDTO getClasificacionSectorialLayer() {
        Criterion criterion = Restrictions.eq("layerType", "CLASIFICACION_SECTORIAL");
        List<SpatialLayer> layers = spatialLayerDAO.findByCriteria(criterion);
        if(layers != null && layers.size() > 0) {
            LayerDTO layerDTO = new LayerDTO();
            SpatialLayer layer = layers.get(0);
            layerDTO.setLayerId(layer.getSpatialLayerId());
            layerDTO.setLayerName(layer.getLayerName());
            layerDTO.setEpsgCode(layer.getEpsgCode());
            return layerDTO;
        }

        return null;
    }

    @Transactional
    public List<LayerDTO> list(Long projectId) {
        Query query = spatialLayerDAO.getNamedQuery("listLayerForProject");
        query.setParameter("projectId", projectId);
        List<SpatialLayer> layers = query.list();
        List<LayerDTO> result = new ArrayList<LayerDTO>();
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
    public void createLayerFeatures(Long layerId, FeatureCollection<SimpleFeatureType, SimpleFeature> collection, List<AttributeDTO> attributes) {
        spatialDataAttributeService.deleteAttributesValuesForLayer(layerId);
        attributeService.deleteAttributesForLayer(layerId);
        spatialDataService.deleteSpatialDataForLayer(layerId);

        for (AttributeDTO e: attributes) {
           attributeService.create(e, layerId);
        }
        FeatureIterator<SimpleFeature> featureIterator = collection.features();
        while (featureIterator.hasNext()) {
            SimpleFeature feature = featureIterator.next();
            SpatialData spatialData = new SpatialData();
            Geometry geometry = (Geometry)feature.getDefaultGeometry();
            spatialData.setTheGeom(geometry);
            spatialData.setSource("geoJson");
            spatialData.setGeometryType(geometry.getGeometryType());
            spatialDataService.persistFeatures(spatialData, layerId);
            for (AttributeDTO e: attributes) {
                String value = feature.getAttribute(e.getAttributeName()) == null? "": feature.getAttribute(e.getAttributeName()).toString();
                spatialDataAttributeService.create(value, e, spatialData);
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
            builder.set("the_geom", e.getTheGeom());
            builder.set("geometryType", e.getGeometryType());
            for (SpatialDataAttribute a: e.getSpatialDataAttributes()) {
                builder.set(a.getAttribute().getAttributeName(), StringUtils.isEmpty(a.getValue())? null: a.getValue());
            }
            SimpleFeature feature = builder.buildFeature(String.valueOf(e.getSpatialDataId()));
            featureCollection.add(feature);
            builder.reset();
        }
        return featureCollection;
    }

    private SimpleFeatureType createFeatureType(List<AttributeDTO> dtos, Class geometryType, Integer epsgCode) throws FactoryException, ClassNotFoundException {
        SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
        builder.setName("Location");
        builder.setCRS(CRS.decode("EPSG:" + epsgCode));
        builder.add("the_geom", geometryType);
        //builder.add("geometry", geometryType);
        builder.add("geometryType", String.class);
        for (AttributeDTO e: dtos)
            builder.add(e.getAttributeName(), Class.forName("java.lang." + e.getAttributeType()));
        final SimpleFeatureType featureType = builder.buildFeatureType();
        return featureType;
    }
}
