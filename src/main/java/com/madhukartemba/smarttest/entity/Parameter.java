package com.madhukartemba.smarttest.entity;

import java.util.Objects;

public class Parameter<T> {
    private final String name;
    private final String aliasName;
    private T value;
    private boolean modified = false;

    public Parameter(String name, String aliasName, T value) {
        this.name = name;
        this.aliasName = aliasName;
        this.value = value;
    }

    public boolean isModified() {
        return modified;
    }

    public String getName() {
        return name;
    }

    public String getAliasName() {
        return aliasName;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.modified = true;
        this.value = value;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Parameter)) {
            return false;
        }
        Parameter<?> other = (Parameter<?>) obj;
        return Objects.equals(name, other.name) && Objects.equals(aliasName, other.aliasName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, aliasName);
    }

}
