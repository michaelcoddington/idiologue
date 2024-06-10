package org.idiologue.api.predicate;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName(PredicateTypeConstants.REFID)
public class RefIdPredicate extends EntityPredicate {

    private String ref;

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

}
