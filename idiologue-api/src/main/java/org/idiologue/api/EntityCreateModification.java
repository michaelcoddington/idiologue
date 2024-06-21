package org.idiologue.api;

import com.fasterxml.jackson.annotation.JsonTypeName;

import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;

@JsonTypeName(ActionConstants.CREATE)
public class EntityCreateModification extends EntityModification {

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EntityCreateModification that)) return false;
        return Objects.equals(type, that.type) && Objects.equals(properties, that.properties);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, properties);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", EntityCreateModification.class.getSimpleName() + "[", "]")
                .add("type='" + type + "'")
                .add("properties=" + properties)
                .toString();
    }
}
