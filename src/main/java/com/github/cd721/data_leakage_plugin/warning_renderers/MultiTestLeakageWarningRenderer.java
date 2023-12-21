package com.github.cd721.data_leakage_plugin.warning_renderers;

public class MultiTestLeakageWarningRenderer extends DataLeakageWarningRenderer {

    @Override
    protected String getMessage() {
        return "Your code may contain multi-test leakage.";
    }



}
