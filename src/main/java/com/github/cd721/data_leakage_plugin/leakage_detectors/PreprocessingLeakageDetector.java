package com.github.cd721.data_leakage_plugin.leakage_detectors;

import com.github.cd721.data_leakage_plugin.data.LeakageInstance;
import com.github.cd721.data_leakage_plugin.enums.LeakageType;

import java.util.ArrayList;
import java.util.List;

public class PreprocessingLeakageDetector extends LeakageDetector{
    private final List<LeakageInstance> leakageInstances;
    public PreprocessingLeakageDetector() {
        super();
        this.leakageType = LeakageType.PreprocessingLeakage;
        this.leakageInstances = new ArrayList<>();
    }

    @Override
    public String getCsvFileName() {
        return "PreProcessingLeak.csv";
    }

    @Override
    public int getCsvInvocationColumn() {
        return 1;
    }

    @Override
    public void addLeakageInstance(LeakageInstance instance) {

    }

    @Override
    public List<LeakageInstance> leakageInstances() {
        return leakageInstances;
    }

    @Override
    public boolean isLeakageDetected() {
        return !this.leakageInstances.isEmpty();
    }
}
