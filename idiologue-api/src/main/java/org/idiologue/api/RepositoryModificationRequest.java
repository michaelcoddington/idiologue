package org.idiologue.api;

import java.util.List;
import java.util.StringJoiner;

/**
 * A request to modify the repository in some way, creating or removing some combination
 * of entities and relationships.
 */
public class RepositoryModificationRequest {

    private List<RelationshipModification> relationships;

    public List<RelationshipModification> getRelationships() {
        return relationships;
    }

    public void setRelationships(List<RelationshipModification> relationships) {
        this.relationships = relationships;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", RepositoryModificationRequest.class.getSimpleName() + "[", "]")
                .add("relationships=" + relationships)
                .toString();
    }
}
