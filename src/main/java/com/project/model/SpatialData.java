package com.project.model;

import com.vividsolutions.jts.geom.Geometry;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Collection;

/**
 * Created by JORGE-HP on 18/4/2017.
 */
@Entity
@Table(name = "t_spacial_data")
public class SpatialData {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long   spatialDataId;
    private String source;

    @Type(type = "org.hibernate.spatial.GeometryType")
    private Geometry theGeom;

    @ManyToOne
    @JoinColumn(name="project_id")
    private Project project;

    @OneToMany(mappedBy="spatialData")
    private Collection<SpatialDataAttribute> spatialDataAttributes;

    public Long getSpatialDataId() {
        return spatialDataId;
    }

    public void setSpatialDataId(Long spatialDataId) {
        this.spatialDataId = spatialDataId;
    }

    public Geometry getTheGeom() {
        return theGeom;
    }

    public void setTheGeom(Geometry theGeom) {
        this.theGeom = theGeom;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Collection<SpatialDataAttribute> getSpatialDataAttributes() {
        return spatialDataAttributes;
    }

    public void setSpatialDataAttributes(Collection<SpatialDataAttribute> spatialDataAttributes) {
        this.spatialDataAttributes = spatialDataAttributes;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
