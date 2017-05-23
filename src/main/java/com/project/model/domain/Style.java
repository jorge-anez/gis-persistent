package com.project.model.domain;

import javax.persistence.*;
import java.util.Collection;

/**
 * Created by JORGE-HP on 18/4/2017.
 */
@Entity
@Table(name = "t_style")
@NamedQueries(
    {
        @NamedQuery(name="getStyles", query="FROM Style s WHERE concat(s.geometryTypeStyle, '.', s.styleName) IN (:styles)")
    }
)
public class Style {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long styleId;
    private String styleName;
    private String geometryTypeStyle;
    private String defaultValue;
    @OneToMany(mappedBy="style")
    private Collection<SpatialLayerStyle> spatialLayerAttribute;

    public Long getStyleId() {
        return styleId;
    }

    public void setStyleId(Long styleId) {
        this.styleId = styleId;
    }

    public String getStyleName() {
        return styleName;
    }

    public void setStyleName(String styleName) {
        this.styleName = styleName;
    }

    public String getGeometryTypeStyle() {
        return geometryTypeStyle;
    }

    public void setGeometryTypeStyle(String geometryTypeStyle) {
        this.geometryTypeStyle = geometryTypeStyle;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public Collection<SpatialLayerStyle> getSpatialLayerAttribute() {
        return spatialLayerAttribute;
    }

    public void setSpatialLayerAttribute(Collection<SpatialLayerStyle> spatialLayerAttribute) {
        this.spatialLayerAttribute = spatialLayerAttribute;
    }
}
