package org.idiologue.api;

import com.fasterxml.jackson.annotation.JsonTypeName;

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

}
