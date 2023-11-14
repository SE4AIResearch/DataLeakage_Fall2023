package com.github.cd721.data_leakage_plugin;

import java.util.List;

public abstract class LeakageDetector {
    public LeakageType leakageType;
    public String folderPath;

    public LeakageDetector() {
        this.folderPath = "C:/dev/paper-sample-2-fact/";

    }

    public abstract int CountInstances();

    public abstract List<Integer> FindLineNumbers();

    public abstract List<LeakageInstance> FindLeakageInstances();

    public boolean isLeakageDetected() {
        return CountInstances() > 0;
    }

}
