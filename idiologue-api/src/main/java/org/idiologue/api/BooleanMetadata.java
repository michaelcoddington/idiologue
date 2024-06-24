package org.idiologue.api;

import com.fasterxml.jackson.annotation.JsonTypeName;

import java.util.Objects;
import java.util.StringJoiner;

@JsonTypeName(TypeConstants.STRING)
public class BooleanMetadata extends Metadata<Boolean> implements ReadableMetadata, WritableMetadata {

    private Boolean value;

    public BooleanMetadata() {

    }

    public BooleanMetadata(boolean b) {
        this.value = b;
    }

    @Override
    public Boolean getValue() {
        return value;
    }

    @Override
    public void setValue(Boolean value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BooleanMetadata that)) return false;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", BooleanMetadata.class.getSimpleName() + "[", "]")
                .add("value=" + value)
                .toString();
    }

}
