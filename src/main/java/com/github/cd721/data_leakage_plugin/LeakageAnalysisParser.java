package com.github.cd721.data_leakage_plugin;

import java.util.ArrayList;
import java.util.List;

public class LeakageAnalysisParser {
    private final List<LeakageDetector> leakageDetectors;

    public LeakageAnalysisParser() {
        this.leakageDetectors = new ArrayList<LeakageDetector>();
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
        List<Integer> lineNumbers = new ArrayList<Integer>();
        for (LeakageDetector detector : leakageDetectors) {
            lineNumbers.addAll(detector.FindLineNumbers());
        }
        return lineNumbers;
    }

}
