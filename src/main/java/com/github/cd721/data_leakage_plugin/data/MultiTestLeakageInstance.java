package com.github.cd721.data_leakage_plugin.data;

import com.github.cd721.data_leakage_plugin.enums.LeakageType;
import com.jetbrains.python.psi.PyReferenceExpression;

public class MultiTestLeakageInstance extends LeakageInstance {

    private final String test;

    public MultiTestLeakageInstance(int lineNumber,  Invocation invocation) {
        super(lineNumber, LeakageType.MultiTestLeakage, invocation);
        this.test = Utils.getTestFromTelemetryFile();
    }

    public String test() {
        return test;
    }
}
