package com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.visitors;

import com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.InspectionUtils;
import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class Utils {
    public static void removeFixedLinesFromLeakageInstance(@NotNull Project project, Document document, int offset, int lineNumber, int potentialOffsetOfSplitCall) {
        var lineNumbersToRemove = new ArrayList<Integer>();
        lineNumbersToRemove.add(document.getLineNumber(offset));
        lineNumbersToRemove.add(document.getLineNumber(lineNumber - 1));
        lineNumbersToRemove.add(document.getLineNumber(offset) + 1);
        lineNumbersToRemove.add(document.getLineNumber(potentialOffsetOfSplitCall) + 1);
        InspectionUtils.addLinesToExclusion(lineNumbersToRemove);
        DaemonCodeAnalyzer.getInstance(project).restart();
    }
}
