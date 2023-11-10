package com.github.cd721.data_leakage_plugin;

public abstract class LeakageDetector {
    public LeakageType leakageType;
    public String folderPath;

    public LeakageDetector() {
        this.folderPath = "C:/dev/paper-sample-2-fact/";

    }

    public abstract int CountInstances();

    public boolean isLeakageDetected() {
        return CountInstances() > 0;
    }

}
