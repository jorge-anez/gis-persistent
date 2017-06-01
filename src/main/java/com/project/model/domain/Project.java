package com.project.model.domain;

import javax.persistence.*;
import java.util.Collection;

/**
 * Created by JORGE-HP on 18/4/2017.
 */
@Entity
@Table(name = "proyecto")
public class Project {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "id_proyecto")
    private Long projectId;
    @Column(name = "proyecto")
    private String projectName;
    @OneToMany(mappedBy="project")
    private Collection<SpatialLayer> spatialLayers;

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public Collection<SpatialLayer> getSpatialLayers() {
        return spatialLayers;
    }

    public void setSpatialLayers(Collection<SpatialLayer> spatialLayers) {
        this.spatialLayers = spatialLayers;
    }

}
