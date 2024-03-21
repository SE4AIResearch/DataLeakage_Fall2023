package com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.visitors.leakage_instances;

import com.intellij.codeInsight.highlighting.HighlightManager;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.markup.HighlighterLayer;
import com.intellij.openapi.editor.markup.HighlighterTargetArea;
import com.intellij.openapi.editor.markup.RangeHighlighter;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;

import com.github.SE4AIResearch.DataLeakage_Fall2023.data.LeakageInstance;
import com.github.SE4AIResearch.DataLeakage_Fall2023.enums.LeakageType;
import com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.InspectionBundle;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiEditorUtil;
import com.jetbrains.python.psi.PyElementVisitor;
import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.colors.EditorColors;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.wm.WindowManager;

import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorManagerEvent;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringJoiner;
import java.util.function.Predicate;

public abstract class InstanceElementVisitor<T extends LeakageInstance> extends PyElementVisitor {
    public ProblemsHolder holder;

    public abstract LeakageType getLeakageType();

    public abstract Predicate<T> leakageInstanceIsAssociatedWithNode(@NotNull PsiElement node);

    Collection<RangeHighlighter> collection = new ArrayList<>();




    public boolean leakageIsAssociatedWithNode(List<T> leakageInstances, @NotNull PsiElement node) {
        return leakageInstances.stream().anyMatch(leakageInstanceIsAssociatedWithNode(node));
    }

    public T getLeakageInstanceAssociatedWithNode(List<T> leakageInstances, @NotNull PsiElement node) {
        return leakageInstances.stream().filter(leakageInstanceIsAssociatedWithNode(node)).findFirst().get();
    }

    public void renderInspectionOnLeakageInstance(List<T> leakageInstances, PsiElement node, LocalQuickFix fix) {
        if (leakageIsAssociatedWithNode(leakageInstances, node)) {
            var instance = getLeakageInstanceAssociatedWithNode(leakageInstances, node);
            var sourceLineNumbers = instance.getLeakageSource().getLineNumbers();
            LeakageType leakageType = getLeakageType();
            var sb = new StringBuilder();



            int startoffset = node.getTextRange().getStartOffset();
            int endoffset = node.getTextRange().getEndOffset();
            Editor editor =     PsiEditorUtil.findEditor(node); //Project curr_project = project[0];
            PsiFile containingFile = node.getContainingFile();
            Project project = containingFile.getProject();



            sb.append(InspectionBundle.get(leakageType.getInspectionTextKey()));
            sb.append(" ");

            if (sourceLineNumbers.size() == 1) {
                sb.append("See Line ");
                sb.append(sourceLineNumbers.get(0));
                sb.append(" which contains the source of the leakage.");
            } else if (sourceLineNumbers.isEmpty()) {//for multitest leakage

            } else {

                sb.append("See Lines: ");
                StringJoiner sj = new StringJoiner(", ");
                for (var l : sourceLineNumbers) {
                    sj.add(l.toString());
                }
                sb.append(sj);
                sb.append(" which contain the source of the leakage.");
            } //TODO: refactor


            holder.registerProblem(node,sb.toString(), ProblemHighlightType.WARNING, fix);

            highlight(project, editor, startoffset, endoffset);


        }
    }


    public void renderInspectionOnLeakageInstance(List<T> leakageInstances, PsiElement node) {
        if (leakageIsAssociatedWithNode(leakageInstances, node)) {
            LeakageType leakageType = getLeakageType();
            int startoffset = node.getTextRange().getStartOffset();
            int endoffset = node.getTextRange().getEndOffset();
            Editor editor =     PsiEditorUtil.findEditor(node); //Project curr_project = project[0];
            PsiFile containingFile = node.getContainingFile();
            Project project = containingFile.getProject();
            holder.registerProblem(node, InspectionBundle.get(leakageType.getInspectionTextKey()), ProblemHighlightType.WARNING);
            highlight(project, editor, startoffset, endoffset);

        }
    }

    public void highlight(Project project, Editor editor, int startoffset, int endoffset ){
                 HighlightManager h1 = HighlightManager.getInstance(project);
                 TextAttributesKey betterColor = EditorColors.INJECTED_LANGUAGE_FRAGMENT;
                  //Project curr_project = project[0];

                h1.addOccurrenceHighlight(editor, startoffset, endoffset, betterColor, 001, collection);

    }


}
