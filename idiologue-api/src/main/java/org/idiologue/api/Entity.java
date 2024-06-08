package org.idiologue.api;

import java.util.Map;

public class Entity {

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
