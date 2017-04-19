package com.project.model;

import javax.persistence.*;

/**
 * Created by JORGE-HP on 18/4/2017.
 */
@Entity
@Table(name = "t_spatial_data_attribute")
public class SpatialDataAttribute {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long spatialDataAttributeId;
    private String value;

    @ManyToOne
    @JoinColumn(name="attribute_id")
    private Attribute attribute;

    @ManyToOne
    @JoinColumn(name="spatial_data_id")
    private SpatialData spatialData;

    public Long getSpatialDataAttributeId() {
        return spatialDataAttributeId;
    }

    public void setSpatialDataAttributeId(Long spatialDataAttributeId) {
        this.spatialDataAttributeId = spatialDataAttributeId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Attribute getAttribute() {
        return attribute;
    }

    public void setAttribute(Attribute attribute) {
        this.attribute = attribute;
    }

    public SpatialData getSpatialData() {
        return spatialData;
    }

    public void setSpatialData(SpatialData spatialData) {
        this.spatialData = spatialData;
    }
}
