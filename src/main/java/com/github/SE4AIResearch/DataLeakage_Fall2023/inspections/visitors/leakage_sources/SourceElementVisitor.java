package com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.visitors.leakage_sources;

import com.github.SE4AIResearch.DataLeakage_Fall2023.data.LeakageInstance;
import com.github.SE4AIResearch.DataLeakage_Fall2023.data.LeakageSource;
import com.github.SE4AIResearch.DataLeakage_Fall2023.data.taints.Taint;
import com.github.SE4AIResearch.DataLeakage_Fall2023.enums.LeakageCause;
import com.github.SE4AIResearch.DataLeakage_Fall2023.enums.LeakageSourceKeyword;
import com.github.SE4AIResearch.DataLeakage_Fall2023.enums.LeakageSourceKeywordFactory;
import com.github.SE4AIResearch.DataLeakage_Fall2023.enums.LeakageType;
import com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.InspectionBundle;
import com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.InspectionUtils;
import com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.PsiUtils;
import com.intellij.codeInsight.highlighting.HighlightManager;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.colors.EditorColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.markup.RangeHighlighter;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiEditorUtil;
import com.jetbrains.python.psi.PyCallExpression;
import com.jetbrains.python.psi.PyElementVisitor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

import static com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.InspectionUtils.anyLinesAreOnExclusionList;

public abstract class SourceElementVisitor<T extends LeakageInstance, U extends LeakageSourceKeyword> extends PyElementVisitor {
    private ProblemsHolder holder;

    Collection<RangeHighlighter> collection = new ArrayList<>();

    public abstract LeakageType getLeakageType();

    public Predicate<T> leakageSourceAssociatedWithNode(@NotNull PsiElement node) {
        var nodeLineNumber = PsiUtils.getNodeLineNumber(node, holder);

        return instance -> (instance.getLeakageSource().getLineNumbers().stream().anyMatch(leakageSourceLineNumber -> leakageSourceLineNumber == nodeLineNumber));

    }

    public T getInstanceForLeakageSourceAssociatedWithNode(List<T> leakageInstances, @NotNull PsiElement node) {
        return leakageInstances.stream().filter(leakageSourceAssociatedWithNode(node)).findFirst().get();
    }


    public boolean leakageSourceIsAssociatedWithNode(List<T> leakageInstances, @NotNull PyCallExpression node) {
        return leakageInstances.stream().anyMatch(leakageSourceAssociatedWithNode(node));
    }
    @NotNull
    public  String getInspectionMessageForLeakageSource(Taint taintAssociatedWithLeakageInstance) {
        StringBuilder inspectionMessage = new StringBuilder(InspectionBundle.get(this.getLeakageType().getInspectionTextKey()));
        inspectionMessage.append(" ");

        //get method keyword associated with taint
        Arrays.stream(LeakageSourceKeywordFactory.getSourceKeywordValuesForleakageType(this.getLeakageType())).filter(value -> taintAssociatedWithLeakageInstance.containsText(value.toString()))//TODO: should just be the text on the right side of the period, not the whole thing
                .findFirst().ifPresent(keyword -> inspectionMessage.append(InspectionBundle.get(keyword.getPotentialCauses().get(0).getInspectionTextKey())));//TODO: refactor?

        return inspectionMessage.toString();
    }
    public  void renderInspectionOnLeakageSource(@NotNull PsiElement node, @NotNull ProblemsHolder holder, List<T> leakageInstances) {
        //TODO: change name?
        int startoffset = node.getTextRange().getStartOffset();
        int endoffset = node.getTextRange().getEndOffset();
        Editor editor = PsiEditorUtil.findEditor(node); //Project curr_project = project[0];
        PsiFile containingFile = node.getContainingFile();
        Project project = containingFile.getProject();
        leakageInstances.stream().filter(leakageSourceAssociatedWithNode(node)).findFirst().ifPresent(
                instance ->
                {
                    if (!anyLinesAreOnExclusionList(instance, PsiUtils.getNodeLineNumber(node, holder))) {
                        holder.registerProblem(node, getInspectionMessageForLeakageSource(instance.getLeakageSource().findTaintThatMatchesText(node.getFirstChild().getText())), ProblemHighlightType.WARNING);

                        highlight(project, editor, startoffset, endoffset);
                    }
                }
        );

    }


    public void renderInspectionOnLeakageSource(@NotNull PsiElement node, @NotNull ProblemsHolder holder, List<T> leakageInstances, LocalQuickFix fix) {
//TODO: change name?
        int startoffset = node.getTextRange().getStartOffset();
        int endoffset = node.getTextRange().getEndOffset();
        Editor editor = PsiEditorUtil.findEditor(node); //Project curr_project = project[0];
        PsiFile containingFile = node.getContainingFile();
        Project project = containingFile.getProject();
        leakageInstances.stream().filter(leakageSourceAssociatedWithNode(node)).findFirst().ifPresent(
                instance -> {
                    if (!anyLinesAreOnExclusionList(instance, PsiUtils.getNodeLineNumber(node, holder))) {
                        holder.registerProblem(node, getInspectionMessageForLeakageSource(instance.getLeakageSource().findTaintThatMatchesText(node.getFirstChild().getText())), ProblemHighlightType.WARNING, fix);

                        highlight(project, editor, startoffset, endoffset);
                    }
                }
        );


    }
    public void renderInspectionOnTaintForInstanceWithKeyword(@NotNull PyCallExpression node, @NotNull ProblemsHolder holder, U keyword) {

        var taintKeyword = keyword.getTaintKeyword();
        var potentialCauses = keyword.getPotentialCauses();

        var key = potentialCauses.get(0).getInspectionTextKey();//TODO: refactor
//TODO: train test split is not necessarily a taint
        if (node.getText().toLowerCase().contains(taintKeyword)) {//TODO: not the whole node text, just the method itself
            int startoffset = node.getTextRange().getStartOffset();
            int endoffset = node.getTextRange().getEndOffset();
            Editor editor = PsiEditorUtil.findEditor(node); //Project curr_project = project[0];
            PsiFile containingFile = node.getContainingFile();
            Project project = containingFile.getProject();

            if (!anyLinesAreOnExclusionList(PsiUtils.getNodeLineNumber(node, holder))) {

                holder.registerProblem(node, InspectionBundle.get(key), ProblemHighlightType.WARNING);
                highlight(project, editor, startoffset, endoffset);
            }

        }
    }

    public void renderInspectionOnTaintWithCause(@NotNull PyCallExpression node, @NotNull ProblemsHolder holder, LeakageCause cause, U keyword) {
        var taintKeyword = keyword.getTaintKeyword();
        var key = cause.getInspectionTextKey();


        if (node.getText().toLowerCase().contains(taintKeyword)) {//TODO: not the whole node text, just the method itself
            int startoffset = node.getTextRange().getStartOffset();
            int endoffset = node.getTextRange().getEndOffset();
            Editor editor = PsiEditorUtil.findEditor(node); //Project curr_project = project[0];
            PsiFile containingFile = node.getContainingFile();
            Project project = containingFile.getProject();

            if (!InspectionUtils.anyLinesAreOnExclusionList(PsiUtils.getNodeLineNumber(node, holder))) {
                holder.registerProblem(node, InspectionBundle.get(key), ProblemHighlightType.WARNING);
                highlight(project, editor, startoffset, endoffset);
            }

        }//TODO: the split call isn't flagged as a taint by the leakage tool, but it is considered as a taint here
    }

    public void renderInspectionOnTaints(@NotNull PyCallExpression node, @NotNull ProblemsHolder holder, List<U> keywords) {
        // for overlap leakage instancesWhoseSourcesHaveDataAugmentation, instancesWhoseSourcesHaveSampling

        keywords.forEach(keyword -> renderInspectionOnTaintForInstanceWithKeyword(node, holder, keyword));

    }


    public Taint getTaintForKeyword(LeakageSource source, @NotNull PsiElement node, U keyword) {

        var taintKeyword = keyword.getTaintKeyword();
        for (var taint : source.getTaints()) {
            if (taint.containsText(taintKeyword)) {
                return taint;
            }
        }
        return null;

    }

    public void highlight(Project project, Editor editor, int startoffset, int endoffset) {
        HighlightManager h1 = HighlightManager.getInstance(project);
        TextAttributesKey betterColor = EditorColors.SEARCH_RESULT_ATTRIBUTES;
        //Project curr_project = project[0];

        h1.addOccurrenceHighlight(editor, startoffset, endoffset, betterColor, 001, collection);

    }


}
