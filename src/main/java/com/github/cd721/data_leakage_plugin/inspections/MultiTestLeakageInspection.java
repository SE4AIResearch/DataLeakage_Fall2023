package com.github.cd721.data_leakage_plugin.inspections;

import com.github.cd721.data_leakage_plugin.data.LeakageInstance;
import com.github.cd721.data_leakage_plugin.data.MultiTestLeakageInstance;
import com.github.cd721.data_leakage_plugin.enums.LeakageType;
import com.github.cd721.data_leakage_plugin.parsers.LeakageAnalysisParser;
import com.intellij.codeInspection.LocalInspectionToolSession;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.editor.Document;
import com.jetbrains.python.psi.PyElementVisitor;
import com.jetbrains.python.psi.PyReferenceExpression;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public class MultiTestLeakageInspection extends ILeakageInspection {
    private final LeakageAnalysisParser leakageAnalysisParser = new LeakageAnalysisParser();

    @Override
    public LeakageType getLeakageType() {
        return LeakageType.MultiTestLeakage;
    }

    @Override
    public ElementVisitor getElementVisitor(Document document, @NotNull ProblemsHolder holder, List<LeakageInstance> leakageInstances) {
        var multiTestLeakageInstances = leakageInstances.stream()
                .filter(instance -> instance.type().equals(LeakageType.MultiTestLeakage))
                .map(instance -> ((MultiTestLeakageInstance) (instance))).toList();
        return new ElementVisitor() {
            @Override
            public void visitPyReferenceExpression(@NotNull PyReferenceExpression node) {
                var offset = node.getTextOffset();
                var nodeLineNumber = document.getLineNumber(offset) + 1; //getLineNumber is zero-based, must add 1

                Predicate<MultiTestLeakageInstance> predicate =

                        instance -> (instance.lineNumber() == nodeLineNumber)
                                && Objects.equals(instance.test(), node.getName()
                        );

                if (multiTestLeakageInstances.stream().anyMatch(predicate)) {
                    holder.registerProblem(node, InspectionBundle.get("inspectionText.multiTestLeakage.text"));
                }

            }
        };
    }

    @Override
    public @NotNull PyElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly, @NotNull LocalInspectionToolSession session) {

        if (Utils.getInjectionHost(holder) != null) {
            return new PyElementVisitor();
        }

        var document = Utils.getDocument(holder);
        if (document == null) return new PyElementVisitor();

        var leakageInstances = leakageAnalysisParser.LeakageInstances();



        return getElementVisitor(document, holder, leakageInstances);

    }

}
