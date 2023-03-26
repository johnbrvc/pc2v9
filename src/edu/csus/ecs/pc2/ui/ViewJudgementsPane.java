// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingUtilities;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.IRunListener;
import edu.csus.ecs.pc2.core.model.Judgement;
import edu.csus.ecs.pc2.core.model.JudgementRecord;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.RunEvent;
import edu.csus.ecs.pc2.core.model.Site;
import edu.csus.ecs.pc2.core.security.Permission;

/**
 * Shows all run judgements for the input run.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ViewJudgementsPane extends JPanePlugin implements UIPlugin {

    /**
     * 
     */
    private static final long serialVersionUID = 2350404181525660056L;

    private JPanel centerPane = null;

    private JPanel northPane = null;

    private JPanel titlePane = null;

    private MCLB judgementsListbox = null;

//    private MCLB testCasesTable = null;
    private JPanePlugin testCasesTable = null;
    
    private JLabel statusLabel = null;

    private Run run;

    private JLabel titleLabel = null;

    public ViewJudgementsPane() {
        initialize();
    }

    public void setRun(Run run) {
        this.run = run;
        populateGUI();
    }

    private void populateGUI() {
        Runnable updateGrid = new Runnable() {
            public void run() {
                // Update Run title

                String deletedString = "";
                if (run.isDeleted()){
                    deletedString = " DELETED ";
                }
                
                setTitle("Run " + run.getNumber() + " elapsed " + run.getElapsedMins()+  deletedString);

                JudgementRecord judgementRecord = run.getJudgementRecord();
                judgementsListbox.removeAllRows();

                if (judgementRecord == null) {
                    showMessage("No judgements found for Run " + run.getNumber());
                } else {
                    populateJudgementRows(run);
                }

            }

        };
        SwingUtilities.invokeLater(updateGrid);

    }

    protected void setTitle(final String infoString) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                titleLabel.setText(infoString);
            }
        });
    }

    protected String getSiteTitle(int siteNumber) {
        Site site = getContest().getSites()[siteNumber - 1];
        if (site == null) {
            return "<missing>";
        }
        return site.toString();
    }

    protected void showMessage(String string) {
        statusLabel.setText(string);

    }

    private void populateJudgementRows(Run inRun) {

        JudgementRecord[] records = inRun.getAllJudgementRecords();

        for (int i = 0; i < records.length; i++) {
            Object[] objects = buildJudgmentRow(i + 1, inRun, records[i]);
            judgementsListbox.addRow(objects);
            
//            testCasesTable.addRow(objects);
        }
        judgementsListbox.autoSizeAllColumns();
//        testCasesTable.autoSizeAllColumns();
        
        if (records.length == 1) {
            showMessage("There is " + records.length + " judgement");
        } else {
            showMessage("There are " + records.length + " judgements");
        }
    }

    private String yesNoString(boolean b) {
        if (b) {
            return "Yes";
        } else {
            return "No";
        }
    }

    /**
     * Return the columns to populate the grid.
     * 
     * Assumes that record is NOT null.
     * 
     * @param rowNumber
     * @param inRun
     * @param record
     * @return
     */
    private Object[] buildJudgmentRow(int rowNumber, Run inRun, JudgementRecord judgementRecord) {

        // Object[] cols = { "##", "Judgement", "Judge", "Active", "Time", "Final", "TWJ", "TTJ", "Comment for team", "Comment for judge", "X time" };

        int numberColumns = judgementsListbox.getColumnCount();
        Object[] column = new Object[numberColumns];

        int col = 0;

        column[col] = new Integer(rowNumber).toString();
        column[++col] = getJudgementName(judgementRecord);

        // Judge
        column[++col] = judgementRecord.getJudgerClientId().getName() + "/s" + judgementRecord.getJudgerClientId().getSiteNumber();
        if (judgementRecord.isComputerJudgement()) {
            column[col] = judgementRecord.getJudgerClientId().getName() + "/Computer";
        } else if (judgementRecord.isUsedValidator()) {
            column[col] = judgementRecord.getJudgerClientId().getName() + "/Val";
        }

        // Active
        column[++col] = yesNoString(judgementRecord.isActive());

        // Time
        column[++col] = "" + inRun.getElapsedMins();

        // Prelim
        col++;
        try {
            column[col] = yesNoString(! judgementRecord.isPreliminaryJudgement());
        } catch (Exception e) {
            // backward compatibility, isPreliminaryJudgement is in use after Feb 2009
            column[col] = "-";
        }

        column[++col] = "" + judgementRecord.getWhenJudgedTime();
        column[++col] = judgementRecord.getHowLongToJudgeInSeconds() + "s";

        col++;
        if (judgementRecord.getCommentForTeam() != null) {
            column[col] = judgementRecord.getCommentForTeam().getComment();
        } else {
            column[col] = "(None)";
        }

        col++;
        if (judgementRecord.getCommentForJudge() != null) {
            column[col] = judgementRecord.getCommentForJudge().getComment();
        } else {
            column[col] = "(None)";
        }
        
        col++;
        column[col] = judgementRecord.getExecuteMS() + "ms";
        return column;
    }

    private String getJudgementName(JudgementRecord judgementRecord) {
        
        String name = "No";
        
        if (judgementRecord != null && judgementRecord.getJudgementId() != null) {
            if (judgementRecord.isUsedValidator() && judgementRecord.getValidatorResultString() != null) {
                name = judgementRecord.getValidatorResultString();
            } else {
                Judgement judgement = getContest().getJudgement(judgementRecord.getJudgementId());
                if (judgement != null){
                    name = judgement.toString();
                }
            }
        } else {
            name = "<missing>";
        }

        return name;
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setSize(new java.awt.Dimension(660, 475));
        this.setLayout(new BorderLayout());
        this.add(getCenterPane(), java.awt.BorderLayout.CENTER);
        this.add(getNorthPane(), java.awt.BorderLayout.NORTH);
        this.add(getButtonPane(), java.awt.BorderLayout.SOUTH);
    }

    /**
     * This method initializes jPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getCenterPane() {
        if (centerPane == null) {
            centerPane = new JPanel();
            centerPane.setLayout(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();
            c.fill = GridBagConstraints.BOTH;
            c.weighty = 0.75;
            c.weightx = 0.5;
            c.gridx = 0;
            c.gridy = 0;
            c.insets = new Insets(0, 0, 20, 0);
            centerPane.add(getJudgementsListbox(), c);
            c.gridy = 1;
            c.weighty = 0.5;
            centerPane.add(getTestCasesTable(), c);
            if(false) {
            centerPane.setLayout(new BorderLayout());
            centerPane.add(getJudgementsListbox(), java.awt.BorderLayout.NORTH);
            centerPane.add(getTestCasesTable(), java.awt.BorderLayout.SOUTH);
            }
        }
        return centerPane;
    }

    /**
     * This method initializes jPanel1
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getNorthPane() {
        if (northPane == null) {
            statusLabel = new JLabel();
            statusLabel.setText("statusLabel");
            statusLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            statusLabel.setName("statusLabel");
            northPane = new JPanel();
            northPane.setLayout(new BorderLayout());
            northPane.setPreferredSize(new java.awt.Dimension(35, 35));
            northPane.add(statusLabel, java.awt.BorderLayout.CENTER);
        }
        return northPane;
    }

    /**
     * This method initializes jPanel2
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getButtonPane() {
        if (titlePane == null) {
            titleLabel = new JLabel();
            titleLabel.setText("No run chosen");
            titleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            titlePane = new JPanel();
            titlePane.setLayout(new BorderLayout());
            titlePane.setPreferredSize(new java.awt.Dimension(35, 35));
            titlePane.add(titleLabel, java.awt.BorderLayout.CENTER);
        }
        return titlePane;
    }

    /**
     * This method initializes multiColumnListbox
     * 
     * @return com.ibm.webrunner.j2mclb.MCLB
     */
    private MCLB getJudgementsListbox() {
        if (judgementsListbox == null) {
            judgementsListbox = new MCLB();
            
            Object[] cols = { "##", "Judgement", "Judge", "Active", "Time", "Final", "TWJ", "TTJ", "Comment for team", "Comment for judge", "X time" };
            judgementsListbox.addColumns(cols);
            judgementsListbox.autoSizeAllColumns();
            
            judgementsListbox.addListboxListener(new com.ibm.webrunner.j2mclb.event.ListboxListener() {
                public void rowSelected(com.ibm.webrunner.j2mclb.event.ListboxEvent e) {
                    System.out.println("Clicked: " + e);
                }
                public void rowDeselected(com.ibm.webrunner.j2mclb.event.ListboxEvent e)
                {
                    
                }
            });
        }
        return judgementsListbox;
    }

    /**
     * This method initializes multiColumnListbox
     * 
     * @return com.ibm.webrunner.j2mclb.MCLB
     */
    private JPanePlugin getTestCasesTable() {
        if(testCasesTable == null) {
            testCasesTable = new TestResultsPane();
        }
        return(testCasesTable);
//        Object[] cols = { "##", "Judgement", "Judge", "Active", "Time", "Final", "TWJ", "TTJ", "Comment for team", "Comment for judge", "X time" };
//        if (testCasesTable == null) {
//            testCasesTable = new JTableCustomized();
//
//            testCasesTable.addMouseListener(new MouseAdapter() {
//                public void mouseClicked(MouseEvent me) {
//                    JTable target = (JTable)me.getSource();
//                    if(target.getSelectedRow() != -1) {
//                        System.out.println("Row " + target.getSelectedRow() + " selected");
//                    }
//                }
//            });
//        }
//        return testCasesTable;
//      if (testCasesTable == null) {
//            testCasesTable = new MCLB();
//            Object[] cols = { "##", "Judgement", "Judge", "Active", "Time", "Final", "TWJ", "TTJ", "Comment for team", "Comment for judge", "X time" };
//            testCasesTable.addColumns(cols);
//            testCasesTable.autoSizeAllColumns();
//        }
//        return testCasesTable;
    }

    @Override
    public String getPluginTitle() {
        return "Run Judgement View";
    }
    
    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        super.setContestAndController(inContest, inController);
        getTestCasesTable().setContestAndController(inContest, inController);
        
        getContest().addRunListener(new RunListenerImplementation());
    }
    
    /**
     * 
     * 
     * @author pc2@ecs.csus.edu
     */
    public class RunListenerImplementation implements IRunListener {

        public void runAdded(RunEvent event) {
            if (run != null && event.getRun().getElementId().equals(run.getElementId())) {
                setRun(event.getRun());
            }
        }
        
        public void refreshRuns(RunEvent event) {
            // FIXME - close this frame, remove from screen 
        }

        public void runChanged(RunEvent event) {
            if (run != null && event.getRun().getElementId().equals(run.getElementId())) {
                setRun(event.getRun());
            }
        }

        public void runRemoved(RunEvent event) {
            if (run != null && event.getRun().getElementId().equals(run.getElementId())) {
                setRun(event.getRun());
            }
        }
    }

} // @jve:decl-index=0:visual-constraint="10,10"
