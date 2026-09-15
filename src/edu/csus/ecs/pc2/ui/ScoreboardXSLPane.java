// Copyright (C) 1989-2026 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.EventObject;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.table.TableColumn;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.StringUtilities;
import edu.csus.ecs.pc2.core.XMLUtilities;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.ContestInformation;
import edu.csus.ecs.pc2.core.model.ContestInformationEvent;
import edu.csus.ecs.pc2.core.model.IContestInformationListener;
import edu.csus.ecs.pc2.core.model.IInternalContest;

/**
 * View and select Scoreboard XSL pane.
 *
 * @author John Buck
 * @version $Id$
 */

// $HeadURL$

public class ScoreboardXSLPane extends JPanePlugin {

    private static final long serialVersionUID = 1L;

    public static final String extensionFilter = "xsl";

    private JPanel xslButtonPane = null;

    private MCLB xslListBox = null;

    private JButton applyButton = null;

    private JButton revertButton = null;

    private JButton selectAllButton = null;

    private JButton deselectAllButton = null;

    private JButton refreshButton = null;

    private JPanel messagePane = null;

    private JLabel messageLabel = null;

    private Log log;

    private JScrollPane xslListPane = null;
    private FileTableModel tableModel = null;
    private JTable table = null;
    private File currentFolder = null;

    private String [] currentXslFiles = null;

    /**
     * This method initializes
     *
     */
    public ScoreboardXSLPane() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     *
     */
    private void initialize() {
        this.setLayout(new BorderLayout());
        this.setSize(new java.awt.Dimension(564, 229));
        this.add(getXslListPane(), java.awt.BorderLayout.CENTER);
        this.add(getMessagePane(), java.awt.BorderLayout.NORTH);
        this.add(getXslButtonPane(), java.awt.BorderLayout.SOUTH);
    }

    @Override
    public String getPluginTitle() {
        return "Scoreboard XSL Pane";
    }

    @Override
    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        super.setContestAndController(inContest, inController);

        log = getController().getLog();
        getCurrentXslFiles();
        getContest().addContestInformationListener(new ContestInformationListenerImplementation());
    }

    private void getCurrentXslFiles() {
        ContestInformation ci = getContest().getContestInformation();

        currentXslFiles = ci.getScoreboardXSLFiles();
        if(currentXslFiles != null) {
            // So we don't clobber the original yet.
            currentXslFiles = currentXslFiles.clone();
        }
    }

    /**
     * This method initializes xslButtonPane
     *
     * @return javax.swing.JPanel
     */
    private JPanel getXslButtonPane() {
        if (xslButtonPane == null) {
            FlowLayout flowLayout = new FlowLayout();
            flowLayout.setHgap(25);
            xslButtonPane = new JPanel();
            xslButtonPane.setLayout(flowLayout);
            xslButtonPane.setPreferredSize(new java.awt.Dimension(35, 35));
            xslButtonPane.add(getApplyButton(), null);
            xslButtonPane.add(getRevertButton(), null);
            xslButtonPane.add(getSelectAllButton(), null);
            xslButtonPane.add(getDeselectAllButton(), null);
            xslButtonPane.add(getRefreshButton(), null);
        }
        return xslButtonPane;
    }

    /**
     * This method initializes xslListBox
     *
     * @return edu.csus.ecs.pc2.core.log.MCLB
     */
    private JScrollPane getXslListPane() {
        if(xslListPane == null) {
            currentFolder = new File(XMLUtilities.getStyleSheetDirectoryName());
            tableModel = new FileTableModel();
            table = new JTable(tableModel);

            // --- Table setup ---
            table.setRowHeight(24);
            table.setShowGrid(true);
            table.setGridColor(new Color(230, 230, 230));
            table.getTableHeader().setReorderingAllowed(false);
            table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            tableModel.addTableModelListener(e -> {
                if(e.getColumn() == 0 && e.getType() == TableModelEvent.UPDATE) {
                    // Checkbox changed, so see what control have to be enabled/disabled
                    updateButtons();
                }
            });

            TableColumn fileColumn = table.getColumnModel().getColumn(0);
            fileColumn.setCellRenderer(new CheckBoxCellRenderer());
            fileColumn.setCellEditor(new CheckBoxCellEditor());
            fileColumn.setPreferredWidth(64);

            TableColumn descColumn = table.getColumnModel().getColumn(1);
            descColumn.setPreferredWidth(500);

            // Create the scroll pane now
            xslListPane = new JScrollPane(table);
            loadFiles(currentFolder);

        }
        return xslListPane;
    }

    /**
     * Called when a checkbox is changed.  Update which buttons are active based
     * on what has changed.
     * TODO: This is called before the selection changes!! so it's all wrong.
     */
    private void updateButtons() {
        System.err.println("TM Update");
//        String [] saveXslFiles = currentXslFiles;
//        getCurrentXslFiles();
//        boolean bChange = !StringUtilities.stringArraySame(saveXslFiles, currentXslFiles);
//        getRevertButton().setEnabled(bChange);
//        getApplyButton().setEnabled(bChange);
//        // JB TODO - this is not right because if the list is really empty (nothing checked), we
//        // have to deal with that - currently no way in current model to that.
//        if(currentXslFiles == null) {
//            getSelectAllButton().setEnabled(true);
//            getDeselectAllButton().setEnabled(false);
//        } else {
//            getSelectAllButton().setEnabled(true);
//            getDeselectAllButton().setEnabled(true);
//        }
    }

    /**
     * This method initializes the Apply button
     *
     * @return javax.swing.JButton
     */
    private JButton getApplyButton() {
        if (applyButton == null) {
            applyButton = new JButton("Apply");
            applyButton.addActionListener(e -> {
                stopEditing();
                applyXslChoices();
                });
            applyButton.setToolTipText("Apply the selected XSL files for scoreboard HTML generation.");
        }
        return applyButton;
    }

    protected void applyXslChoices() {
        String [] saveXslFiles = currentXslFiles;

        getSelectedFiles();

        if(!StringUtilities.stringArraySame(saveXslFiles, currentXslFiles)) {
            ContestInformation ci = getContest().getContestInformation();
            ci.setScoreboardXSLFiles(currentXslFiles);
            if(currentXslFiles != null) {
                currentXslFiles = currentXslFiles.clone();
            }
            // save ContesInformation to model
            getController().updateContestInformation(ci);
        }
    }

    /**
     * This method initializes revertButton
     *
     * @return javax.swing.JButton
     */
    private JButton getRevertButton() {
        if (revertButton == null) {
            revertButton = new JButton("Revert");
            revertButton.addActionListener(e -> { stopEditing(); revertXslChoices(); });
            revertButton.setToolTipText("Revert to original settings before the recent changes you made.");
        }
        return revertButton;
    }

    protected void revertXslChoices() {
        stopEditing();
        getCurrentXslFiles();
        loadFiles(currentFolder);
    }

    /**
     * This method initializes the select all button
     *
     * @return javax.swing.JButton
     */
    private JButton getSelectAllButton() {
        if (selectAllButton == null) {
            selectAllButton = new JButton("Select All");
            selectAllButton.addActionListener(e -> { stopEditing(); tableModel.setAllSelected(true); });
            selectAllButton.setToolTipText("Select all XSL files.");
        }
        return selectAllButton;
    }

    /**
     * This method initializes the deselect all button
     *
     * @return javax.swing.JButton
     */
    private JButton getDeselectAllButton() {
        if (deselectAllButton == null) {
            deselectAllButton = new JButton("Deselect All");
            deselectAllButton.addActionListener(e -> { stopEditing(); tableModel.setAllSelected(false); });
            deselectAllButton.setToolTipText("Deselect all XSL files.");
        }
        return deselectAllButton;
    }

    /**
     * This method initializes the refresh button
     *
     * @return javax.swing.JButton
     */
    private JButton getRefreshButton() {
        if (refreshButton == null) {
            refreshButton = new JButton("Refresh");
            refreshButton.addActionListener(e -> {
                stopEditing();
                getSelectedFiles();
                loadFiles(currentFolder);
            });
            refreshButton.setToolTipText("Refresh file list in case something was added or deleted.");
        }
        return refreshButton;
    }

    private void getSelectedFiles() {
        List<File> selected = tableModel.getSelectedFiles();
        List<String> selectedFiles = new ArrayList<>();

        for (File f : selected) {
            selectedFiles.add(f.getName());
        }
        if (selected.isEmpty()) {
            currentXslFiles = null;
        } else {
            currentXslFiles = selectedFiles.stream().toArray(String[]::new);
        }
    }

    private void printSelected() {
        List<File> selected = tableModel.getSelectedFiles();
        StringBuilder sb = new StringBuilder("Selected files:\n");
        for (File f : selected) {
            sb.append(f.getName()).append("\n");
        }
        if (selected.isEmpty()) {
            sb.append("(none)");
        }
        JOptionPane.showMessageDialog(this, sb.toString());
    }

    private static class CheckBoxListRenderer implements ListCellRenderer<JCheckBox> {
        @Override
        public Component getListCellRendererComponent(JList<? extends JCheckBox> list,
                                                        JCheckBox value,
                                                        int index,
                                                        boolean isSelected,
                                                        boolean cellHasFocus) {
            value.setBackground(isSelected ? list.getSelectionBackground() : list.getBackground());
            value.setForeground(isSelected ? list.getSelectionForeground() : list.getForeground());
            return value;
        }
    }


    /**
     * This method initializes messagePane
     *
     * @return javax.swing.JPanel
     */
    private JPanel getMessagePane() {
        if (messagePane == null) {
            messageLabel = new JLabel();
            messageLabel.setText("");
            messageLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            messagePane = new JPanel();
            messagePane.setLayout(new BorderLayout());
            messagePane.setPreferredSize(new java.awt.Dimension(25,25));
            messagePane.add(messageLabel, java.awt.BorderLayout.CENTER);
        }
        return messagePane;
    }

    /**
     * show message to user
     *
     * @param string
     */
    private void showMessage(final String string) {

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JOptionPane.showMessageDialog(getParentFrame(), string, "Warning", JOptionPane.WARNING_MESSAGE);
            }
        });

    }

    private void loadFiles(File folder) {

        if (folder == null || !folder.exists() || !folder.isDirectory()) {
            showMessage("Invalid folder: " + folder);
            return;
        }

        // Capture the current filter value in a local so the lambda below
        // closes over an effectively-final variable.
        final String currentExtensionFilter = extensionFilter;

        FilenameFilter filter = (dir, name) -> {
            if (currentExtensionFilter == null) {
                return true; // no filter -> show everything
            }
            return name.toLowerCase(Locale.ROOT).endsWith("." + currentExtensionFilter);
        };

        File[] files = folder.listFiles(filter);
        if (files == null) {
            files = new File[0];
        }

        Arrays.sort(files, Comparator.comparing(File::getName, String.CASE_INSENSITIVE_ORDER));

        tableModel.setFiles(Arrays.asList(files), currentXslFiles);
    }

    /** One row of data: the underlying file plus its current checked state. */
    private static class FileRow {
        final File file;
        boolean selected;

        FileRow(File file) {
            this.file = file;
        }
    }

    /**
     * Table model with two columns:
     *   Column 0 ("File")        -> a FileRow, rendered/edited as a checkbox + filename
     *   Column 1 ("Description") -> a plain description string (size, folder/file, modified date)
     */
    private static class FileTableModel extends javax.swing.table.AbstractTableModel {
        private final List<FileRow> rows = new ArrayList<>();
        private static final String[] COLUMNS = {"File", "Description"};
        private Pattern descPattern = Pattern.compile("^<!--.+(?i:Description):\\s+(.+)\\s+-->$");
        private static final int LINES_TO_CHECK_FOR_DESCRIPTION = 5;

        @Override
        public int getRowCount() {
            return rows.size();
        }

        @Override
        public int getColumnCount() {
            return COLUMNS.length;
        }

        @Override
        public String getColumnName(int column) {
            return COLUMNS[column];
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            FileRow row = rows.get(rowIndex);
            if (columnIndex == 0) {
                return row; // renderer/editor pulls file name + selected state out of this
            }
            return describe(row.file);
        }

        @Override
        public void setValueAt(Object value, int rowIndex, int columnIndex) {
            if (columnIndex == 0 && value instanceof Boolean) {
                rows.get(rowIndex).selected = (Boolean) value;
                fireTableCellUpdated(rowIndex, columnIndex);
            }
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return columnIndex == 0;
        }

        void setFiles(List<File> files, String [] currentXslFiles) {
            rows.clear();
            String fname;
            boolean bAll;
            HashSet<String> strXslFiles = null;

            if(currentXslFiles == null) {
                bAll = true;
            } else {
                bAll = false;
                strXslFiles = new HashSet<>();
                Collections.addAll(strXslFiles, currentXslFiles);
            }
            for (File f : files) {
                FileRow row = new FileRow(f);
                // Select the row if in the list
                row.selected = (bAll || strXslFiles.contains(row.file.getName()));
                rows.add(row);
            }
            fireTableDataChanged();
        }

        void setAllSelected(boolean selected) {
            for (FileRow row : rows) {
                row.selected = selected;
            }
            fireTableDataChanged();
        }

        List<File> getSelectedFiles() {
            List<File> result = new ArrayList<>();
            for (FileRow row : rows) {
                if (row.selected) {
                    result.add(row.file);
                }
            }
            return result;
        }

        private String describe(File file) {
            if (file.isDirectory()) {
                return "Folder";
            }

            // Use try-with-resources to ensure the underlying file stream is closed
            try (Stream<String> lines = Files.lines(file.toPath())) {
                Optional<String> capturedValue = lines
                        .limit(LINES_TO_CHECK_FOR_DESCRIPTION)
                        .map(line -> {
                            Matcher matcher = descPattern.matcher(line);
                            return matcher.find() ? matcher.group(1) : null;
                        })
                        .filter(capturedGroup -> capturedGroup != null) // Ignore lines without a match
                        .findFirst();                                   // Short-circuits & stops reading the file

                if (capturedValue.isPresent()) {
                    return(capturedValue.get());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "No description";
        }
    }

    /** Displays column 0 as a non-interactive checkbox labeled with the file name. */
    private static class CheckBoxCellRenderer implements javax.swing.table.TableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                         boolean hasFocus, int row, int column) {
            FileRow fileRow = (FileRow) value;
            JCheckBox checkBox = new JCheckBox(fileRow.file.getName(), fileRow.selected);
            checkBox.setOpaque(true);
            checkBox.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
            checkBox.setForeground(isSelected ? table.getSelectionForeground() : table.getForeground());
            return checkBox;
        }
    }

    /** Lets the user actually click the checkbox in column 0 to toggle selection. */
    private static class CheckBoxCellEditor extends AbstractCellEditor implements javax.swing.table.TableCellEditor {
        private final JCheckBox checkBox = new JCheckBox();

        CheckBoxCellEditor() {
            checkBox.setOpaque(true);
            checkBox.addActionListener(e -> stopCellEditing());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected,
                                                       int row, int column) {
            FileRow fileRow = (FileRow) value;
            checkBox.setText(fileRow.file.getName());
            checkBox.setSelected(fileRow.selected);
            checkBox.setBackground(table.getSelectionBackground());
            return checkBox;
        }

        @Override
        public Object getCellEditorValue() {
            return checkBox.isSelected();
        }

        @Override
        public boolean isCellEditable(EventObject anEvent) {
            return true; // start editing on a single click, not the default double-click
        }
    }

    private void stopEditing() {
        if (table.isEditing()) {
            table.getCellEditor().stopCellEditing();
        }
    }

    class ContestInformationListenerImplementation implements IContestInformationListener {

        @Override
        public void contestInformationAdded(ContestInformationEvent event) {
            checkXslFilesChanged(event.getContestInformation());
        }

        @Override
        public void contestInformationChanged(ContestInformationEvent event) {
            checkXslFilesChanged(event.getContestInformation());
        }

        @Override
        public void contestInformationRemoved(ContestInformationEvent event) {
            // TODO Auto-generated method stub

        }

        @Override
        public void contestInformationRefreshAll(ContestInformationEvent contestInformationEvent) {
            checkXslFilesChanged(contestInformationEvent.getContestInformation());
        }

        @Override
        public void finalizeDataChanged(ContestInformationEvent contestInformationEvent) {
            // Not used
        }

        /**
         * Check if we need to update the list of selected XSL files due to a settings change
         */
        void checkXslFilesChanged(ContestInformation ci) {
            String [] newXslFiles = ci.getScoreboardXSLFiles();
            if(!StringUtilities.stringArraySame(newXslFiles, currentXslFiles)) {
                currentXslFiles = newXslFiles.clone();
                stopEditing();
                loadFiles(currentFolder);
            }
        }
    }


} // @jve:decl-index=0:visual-constraint="10,10"
