package com.github.cd721.data_leakage_plugin.parsers;

import com.github.cd721.data_leakage_plugin.data.LeakageInstance;
import com.github.cd721.data_leakage_plugin.leakage_detectors.LeakageDetector;
import com.github.cd721.data_leakage_plugin.leakage_detectors.MultiTestLeakageDetector;
import com.github.cd721.data_leakage_plugin.leakage_detectors.OverlapLeakageDetector;
import com.github.cd721.data_leakage_plugin.leakage_detectors.PreprocessingLeakageDetector;

import java.util.ArrayList;
import java.util.List;

public class LeakageAnalysisParser {
    private final List<LeakageDetector> leakageDetectors;

    public LeakageAnalysisParser() {
        this.leakageDetectors = new ArrayList<>();
        leakageDetectors.add(new OverlapLeakageDetector());
        leakageDetectors.add(new MultiTestLeakageDetector());
        leakageDetectors.add(new PreprocessingLeakageDetector());

    }

    public boolean isLeakageDetected() {
        for (LeakageDetector detector : leakageDetectors) {
            if (detector.isLeakageDetected()) {
                return true;
            }
        }
        return false;
    }

    public List<LeakageInstance> LeakageInstances(){
        List<LeakageInstance> instances = new ArrayList<>();
        for (LeakageDetector detector : leakageDetectors) {
            instances.addAll(detector.FindLeakageInstances(detector.leakageType));
        }
        return instances;

    }

}

