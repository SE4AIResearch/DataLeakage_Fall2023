package com.github.cd721.data_leakage_plugin.data;

import com.github.cd721.data_leakage_plugin.leakage_detectors.Utils;
import com.intellij.psi.PsiFile;
import com.jetbrains.python.psi.PyCallExpression;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;

public class LeakageSource {
    private final Invocation invocation;
    private final String pyCallExpression;

    private final int lineNumber;

    public LeakageSource() {
        String taint = getTaintFromFile();

        this.invocation = getInvoFromTaint(taint);
        this.pyCallExpression = getPyCallExpressionFromTaint(taint);
        this.lineNumber =
                com.github.cd721.data_leakage_plugin.leakage_detectors.Utils.getActualLineNumberFromInternalLineNumber(LeakageOutput.folderPath(), Utils.getInternalLineNumberFromInvocation(LeakageOutput.folderPath(), this.invocation));


    }

    private String getPyCallExpressionFromTaint(String taint) {
        String[] taintSplit = taint.split("\t");
        if (taintSplit.length < 6) {
            return "";
        }

        return (taintSplit[5]);
    }

    private Invocation getInvoFromTaint(String taint) {
        String[] taintSplit = taint.split("\t");
        if (taintSplit.length < 6) {
            return new Invocation("$invo0");
        }

        return new Invocation(taintSplit[4]);
    }

    private String getTaintFromFile() {
        File file = new File(LeakageOutput.folderPath() + "TaintStartsTarget.csv");

        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));

            String line;

            while (((line = reader.readLine()) != null)) {
                String[] columns = line.split(("\t"));
                if (Objects.equals(columns[6], "dup")) {
                    return line;
                }

            }


        } catch (IOException e) {
            e.printStackTrace();

        }
        return "";
    }

    public Invocation getInvocation() {
        return invocation;
    }

    public String getPyCallExpression() {
        return pyCallExpression;
    }

    public int getLineNumber() {
        return lineNumber;
    }
}
