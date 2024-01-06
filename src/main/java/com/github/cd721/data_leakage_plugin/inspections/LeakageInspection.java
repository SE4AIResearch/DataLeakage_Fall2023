package com.github.cd721.data_leakage_plugin.inspections;

import com.github.cd721.data_leakage_plugin.data.leakage_data.LeakageInstance;
import com.github.cd721.data_leakage_plugin.enums.LeakageType;
import com.github.cd721.data_leakage_plugin.parsers.LeakageAnalysisParser;
import com.intellij.codeInspection.LocalInspectionToolSession;
import com.intellij.codeInspection.ProblemsHolder;
import com.jetbrains.python.inspections.PyInspection;
import com.jetbrains.python.psi.PyElementVisitor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class LeakageInspection<T extends LeakageInstance> extends PyInspection {
    public abstract LeakageType getLeakageType();
    public final LeakageAnalysisParser leakageAnalysisParser = new LeakageAnalysisParser();

    /**
     * @return A list of {@link LeakageInstance}s having the same type as this {@link LeakageInspection}.
     * //TODO: remove unchecked cast
     */
    public List<T> getLeakageInstancesForType(List<LeakageInstance> leakageInstances){
        return leakageInstances.stream()
                .filter(instance -> instance.type().equals(getLeakageType()))
                .map(instance -> (((T) instance))).toList();
    }

    public abstract ElementVisitor getElementVisitor(@NotNull ProblemsHolder holder);
    @Override
    public  @NotNull PyElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly, @NotNull LocalInspectionToolSession session){
        if (Utils.getInjectionHost(holder) != null) {
            return new PyElementVisitor();
        }

        var document = Utils.getDocument(holder);
        if (document == null) return new PyElementVisitor();

        return getElementVisitor(holder);
    }

;

}
