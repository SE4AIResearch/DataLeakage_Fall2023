package com.github.SE4AIResearch.DataLeakage_Fall2023.tool_windows;

import com.github.SE4AIResearch.DataLeakage_Fall2023.actions.RunLeakageAnalysis;
import com.github.SE4AIResearch.DataLeakage_Fall2023.data.LeakageInstance;
import com.github.SE4AIResearch.DataLeakage_Fall2023.enums.LeakageType;
import com.github.SE4AIResearch.DataLeakage_Fall2023.parsers.LeakageAnalysisParser;
import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.diagnostic.Logger;
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

// implementing DumbAware makes the tool window not available until indexing is complete
public class LeakageToolWindow implements ToolWindowFactory, DumbAware {

   private final JPanel summaryPanel = new JPanel();
   private final JPanel instancePanel = new JPanel();
   @Override
   public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
      LeakageToolWindowContent toolWindowContent = new LeakageToolWindowContent(toolWindow);

      JPanel contentPanel = new JPanel();

      contentPanel.setLayout(new BorderLayout(0, 5));
      contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

//      contentPanel.add(SummaryPanelFactory.getPanel(toolWindow), BorderLayout.NORTH);
//      contentPanel.add(InstancePanelFactory.getPanel(), BorderLayout.CENTER);

      contentPanel.add(toolWindowContent.createControlsPanel(toolWindow), BorderLayout.SOUTH);

      Content content = ContentFactory.getInstance().createContent(toolWindowContent.getContentPanel(), "", false);
      toolWindow.getContentManager().addContent(content);
   }

   private void updatePanels() {
      // get data for panels
      int preProcessingCount = 0;
      int multiTestCount = 0;
      int overlapCount = 0;

      LeakageAnalysisParser leakageAnalysisParser = new LeakageAnalysisParser();
      List<LeakageInstance> instances = leakageAnalysisParser.LeakageInstances();

      ArrayList<String> instanceData = new ArrayList<>();
      for (int i = 0; i < instances.size(); i++) {
         LeakageInstance instance = instances.get(i);
         instance.lineNumber();

      }
      for (LeakageInstance instance : instances) {
         instance.lineNumber();
         LeakageType leakageType =  instance.type();

      }

//      SummaryPanelFactory.getPanel(toolWindow);
   }

   //TODO function to add or update content

   private class LeakageToolWindowContent {

      private final JPanel contentPanel;
      private JBTable summaryTable;
      private JBTable instanceTable;
      private DefaultTableModel instanceTableModel;

      public LeakageToolWindowContent(ToolWindow toolWindow) {
         contentPanel = new JPanel();
         contentPanel.setLayout(new BorderLayout(0, 5));
         contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

         contentPanel.add(createSummaryPanel(), BorderLayout.NORTH);
         contentPanel.add(createInstancePanel(), BorderLayout.CENTER);

         contentPanel.add(createControlsPanel(toolWindow), BorderLayout.SOUTH);

         updateTableData();
      }

      @NotNull
      private JPanel createControlsPanel(ToolWindow toolWindow) {
         JPanel controlsPanel = new JPanel();
         JButton runAnalysisButton = new JButton("Run Leakage Analysis on Open File");

         runAnalysisButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               // Call method to update table data
               updateTableData();
               // TODO run analysis somehow
            }
         });

         controlsPanel.add(runAnalysisButton);

         return controlsPanel;
      }

      @NotNull
      private JPanel createSummaryPanel() {
         JPanel summaryPanel = new JPanel(new BorderLayout());

         String[] columnNames = {"Leakage Type", "Leakage Count", "Information on Leakage Type"};
         DefaultTableModel tableModel = new DefaultTableModel(columnNames, 3);
         summaryTable = new JBTable(tableModel);

         summaryTable.setValueAt("Pre-Processing", 0, 0);
         summaryTable.setValueAt("Multi-Test", 1, 0);
         summaryTable.setValueAt("Overlap", 2, 0);

         // TODO Make links work
//         summaryTable.getColumn(2).setCellRenderer();

         JBScrollPane scrollPane = new JBScrollPane(summaryTable);
         scrollPane.setBorder(BorderFactory.createTitledBorder("Leakage Summary"));

         summaryPanel.add(scrollPane, BorderLayout.CENTER);

         return summaryPanel;
      }

      @NotNull
      private JPanel createInstancePanel() {
         JPanel instancesPanel = new JPanel(new BorderLayout());

         String[] columnNames = {"Leakage Type", "Line Number", "Variable Associated"};
         instanceTableModel = new DefaultTableModel(columnNames, 3);
         instanceTable = new JBTable(instanceTableModel);

         JBScrollPane scrollPane = new JBScrollPane(instanceTable);
         scrollPane.setBorder(BorderFactory.createTitledBorder("Leakage Instances"));

         instancesPanel.add(scrollPane, BorderLayout.CENTER);

         return instancesPanel;
      }

      private void updateTableData() {
         instanceTable.removeAll();
         instanceTableModel.setRowCount(0);

         Object[][] newInstanceData = fetchInstanceData();

         for(Object[] row : newInstanceData) {
            instanceTableModel.addRow(row);
         }

         // Refresh table view
         instanceTable.revalidate();
         instanceTable.repaint();
      }

      private Object[][] fetchInstanceData() {
         // for instance it is type, line number, variable name
         Object[][] data;
         LeakageAnalysisParser leakageAnalysisParser = new LeakageAnalysisParser();
         List<LeakageInstance> leakageInstances = leakageAnalysisParser.LeakageInstances();
         int row = leakageInstances.size();
         int col = 3;

         if (row == 0) {
            return new String[1][3];
         }

         data = new String[row][col];

         for (int i = 0; i < row; i++) {
            LeakageInstance instance =  leakageInstances.get(i);
            data[i][0] = instance.type().toString();
            data[i][1] = String.valueOf(instance.lineNumber());
            data[i][2] = "";
         }

         return data;
      }

      @NotNull
      private JPanel getContentPanel() {
         return contentPanel;
      }


   }
}
