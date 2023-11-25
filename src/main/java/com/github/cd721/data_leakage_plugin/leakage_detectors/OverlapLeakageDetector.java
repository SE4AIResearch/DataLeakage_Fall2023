package com.github.cd721.data_leakage_plugin.leakage_detectors;

import com.github.cd721.data_leakage_plugin.data.Invocation;
import com.github.cd721.data_leakage_plugin.data.LeakageInstance;
import com.github.cd721.data_leakage_plugin.enums.LeakageType;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OverlapLeakageDetector extends LeakageDetector {
    private final List<LeakageInstance> leakageInstances;

    public OverlapLeakageDetector() {
        super();
        this.leakageType = LeakageType.OverlapLeakage;
        this.leakageInstances = new ArrayList<>();
    }


    @Override
    public boolean isLeakageDetected() {
        return !this.leakageInstances.isEmpty();
    }


    @Override
    public int getCsvInvocationColumn() {
        return 2;
    }

    @Override
    public void addLeakageInstance(LeakageInstance instance) {
        this.leakageInstances.add(instance);
    }

    @Override
    public List<LeakageInstance> leakageInstances() {
        return leakageInstances;
    }

    @Override
    public String getCsvFileName() {
        return "FinalOverlapLeak.csv";
    }
}
