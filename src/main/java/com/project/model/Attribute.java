package com.project.model;

import javax.persistence.*;
import java.util.Collection;

/**
 * Created by JORGE-HP on 18/4/2017.
 */
@Entity
@Table(name = "t_attribute")
public class Attribute {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long attributeId;
    private String attributeName;

    @OneToMany(mappedBy="attribute")
    private Collection<SpatialDataAttribute> spatialDataAttribute;

    public Long getAttributeId() {
        return attributeId;
    }

    public void setAttributeId(Long attributeId) {
        this.attributeId = attributeId;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public Collection<SpatialDataAttribute> getSpatialDataAttribute() {
        return spatialDataAttribute;
    }

    public void setSpatialDataAttribute(Collection<SpatialDataAttribute> spatialDataAttribute) {
        this.spatialDataAttribute = spatialDataAttribute;
    }
}
