package com.github.cd721.data_leakage_plugin;

import java.util.ArrayList;
import java.util.List;

import static com.github.cd721.data_leakage_plugin.LeakageType.OverlapLeakage;

public class DataLeakageWarningRendererFactory {
    //TODO: fix improper use of factory pattern
    private final List<DataLeakageWarningRenderer> renderers;

    public DataLeakageWarningRendererFactory() {
       renderers = new ArrayList<>();
        renderers.add(new OverlapLeakageWarningRenderer());
        renderers.add(new MultiTestLeakageWarningRenderer());
        renderers.add(new PreprocessingLeakageWarningRenderer());
    }

    public DataLeakageWarningRenderer GetRendererForLeakageType(LeakageType leakageType) {
        return switch (leakageType) {
            case OverlapLeakage -> renderers.get(0);
            case MultiTestLeakage -> renderers.get(1);
            case PreprocessingLeakage -> renderers.get(2);
        };

    }
}
