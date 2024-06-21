package org.idiologue.api;

import java.util.List;
import java.util.StringJoiner;

/**
 * A request to modify the repository in some way, creating or removing some combination
 * of entities and relationships.
 */
public class RepositoryModificationRequest {

    private List<EntityModification> entities;

    public List<EntityModification> getEntities() {
        return entities;
    }

    public void setEntities(List<EntityModification> entities) {
        this.entities = entities;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", RepositoryModificationRequest.class.getSimpleName() + "[", "]")
                .add("entities=" + entities)
                .toString();
    }
}
