package org.idiologue.api;

import com.fasterxml.jackson.annotation.JsonTypeName;

import java.util.Objects;
import java.util.StringJoiner;

@JsonTypeName(ActionConstants.DELETE)
public class EntityDeleteModification extends EntityModification {

    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EntityDeleteModification that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", EntityDeleteModification.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .toString();
    }
}
