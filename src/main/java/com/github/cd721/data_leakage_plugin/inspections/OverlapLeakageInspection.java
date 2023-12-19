package com.github.cd721.data_leakage_plugin.inspections;

import com.github.cd721.data_leakage_plugin.data.MultiTestLeakageInstance;
import com.github.cd721.data_leakage_plugin.data.OverlapLeakageInstance;
import com.github.cd721.data_leakage_plugin.data.PreprocessingLeakageInstance;
import com.github.cd721.data_leakage_plugin.enums.LeakageType;
import com.github.cd721.data_leakage_plugin.parsers.LeakageAnalysisParser;
import com.intellij.codeInspection.LocalInspectionToolSession;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.openapi.editor.Document;
import com.intellij.psi.PsiBinaryFile;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiFile;
import com.jetbrains.python.inspections.PyInspection;
import com.jetbrains.python.psi.PyElementVisitor;
import com.jetbrains.python.psi.PyReferenceExpression;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class OverlapLeakageInspection extends PyInspection {
    private final LeakageAnalysisParser leakageAnalysisParser = new LeakageAnalysisParser();

    @Override
    public @NotNull PyElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly, @NotNull LocalInspectionToolSession session) {

        final PsiFile file = holder.getFile();
        if (InjectedLanguageManager.getInstance(file.getProject()).getInjectionHost(file) != null)
            return new PyElementVisitor();

        final Document document = file.getViewProvider().getDocument();
        if (document == null) return new PyElementVisitor();

        var leakageInstances = leakageAnalysisParser.LeakageInstances();

        var overlapLeakageInstances = leakageInstances.stream()
                .filter(instance -> instance.type().equals(LeakageType.OverlapLeakage))
                .map(instance -> ((OverlapLeakageInstance) (instance))).toList();


        return new PyElementVisitor() {
            @Override
            public void visitPyReferenceExpression(@NotNull PyReferenceExpression node) {
                var offset = node.getTextOffset();
                var nodeLineNumber = document.getLineNumber(offset) + 1; //getLineNumber is zero-based, must add 1


                if (overlapLeakageInstances.stream().anyMatch(instance -> (instance.lineNumber() == nodeLineNumber)
                        && Objects.equals(instance.test(), node.getName()))) {
                    holder.registerProblem(node, "Potential overlap leakage associated with this variable.");
                }


            }
        };

    }

}
