package com.project.model.domain;

import com.vividsolutions.jts.geom.Geometry;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Collection;

/**
 * Created by JORGE-HP on 18/4/2017.
 */
@Entity
@Table(name = "t_spatial_data")
@NamedQueries(
        {
             @NamedQuery(name="getSpatialDataAndAttribs", query="FROM SpatialData s LEFT JOIN FETCH s.spatialDataAttributes sd LEFT JOIN FETCH sd.attribute a WHERE s.spatialLayer.spatialLayerId = :layerId")
            ,@NamedQuery(name="getSpatialDataForLayer", query="FROM SpatialData s WHERE s.spatialLayer.spatialLayerId = :spatialLayerId")
        }
)
public class SpatialData {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long   spatialDataId;
    private String source;

    @Type(type = "org.hibernate.spatial.GeometryType")
    private Geometry theGeom;

    @ManyToOne
    @JoinColumn(name="spatial_layer_id")
    private SpatialLayer spatialLayer;

    @OneToMany(mappedBy="spatialData")
    private Collection<SpatialDataAttribute> spatialDataAttributes;

    public Long getSpatialDataId() {
        return spatialDataId;
    }

    public void setSpatialDataId(Long spatialDataId) {
        this.spatialDataId = spatialDataId;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Geometry getTheGeom() {
        return theGeom;
    }

    public void setTheGeom(Geometry theGeom) {
        this.theGeom = theGeom;
    }

    public SpatialLayer getSpatialLayer() {
        return spatialLayer;
    }

    public void setSpatialLayer(SpatialLayer spatialLayer) {
        this.spatialLayer = spatialLayer;
    }

    public Collection<SpatialDataAttribute> getSpatialDataAttributes() {
        return spatialDataAttributes;
    }

    public void setSpatialDataAttributes(Collection<SpatialDataAttribute> spatialDataAttributes) {
        this.spatialDataAttributes = spatialDataAttributes;
    }
}
