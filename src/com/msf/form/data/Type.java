package com.msf.form.data;

import java.util.List;

public interface Type<T extends Type<T>> {
    public String getType();

    public int getValue();

    public String getLabel();

    public String getName();

    public String inspect();

    public List<T> getAll();
}
