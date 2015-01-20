package com.msf.form.data;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum FormType implements Type<FormType>{
    
    EPID(1, "Epidemics", "EPID"),
    OPD(2, "Mental Health", "OPD"),
    IPD(3, "Hospital IPD", "IPD"),
    CNCD(4, "NCD", "CNCD"),
    SGBV(5, "SGBV", "SGBV"),
    NUT(6, "Nutrition", "NUT"),
    HIV(7, "TB / HIV", "HIV"),
    AGG_OPD(8, "General OPD", "AGG_OPD"),
    AGG_VAC(9, "Vaccination", "AGG_VAC"),
    AGG_NUT(10, "Nutrition", "AGG_NUT");
    
    private int value;
    private String type;
    private String label;
    
    private FormType(int value, String type, String label) {
        this.value = value;
        this.type = type;
        this.label = label;
    }
    
    private static Map<Integer, FormType> veMap = new HashMap<Integer, FormType>();
    
    static{
        for(FormType e : FormType.values()){
            veMap.put(e.value, e);
        }
    }
    
    public static boolean isExistCode(String code){
        try {
            int value = Integer.valueOf(code);
            return veMap.get(value) != null;
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    public String toString() {
        return String.valueOf(value);
    }
    
    @Override
    public String getName() {
        return name();
    }
    
    @Override
    public String getType() {
        return type;
    }
    
    @Override
    public int getValue() {
        return value;
    }
    
    @Override
    public String getLabel() {
        return label;
    }
    
    public String inspect() {
        return FormType.class.getSimpleName() + "." + type + ": " + value + "";
    }
    
    @Override
    public List<FormType> getAll() {
        return Arrays.asList(values());
    }
    
    public static FormType create(int value) {
        for (FormType type : FormType.values()) {
            if (type.value == value) {
                return type;
            }
        }
        return null;
    }
}
