package com.github.cd721.data_leakage_plugin.warning_renderers;

public class OverlapLeakageWarningRenderer extends DataLeakageWarningRenderer {
    @Override
    protected String getMessage() {
        return "Your code may contain overlap leakage.";
    }

}
