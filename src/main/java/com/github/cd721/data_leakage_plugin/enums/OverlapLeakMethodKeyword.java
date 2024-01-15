package com.github.cd721.data_leakage_plugin.enums;

public enum OverlapLeakMethodKeyword {
    sample("sample"),
    flow("flow");

    private final String methodKeyword;

    OverlapLeakMethodKeyword(String methodKeyword) {
        this.methodKeyword = methodKeyword;
    }


    public String getTaintKeyword() {
        return switch (this) {
            case flow -> "TODO";
            case sample -> "split";
        };
    }

    @Override
    public String toString() {
        return methodKeyword;
    }
}
