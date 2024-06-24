package org.idiologue.api;

import com.fasterxml.jackson.annotation.JsonTypeName;

import java.util.Map;
import java.util.Objects;

@JsonTypeName(ActionConstants.UPDATE)
public class EntityUpdateModification extends EntityModification {

    private Long id;

    private Map<String, WritableMetadata> properties;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Map<String, WritableMetadata> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, WritableMetadata> properties) {
        this.properties = properties;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EntityUpdateModification that)) return false;
        return Objects.equals(id, that.id) && Objects.equals(properties, that.properties);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, properties);
    }
}
