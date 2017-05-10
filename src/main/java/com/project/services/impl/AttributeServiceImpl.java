package com.project.services.impl;

import com.project.dao.GenericDAOImpl;
import com.project.model.domain.Attribute;
import com.project.model.domain.SpatialLayer;
import com.project.model.transfer.AttributeDTO;
import com.project.services.AttributeService;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by JORGE-HP on 18/4/2017.
 */
@Service
public class AttributeServiceImpl implements AttributeService {
    @Autowired
    private SessionFactory sessionFactory;
    private GenericDAOImpl<Attribute, Long> attributeDAO;

    @PostConstruct
    public void init() {
        attributeDAO = new GenericDAOImpl<Attribute, Long>(sessionFactory, Attribute.class);
    }
    @Transactional
    public void create(AttributeDTO attributeDTO, Long spatialLayerId) {
        Attribute attribute = new Attribute();
        SpatialLayer spatialLayer = new SpatialLayer();
        spatialLayer.setSpatialLayerId(spatialLayerId);
        attribute.setAttributeName(attributeDTO.getAttributeName());
        attribute.setAttributeType(attributeDTO.getAttributeType());
        attribute.setSpatialLayer(spatialLayer);
        attributeDAO.save(attribute);
        attributeDTO.setAttributeId(attribute.getAttributeId());
    }

    @Transactional
    public Attribute findByName(String name) {
        Criterion criterion = Restrictions.eq("attributeName", name);
        Attribute attribute = attributeDAO.findByCriteriauniqueResult(criterion);
        return  attribute;
    }

    public void deleteAttributesForLayer(Long layerId) {
        String sql = String.format("DELETE FROM t_attribute WHERE spatial_layer_id = %d", layerId);
        sessionFactory.getCurrentSession().createSQLQuery(sql).executeUpdate();
    }

    @Transactional
    public List<AttributeDTO> getLayerAttribs(Long layerId) {
        Query query = attributeDAO.getNamedQuery("getAttributesForLayer");
        query.setParameter("spatialLayerId", layerId);
        List<Attribute> attributes = query.list();
        List<AttributeDTO> dtos = new ArrayList<AttributeDTO>(attributes.size());
        for (Attribute e: attributes) {
            AttributeDTO dto = new AttributeDTO();
            dto.setAttributeId(e.getAttributeId());
            dto.setAttributeName(e.getAttributeName());
            dto.setAttributeType(e.getAttributeType());
            dtos.add(dto);
        }
        return dtos;
    }

    @Transactional
    public Attribute getAttributesForLayer(Long spatialLayerId) {
        Query query = attributeDAO.getNamedQuery("getAttributesForLayer");
        query.setParameter("spatialLayerId", spatialLayerId);
        Attribute attribute = (Attribute) query.list();
        return  attribute;
    }
}
