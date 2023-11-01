package com.github.cd721.data_leakage_plugin;

import com.github.cd721.data_leakage_plugin.listeners.FileChangeDetector;

public class DataLeakageService {
    private final DataLeakageIndicator dataLeakageIndicator;
    private final FileChangeDetector fileChangeDetector;
    private final LeakageAnalysisParser leakageAnalysisParser;

    public DataLeakageService() {
        dataLeakageIndicator = new DataLeakageIndicator();
        fileChangeDetector = new FileChangeDetector();
        leakageAnalysisParser = new LeakageAnalysisParser();

        if (fileChangeDetector.isFileChanged() && leakageAnalysisParser.isOverlapLeakageDetected()) {
            dataLeakageIndicator.renderDataLeakageWarning(fileChangeDetector.getEditorForFileChanged());
        }

    }


}
