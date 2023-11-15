package com.github.cd721.data_leakage_plugin.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

public class IPYNBAnalysis extends AnAction {

    @Override
    public void update(@NotNull AnActionEvent event) {
        // Using the event, evaluate the context,
        // and enable or disable the action.

        // only should be available when an IPYNB file is selected
    }
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        // convert IPYNB file to python and perform analysis on it,
        // naming the functions that have leakage
    }
}
