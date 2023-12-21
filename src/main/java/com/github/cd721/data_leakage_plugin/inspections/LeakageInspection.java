package com.github.cd721.data_leakage_plugin.inspections;

import com.github.cd721.data_leakage_plugin.data.LeakageInstance;
import com.github.cd721.data_leakage_plugin.enums.LeakageType;
import com.intellij.codeInspection.LocalInspectionToolSession;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.editor.Document;
import com.jetbrains.python.inspections.PyInspection;
import com.jetbrains.python.psi.PyElementVisitor;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Predicate;

public abstract class LeakageInspection<T> extends PyInspection {
    public abstract LeakageType getLeakageType();
    public abstract ElementVisitor getElementVisitor(Document document, @NotNull ProblemsHolder holder, List<LeakageInstance> leakageInstances);
    @Override
    public abstract @NotNull PyElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly, @NotNull LocalInspectionToolSession session);

;

}
