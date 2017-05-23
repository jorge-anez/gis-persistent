package com.project.services.impl;

import com.project.dao.GenericDAOImpl;
import com.project.model.domain.Attribute;
import com.project.model.domain.SpatialLayer;
import com.project.model.domain.Style;
import com.project.model.transfer.AttributeDTO;
import com.project.services.AttributeService;
import com.project.services.StyleService;
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
public class StyleServiceImpl implements StyleService {
    @Autowired
    private SessionFactory sessionFactory;
    private GenericDAOImpl<Style, Long> styleDAO;

    @PostConstruct
    public void init() {
        styleDAO = new GenericDAOImpl<Style, Long>(sessionFactory, Style.class);
    }


    @Transactional
    public void create(String name, String defaultValue) {

    }

    @Transactional
    public List<Style> getStyles(List<String> styleNames) {
        Query query = styleDAO.getNamedQuery("getStyles");
        query.setParameterList("styles", styleNames);
        return query.list();
    }
}
