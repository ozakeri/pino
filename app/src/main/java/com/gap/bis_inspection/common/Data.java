package com.gap.bis_inspection.common;

/**
 * Created by root on 8/28/16.
 */
public class Data {
    private String key;
    private Object value;

    public Data() {
    }

    public Data(String key, Object value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
