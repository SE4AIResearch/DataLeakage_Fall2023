package com.github.cd721.data_leakage_plugin.warning_renderers;

public class PreprocessingLeakageWarningRenderer extends DataLeakageWarningRenderer {

    @Override
    protected String getMessage() {
        return "Your code may contain preprocessing leakage.";
    }




}
