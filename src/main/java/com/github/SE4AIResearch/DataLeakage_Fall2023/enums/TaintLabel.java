package com.github.SE4AIResearch.DataLeakage_Fall2023.enums;

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
