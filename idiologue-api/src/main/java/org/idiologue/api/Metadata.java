package org.idiologue.api;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = BooleanMetadata.class, name = TypeConstants.BOOLEAN),
        @JsonSubTypes.Type(value = StringMetadata.class, name = TypeConstants.STRING)
})
public abstract class Metadata<T> {

    public abstract T getValue();
    public abstract void setValue(T value);

}
