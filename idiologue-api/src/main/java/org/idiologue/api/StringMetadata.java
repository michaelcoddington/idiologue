package org.idiologue.api;

import com.fasterxml.jackson.annotation.JsonTypeName;

import java.util.StringJoiner;

@JsonTypeName(TypeConstants.STRING)
public class StringMetadata extends Metadata<String> {

    private String value;

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", StringMetadata.class.getSimpleName() + "[", "]")
                .add("value='" + value + "'")
                .toString();
    }
}
