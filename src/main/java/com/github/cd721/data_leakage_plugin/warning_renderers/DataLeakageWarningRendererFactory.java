package com.github.cd721.data_leakage_plugin.warning_renderers;

import com.github.cd721.data_leakage_plugin.enums.LeakageType;

import java.util.ArrayList;
import java.util.List;

public class DataLeakageWarningRendererFactory {
    private final List<DataLeakageWarningRenderer> renderers;

    public DataLeakageWarningRendererFactory() {
       renderers = new ArrayList<>();
        renderers.add(new OverlapLeakageWarningRenderer());
        renderers.add(new MultiTestLeakageWarningRenderer());
        renderers.add(new PreprocessingLeakageWarningRenderer());
    }

    public DataLeakageWarningRenderer GetRendererForLeakageType(LeakageType leakageType) {
        return switch (leakageType) {
            case OverlapLeakage -> new OverlapLeakageWarningRenderer();
            case MultiTestLeakage -> new MultiTestLeakageWarningRenderer();
            case PreprocessingLeakage -> new PreprocessingLeakageWarningRenderer();
        };

    }
}
