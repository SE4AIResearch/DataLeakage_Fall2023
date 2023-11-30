package com.github.cd721.data_leakage_plugin.listeners;

import com.github.cd721.data_leakage_plugin.data.LeakageInstance;
import com.github.cd721.data_leakage_plugin.leakage_detectors.LeakageDetector;
import com.github.cd721.data_leakage_plugin.leakage_detectors.MultiTestLeakageDetector;
import com.github.cd721.data_leakage_plugin.leakage_detectors.OverlapLeakageDetector;
import com.github.cd721.data_leakage_plugin.leakage_indicators.DataLeakageIndicator;
import com.intellij.openapi.vfs.newvfs.BulkFileListener;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.github.cd721.data_leakage_plugin.listeners.Utils.aCSVFileWasChanged;
import static com.github.cd721.data_leakage_plugin.listeners.Utils.getEditorForFileChanged;

public class LeakageFileChangeDetector implements BulkFileListener {
    private final List<LeakageDetector> leakageDetectors;
    private final DataLeakageIndicator dataLeakageIndicator;
    //TODO: remove debug flag
    private final boolean debug = true;

    public LeakageFileChangeDetector() {
        this.leakageDetectors = new ArrayList<>();
        leakageDetectors.add(new OverlapLeakageDetector());
        leakageDetectors.add(new MultiTestLeakageDetector());
        dataLeakageIndicator = new DataLeakageIndicator();
    }

    public boolean isLeakageDetected() {
        for (LeakageDetector detector : leakageDetectors) {
            if (detector.isLeakageDetected()) {
                return true;
            }
        }
        return false;
    }


    public List<LeakageInstance> LeakageInstances(String folderPath) {
        List<LeakageInstance> instances = new ArrayList<>();
        for (LeakageDetector detector : leakageDetectors) {

            instances.addAll(detector.FindLeakageInstances(folderPath, detector.leakageType));


        }
        return instances;

    }

    @Override
    public void after(@NotNull List<? extends @NotNull VFileEvent> events) {
        for (VFileEvent event : events) {
            var editor = getEditorForFileChanged(event);
            if (editor != null) {
                if (debug || (OverlapLeakageCSVFileWasChanged(event) || MultiTestLeakageCSVFileWasChanged(event))) {
                    if (debug) {
                        dataLeakageIndicator.clearAllDataLeakageWarnings(editor);
                    }

                    String leakageAnalysisToolOutputFolderPath = "c:/dev/paper-sample-4-fact/";
                    var instances = LeakageInstances(leakageAnalysisToolOutputFolderPath);

                    if (!instances.isEmpty()) {
                        for (var instance : instances) {
                            dataLeakageIndicator.renderDataLeakageWarning(editor, instance.lineNumber(), instance.type());
                        }

                    } else {
                        dataLeakageIndicator.clearAllDataLeakageWarnings(editor);

                    }
                }
            }
        }

    }

    private boolean MultiTestLeakageCSVFileWasChanged(VFileEvent event) {
        return aCSVFileWasChanged(event) && event.getPath().endsWith("MultiUseTestLeak.csv");
    }

    private boolean OverlapLeakageCSVFileWasChanged(VFileEvent event) {
        return aCSVFileWasChanged(event) && event.getPath().endsWith("FinalOverlapLeak.csv");
    }


}
