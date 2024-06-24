package org.idiologue.api;

import java.util.Map;

public class Entity {

    private String type;

    private Map<String, ReadableMetadata> properties;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, ReadableMetadata> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, ReadableMetadata> properties) {
        this.properties = properties;
    }

}
