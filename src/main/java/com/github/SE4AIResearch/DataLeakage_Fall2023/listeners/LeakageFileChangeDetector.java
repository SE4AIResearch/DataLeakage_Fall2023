package com.github.SE4AIResearch.DataLeakage_Fall2023.listeners;

import com.github.SE4AIResearch.DataLeakage_Fall2023.data.LeakageOutput;
import com.github.SE4AIResearch.DataLeakage_Fall2023.leakage_indicators.DataLeakageIndicator;
import com.github.SE4AIResearch.DataLeakage_Fall2023.parsers.LeakageAnalysisParser;
import com.intellij.openapi.vfs.newvfs.BulkFileListener;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.github.SE4AIResearch.DataLeakage_Fall2023.listeners.Utils.aCSVFileWasChanged;
import static com.github.SE4AIResearch.DataLeakage_Fall2023.listeners.Utils.getEditorForFileChanged;

public  class LeakageFileChangeDetector implements BulkFileListener {
  //  private final List<LeakageDetector> leakageDetectors;
    private LeakageAnalysisParser leakageAnalysisParser;
    private final DataLeakageIndicator dataLeakageIndicator;
    //TODO: remove debug flag
    private final boolean debug = false;

    public LeakageFileChangeDetector() {
        leakageAnalysisParser = new LeakageAnalysisParser();
        dataLeakageIndicator = new DataLeakageIndicator();
    }


    @Override
    public void after(@NotNull List<? extends @NotNull VFileEvent> events) {
        for (VFileEvent event : events) {
            var editor = getEditorForFileChanged(event);
            if (editor != null) {
                if (debug || (LeakageFileChanged(event))) {
                 //   if (debug) {
                        dataLeakageIndicator.clearAllDataLeakageWarnings(editor);
                   // }

                    var instances = leakageAnalysisParser.LeakageInstances();

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

private boolean LeakageFileChanged(VFileEvent event){
        if(event.getFile().getPath().contains(LeakageOutput.folderPath())){
            return true;
        }return false;
}


}
