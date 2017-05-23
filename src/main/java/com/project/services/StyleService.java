package com.project.services;


import com.project.model.domain.Attribute;
import com.project.model.domain.Style;
import com.project.model.transfer.AttributeDTO;

import java.util.List;

/**
 * Created by JORGE-HP on 18/4/2017.
 */
public interface StyleService {
    void create(String name, String defaultValue);
    List<Style> getStyles(List<String> styleNames);
}
