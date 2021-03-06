package com.project.model.domain;

import javax.persistence.*;
import java.util.Collection;

/**
 * Created by JORGE-HP on 18/4/2017.
 */
@Entity
@Table(name = "t_attribute")
@NamedQueries(
        {@NamedQuery(name="getAttributesForLayer", query="FROM Attribute a WHERE a.spatialLayer.spatialLayerId = :spatialLayerId")}
)
public class Attribute {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long attributeId;

    private String attributeName;
    private String attributeType;

    @OneToMany(mappedBy="attribute")
    private Collection<SpatialDataAttribute> spatialDataAttribute;

    @ManyToOne
    @JoinColumn(name="spatial_layer_id")
    private SpatialLayer spatialLayer;


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

    public SpatialLayer getSpatialLayer() {
        return spatialLayer;
    }

    public void setSpatialLayer(SpatialLayer spatialLayer) {
        this.spatialLayer = spatialLayer;
    }

    public String getAttributeType() {
        return attributeType;
    }

    public void setAttributeType(String attributeType) {
        this.attributeType = attributeType;
    }
}
