package com.project.services.impl;

import com.project.dao.GenericDAOImpl;
import com.project.model.domain.*;
import com.project.services.*;
import com.vividsolutions.jts.geom.Geometry;
import org.apache.commons.io.IOUtils;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.opengis.feature.simple.SimpleFeatureType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by JORGE-HP on 18/4/2017.
 */
@Service
public class SpatialLayerStyleServiceImpl implements SpatialLayerStyleService {
    @Autowired
    private StyleService styleService;
    @Autowired
    private SessionFactory sessionFactory;
    private GenericDAOImpl<SpatialLayerStyle, Long> spatialLayerStyleDAO;
    @Value(value = "classpath:defaultStyle.sld")
    private Resource sldResouce;

    @PostConstruct
    public void init() {
        spatialLayerStyleDAO = new GenericDAOImpl<SpatialLayerStyle, Long>(sessionFactory, SpatialLayerStyle.class);
    }

    public void persistStyle(Map<String, String> styles, Long layerId) {

    }

    @Transactional
    public Map<String, String> getSpatialLayerStyles(Long layerId, List<String> styleNames) {
        Map<String, String> result = new HashMap<String, String>();
        List<Style> styles = styleService.getStyles(styleNames);
        Query query = spatialLayerStyleDAO.getNamedQuery("getLayerStyles");
        query.setParameter("layerId", layerId);
        List<SpatialLayerStyle> spatialLayerStyles = query.list();
        Map<Long, SpatialLayerStyle> map = new HashMap<Long, SpatialLayerStyle>(spatialLayerStyles.size());
        for(SpatialLayerStyle e: spatialLayerStyles)
            map.put(e.getStyle().getStyleId(), e);
        for(Style s: styles) {
            SpatialLayerStyle layerStyle = map.get(s.getStyleId());
            String key = String.format("${%s.%s}", s.getGeometryTypeStyle(), s.getStyleName());
            if(layerStyle == null)
                result.put(key, s.getDefaultValue());
            else
                result.put(key, layerStyle.getValue());
        }
        return result;
    }

    @Transactional
    public String readSLDStyle(Long layerId) throws Exception {
        InputStream input = sldResouce.getInputStream();
        String buffer = IOUtils.toString(input);
        List<String> vars = find(buffer);
        String result = "";
        if(vars == null || vars.isEmpty()) {
            result = buffer;
        } else {
            Map<String, String> map = this.getSpatialLayerStyles(layerId, vars);
            result = replaceVariables(buffer, map);
        }
        input.close();
        return result;
    }

    private List<String> find(String string) {
        Pattern pattern = Pattern.compile("(\\$\\{)([^}]+)(\\})");
        Matcher matcher = pattern.matcher(string);
        List<String> listMatches = new ArrayList<String>();
        while(matcher.find()){
            listMatches.add(matcher.group(2));
        }
        return listMatches;
    }

    private String replaceVariables(String line, Map<String, String> replacements) {
        String rx = "(\\$\\{[^}]+\\})";
        StringBuffer sb = new StringBuffer();
        Pattern p = Pattern.compile(rx);
        Matcher m = p.matcher(line);
        while (m.find()) {
            String repString = replacements.get(m.group(1));
            if (repString != null)
                m.appendReplacement(sb, repString);
        }
        m.appendTail(sb);
        return sb.toString();
    }
}
