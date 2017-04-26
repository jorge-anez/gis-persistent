package com.project.model.transfer;

/**
 * Created by JORGE-HP on 25/4/2017.
 */
public class AttributeDTO {
    private Long attributeId;
    private String attributeName;
    private String classType;

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

    public String getClassType() {
        return classType;
    }

    public void setClassType(String classType) {
        this.classType = classType;
    }
}
