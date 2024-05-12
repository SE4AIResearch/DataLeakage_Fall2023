package com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.visitors.leakage_instances;

import com.github.SE4AIResearch.DataLeakage_Fall2023.data.leakage_instances.MultiTestLeakageInstance;
import com.github.SE4AIResearch.DataLeakage_Fall2023.enums.LeakageType;
import com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.InspectionBundle;
import com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.PsiUtils;
import com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.QuickFixActionNotifier;
import com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.visitors.Utils;
import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.find.FindManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiRecursiveElementVisitor;
import com.intellij.util.DocumentUtil;
import com.jetbrains.python.psi.PyReferenceExpression;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class MultiTestLeakageInstanceVisitor extends InstanceElementVisitor<MultiTestLeakageInstance> {
    private final List<MultiTestLeakageInstance> multiTestLeakageInstances;
    private final PsiRecursiveElementVisitor recursiveElementVisitor;
    private final MultiTestLeakageQuickFix myQuickFix = new MultiTestLeakageQuickFix();

    public MultiTestLeakageInstanceVisitor(List<MultiTestLeakageInstance> multiTestLeakageInstances, @NotNull ProblemsHolder holder) {
        this.multiTestLeakageInstances = multiTestLeakageInstances;
        this.holder = holder;
        this.recursiveElementVisitor = new PsiRecursiveElementVisitor() {

            @Override
            public void visitElement(@NotNull PsiElement element) {
                //  super.visitElement(element);//TODO:
                renderInspectionOnLeakageInstance(multiTestLeakageInstances, element, myQuickFix);
            }
        };
    }


    @Override
    public LeakageType getLeakageType() {
        return LeakageType.MultiTestLeakage;
    }

    @Override
    public Predicate<MultiTestLeakageInstance> leakageInstanceIsAssociatedWithNode(@NotNull PsiElement node) {

        return instance -> (instance.lineNumber() == PsiUtils.getNodeLineNumber(node, holder))
                && instance.variableName().contains(node.getText());

    }

    @Override
    public void visitPyReferenceExpression(@NotNull PyReferenceExpression node) {
        renderInspectionOnLeakageInstance(multiTestLeakageInstances, node, myQuickFix);
    }

    class MultiTestLeakageQuickFix implements LocalQuickFix {
        @NotNull
        @Override
        public String getName() {
            return InspectionBundle.get("inspectionText.removeRedundantTestEvaluations.quickfix.text");
        }

        @Override
        public @IntentionFamilyName @NotNull String getFamilyName() {
            return getName();
        }

        @Override
        public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
            var instance = descriptor.getPsiElement();


            FindManager myFindManager = FindManager.getInstance(project);


            var psiElement = descriptor.getPsiElement();
            var psiFile = psiElement.getContainingFile();
            PsiDocumentManager documentManager = PsiDocumentManager.getInstance(project);
            Document document = documentManager.getDocument(psiFile);
            var lineNumber = descriptor.getLineNumber();

            int offset = document.getLineStartOffset(lineNumber);
            var fixedLines = new ArrayList<Integer>();

            renameVariablesInDocument(document, instance, fixedLines);

            fixedLines.add(document.getLineNumber(offset) + 1);
            Utils.removeFixedLinesFromLeakageInstance(project, fixedLines);

            DaemonCodeAnalyzer.getInstance(project).restart();

            QuickFixActionNotifier publisher =
                    project.getMessageBus().syncPublisher(QuickFixActionNotifier.QUICK_FIX_ACTION_TOPIC);

            try {

            } finally {
                publisher.afterLinesFixed(fixedLines);
            }
        }

        private void renameVariablesInDocument(Document document, PsiElement instance, ArrayList<Integer> fixedLines) {
            var inc = 0;
            for (int i = 0; i < multiTestLeakageInstances.size(); i++) {
                //Doesn't always reload contents of document from disk
                var inst = multiTestLeakageInstances.get(i);
                var lineNumber = inst.lineNumber() - 1 + inc;

                var lineTextRange = DocumentUtil.getLineTextRange(document, lineNumber);
                var lineContent = document.getText(lineTextRange);
                var newStr = "# TODO: load the test data for the evaluation.\n" + lineContent.replace(instance.getText(), instance.getText() + "_" + i);
                document.replaceString(document.getLineStartOffset(lineNumber), document.getLineEndOffset(lineNumber), newStr);
                fixedLines.add(lineNumber);
                inc++;
            }
        }


    }


}
