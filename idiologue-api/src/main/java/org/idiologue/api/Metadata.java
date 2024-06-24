package org.idiologue.api;

public abstract class Metadata<T> {

    public abstract T getValue();
    public abstract void setValue(T value);

}
