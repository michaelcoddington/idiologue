package org.idiologue.api;

import java.util.StringJoiner;

public class RelationshipModification {

    private Action action;

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", RelationshipModification.class.getSimpleName() + "[", "]")
                .add("action=" + action)
                .toString();
    }
}
