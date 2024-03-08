package com.github.SE4AIResearch.DataLeakage_Fall2023.tool_windows;

import com.github.SE4AIResearch.DataLeakage_Fall2023.actions.RunLeakageAnalysis;
import com.github.SE4AIResearch.DataLeakage_Fall2023.data.LeakageInstance;
import com.github.SE4AIResearch.DataLeakage_Fall2023.data.LeakageSource;
import com.github.SE4AIResearch.DataLeakage_Fall2023.enums.LeakageType;
import com.github.SE4AIResearch.DataLeakage_Fall2023.parsers.LeakageAnalysisParser;
import com.intellij.icons.AllIcons;
import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.panels.HorizontalLayout;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.table.JBTable;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// implementing DumbAware makes the tool window not available until indexing is complete
public class LeakageToolWindow implements ToolWindowFactory, DumbAware {
   private LeakageToolWindowContent toolWindowContent;

   @Override
   public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
      toolWindowContent = new LeakageToolWindowContent(project, toolWindow);

      JPanel contentPanel = new JPanel();

      contentPanel.setLayout(new BorderLayout(0, 5));
      contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

//      contentPanel.add(toolWindowContent.createControlsPanel(toolWindow), BorderLayout.SOUTH);

      Content content = ContentFactory.getInstance().createContent(toolWindowContent.getContentPanel(), "", false);
      toolWindow.getContentManager().addContent(content);
   }

   private class LeakageToolWindowContent {

      private final JPanel contentPanel;
      private JBTable summaryTable;
      private JBTable instanceTable;
      private DefaultTableModel instanceTableModel;
      private DefaultTableModel summaryTableModel;
      private final Project project;
      private List<LeakageInstance> leakageInstances;
      private int execTime;
      private String formattedStartTime;
      private JBLabel timeLabel;

      public LeakageToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
         this.project = project;
         this.contentPanel = createContentPanel(toolWindow);
         this.execTime = -1;
         this.formattedStartTime = "";

         // TODO declare everything in here

         toolWindow.getComponent().add(contentPanel);

         updateTableData();
      }

      private JPanel createContentPanel(@NotNull ToolWindow toolWindow) {
         JPanel mainPanel, summaryPanel, instancePanel, timePanel, controlsPanel;
         GridBagLayout layout;
         GridBagConstraints gbc;
         ActionToolbar toolbar;
         int toolWindowWidth, row;

         toolWindowWidth = toolWindow.getComponent().getWidth();
         mainPanel = new JPanel();
         layout = new GridBagLayout();
         gbc = new GridBagConstraints();
         mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
         toolbar = createToolBar(mainPanel);

         summaryPanel = createSummaryPanel(new String[]{"Leakage Type", "Leakage Count"});
         instancePanel = createInstancePanel(new String[]{"Leakage Type", "Line Number", "Variable Associated", "Cause"});
         timePanel = createTimePanel();
         controlsPanel = createControlsPanel(toolWindow);
         JComponent toolbarComp = toolbar.getComponent();

         row = 0;
         gbc.fill = GridBagConstraints.HORIZONTAL;
         gbc.anchor = GridBagConstraints.EAST;
         GridAdder.addObject(toolbarComp, mainPanel, layout, gbc, 0, row++, 1, 1, 1, 0);
         gbc.fill = GridBagConstraints.BOTH;
         GridAdder.addObject(summaryPanel, mainPanel, layout, gbc, 0, row++, 1, 1, 1, 1);
         GridAdder.addObject(instancePanel, mainPanel, layout, gbc, 0, row++, 1, 1, 1, 1);
         gbc.fill = GridBagConstraints.HORIZONTAL;
         GridAdder.addObject(timePanel, mainPanel, layout, gbc, 0, row++, 1, 1, 1, 0);
         GridAdder.addObject(controlsPanel, mainPanel, layout, gbc, 0, row, 1, 1, 1, 0);

         mainPanel.setLayout(layout);

         return mainPanel;
      }

      private ActionToolbar createToolBar(JPanel targetPanel) {
         ActionToolbar toolbar;

         // Create a button with an IntelliJ icon
         AnAction helpAction = new AnAction("Help", "Show help", AllIcons.General.ContextHelp) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
               // Show help message when button is clicked
               Messages.showInfoMessage(
                     project,
                     "HELP MESSAGE",
                     ""
               );

               openWebpage("https://se4airesearch.github.io/DataLeakage_Website_Fall2023/pages/leakage/multi-test/");
            }
         };

         DefaultActionGroup actionGroup = new DefaultActionGroup(
               flexibleSpace,
               helpAction);
         toolbar = ActionManager.getInstance().createActionToolbar(ActionPlaces.TOOLWINDOW_TITLE, actionGroup, true);
         toolbar.setTargetComponent(targetPanel);

         return toolbar;
      }

//      private JButton createHelpButton() {
//         JButton helpButton;
//
//         helpButton = new JButton(AllIcons.General.ContextHelp);
//
//         helpButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//               // Show help message when button is clicked TODO fix with a link action
//               Messages.showErrorDialog(
//                     project,
//                     "HELP MESSAGE",
//                     ""
//               );
//            }
//         });
//
//         return helpButton;
//      }
//
//      private static class myAction extends AnAction {
//         private final JButton button;
//
//         public myAction(JButton button) {
//            super("");
//            this.button = button;
//         }
//
//         @Override
//         public void actionPerformed(@NotNull AnActionEvent e) {
//            // Button's action performed
//            button.doClick();
//         }
//      }


      @NotNull
      private JPanel createControlsPanel(ToolWindow toolWindow) {
         JPanel controlsPanel = new JPanel();
         JButton runAnalysisButton = new JButton("Run Leakage Analysis");
//         JButton refreshTableButton = new JButton("Refresh Table");

//         refreshTableButton.addActionListener(e -> updateTableData());

         runAnalysisButton.addActionListener(e -> {
            long startTime = System.currentTimeMillis();

            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            formattedStartTime = now.format(formatter);

            DataContext dataContext = DataManager.getInstance().getDataContext(toolWindow.getComponent());
            AnActionEvent actionEvent = AnActionEvent.createFromDataContext("MyAction", null, dataContext);
            new RunLeakageAnalysis(project).actionPerformed(actionEvent);

            long endTime = System.currentTimeMillis();
            double executionTimeSeconds = (endTime - startTime) / 1000.0;
            this.execTime = (int) executionTimeSeconds;

            updateTableData();
            updateTimeLabel();
         });

         controlsPanel.add(runAnalysisButton, BorderLayout.CENTER);
//         controlsPanel.add(refreshTableButton, BorderLayout.WEST);

         return controlsPanel;
      }

      @NotNull
      private JPanel createSummaryPanel(String[] columnNames) {
         JPanel summaryPanel;

         summaryPanel = new JPanel(new BorderLayout());
         summaryTableModel = new DefaultTableModel(columnNames, 3) {
            @Override
            public boolean isCellEditable(int row, int column) {
               // Make all cells non-editable
               return false;
            }
         };
         summaryTable = new JBTable(summaryTableModel);
         summaryTable.setPreferredScrollableViewportSize(new Dimension(2, 3));

         summaryTable.setValueAt("Pre-Processing", 0, 0);
         summaryTable.setValueAt("Multi-Test", 1, 0);
         summaryTable.setValueAt("Overlap", 2, 0);

         JBScrollPane scrollPane = new JBScrollPane();
         scrollPane.setViewportView(summaryTable);

         scrollPane.setBorder(BorderFactory.createTitledBorder("Leakage Summary"));

         summaryPanel.add(scrollPane, BorderLayout.CENTER);

         return summaryPanel;
      }


      private void updateTimeLabel() {
         String newText = "Showing results from " +
               this.formattedStartTime +
               " | Analysis completed in " +
               execTime
               + " seconds";

         this.timeLabel.setText(newText);
      }

      private JPanel createTimePanel() {
         JPanel panel = new JPanel(new BorderLayout());

         String labelString = "";

         JBLabel timeLabel = new JBLabel(labelString, SwingConstants.CENTER);
         this.timeLabel = timeLabel;

         panel.add(timeLabel);

         return panel;
      }

      private static JPanel createNewInfoPanel() {

         JPanel panel = new JPanel(new BorderLayout());

         JBLabel multi = createNewLinkLabel("Multi-Test Leakage Info", "https://se4airesearch.github.io/DataLeakage_Website_Fall2023/pages/leakage/multi-test/");
         JBLabel preprop = createNewLinkLabel("Preprocessing Leakage Info", "https://se4airesearch.github.io/DataLeakage_Website_Fall2023/pages/leakage/preprocessing/");
         JBLabel overlap = createNewLinkLabel("Overlap Leakage Info", "https://se4airesearch.github.io/DataLeakage_Website_Fall2023/pages/leakage/overlap/");

         panel.add(multi, BorderLayout.NORTH);
         panel.add(preprop, BorderLayout.CENTER);
         panel.add(overlap, BorderLayout.SOUTH);

         return panel;
      }

      private static JBLabel createNewLinkLabel(String name, String url) {
         JBLabel label = new JBLabel("<html><u>" + name + "</u></html>");
         label.setForeground(Color.LIGHT_GRAY);
         label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

         label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
               openWebpage(url);
            }
         });

         return label;
      }

      private static void openWebpage(String url) {
         try {
            Desktop.getDesktop().browse(URI.create(url));
         } catch (Exception e) {
            return;
         }
      }

      @NotNull
      private JPanel createInstancePanel(String[] columnNames) {
         JPanel instancesPanel = new JPanel(new BorderLayout());

         instanceTableModel = new DefaultTableModel(columnNames, 3) {
            @Override
            public boolean isCellEditable(int row, int column) {
               // Make all cells non-editable
               return false;
            }
         };
         instanceTable = new JBTable(instanceTableModel);

         JBScrollPane scrollPane = new JBScrollPane(instanceTable);
         scrollPane.setBorder(BorderFactory.createTitledBorder("Leakage Instances"));

         instancesPanel.add(scrollPane, BorderLayout.CENTER);

         return instancesPanel;
      }

      public void updateTableData() {
         // Update Instance
         instanceTable.removeAll();
         instanceTableModel.setRowCount(0);

         Object[][] newInstanceData = fetchInstanceData();

         for (Object[] row : newInstanceData) {
            instanceTableModel.addRow(row);
         }

         // Refresh table view
         instanceTable.revalidate();
         instanceTable.repaint();

         // Update Summary
         summaryTable.removeAll();
         summaryTableModel.setRowCount(0);

         Object[][] newSummaryData = fetchSummaryData();

         for (Object[] row : newSummaryData) {
            summaryTableModel.addRow(row);
         }

         // Mouse listener for line number navigation
         MyTableMouseListener myTableMouseListener = new MyTableMouseListener(instanceTable, project);
         instanceTable.addMouseListener(myTableMouseListener);

         // Refresh table view
         summaryTable.revalidate();
         summaryTable.repaint();
      }

      private Object[][] fetchInstanceData() {
         // for instance it is type, line number, variable name
         ArrayList<String[]> data;
         LeakageAnalysisParser leakageAnalysisParser = new LeakageAnalysisParser();
         List<LeakageInstance> leakageInstances = leakageAnalysisParser.LeakageInstances();
         int row = leakageInstances.size();
         int col = instanceTableModel.getColumnCount();

         if (row == 0) {
            return new String[1][4];
         }

         data = new ArrayList<>();

//         {"Leakage Type", "Line Number", "Variable Associated", "Cause"}

         int curRow = 0;
         for (LeakageInstance instance : leakageInstances) {
            String[] newDataRow = new String[col];
            LeakageSource source = instance.getLeakageSource();

            newDataRow[0] = instance.type().toString(); // Leakage Type
            newDataRow[1] = String.valueOf(instance.lineNumber()); //Line Number
            newDataRow[2] = instance.variableName(); // Variable Associated
            if (source != null) {
               newDataRow[3] = source.getCause().name(); // Cause
            }

            data.add(curRow, newDataRow);
            curRow++;
            if (source != null) {
               for (int i = 0; i < source.getLineNumbers().size(); i++) {
                  String[] subDataRow = newDataRow.clone();
                  subDataRow[1] = source.getLineNumbers().get(i).toString();
                  data.add(curRow, subDataRow);
                  curRow++;
               }
            }
         }

         return data.toArray(String[][]::new); // Convert data arraylist to array
      }

      private Object[][] fetchSummaryData() {
         Object[][] data = new String[3][2];
         LeakageAnalysisParser leakageAnalysisParser = new LeakageAnalysisParser();
         List<LeakageInstance> leakageInstances = leakageAnalysisParser.LeakageInstances();
         int preproc = 0, multitest = 0, overlap = 0;

         for (LeakageInstance instance : leakageInstances) {
            switch (instance.type()) {
               case PreprocessingLeakage:
                  preproc++;
                  break;
               case MultiTestLeakage:
                  multitest++;
                  break;
               case OverlapLeakage:
                  overlap++;
                  break;
            }
         }

         data[0][0] = "Pre-Processing";
         data[1][0] = "Multi-Test";
         data[2][0] = "Overlap";

         data[0][1] = String.valueOf(preproc);
         data[1][1] = String.valueOf(multitest);
         data[2][1] = String.valueOf(overlap);

         return data;
      }

      @NotNull
      private JPanel getContentPanel() {
         return contentPanel;
      }


   }
}
