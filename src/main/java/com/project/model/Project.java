package com.project.model;

import com.vividsolutions.jts.geom.Geometry;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Collection;

/**
 * Created by JORGE-HP on 18/4/2017.
 */
@Entity
@Table(name = "t_project")
public class Project {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long projectId;
    private String projectName;
    @OneToMany(mappedBy="project")
    private Collection<SpatialData> spatialData;

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

    public Collection<SpatialData> getSpatialData() {
        return spatialData;
    }

    public void setSpatialData(Collection<SpatialData> spatialData) {
        this.spatialData = spatialData;
    }
}
