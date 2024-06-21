package org.idiologue.api;

import com.fasterxml.jackson.annotation.JsonTypeName;

import java.util.Map;

@JsonTypeName(ActionConstants.UPDATE)
public class EntityUpdateModification {

    private Long id;

    private Map<String, Metadata> properties;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Map<String, Metadata> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Metadata> properties) {
        this.properties = properties;
    }

}
