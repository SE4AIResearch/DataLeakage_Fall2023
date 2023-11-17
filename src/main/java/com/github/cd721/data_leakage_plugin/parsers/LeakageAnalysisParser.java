package com.github.cd721.data_leakage_plugin.parsers;

import com.github.cd721.data_leakage_plugin.data.LeakageInstance;
import com.github.cd721.data_leakage_plugin.leakage_detectors.LeakageDetector;
import com.github.cd721.data_leakage_plugin.leakage_detectors.OverlapLeakageDetector;

import java.util.ArrayList;
import java.util.List;

public class LeakageAnalysisParser {
    private final List<LeakageDetector> leakageDetectors;

    public LeakageAnalysisParser() {
        this.leakageDetectors = new ArrayList<>();
        leakageDetectors.add(new OverlapLeakageDetector());
    }

    public boolean isLeakageDetected() {
        for (LeakageDetector detector : leakageDetectors) {
            if (detector.isLeakageDetected()) {
                return true;
            }
        }
        return false;
    }

    public List<Integer> LeakageLineNumbers() {
        List<Integer> lineNumbers = new ArrayList<>();
        for (LeakageDetector detector : leakageDetectors) {
            lineNumbers.addAll(detector.FindLineNumbers());
        }
        return lineNumbers;
    }
    public List<LeakageInstance> LeakageInstances(){
        List<LeakageInstance> instances = new ArrayList<>();
        for (LeakageDetector detector : leakageDetectors) {
            instances.addAll(detector.FindLeakageInstances());
        }
        return instances;

    }

}

