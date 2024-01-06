package com.github.cd721.data_leakage_plugin.data;

import com.github.cd721.data_leakage_plugin.data.leakage_data.LeakageOutput;
import com.github.cd721.data_leakage_plugin.enums.TaintLabel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Very roughly speaking, taints correspond to sources of data leakage.
 */
public class Taint {

    private final Invocation invocation;
    private final String pyCallExpression;
    private final TaintLabel taintType;

    public Taint(String taintString, TaintLabel taintType) {
        this.taintType = taintType;
        this.invocation = getInvoFromTaint(taintString);
        this.pyCallExpression = getPyCallExpressionFromTaint(taintString);
    }

    private Invocation getInvoFromTaint(String taint) {
        String[] taintSplit = taint.split("\t");
        if (taintSplit.length < 6) {
            //TODO: how to handle this case?
            return new Invocation("$invo0");
        }

        return new Invocation(taintSplit[4]);
    }



    private String getPyCallExpressionFromTaint(String taint) {
        String[] taintSplit = taint.split("\t");
        if (taintSplit.length < 6) {
            return "";
        }

        return (taintSplit[5]);
    }

    public Invocation getInvocation() {
        return invocation;
    }

    public String getPyCallExpression() {
        return pyCallExpression;
    }

}
