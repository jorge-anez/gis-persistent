package com.project.model.domain;

import javax.persistence.*;
import java.util.Collection;

/**
 * Created by JORGE-HP on 18/4/2017.
 */
@Entity
@Table(name = "t_spatial_layer")
@NamedQueries(
        {@NamedQuery(name="spatialQuery", query="FROM SpatialData s LEFT JOIN FETCH s.spatialDataAttributes sd LEFT JOIN FETCH sd.attribute a")}
)
public class SpatialLayer {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long spatialLayerId;
    private String layerName;
    private Integer epsgCode;

    @ManyToOne
    @JoinColumn(name="project_id")
    private Project project;

    @OneToMany(mappedBy="spatialLayer")
    private Collection<SpatialData> spatialData;

    @OneToMany(mappedBy="spatialLayer")
    private Collection<Attribute> attributes;

    public Long getSpatialLayerId() {
        return spatialLayerId;
    }

    public void setSpatialLayerId(Long spatialLayerId) {
        this.spatialLayerId = spatialLayerId;
    }

    public String getLayerName() {
        return layerName;
    }

    public void setLayerName(String layerName) {
        this.layerName = layerName;
    }

    public Integer getEpsgCode() {
        return epsgCode;
    }

    public void setEpsgCode(Integer epsgCode) {
        this.epsgCode = epsgCode;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Collection<SpatialData> getSpatialData() {
        return spatialData;
    }

    public void setSpatialData(Collection<SpatialData> spatialData) {
        this.spatialData = spatialData;
    }

    public Collection<Attribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(Collection<Attribute> attributes) {
        this.attributes = attributes;
    }
}
