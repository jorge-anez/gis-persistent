package com.project.services;

import com.project.dao.GenericDAOImpl;
import com.project.model.Attribute;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;

/**
 * Created by JORGE-HP on 18/4/2017.
 */
@Service
public class AttributeServiceImpl implements AttributeService{
    @Autowired
    private SessionFactory sessionFactory;
    private GenericDAOImpl<Attribute, Long> attributeDAO;

    @PostConstruct
    public void init() {
        attributeDAO = new GenericDAOImpl<Attribute, Long>(sessionFactory, Attribute.class);
    }
    @Transactional
    public Attribute create(String propertyName) {
        Attribute attribute = new Attribute();
        attribute.setAttributeName(propertyName);
        attributeDAO.save(attribute);
        return attribute;
    }

    @Transactional
    public Attribute findByName(String name) {
        Criterion criterion = Restrictions.eq("attributeName", name);
        Attribute attribute = attributeDAO.findByCriteriauniqueResult(criterion);
        return  attribute;
    }

}
