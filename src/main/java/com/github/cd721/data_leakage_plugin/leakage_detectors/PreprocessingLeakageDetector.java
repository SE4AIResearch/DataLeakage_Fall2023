package com.github.cd721.data_leakage_plugin.leakage_detectors;

import com.github.cd721.data_leakage_plugin.data.LeakageInstance;

import java.util.List;

public class PreprocessingLeakageDetector extends LeakageDetector{
    @Override
    public String getCsvFileName() {
        return null;
    }

    @Override
    public int getCsvInvocationColumn() {
        return 0;
    }

    @Override
    public void addLeakageInstance(LeakageInstance instance) {

    }

    @Override
    public List<LeakageInstance> leakageInstances() {
        return null;
    }

    @Override
    public boolean isLeakageDetected() {
        return false;
    }
}
