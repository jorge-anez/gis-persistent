package com.project.model.domain;

import javax.persistence.*;

/**
 * Created by JORGE-HP on 18/4/2017.
 */
@Entity
@Table(name = "t_spatial_layer_style")
@NamedQueries(
        {
                @NamedQuery(name="getLayerStyles", query="FROM SpatialLayerStyle ls LEFT JOIN FETCH ls.style s WHERE ls.spatialLayer.spatialLayerId = :layerId")
        }
)
public class SpatialLayerStyle {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long spatialLayerStyleId;
    private String value;
    @ManyToOne
    @JoinColumn(name="style_id")
    private Style style;

    @ManyToOne
    @JoinColumn(name="spatial_layer_id")
    private SpatialLayer spatialLayer;

    public Long getSpatialLayerStyleId() {
        return spatialLayerStyleId;
    }

    public void setSpatialLayerStyleId(Long spatialLayerStyleId) {
        this.spatialLayerStyleId = spatialLayerStyleId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Style getStyle() {
        return style;
    }

    public void setStyle(Style style) {
        this.style = style;
    }

    public SpatialLayer getSpatialLayer() {
        return spatialLayer;
    }

    public void setSpatialLayer(SpatialLayer spatialLayer) {
        this.spatialLayer = spatialLayer;
    }
}
