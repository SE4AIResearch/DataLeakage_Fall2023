package com.github.cd721.data_leakage_plugin.enums;

public enum OverlapLeakTaintMethod {
    fit_resample("fit_resample") ,
    fit_sample("fit_sample");

    private final String method;

    OverlapLeakTaintMethod(String method){
        this.method = method;
    }

    @Override
    public String toString(){
        return method;
    }
}
