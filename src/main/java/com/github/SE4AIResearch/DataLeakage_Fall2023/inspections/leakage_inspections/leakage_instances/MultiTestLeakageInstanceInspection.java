package com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.leakage_inspections.leakage_instances;

import com.github.SE4AIResearch.DataLeakage_Fall2023.data.LeakageInstance;
import com.github.SE4AIResearch.DataLeakage_Fall2023.data.MultiTestLeakageInstance;
import com.github.SE4AIResearch.DataLeakage_Fall2023.data.OverlapLeakageInstance;
import com.github.SE4AIResearch.DataLeakage_Fall2023.enums.LeakageType;
import com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.InspectionBundle;
import com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.PsiUtils;
import com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.leakage_inspections.LeakageInspection;
import com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.visitors.leakage_instances.InstanceElementVisitor;
import com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.visitors.leakage_instances.MultiTestLeakageInstanceVisitor;
import com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.visitors.leakage_instances.OverlapLeakageInstanceVisitor;
import com.intellij.codeInspection.ProblemsHolder;
import com.jetbrains.python.psi.PyCallExpression;
import com.jetbrains.python.psi.PyElementVisitor;
import com.jetbrains.python.psi.PyReferenceExpression;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public class MultiTestLeakageInstanceInspection extends InstanceInspection<MultiTestLeakageInstance> {

    @Override
    public LeakageType getLeakageType() {
        return LeakageType.MultiTestLeakage;
    }


    @Override
    public InstanceElementVisitor<MultiTestLeakageInstance> instanceElementVisitor(List<MultiTestLeakageInstance> leakageInstances, @NotNull ProblemsHolder holder) {
        return new MultiTestLeakageInstanceVisitor(leakageInstances, holder);

    }
}
