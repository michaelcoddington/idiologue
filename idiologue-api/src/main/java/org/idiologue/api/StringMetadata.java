package org.idiologue.api;

import com.fasterxml.jackson.annotation.JsonTypeName;

import java.util.Objects;
import java.util.StringJoiner;

@JsonTypeName(TypeConstants.STRING)
public class StringMetadata extends Metadata<String> {

    private String value;

    public StringMetadata() {

    }

    public StringMetadata(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StringMetadata that)) return false;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", StringMetadata.class.getSimpleName() + "[", "]")
                .add("value='" + value + "'")
                .toString();
    }
}
