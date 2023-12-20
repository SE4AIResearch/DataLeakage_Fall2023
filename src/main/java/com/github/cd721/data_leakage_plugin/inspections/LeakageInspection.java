//package com.github.cd721.data_leakage_plugin.inspections;
//
//import com.github.cd721.data_leakage_plugin.data.LeakageInstance;
//import com.github.cd721.data_leakage_plugin.enums.LeakageType;
//import com.github.cd721.data_leakage_plugin.parsers.LeakageAnalysisParser;
//import com.intellij.codeInspection.LocalInspectionToolSession;
//import com.intellij.codeInspection.ProblemsHolder;
//import com.intellij.lang.injection.InjectedLanguageManager;
//import com.intellij.openapi.editor.Document;
//import com.intellij.psi.PsiFile;
//import com.jetbrains.python.inspections.PyInspection;
//import com.jetbrains.python.psi.PyElementVisitor;
//import com.jetbrains.python.psi.PyReferenceExpression;
//import org.jetbrains.annotations.NotNull;
//
//import java.util.List;
//import java.util.Objects;
//
//public class LeakageInspection extends PyInspection {
//    private final LeakageAnalysisParser leakageAnalysisParser = new LeakageAnalysisParser();
//    public LeakageType leakageType;
//    public List<LeakageInstance> leakageInstances;
//
//    public Class<? extends LeakageInstance> typeToCastTo = LeakageInstance.getInstanceTypeForLeakageType(leakageType);
//
//    public List<? extends LeakageInstance> leakageInstancesOfThisType() {
//        return this.leakageInstances.stream()
//                .filter(instance -> instance.type().equals(leakageType))
//                .map(instance -> (typeToCastTo.cast(instance))).toList();
//    }
//
//    @Override
//    public @NotNull PyElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly, @NotNull LocalInspectionToolSession session) {
//
//
//        this.leakageInstances = leakageAnalysisParser.LeakageInstances();
//        var leakageInstancesOfThisType = leakageInstancesOfThisType();
//
//
//        return makePyElementVisitor(holder,leakageInstancesOfThisType);
//    }
//
//    private PyElementVisitor makePyElementVisitor(@NotNull ProblemsHolder holder, List<? extends LeakageInstance> leakageInstancesOfThisType) {
//
//
//        return new
//
//                PyElementVisitor() {
//                    @Override
//                    public void visitPyReferenceExpression(@NotNull PyReferenceExpression node) {
//                  var nodeLineNumber=   Utils.getNodeLineNumber(node,holder);
//                        if (pyReferenceExpressionIsAssociatedWithLeakage(node)) {
//                            holder.registerProblem(node, InspectionBundle.get("inspectionText.multiTestLeakage.text"));
//                        }
//
//                    }
//                }
//
//                ;
//
//    }
