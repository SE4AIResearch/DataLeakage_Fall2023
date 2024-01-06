package com.github.cd721.data_leakage_plugin.enums;

public enum TaintLabel {
    dup("dup") ,
    rowset("rowset");

    private final String label;

    TaintLabel(String label){
        this.label = label;
    }

    @Override
    public String toString(){
        return label;
    }
}
