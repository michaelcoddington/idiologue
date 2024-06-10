package org.idiologue.api.predicate;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.idiologue.api.BooleanMetadata;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = BooleanMetadata.class, name = PredicateTypeConstants.REFID)
})
public abstract class EntityPredicate {
}
