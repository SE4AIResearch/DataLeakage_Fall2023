package com.github.cd721.data_leakage_plugin.leakage_detectors;

import com.github.cd721.data_leakage_plugin.data.LeakageInstance;
import com.github.cd721.data_leakage_plugin.enums.LeakageType;

import java.util.List;

public abstract class LeakageDetector {
    public LeakageType leakageType;
    public final String folderPath = "C:/dev/paper-sample-2-fact/";

    public LeakageDetector() {

    }

    public abstract int CountInstances();

    public abstract List<Integer> FindLineNumbers();

    public abstract List<LeakageInstance> FindLeakageInstances();

    public boolean isLeakageDetected() {
        return CountInstances() > 0;
    }

}
