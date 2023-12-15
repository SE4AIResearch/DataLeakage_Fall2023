package com.github.cd721.data_leakage_plugin.inspections;

import com.intellij.codeInspection.LocalInspectionToolSession;
import com.intellij.codeInspection.ProblemsHolder;
import com.jetbrains.python.inspections.PyInspectionVisitor;
import com.jetbrains.python.psi.types.TypeEvalContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MyInspectionVisitor extends PyInspectionVisitor {
    public MyInspectionVisitor(@Nullable ProblemsHolder holder, @NotNull TypeEvalContext context) {
        super(holder, context);
    }

}
