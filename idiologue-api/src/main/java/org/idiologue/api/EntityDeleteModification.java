package org.idiologue.api;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName(ActionConstants.DELETE)
public class EntityDeleteModification {

    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
