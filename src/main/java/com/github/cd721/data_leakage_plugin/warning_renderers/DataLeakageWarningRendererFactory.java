package com.github.cd721.data_leakage_plugin.warning_renderers;

import com.github.cd721.data_leakage_plugin.enums.LeakageType;

import java.util.ArrayList;
import java.util.List;

public class DataLeakageWarningRendererFactory {

    public DataLeakageWarningRenderer GetRendererForLeakageType(LeakageType leakageType) {
        return switch (leakageType) {
            case OverlapLeakage -> new OverlapLeakageWarningRenderer();
            case MultiTestLeakage -> new MultiTestLeakageWarningRenderer();
            case PreprocessingLeakage -> new PreprocessingLeakageWarningRenderer();
        };

    }
}
