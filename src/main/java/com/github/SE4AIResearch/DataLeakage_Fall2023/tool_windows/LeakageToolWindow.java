package com.github.SE4AIResearch.DataLeakage_Fall2023.tool_windows;

import com.github.SE4AIResearch.DataLeakage_Fall2023.actions.RunLeakageAnalysis;
import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.table.JBTable;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

// implementing DumbAware makes the tool window not available until indexing is complete
public class LeakageToolWindow implements ToolWindowFactory, DumbAware {
   @Override
   public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
      LeakageToolWindowContent toolWindowContent = new LeakageToolWindowContent(toolWindow);
      Content content = ContentFactory.getInstance().createContent(toolWindowContent.getContentPanel(), "", false);
      toolWindow.getContentManager().addContent(content);
   }

   //TODO function to add or update content

   private static class LeakageToolWindowContent {

      private final JPanel contentPanel = new JPanel();

      public LeakageToolWindowContent(ToolWindow toolWindow) {
         contentPanel.setLayout(new BorderLayout(0, 5));
         contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
         contentPanel.add(createSummaryPanel(toolWindow), BorderLayout.NORTH);
         contentPanel.add(createInstancesPanel(toolWindow), BorderLayout.CENTER);
         contentPanel.add(createControlsPanel(toolWindow), BorderLayout.SOUTH);
      }

      @NotNull
      private JPanel createControlsPanel(ToolWindow toolWindow) {
         JPanel controlsPanel = new JPanel();
         // TODO somehow run the analysis action with this button
         JButton runAnalysisButton = new JButton("Run Leakage Analysis on Open File");
         controlsPanel.add(runAnalysisButton);

         return controlsPanel;
      }

      @NotNull
      private JPanel createSummaryPanel (ToolWindow toolWindow) {
         JPanel summaryPanel = new JPanel();

         String[] columnNames = {"Leakage Type", "Leakage Count", "Information on Leakage Type"};
         DefaultTableModel tableModel = new DefaultTableModel(columnNames, 3);
         JBTable summaryTable = new JBTable(tableModel);

         summaryTable.setShowGrid(true);
         summaryTable.setDragEnabled(false);
         summaryTable.setSize(3, 3);

         summaryTable.setValueAt("Pre-Processing", 0, 0);
         summaryTable.setValueAt("Multi-Test", 1, 0);
         summaryTable.setValueAt("Overlap", 2, 0);

//         summaryTable.getColumn(2).setCellRenderer();

         JBScrollPane scrollPane = new JBScrollPane(summaryTable);
         scrollPane.setBorder(BorderFactory.createTitledBorder("Leakage Summary"));

         summaryPanel.add(scrollPane);

         return summaryPanel;
      }

      @NotNull
      private JPanel createInstancesPanel (ToolWindow toolWindow) {
         JPanel instancesPanel = new JPanel();

         String[] columnNames = {"Leakage Type", "Line Number", "Variable Associated"};
         DefaultTableModel tableModel = new DefaultTableModel(columnNames, 3);
         JBTable instancesTable = new JBTable(tableModel);

         JBScrollPane scrollPane = new JBScrollPane(instancesTable);
         scrollPane.setBorder(BorderFactory.createTitledBorder("Leakage Instances"));

         instancesPanel.add(scrollPane);

         return instancesPanel;
      }

      @NotNull
      private JPanel getContentPanel () {
         return contentPanel;
      }


   }
}
