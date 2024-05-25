// Copyright (C) 1989-2024 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.logging.Level;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.model.Clarification;
import edu.csus.ecs.pc2.core.model.ContestInformationEvent;
import edu.csus.ecs.pc2.core.model.ContestTime;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.FinalizeData;
import edu.csus.ecs.pc2.core.model.IContestInformationListener;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Judgement;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.Run.RunStates;
import edu.csus.ecs.pc2.core.report.FinalizeReport;
import edu.csus.ecs.pc2.exports.ccs.ResultsFile;

/**
 * Edit Finalize settings pane.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */
public class FinalizePane extends JPanePlugin {

    /**
     * 
     */
    private static final long serialVersionUID = 3089291613784484371L;

    private JPanel buttonPane = null;

    private JButton finalizeButton = null;
    
    private JButton updateButton = null;

    private JPanel centerPane = null;

    private JLabel goldLabel = null;

    private JLabel silverLabel = null;

    private JLabel bronzeLabel = null;

    private JLabel certifierLabel = null;

    private JTextField goldRankTextField = null;

    private JTextField silverRankTextField = null;

    private JTextField bronzeRankTextField = null;

    private JTextField commentTextField = null;

    private JLabel certificationCommentLabel = null;

    private JButton reportButton = null;

    private JPanel southPanel;

    private JPanel viewResultsPane;

    private JLabel resultsFileLabel;

    private JButton viewButton;

    /**
     * This method initializes
     * 
     */
    public FinalizePane() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setLayout(new BorderLayout());
        this.setSize(new Dimension(457, 239));
        this.add(getCenterPane(), BorderLayout.CENTER);
        add(getSouthPanel(), BorderLayout.SOUTH);
    }

    @Override
    public String getPluginTitle() {
        return "Finalized Pane";
    }

    /**
     * This method initializes buttonPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getButtonPane() {
        if (buttonPane == null) {
            FlowLayout flowLayout = new FlowLayout();
            flowLayout.setHgap(45);
            buttonPane = new JPanel();
            buttonPane.setLayout(flowLayout);
            buttonPane.setPreferredSize(new Dimension(52, 35));
            buttonPane.add(getUpdateButton(), null);
            buttonPane.add(getFinalizeButton(), null);
            buttonPane.add(getReportButton(), null);
        }
        return buttonPane;
    }

    @Override
    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        super.setContestAndController(inContest, inController);

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                reloadFrame();
            }
        });

        getContest().addContestInformationListener(new ContestInformationListenerImplementation());

    }

    protected FinalizeData getFromFields() {
        FinalizeData data = new FinalizeData();

        data.setGoldRank(getIntegerValue(getGoldRankTextField()));
        data.setSilverRank(getIntegerValue(getSilverRankTextField()));
        data.setBronzeRank(getIntegerValue(getBronzeRankTextField()));
        data.setComment("" + getCommentTextField().getText());

        return data;
    }

    private int getIntegerValue(JTextField textField) {
        String s = "0" + textField.getText();
        return Integer.parseInt(s);
    }

    private void populateDefaults() {
        getGoldRankTextField().setText("4");
        getSilverRankTextField().setText("8");
        getBronzeRankTextField().setText("12");
    }

    protected void reloadFrame() {

        FinalizeData data = getContest().getFinalizeData();
        if (data != null) {
            getGoldRankTextField().setText(Integer.toString(data.getGoldRank()));
            getSilverRankTextField().setText(Integer.toString(data.getSilverRank()));
            getBronzeRankTextField().setText(Integer.toString(data.getBronzeRank()));
            getCommentTextField().setText(data.getComment());

            if (data.isCertified()) {
                certificationCommentLabel.setText("Contest Finalized (Certified done)");
                certificationCommentLabel.setToolTipText("Certified at: " + data.getCertificationDate());
            }

        } else {
            certificationCommentLabel.setText("Contest not finalized");
            certificationCommentLabel.setToolTipText("");
            populateDefaults();
        }

        enableButtons();

    }

    /**
     * Just update the data, do not certify/finalize.
     */
    protected void updateFinalizeData() {

        FinalizeData data = getFromFields();

        FinalizeData contestFinalizedata = getContest().getFinalizeData();

        if (contestFinalizedata != null) {
            data.setCertified(contestFinalizedata.isCertified());
        }

        getController().updateFinalizeData(data);
    }

    /**
     * This method initializes finalizeButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getFinalizeButton() {
        if (finalizeButton == null) {
            finalizeButton = new JButton();
            finalizeButton.setText("Finalize");
            finalizeButton.setMnemonic(KeyEvent.VK_Z);
            finalizeButton.setToolTipText("Certify Contest Results");
            finalizeButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    certifyContest();
                }
            });
        }
        return finalizeButton;
    }

    /**
     * This method initializes updateButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getUpdateButton() {
        if (updateButton == null) {
            updateButton = new JButton();
            updateButton.setText("Update");
            updateButton.setMnemonic(KeyEvent.VK_U);
            updateButton.setToolTipText("Update medal counts");
            updateButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    updateMedalCounts();
                }
            });
        }
        return updateButton;
    }

    protected void updateMedalCounts() {

        FinalizeData data = getFromFields();
        FinalizeData currentData = getContest().getFinalizeData();
        
        try {
            if(currentData != null && currentData.isCertified()) {
                throw new InvalidFieldValue("You can not change the medal counts on a certfied contest");                
            }
            if (data.getGoldRank() <= 0) {
                throw new InvalidFieldValue("Gold rank must be greater than zero");                
            }
            if (data.getSilverRank() <= 0) {
                throw new InvalidFieldValue("Silver rank must be greater than zero");                
            }
            if (data.getBronzeRank() <= 0) {
                throw new InvalidFieldValue("Bronze rank must be greater than zero");
            }
            if(data.getGoldRank() >= data.getSilverRank()) {
                throw new InvalidFieldValue("Gold rank must not be greater than silver rank");
            }
            if(data.getSilverRank() >= data.getBronzeRank()) {
                throw new InvalidFieldValue("Silver rank must not be greater than bronze rank");
            }

        } catch (InvalidFieldValue e) {
            showMessage(e.getMessage());
            return;
        }

        data.setCertified(false);
        getController().updateFinalizeData(data);
    }

    protected void certifyContest() {

        FinalizeData data = getFromFields();

        try {

            int numberUnjudgedRuns = getNumberUnjudgedRuns();
            if (numberUnjudgedRuns > 0) {
                throw new InvalidFieldValue("Cannot finalize all runs must be judged, " + numberUnjudgedRuns + " unjudged runs");
            }

            int numberUnasweredClars = getNumberUnansweredClars();
            if (numberUnasweredClars > 0) {
                throw new InvalidFieldValue("Cannot finalize all clars must be answered, " + numberUnasweredClars + " un-answered clarifications");
            }

            int numberJudgingErrorRuns = getNumberJERuns(getContest());
            if (numberJudgingErrorRuns > 0) {
                throw new InvalidFieldValue("Cannot finalize there are runs with Judging Errors (JEs), " + numberJudgingErrorRuns + " un-answered clarifications");
            }
            ContestTime contestTime = getContest().getContestTime();

            if (contestTime.isContestRunning()) {
                throw new InvalidFieldValue("Cannot finalize contest, contest clock not stopped");
            }

            if (contestTime.getRemainingSecs() > 0) {
                throw new InvalidFieldValue("Cannot finalize contest - contest not over - remaining time: " + contestTime.getRemainingTimeStr());
            }

            if (data.getBronzeRank() == 0) {
                throw new InvalidFieldValue("Cannot finalize contest - Bronze rank must be greater than zero");
            }

            if (data.getComment().trim().length() < 1) {
                throw new InvalidFieldValue("Cannot finalize contest - missing comment, enter a comment");
            }

        } catch (InvalidFieldValue e) {
            showMessage(e.getMessage());
            return;
        }

        int result = FrameUtilities.yesNoCancelDialog(this, "Are you sure you want to finalize?", "Sure, really realy sure?");
        if (result == JOptionPane.YES_OPTION) {
            data.setCertified(true);
            getController().updateFinalizeData(data);
        }

    }

    /**
     * Get number of JE runs.
     * 
     * @param contest
     * @return
     */
    public static int getNumberJERuns(IInternalContest contest) {
        Run[] runs = contest.getRuns();

        Filter filter = new Filter();
        filter.addRunState(RunStates.JUDGED);

        runs = filter.getRuns(runs);

        Judgement judgementJE = null;
        Judgement[] judgeList = contest.getJudgements();
        for (Judgement judgement : judgeList) {
            if (judgement.getAcronym() != null) {
                if (judgement.getAcronym().equalsIgnoreCase("JE")) {
                    judgementJE = judgement;
                }
            }
        }

        if (judgementJE == null) {
            /**
             * No JE judgement, there is no way to have any runs judged as JE.
             */

            return 0; // ------------------------ RETURN -------------
        }

        int count = 0;

        for (Run run : runs) {
            if (!run.isDeleted()) {

                ElementId id = run.getJudgementRecord().getJudgementId();
                if (judgementJE.getElementId().equals(id)) {
                    count++;
                }
            }
        }
        return count;
    }

    private int getNumberUnjudgedRuns() {
        Run[] runs = getContest().getRuns();

        Filter filter = new Filter();
        filter.addRunState(RunStates.JUDGED);

        int deletedRuns = 0;
        for (Run run : runs) {
            if (run.isDeleted()) {
                deletedRuns++;
            }
        }

        return runs.length - deletedRuns - filter.getRuns(runs).length;
    }

    private int getNumberUnansweredClars() {
        Clarification[] clarifications = getContest().getClarifications();

        int count = 0;
        for (Clarification clarification : clarifications) {
            if (!clarification.isAnswered()) {
                count++;
            }
        }

        return count;
    }

    private void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    /**
     * This method initializes centerPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getCenterPane() {
        if (centerPane == null) {
            certificationCommentLabel = new JLabel();
            certificationCommentLabel.setBounds(new Rectangle(83, 19, 349, 26));
            certificationCommentLabel.setFont(new Font("Dialog", Font.BOLD, 14));
            certificationCommentLabel.setHorizontalAlignment(SwingConstants.CENTER);
            certificationCommentLabel.setText("Contest Not Certified");
            certifierLabel = new JLabel();
            certifierLabel.setBounds(new Rectangle(64, 167, 170, 22));
            certifierLabel.setText("Who certifies");
            certifierLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            bronzeLabel = new JLabel();
            bronzeLabel.setBounds(new Rectangle(64, 134, 170, 22));
            bronzeLabel.setText("Last Bronze Rank");
            bronzeLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            silverLabel = new JLabel();
            silverLabel.setBounds(new Rectangle(64, 97, 170, 22));
            silverLabel.setText("Last Silver Rank");
            silverLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            goldLabel = new JLabel();
            goldLabel.setBounds(new Rectangle(64, 56, 170, 22));
            goldLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            goldLabel.setText("Last Gold Rank");
            centerPane = new JPanel();
            centerPane.setLayout(null);
            centerPane.add(goldLabel, null);
            centerPane.add(silverLabel, null);
            centerPane.add(bronzeLabel, null);
            centerPane.add(certifierLabel, null);
            centerPane.add(getGoldRankTextField(), null);
            centerPane.add(getSilverRankTextField(), null);
            centerPane.add(getBronzeRankTextField(), null);
            centerPane.add(getCommentTextField(), null);
            centerPane.add(certificationCommentLabel, null);
        }
        return centerPane;
    }

    /**
     * This method initializes goldRankTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getGoldRankTextField() {
        if (goldRankTextField == null) {
            goldRankTextField = new JTextField();
            goldRankTextField.setBounds(new Rectangle(250, 57, 40, 20));
            goldRankTextField.setDocument(new IntegerDocument());
        }
        return goldRankTextField;
    }

    /**
     * This method initializes silverRankTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getSilverRankTextField() {
        if (silverRankTextField == null) {
            silverRankTextField = new JTextField();
            silverRankTextField.setBounds(new Rectangle(250, 94, 40, 20));
            silverRankTextField.setDocument(new IntegerDocument());
        }
        return silverRankTextField;
    }

    /**
     * This method initializes bronzeRankTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getBronzeRankTextField() {
        if (bronzeRankTextField == null) {
            bronzeRankTextField = new JTextField();
            bronzeRankTextField.setBounds(new Rectangle(250, 131, 40, 20));
            goldRankTextField.setDocument(new IntegerDocument());
        }
        return bronzeRankTextField;
    }

    /**
     * This method initializes commentTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getCommentTextField() {
        if (commentTextField == null) {
            commentTextField = new JTextField();
            commentTextField.setBounds(new Rectangle(250, 168, 207, 20));
        }
        return commentTextField;
    }

    /**
     * Contest Information Listener for Judgement Notifications.
     * 
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */

    // $HeadURL$
    public class ContestInformationListenerImplementation implements IContestInformationListener {

        public void contestInformationAdded(ContestInformationEvent event) {
            // not used
        }

        public void contestInformationChanged(ContestInformationEvent event) {
            // not used
        }

        public void contestInformationRemoved(ContestInformationEvent event) {
            // not used
        }

        public void contestInformationRefreshAll(ContestInformationEvent contestInformationEvent) {
            // not used
        }

        public void finalizeDataChanged(ContestInformationEvent contestInformationEvent) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    reloadFrame();
                }
            });
        }

    }

    /**
     * This method initializes reportButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getReportButton() {
        if (reportButton == null) {
            reportButton = new JButton();
            reportButton.setText("Report");
            reportButton.setMnemonic(KeyEvent.VK_R);
            reportButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    Utilities.viewReport(new FinalizeReport(), "Finalize Report", getContest(), getController());
                }
            });
        }
        return reportButton;
    }

    void enableButtons() {

        boolean finalized = false;
        if (getContest().getFinalizeData() != null) {
            finalized = getContest().getFinalizeData().isCertified();
        }

        getFinalizeButton().setEnabled(true);

        getViewButton().setEnabled(finalized);

        if (finalized) {
            String text = "Results file generated to " + genererateResultsFile();
            getResultsFileLabel().setText(text);
            getResultsFileLabel().setToolTipText(text);
        }

        getResultsFileLabel().setVisible(finalized);

    }

    private String genererateResultsFile() {

        String resultsDir = Utilities.getCurrentDirectory() + File.separator + "results";

        File dir = new File(resultsDir);
        if (!dir.isDirectory()) {
            dir.mkdirs();
        }

        String outfilename = resultsDir + File.separator + ResultsFile.RESULTS_FILENAME;

        ResultsFile resultsFile = new ResultsFile();
        try {
            String[] lines = resultsFile.createTSVFileLines(getContest());
            Utilities.writeLinesToFile(outfilename, lines);
            return outfilename;
        } catch (Exception e) {
            getLog().info("Unable to write results file " + outfilename);
            getLog().log(Level.WARNING, "Writing " + outfilename, e);
            return "Unable to write " + outfilename;
        }

    }

    private JPanel getSouthPanel() {
        if (southPanel == null) {
            southPanel = new JPanel();
            southPanel.setLayout(new BorderLayout(0, 0));
            southPanel.add(getButtonPane(), BorderLayout.SOUTH);
            southPanel.add(getViewResultsPane());
        }
        return southPanel;
    }

    private JPanel getViewResultsPane() {
        if (viewResultsPane == null) {
            viewResultsPane = new JPanel();
            viewResultsPane.setLayout(new BorderLayout(0, 0));
            viewResultsPane.add(getResultsFileLabel());
            viewResultsPane.add(getViewButton(), BorderLayout.EAST);
        }
        return viewResultsPane;
    }

    private JLabel getResultsFileLabel() {
        if (resultsFileLabel == null) {
            resultsFileLabel = new JLabel("results/resulst.tsv");
            resultsFileLabel.setHorizontalAlignment(SwingConstants.CENTER);
        }
        return resultsFileLabel;
    }

    private JButton getViewButton() {
        if (viewButton == null) {
            viewButton = new JButton("View");
            viewButton.setToolTipText("View results.tsv");
            viewButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    showResultsFile();
                }
            });
        }
        return viewButton;
    }

    protected void showResultsFile() {

        String resultsFile = genererateResultsFile();

        FrameUtilities.viewFile(resultsFile, "Results File", getLog());
    }

} // @jve:decl-index=0:visual-constraint="10,10"
