package com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.visitors.leakage_sources;

import com.github.SE4AIResearch.DataLeakage_Fall2023.data.LeakageInstance;
import com.github.SE4AIResearch.DataLeakage_Fall2023.data.LeakageOutput;
import com.github.SE4AIResearch.DataLeakage_Fall2023.data.OverlapLeakageInstance;
import com.github.SE4AIResearch.DataLeakage_Fall2023.data.taints.Taint;
import com.github.SE4AIResearch.DataLeakage_Fall2023.docker_api.FileChanger;
import com.github.SE4AIResearch.DataLeakage_Fall2023.enums.LeakageCause;
import com.github.SE4AIResearch.DataLeakage_Fall2023.enums.LeakageType;
import com.github.SE4AIResearch.DataLeakage_Fall2023.enums.OverlapLeakageSourceKeyword;
import com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.InspectionBundle;
import com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.PsiUtils;
import com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.quick_fixes.OverlapLeakageQuickFix;
import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.markup.RangeHighlighter;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtilRt;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiEditorUtil;
import com.jetbrains.python.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.file.Paths;
import java.util.*;

import static com.github.SE4AIResearch.DataLeakage_Fall2023.data.LeakageOutput.getExclusionFileName;

/**
 * A type of {@link PyElementVisitor} that visits different types of elements within the PSI tree,
 * such as {@link PyReferenceExpression}s.
 */
public class OverlapLeakageSourceVisitor extends SourceElementVisitor<OverlapLeakageInstance, OverlapLeakageSourceKeyword> {
    private List<OverlapLeakageInstance> overlapLeakageInstances;
    private final PsiRecursiveElementVisitor recursiveElementVisitor;

    private final OverlapLeakageQuickFix myQuickFix = new OverlapLeakageQuickFix();

    Collection<RangeHighlighter> collection = new ArrayList<>();


    protected void removeInstance(OverlapLeakageInstance instance) {
        var newArr = new ArrayList<OverlapLeakageInstance>();
        var it = this.overlapLeakageInstances.iterator();
        while (it.hasNext()) {
            if (!it.next().equals(instance)) {
                newArr.add(it.next());

            }
        }
        this.overlapLeakageInstances = newArr;
    }

    public OverlapLeakageSourceVisitor(List<OverlapLeakageInstance> overlapLeakageInstances, @NotNull ProblemsHolder holder) {
        this.overlapLeakageInstances = new ArrayList<>(overlapLeakageInstances);
        this.holder = holder;
        this.recursiveElementVisitor = new PsiRecursiveElementVisitor() {

            @Override
            public void visitElement(@NotNull PsiElement element) {
                //  super.visitElement(element);//TODO:
                renderInspectionOnLeakageSource(element, holder, overlapLeakageInstances, myQuickFix);
            }
        };
        //  this.myQuickFix = new OverlapLeakageQuickFix(overlapLeakageInstances);
    }


    @Override
    public LeakageType getLeakageType() {
        return LeakageType.OverlapLeakage;
    }


    //TODO: consider different making different visitors for performance
    @Override
    public void visitPyCallExpression(@NotNull PyCallExpression node) {//TODO: consider moving this into visitPyReferenceExpression.. will require some refactoring.

        //TODO: extract

        if (!overlapLeakageInstances.isEmpty()) {
            if (leakageSourceIsAssociatedWithNode(overlapLeakageInstances, node)) {

                renderInspectionOnLeakageSource(node, holder, overlapLeakageInstances, myQuickFix);
            }

            renderInspectionOnTaints(node, holder, Arrays.stream(OverlapLeakageSourceKeyword.values()).toList());


        }
    }


    @Override
    public void renderInspectionOnLeakageSource(@NotNull PsiElement node, @NotNull ProblemsHolder holder, List<OverlapLeakageInstance> overlapLeakageInstances) {
//TODO: change name?
        int startoffset = node.getTextRange().getStartOffset();
        int endoffset = node.getTextRange().getEndOffset();
        Editor editor = PsiEditorUtil.findEditor(node); //Project curr_project = project[0];
        PsiFile containingFile = node.getContainingFile();
        Project project = containingFile.getProject();
        overlapLeakageInstances.stream().filter(leakageSourceAssociatedWithNode(node)).findFirst().ifPresent(
                instance ->
                {
                    if (!anyLinesAreOnExclusionList(instance,PsiUtils.getNodeLineNumber(node,holder))) {
                        holder.registerProblem(node, getInspectionMessageForLeakageSource(instance.getLeakageSource().findTaintThatMatchesText(node.getFirstChild().getText())), ProblemHighlightType.WARNING);

                        highlight(project, editor, startoffset, endoffset);
                    }
                }
        );


    }

    public void renderInspectionOnLeakageSource(@NotNull PsiElement node, @NotNull ProblemsHolder holder, List<OverlapLeakageInstance> overlapLeakageInstances, LocalQuickFix fix) {
//TODO: change name?
        int startoffset = node.getTextRange().getStartOffset();
        int endoffset = node.getTextRange().getEndOffset();
        Editor editor = PsiEditorUtil.findEditor(node); //Project curr_project = project[0];
        PsiFile containingFile = node.getContainingFile();
        Project project = containingFile.getProject();
        overlapLeakageInstances.stream().filter(leakageSourceAssociatedWithNode(node)).findFirst().ifPresent(
                instance -> {
                    if(!anyLinesAreOnExclusionList(instance,PsiUtils.getNodeLineNumber(node,holder))) {
                        holder.registerProblem(node, getInspectionMessageForLeakageSource(instance.getLeakageSource().findTaintThatMatchesText(node.getFirstChild().getText())), ProblemHighlightType.WARNING, fix);

                        highlight(project, editor, startoffset, endoffset);
                    }
                }
        );


    }

    @NotNull
    private static String getInspectionMessageForLeakageSource(Taint taintAssociatedWithLeakageInstance) {
        StringBuilder inspectionMessage = new StringBuilder(InspectionBundle.get(LeakageType.OverlapLeakage.getInspectionTextKey()));
        inspectionMessage.append(" ");

        //get method keyword associated with taint
        Arrays.stream(OverlapLeakageSourceKeyword.values()).filter(value -> taintAssociatedWithLeakageInstance.containsText(value.toString()))//TODO: should just be the text on the right side of the period, not the whole thing
                .findFirst().ifPresent(keyword -> inspectionMessage.append(InspectionBundle.get(keyword.getPotentialCauses().get(0).getInspectionTextKey())));//TODO: refactor?

        return inspectionMessage.toString();
    }

    @Override
    public void visitPyFunction(@NotNull PyFunction node) {
        this.recursiveElementVisitor.visitElement(node);

    }

    private class OverlapLeakageQuickFix implements LocalQuickFix {


        public OverlapLeakageQuickFix() {
        }

        @NotNull
        @Override
        public String getName() {
            return InspectionBundle.get("inspectionText.swapSplitAndSample.quickfix.text");
        }

        @Override
        public @IntentionFamilyName @NotNull String getFamilyName() {
            return getName();
        }

        @Override
        public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {

            var lineNumber = descriptor.getLineNumber() + 1;//was off by one

            var descriptionText = descriptor.getDescriptionTemplate();
            var psiElement = descriptor.getPsiElement();
            var psiFile = psiElement.getContainingFile();
            PsiDocumentManager documentManager = PsiDocumentManager.getInstance(project);
            Document document = documentManager.getDocument(psiFile);
            //Split
            if (descriptionText.equals(InspectionBundle.get("inspectionText.splitBeforeSampleReminder.text"))) {

            }
            //Source not linked to instance

            //Sample
            if (descriptionText.equals(InspectionBundle.get("inspectionText.overlapLeakage.text"))
                    && psiElement.getText().contains(OverlapLeakageSourceKeyword.sample.toString())) {

            }

//won't work if assignment is split on multiple lines
            var instance = getInstanceForLeakageSourceAssociatedWithNode(overlapLeakageInstances, psiElement);
            var source = instance.getLeakageSource();
            if (source.getCause().equals(LeakageCause.SplitBeforeSample)) {
                Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();

                int offset = document.getLineStartOffset(lineNumber - 1);

                @Nullable
                PsiElement firstElementOnLine = psiFile.findElementAt(offset
                );
                PsiManager manager = PsiManager.getInstance(project);
                var myFacade = PyPsiFacade.getInstance(project);

                var lineContentOfSplitCall = holder.getResults().stream().map(
                        problem -> problem.getPsiElement().getParent().getText()
                ).filter(taint -> taint.toLowerCase().contains("split")).findFirst().get();

                var offsetOfSplitCall = holder.getResults().stream().map(
                                problem -> problem.getPsiElement().getParent()
                        ).filter(taint -> taint.getText().toLowerCase().contains("split"))
                        .map(taint -> taint.getTextOffset()).filter(splitOffset -> splitOffset > offset).findFirst().get();

                document.replaceString(offsetOfSplitCall, offsetOfSplitCall +
                        lineContentOfSplitCall.length(), "");

                document.insertString(offset, lineContentOfSplitCall + "\n");


                //Remove split sample from leakage instances
              //  removeInstance(instance);

                var lineNumbersToRemove = new ArrayList<Integer>();
                lineNumbersToRemove.add(document.getLineNumber(offset));
                lineNumbersToRemove.add(document.getLineNumber(lineNumber-1));
                lineNumbersToRemove.add(document.getLineNumber(offset) + 1);
                lineNumbersToRemove.add(document.getLineNumber(offsetOfSplitCall)+1);
//                overlapLeakageInstances.stream().forEach(thisInstance ->
//                        thisInstance.getLeakageSource().removeLineNumbers(lineNumbersToRemove));

                try {
                    addLinesToExclusion(lineNumbersToRemove);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                DaemonCodeAnalyzer.getInstance(project).restart();
            }


        }
    }

    private static void addLinesToExclusion(List<Integer> lines) throws IOException {
        // File destinationFile = new File(String.valueOf(Paths.get(LeakageOutput.folderPath()).resolve(LeakageOutput.getExclusionFileName())));


        String exclusionFilePath = Paths.get(LeakageOutput.folderPath()).resolve(LeakageOutput.getExclusionFileName()).toString();
        File exclusionFile = new File(exclusionFilePath);

        FileUtilRt.createIfNotExists(exclusionFile);


        try {
            FileWriter fr = new FileWriter(exclusionFile.getPath(), true);
            for (var line : lines) {
                fr.write(line.toString());
                fr.write("\n");

            }
            fr.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean anyLinesAreOnExclusionList(LeakageInstance leakageInstance,int nodeLineNumber) {
        List<Integer> linesOnExlcusionList = linesOnExclusionList();

        if (linesOnExlcusionList.contains(leakageInstance.lineNumber())) {
            return true;
        }
        if (linesOnExlcusionList.contains(nodeLineNumber)) {
            return true;
        }

        var source = leakageInstance.getLeakageSource();

        for (Integer lineNo : source.getLineNumbers()) {
            if (linesOnExlcusionList.contains(lineNo)) {
                return true;
            }
        }

        return false;
    }

    private List<Integer> linesOnExclusionList() {
        String exclusionFilePath = Paths.get(LeakageOutput.folderPath()).resolve(LeakageOutput.getExclusionFileName()).toString();
        File file = new File(exclusionFilePath);


        List<Integer> linesToExclude = new ArrayList<>();
        if (file.exists()) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(file));

                String line;
                while ((line = reader.readLine()) != null) {
                    try {
                        linesToExclude.add(Integer.parseInt(line.strip()));
                    } catch (NumberFormatException e) {
                        //ignore
                    }


                }
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return linesToExclude;
    }

}
