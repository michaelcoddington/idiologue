package org.idiologue.api;

import java.util.Map;
import java.util.StringJoiner;

public class EntityModification {

    private Action action;

    private String type;

    private Map<String, Metadata> properties;

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

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

    @Override
    public String toString() {
        return new StringJoiner(", ", EntityModification.class.getSimpleName() + "[", "]")
                .add("action=" + action)
                .add("type='" + type + "'")
                .add("properties=" + properties)
                .toString();
    }

}
