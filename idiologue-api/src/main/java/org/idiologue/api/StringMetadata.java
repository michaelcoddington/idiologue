package org.idiologue.api;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName(TypeConstants.STRING)
public class StringMetadata extends Metadata<String> {

    private String value;

    @Override
    public String getVablue() {
        return value;
    }

    @Override
    public void setValue(String value) {
        this.value = value;
    }

}
