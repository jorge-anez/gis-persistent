package com.project.services;


import com.project.model.Attribute;

/**
 * Created by JORGE-HP on 18/4/2017.
 */
public interface AttributeService {
    Attribute create(String propertyName);
    public Attribute findByName(String name);
}
