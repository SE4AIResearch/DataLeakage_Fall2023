package com.github.SE4AIResearch.DataLeakage_Fall2023.warning_renderers;

public class MultiTestLeakageWarningRenderer extends DataLeakageWarningRenderer {

    @Override
    protected String getMessage() {
        return "Your code may contain multi-test leakage.";
    }



}
