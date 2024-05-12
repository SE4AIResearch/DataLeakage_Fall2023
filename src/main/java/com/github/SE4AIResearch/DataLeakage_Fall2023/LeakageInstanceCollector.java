package com.github.SE4AIResearch.DataLeakage_Fall2023;

import com.github.SE4AIResearch.DataLeakage_Fall2023.data.leakage_instances.LeakageInstance;
import com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.QuickFixActionNotifier;
import com.github.SE4AIResearch.DataLeakage_Fall2023.leakage_detectors.LeakageDetector;
import com.github.SE4AIResearch.DataLeakage_Fall2023.leakage_detectors.MultiTestLeakageDetector;
import com.github.SE4AIResearch.DataLeakage_Fall2023.leakage_detectors.OverlapLeakageDetector;
import com.github.SE4AIResearch.DataLeakage_Fall2023.leakage_detectors.PreprocessingLeakageDetector;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.WindowManager;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class LeakageInstanceCollector implements  QuickFixActionNotifier{
    //   private final List<LeakageDetector<? extends LeakageInstance>> leakageDetectors;
    private static List<LeakageInstance> leakageInstances;

    private Project project;

    public LeakageInstanceCollector() {
        setProject();

        if (project != null) {
            project.getMessageBus().connect().subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, new FileEditorManagerListener() {
                @Override
                public void fileClosed(@NotNull FileEditorManager source, @NotNull VirtualFile file) {
                    clearLeakageInstances();
                }

            });

            project.getMessageBus().connect().subscribe(QuickFixActionNotifier.QUICK_FIX_ACTION_TOPIC,
                    new QuickFixActionNotifier() {

                        @Override
                        public void afterLinesFixed(List<Integer> lines) {
                            ClearLeakageInstancesOnLines(lines);
                        }
                    });


        }

        // this.leakageInstances = LeakageInstances();
        detectNewInstances();
    }

    public static void detectNewInstances() {
        var leakageDetectors = new ArrayList<LeakageDetector<? extends LeakageInstance>>();
        leakageDetectors.add(new OverlapLeakageDetector());
        leakageDetectors.add(new MultiTestLeakageDetector());
        leakageDetectors.add(new PreprocessingLeakageDetector());
        leakageInstances = new ArrayList<>();
        for (var detector : leakageDetectors) {
            leakageInstances.addAll(detector.FindLeakageInstances());
        }
    }

    private void setProject() {
        ApplicationManager.getApplication().invokeLater(() -> {
            Project[] projects = ProjectManager.getInstance().getOpenProjects();

            Project activeProject = null;
            for (Project project : projects) {

                Window window = WindowManager.getInstance().suggestParentWindow(project);
                if (window != null && window.isActive()) {
                    activeProject = project;
                }
            }
            this.project = activeProject;

        });

    }

    private void clearLeakageInstances() {
        leakageInstances = new ArrayList<>();
    }

    public boolean isLeakageDetected(List<LeakageDetector<? extends LeakageInstance>> leakageDetectors) {
        for (var detector : leakageDetectors) {
            if (detector.isLeakageDetected()) {
                return true;
            }
        }
        return false;
    }

    public static List<LeakageInstance> LeakageInstances() {
//        List<LeakageInstance> instances = new ArrayList<>();
//        for (var detector : leakageDetectors) {
//            instances.addAll(detector.FindLeakageInstances());
//        }
//        return instances;
        return leakageInstances;

    }

    public void ClearLeakageInstancesOnLines(List<Integer> lineNumbers) {
        leakageInstances.removeIf(instance -> lineNumbers.stream().anyMatch(lineNumber -> lineNumber.equals(instance.lineNumber())
                )


        );
        for (var l : lineNumbers) {

            leakageInstances.removeIf(instance -> instance.getLeakageSource().get()
                    .getLineNumbers().stream().
                    anyMatch(srcLine ->
                            srcLine.equals(l))
            );


        }

    }

    public List<LeakageInstance> GetLeakageInstances() {
        return leakageInstances;
    }

    public List<LeakageInstance> SetLeakageInstances(List<LeakageInstance> leakageInstances) {
        return leakageInstances;
    }

    @Override
    public void afterLinesFixed(List<Integer> lines) {
        ClearLeakageInstancesOnLines(lines);
    }
}

