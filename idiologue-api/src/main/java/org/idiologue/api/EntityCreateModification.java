package org.idiologue.api;

import com.fasterxml.jackson.annotation.JsonTypeName;

import java.util.Map;

@JsonTypeName(ActionConstants.CREATE)
public class EntityCreateModification {

    private String type;

    private Map<String, Metadata> properties;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, Metadata> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Metadata> properties) {
        this.properties = properties;
    }

}
