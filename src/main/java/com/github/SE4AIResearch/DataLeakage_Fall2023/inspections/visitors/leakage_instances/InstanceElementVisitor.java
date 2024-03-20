package com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.visitors.leakage_instances;

import com.github.SE4AIResearch.DataLeakage_Fall2023.data.LeakageInstance;
import com.github.SE4AIResearch.DataLeakage_Fall2023.enums.LeakageType;
import com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.InspectionBundle;
import com.intellij.codeInsight.highlighting.HighlightManager;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.HighlighterColors;
import com.intellij.openapi.editor.colors.CodeInsightColors;
import com.intellij.openapi.editor.colors.EditorColors;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.jetbrains.python.psi.PyElementVisitor;
import org.jetbrains.annotations.NotNull;

import javax.swing.text.Highlighter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public abstract class InstanceElementVisitor<T extends LeakageInstance> extends PyElementVisitor {
    public ProblemsHolder holder;

    public abstract LeakageType getLeakageType();

    public abstract Predicate<T> leakageInstanceIsAssociatedWithNode(@NotNull PsiElement node);


    public boolean leakageIsAssociatedWithNode(List<T> leakageInstances, @NotNull PsiElement node) {
        return leakageInstances.stream().anyMatch(leakageInstanceIsAssociatedWithNode(node));
    }

    public T getLeakageInstanceAssociatedWithNode(List<T> leakageInstances, @NotNull PsiElement node) {
        return leakageInstances.stream().filter(leakageInstanceIsAssociatedWithNode(node)).findFirst().get();
    }


    public void renderInspectionOnLeakageInstance(List<T> leakageInstances, PsiElement node, LocalQuickFix fix) {
        var project = node.getProject();
        var nodes = new PsiElement[]{node};

        var highlightManager = HighlightManager.getInstance(project);
        var attrs = EditorColorsManager.getInstance().getGlobalScheme().getAttributes(EditorColors.SEARCH_RESULT_ATTRIBUTES);//TODO: revise this line
        var attrKey = TextAttributesKey.createTextAttributesKey("highlight",EditorColors.SEARCH_RESULT_ATTRIBUTES);//TODO: find something similar EditorColors.SEARCH_RESULT_ATTRIBUTES that will achieve the highlight we want
        var editor = FileEditorManager.getInstance(project).getSelectedTextEditor();

        TextAttributesKey betterColor = EditorColors. INJECTED_LANGUAGE_FRAGMENT;

        if (leakageIsAssociatedWithNode(leakageInstances, node)) {
            LeakageType leakageType = getLeakageType();
           // holder.registerProblem(node, InspectionBundle.get(leakageType.getInspectionTextKey()), ProblemHighlightType.WARNING, fix);
          // highlightManager.addOccurrenceHighlights(editor, nodes, betterColor, true, null);//TODO: revise this line
        highlightManager.addOccurrenceHighlight(editor,node.getTextOffset(),node.getTextOffset()+100,attrKey,001,null);
        }
    }

    public void renderInspectionOnLeakageInstance(List<T> leakageInstances, PsiElement node) {
        if (leakageIsAssociatedWithNode(leakageInstances, node)) {
            LeakageType leakageType = getLeakageType();
            holder.registerProblem(node, InspectionBundle.get(leakageType.getInspectionTextKey()), ProblemHighlightType.WARNING);

        }
    }
}
