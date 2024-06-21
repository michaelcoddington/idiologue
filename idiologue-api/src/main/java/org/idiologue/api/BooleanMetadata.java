package org.idiologue.api;

import com.fasterxml.jackson.annotation.JsonTypeName;

import java.util.StringJoiner;

@JsonTypeName(TypeConstants.STRING)
public class BooleanMetadata extends Metadata<Boolean> {

    private Boolean value;

    @Override
    public Boolean getValue() {
        return value;
    }

    @Override
    public void setValue(Boolean value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", BooleanMetadata.class.getSimpleName() + "[", "]")
                .add("value=" + value)
                .toString();
    }

}
