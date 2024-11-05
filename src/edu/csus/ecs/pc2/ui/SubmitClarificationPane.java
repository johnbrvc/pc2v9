// Copyright (C) 1989-2024 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.list.AccountComparator;
import edu.csus.ecs.pc2.core.list.GroupComparator;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.AccountEvent;
import edu.csus.ecs.pc2.core.model.CategoryEvent;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.ContestTimeEvent;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.Group;
import edu.csus.ecs.pc2.core.model.IAccountListener;
import edu.csus.ecs.pc2.core.model.ICategoryListener;
import edu.csus.ecs.pc2.core.model.IContestTimeListener;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.IProblemListener;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.ProblemEvent;
import edu.csus.ecs.pc2.core.security.Permission;

/**
 * Submit Clarification Pane.
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class SubmitClarificationPane extends JPanePlugin {

    /**
     * 
     */
    private static final long serialVersionUID = 6395977089692171705L;
    
    public static final String CHECKBOX_GROUP_TEAM_PROPERTY = "groupteam";
    
    public static final String ALL_TEAMS = "All Teams";
    
    public static final String GROUPS = "Group(s)";
    
    public static final String SPECIFIC_TEAMS = "Specific Team(s)";
 // the original height of the jcombobox was 22.  the groups jlist is 3 lines, so we added 46(?)
    // this makes it easier to make the groups list box bigger without having to change all the
    // control offsets below it.
    private static final int GROUPS_LIST_HEIGHT = 68;

    private Log log;  //  @jve:decl-index=0:

    private JPanel problemPane = null;
    
    private JPanel announcementDestinationPane = null;

    private JComboBox<Problem> problemComboBox = null;
    
    private JComboBox<String> announcementDestinationComboBox = null;

    private JPanel largeTextBoxPane = null;

    private JTextArea largeTextArea = null;

    private JButton submitClarificationButton = null;
    
    private JCheckBox submitAnnouncement = null;
      
    private boolean isTeam = false;
    
    private ListModel<Object> groupsandTeamsListModel = new DefaultListModel<Object>();
    
    private JCheckBoxJList groupsandTeamsJList = null;
    
    private JScrollPane groupsandTeamsScrollPane = null;
    
    private JPanel groupsPanel = null;
    /**
     * This method initializes
     * 
     */
    public SubmitClarificationPane() {      
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    protected void initialize() {
        this.setLayout(null);
        this.setSize(new java.awt.Dimension(456, 285));
        SubmitClarificationPane current = this;
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                isTeam = getContest().getClientId().getClientType().equals(ClientType.Type.TEAM);
                if (!isTeam) {
                    current.add(getSubmitAnnouncementCheckBox(),null);
                    current.add(getGroupsandTeamsPanel(),null);
                    current.add(getAnnouncementDestinationPane(),null);
                }
                current.add(getProblemPane(), null);
                current.add(getLargeTextBoxPane(), null);
                current.add(getSubmitClarificationButton(), null);
            }
        });
    }
    
    /**
     * Initializes Pane that has the combobox to select the destination group such as "All Teams","Groups", "Specific Teams"
     * @return
     */
    private JPanel getAnnouncementDestinationPane() {
        if (announcementDestinationPane == null) {
            announcementDestinationPane = new JPanel();
            announcementDestinationPane.setLayout(new BorderLayout());
            announcementDestinationPane.setBounds(new java.awt.Rectangle(370, 50, 336, 54));
            announcementDestinationPane.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Destination", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                    javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
            announcementDestinationPane.add(getAnnouncementDestinationComboBox(), java.awt.BorderLayout.CENTER);
            announcementDestinationPane.setVisible(false);
        }
        return announcementDestinationPane;
    }
    
    /**
     * This method initializes announcementComboBox
     * 
     * @return javax.swing.JComboBox
     */
    private JComboBox<String> getAnnouncementDestinationComboBox() {
        if (announcementDestinationComboBox == null) {
            announcementDestinationComboBox = new JComboBox<String>();
        }
        return announcementDestinationComboBox;
    }
    
    /**
     * This panel contains JCheckBoxJlist that has either groups or teams to select.
     * @return
     */
    private JPanel getGroupsandTeamsPanel() {
        if (groupsPanel == null) {
            groupsPanel = new JPanel();
            groupsPanel.setLocation(new java.awt.Point(370, 119));
            groupsPanel.setSize(new java.awt.Dimension(336, 200));
            groupsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Groups", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                    javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
            groupsPanel.add(getGroupsandTeamsList(),null);
            groupsPanel.add(getGroupsandTeamsScrollPane(),null);
            groupsPanel.setVisible(false);
        }
        
        return groupsPanel;
    }
    
    /**
     * Initializes groupsJList that either contain nothing or groups or teams.
     * @return
     */
    private JCheckBoxJList getGroupsandTeamsList() {
        
        if (groupsandTeamsJList == null) {
            groupsandTeamsJList = new JCheckBoxJList();
            
            
            ((DefaultListModel<Object>) groupsandTeamsListModel).removeAllElements();
                           
            groupsandTeamsJList.setModel(groupsandTeamsListModel);
            groupsandTeamsJList.setSize(new java.awt.Dimension(336, 200));

        }
        return groupsandTeamsJList;
    }
    
    /**
     * This method initializes groups ScrollPane
     *
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getGroupsandTeamsScrollPane() {
        if (groupsandTeamsScrollPane == null) {
            groupsandTeamsScrollPane = new JScrollPane();
            groupsandTeamsScrollPane.setBounds(new java.awt.Rectangle(14, 291, 272, GROUPS_LIST_HEIGHT));
            groupsandTeamsScrollPane.setViewportView(getGroupsandTeamsList());
        }
        return groupsandTeamsScrollPane;
    }

    @Override
    public String getPluginTitle() {
        return "Submit Clarifications Pane";
    }

    /**
     * This method initializes problemPane
     * 
     * @return javax.swing.JPanel
     */
    protected JPanel getProblemPane() {
        if (problemPane == null) {
            problemPane = new JPanel();
            problemPane.setLayout(new BorderLayout());
            if (isTeam) {
                problemPane.setBounds(new java.awt.Rectangle(19, 13, 336, 54));
            }
            else {
                problemPane.setBounds(new java.awt.Rectangle(19, 50, 336, 54));
            }
            problemPane.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Problem", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                    javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
            problemPane.add(getProblemComboBox(), java.awt.BorderLayout.CENTER);
        }
        return problemPane;
    }

    /**
     * This method initializes problemComboBox
     * 
     * @return javax.swing.JComboBox
     */
    private JComboBox<Problem> getProblemComboBox() {
        if (problemComboBox == null) {
            problemComboBox = new JComboBox<Problem>();
        }
        return problemComboBox;
    }
    
    /**
     * Inializes Checkbox. Checkbox can changeif this page submits an announcement or a clarification.
     * @return
     */
    protected JCheckBox getSubmitAnnouncementCheckBox() {
        if (submitAnnouncement == null) {
            submitAnnouncement = new JCheckBox();
            submitAnnouncement.setText("Generate Announcement");
            submitAnnouncement.setBounds(19, 15, 170, 20);
            ToolTipManager.sharedInstance().setDismissDelay(6000);
            submitAnnouncement.setToolTipText("Announcement clarification is a clarification that directly goes to teams with an answer but without question.");
            submitAnnouncement.addItemListener(new java.awt.event.ItemListener() {
                public void itemStateChanged(java.awt.event.ItemEvent e) {
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        getLargeTextBoxPane().setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Answer", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                                javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
                        getSubmitClarificationButton().setText("Submit Announcement");
                        getSubmitClarificationButton().setToolTipText("Click this button to submit your Announcement");
                        getAnnouncementDestinationPane().setVisible(true);
                    } else {
                        getLargeTextBoxPane().setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Question", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                                javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
                        getSubmitClarificationButton().setText("Submit Clarification");
                        getSubmitClarificationButton().setToolTipText("Click this button to submit your Clarification");
                        getAnnouncementDestinationPane().setVisible(false);
                        getAnnouncementDestinationComboBox().setSelectedItem("All Teams");

                        
                    }
                    
                }
            });
        }
        return submitAnnouncement;
    }

    /**
     * This method initializes largeTextBoxPane
     * 
     * @return javax.swing.JPanel
     */
    protected JPanel getLargeTextBoxPane() {
        if (largeTextBoxPane == null) {
            largeTextBoxPane = new JPanel();
            largeTextBoxPane.setLayout(new BorderLayout());
            if (isTeam) {
                largeTextBoxPane.setBounds(new java.awt.Rectangle(19,80,406,125));
            }
            else {
                largeTextBoxPane.setBounds(new java.awt.Rectangle(19,119,336,125));
            }
            largeTextBoxPane.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Question", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                    javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
            largeTextBoxPane.add(getLargeTextArea(), java.awt.BorderLayout.CENTER);
        }
        return largeTextBoxPane;
    }

    /**
     * This method initializes questionTextArea
     * 
     * @return javax.swing.JTextArea
     */
    private JTextArea getLargeTextArea() {
        if (largeTextArea == null) {
            largeTextArea = new JTextArea();
        }
        return largeTextArea;
    }

    /**
     * This method initializes submitClarificationButton
     * 
     * @return javax.swing.JButton
     */
    protected JButton getSubmitClarificationButton() {
        if (submitClarificationButton == null) {
            submitClarificationButton = new JButton();
            submitClarificationButton.setText("Submit Clarification");
            submitClarificationButton.setPreferredSize(new Dimension(200, 26));
            if (isTeam) {
                submitClarificationButton.setLocation(new Point(20, 219));
            }
            else {
                submitClarificationButton.setLocation(new Point(20, 259));
            }
            submitClarificationButton.setSize(new Dimension(200, 34));
            submitClarificationButton.setToolTipText("Click this button to submit your Clarification");
            submitClarificationButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    submit();
                    
                }
            });
        }
        return submitClarificationButton;
    }

    private void reloadProblems(){
        //TODO all problems shouldnt be listed! Selected Group effects which one that needs to be listed.
        getProblemComboBox().removeAllItems();
        Problem problemN = new Problem("Select Problem");
        getProblemComboBox().addItem(problemN);

        if (getContest().getCategories().length > 0) {
            for (Problem problem : getContest().getCategories()) {
                if (problem.isActive()) {
                    getProblemComboBox().addItem(problem);
                }
            }
        }
        
        for (Problem problem : getContest().getProblems()) {
            if (problem.isActive()){
                getProblemComboBox().addItem(problem);
            }
        }

    }
    
    /**
     * Loads destinations such as "All Teams", "Groups" to combobox and listens if the user changes it.
     */
    private void reloadAnnouncementDestinations(){
    
        getAnnouncementDestinationComboBox().removeAllItems();
        getAnnouncementDestinationComboBox().addItem(ALL_TEAMS);
        
        if (getContest().doGroupsExist() && getContest().getNumberofGroups() != 1){
            getAnnouncementDestinationComboBox().addItem(GROUPS);
        }
        getAnnouncementDestinationComboBox().addItem(SPECIFIC_TEAMS);
        
        getAnnouncementDestinationComboBox().addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    JComboBox<?> source = (JComboBox<?>) e.getSource();
                    String selectedValue = (String) source.getSelectedItem();
                    if (selectedValue.equals(GROUPS)){
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                reloadGroupsTeamsList();
                                getGroupsandTeamsPanel().setVisible(true);
                                getGroupsandTeamsPanel().setBorder(javax.swing.BorderFactory.createTitledBorder(null, GROUPS, javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                                        javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
                            }
                        });
                    }
                    else if (selectedValue.equals(SPECIFIC_TEAMS)) {
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                reloadGroupsTeamsList();
                                getGroupsandTeamsPanel().setVisible(true);
                                getGroupsandTeamsPanel().setBorder(javax.swing.BorderFactory.createTitledBorder(null, SPECIFIC_TEAMS, javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                                        javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
                            }
                        });
                    }
                    else {
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                reloadGroupsTeamsList();
                                getGroupsandTeamsPanel().setVisible(false);
                                getGroupsandTeamsPanel().setBorder(javax.swing.BorderFactory.createTitledBorder(null, "-", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                                        javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
                            }
                        });
                    }
                    
                }
            }
        });
    }
    
    /**
     * groupListModel gets filled with groups and teams or removed.
     */
    private void reloadGroupsTeamsList() {
        // TODO must be modified so that Groups,teams that shouldnt be displayed stop being displayed because
        // they do not have the problem selected could be implemented. 
        if (getAnnouncementDestinationComboBox().getSelectedItem().equals(GROUPS)){
            ((DefaultListModel<Object>) groupsandTeamsListModel).removeAllElements();
            Group [] allgroups = getContest().getGroups();
            Arrays.sort(allgroups, new GroupComparator());
            for (Group group : allgroups) {
                JCheckBox checkBox = new JCheckBox(group.getDisplayName());
                checkBox.putClientProperty(CHECKBOX_GROUP_TEAM_PROPERTY, group.getElementId());
                ((DefaultListModel<Object>) groupsandTeamsListModel).addElement(checkBox);
            }
        }
        else if (getAnnouncementDestinationComboBox().getSelectedItem().equals(SPECIFIC_TEAMS)) {
            ((DefaultListModel<Object>) groupsandTeamsListModel).removeAllElements();
            Vector<Account> allTeamsVector = getContest().getAccounts(ClientType.Type.TEAM);
            
            Account[] allTeams = new Account[allTeamsVector.size()];
            allTeamsVector.toArray(allTeams);
            Arrays.sort(allTeams, new AccountComparator());

            for (Account team : allTeams) {
                //TODO if teams string is really wrong it could create visual problems
                JCheckBox checkBox = new JCheckBox(
                        team.getClientId().getClientNumber()+
                        " "+
                        team.getDisplayName());
                checkBox.putClientProperty(CHECKBOX_GROUP_TEAM_PROPERTY, team.getClientId());
                ((DefaultListModel<Object>) groupsandTeamsListModel).addElement(checkBox);
            }
        }
        else {
            ((DefaultListModel<Object>) groupsandTeamsListModel).removeAllElements();
        }
    }

    private void populateGUI() {
  
        reloadProblems();
        reloadAnnouncementDestinations();
        reloadGroupsTeamsList();
        setButtonsActive(getContest().getContestTime().isContestRunning());
    }

    /**
     * Enable or disable submission buttons, Question pane and Problem drop-down list.
     * 
     * @param turnButtonsOn
     *            if true, buttons enabled.
     */
    private void setButtonsActive(final boolean turnButtonsOn) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                getProblemComboBox().setEnabled(turnButtonsOn);
                getLargeTextArea().setEnabled(turnButtonsOn);
                getSubmitClarificationButton().setEnabled(turnButtonsOn);
            }
        });
        FrameUtilities.regularCursor(this);
    }
    
    /**
     * Submits announcement or clarification for judge.
     */
    protected void submit() {

        Problem problem = ((Problem) getProblemComboBox().getSelectedItem());
        String destination = (String) getAnnouncementDestinationComboBox().getSelectedItem();
        //TODO normal questions do not need to have a destination!
        Object[] ultimateDestinationsPacked = getGroupsandTeamsList().getSelectedValues();
        
        
        if (getSubmitAnnouncementCheckBox().isSelected()) {

            if (getProblemComboBox().getSelectedIndex() < 1) {
                showMessage("Please select problem");
                return;
            }

            if (getAnnouncementDestinationComboBox().getSelectedItem().equals(GROUPS) && getGroupsandTeamsList().isSelectionEmpty()) {
                showMessage("Please select group(s)");
                return;
            }
            if (getAnnouncementDestinationComboBox().getSelectedItem().equals(SPECIFIC_TEAMS) && getGroupsandTeamsList().isSelectionEmpty()) {
                showMessage("Please select team(s)");
                return;
            }
            submitAnnouncement(problem,destination,ultimateDestinationsPacked);
        }
        else {
            if (getProblemComboBox().getSelectedIndex() < 1) {
                showMessage("Please select problem");
                return;
            }
            submitClarification(problem);
        }
        
    }
    
    /**
     * Reads the user inputs and gets it from parameter to submit a announcement clarification. Asks for a confirmation in a seperate frame.
     * @param problem
     * @param destination
     * @param ultimateDestinationsPacked
     */
    protected void submitAnnouncement(Problem problem,String destination,Object[] ultimateDestinationsPacked) {
        
        String answerAnnouncement = largeTextArea.getText().trim();

        if (answerAnnouncement.length() < 1) {
            showMessage("Please enter an answer for announcement");
            return;
        }
        String[] stringDestinations = new String[ultimateDestinationsPacked.length];
        ArrayList<ElementId> ultimateDestinationsGroup = new ArrayList<>();
        ArrayList<ClientId> ultimateDestinationsTeam = new ArrayList<>();
        
        for (int i = 0; i < ultimateDestinationsPacked.length; i++) { //Converts ultimateDestinationsPacked to html ready string
            Object associatedObject = (Object) ((JCheckBox) ultimateDestinationsPacked[i]).getClientProperty(CHECKBOX_GROUP_TEAM_PROPERTY);
            
            if (associatedObject instanceof ClientId) { //Team
                ultimateDestinationsTeam.add((ClientId)associatedObject);
                Account account = getContest().getAccount((ClientId)associatedObject);
                stringDestinations[i] = String.valueOf(account.getDisplayName());
            }
            else {//ElementId for group
                ultimateDestinationsGroup.add((ElementId)associatedObject);
                Group group = getContest().getGroup((ElementId)associatedObject);
                stringDestinations[i] = String.valueOf(group.getDisplayName());
            }

        }
                    
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(
                "<html>"
                + "    <head>"
                + "    <style>"
                + "        td {"
                + "            padding: 0 0 8px 0; /* top right bottom left */"
                + "            text-align: left;"
                + "            vertical-align: top;"
                + "        }"
                + "        .no-padding {"
                + "            padding-top: 0px;"
                + "            padding-bottom: 0px;"
                + "        }"
                + "         body{"
                + "            font-size: 1.1em;"
                + "        }"
                + "    </style>"
                + "    </head>"
                + "    <body>"
                + "    <div style = \"padding-bottom: 8px\">Do you wish to submit an announcement clarification for </div>"
                + "    <table style=\"width:100%; max-width: 700px\">"
                + "        <tr>"
                + "            <td style=\"width:20%\">Problem:</td>"
                + "            <td style = \"width:50%\"><font color=\"blue\">"+Utilities.forHTML(problem.toString())+"</font></td>"
                + "        </tr>"
                + "        <tr>"
                + "            <td>Announcement:</td>"
                + "            <td><font color=\"blue\">"+Utilities.forHTML(answerAnnouncement)+"</font></td>"
                + "        </tr>"
                + "        <tr>"
                );
        if (destination.equals(ALL_TEAMS)) {
            stringBuilder.append(
            "            <td  class=\"no-padding\">Destination:</td>"
            + "            <td class=\"no-padding\"><font color=\"blue\">"+Utilities.forHTML(ALL_TEAMS)+"</font></td>"
            + "        </tr>"
            );
        }
        else {
            stringBuilder.append(
                    "            <td  class=\"no-padding\">Destination:</td>"
                    + "            <td class=\"no-padding\"><font color=\"blue\">"+Utilities.forHTML(stringDestinations[0])+"</font></td>"
                    + "        </tr>"
                    );
        }
        for (int i = 1;i <stringDestinations.length;i++) {
            stringBuilder.append(
                            "        <tr>"
                            + "            <td class=\"no-padding\"></td>"
                            + "            <td class=\"no-padding\"><font color=\"blue\">"+Utilities.forHTML(stringDestinations[i]) +"</font></td>"
                            + "        </tr>");
        }
        stringBuilder.append(
                        "    </table>"
                        + "    </body>"
                        + "</html>"
                        );
        int result = FrameUtilities.yesNoCancelDialog(getParentFrame(), stringBuilder.toString(), "Submit Clarification Confirm");

        if (result != JOptionPane.YES_OPTION) {
            return;
        }
        
        try {
            log.info("submit announcement clarification for " + problem + " " + stringBuilder);            
            getController().submitAnnouncement(problem, answerAnnouncement,
                    ultimateDestinationsGroup.toArray(new ElementId[ultimateDestinationsGroup.size()]),
                    ultimateDestinationsTeam.toArray(new ClientId[ultimateDestinationsTeam.size()]));
            largeTextArea.setText("");

        } catch (Exception e) {
            showMessage("Error sending announcement clar, contact staff");
            log.log(Log.SEVERE, "Exception sending announcement clarification ", e);
        }
    }
    
    /**
     * Reads the user inputs to submit a clarification. Asks for a confirmation in a seperate frame.
     * @param problem
     */
    protected void submitClarification(Problem problem) {
        String question = largeTextArea.getText().trim();

        if (question.length() < 1) {
            showMessage("Please enter a question");
            return;
        }
        
        String confirmQuestion = "<HTML><FONT SIZE=+1>Do you wish to submit a clarification for<BR><BR>" + "Problem:  <FONT COLOR=BLUE>" + Utilities.forHTML(problem.toString()) + "</FONT><BR><BR>"
        + "Question: <FONT COLOR=BLUE>" + Utilities.forHTML(question)
        + "</FONT><BR><BR></FONT>";
        
        int result = FrameUtilities.yesNoCancelDialog(getParentFrame(), confirmQuestion, "Submit Clarification Confirm");

        if (result != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            log.info("submit clarification for " + problem + " " + question);
            getController().submitClarification(problem, question);
            largeTextArea.setText("");

        } catch (Exception e) {
            // TODO need to make this cleaner
            showMessage("Error sending clar, contact staff");
            log.log(Log.SEVERE, "Exception sending clarification ", e);
        }
    }
    
    /**
     * 
     * @author pc2@ecs.csus.edu
     * 
     */
    private class ContestTimeListenerImplementation implements IContestTimeListener {

        public void contestTimeAdded(ContestTimeEvent event) {
            if (isThisSite(event.getSiteNumber())) {
                setButtonsActive(event.getContestTime().isContestRunning());
            }
        }

        public void contestTimeRemoved(ContestTimeEvent event) {
        }

        public void contestTimeChanged(ContestTimeEvent event) {
        }

        public void contestStarted(ContestTimeEvent event) {
            if (isThisSite(event.getSiteNumber())) {
                setButtonsActive(event.getContestTime().isContestRunning());
            }
        }

        public void contestStopped(ContestTimeEvent event) {
            if (isThisSite(event.getSiteNumber())) {
                setButtonsActive(event.getContestTime().isContestRunning());
            }
        }

        public void refreshAll(ContestTimeEvent event) {
            if (isThisSite(event.getSiteNumber())) {
                setButtonsActive(event.getContestTime().isContestRunning());
            }
            
        }
        
        /** This method exists to support differentiation between manual and automatic starts,
         * in the event this is desired in the future.
         * Currently it just delegates the handling to the contestStarted() method.
         */
        @Override
        public void contestAutoStarted(ContestTimeEvent event) {
            contestStarted(event);
        }
    }
    
    /**
     * 
     * @author pc2@ecs.csus.edu
     * 
     */
    private class ProblemListenerImplementation implements IProblemListener {

        public void problemAdded(final ProblemEvent event) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    getProblemComboBox().addItem(event.getProblem());
                }
            });
        }

        public void problemChanged(ProblemEvent event) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    int selectedIndex = getProblemComboBox().getSelectedIndex();
                    reloadProblems();
                    if (selectedIndex > -1) {
                        getProblemComboBox().setSelectedIndex(selectedIndex);
                    }
                }
            });
        }

        public void problemRemoved(ProblemEvent event) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    reloadProblems();
                }
            });
        }

        public void problemRefreshAll(ProblemEvent event) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    reloadProblems();
                }
            });
        }
    }

    /**
     * 
     * @author pc2@ecs.csus.edu
     *
     */
    private class CategoryListenerImplementation implements ICategoryListener {

        public void categoryAdded(CategoryEvent event) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    reloadProblems();
                }
            });
        }

        public void categoryChanged(CategoryEvent event) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    reloadProblems();
                }
            });
        }

        public void categoryRemoved(CategoryEvent event) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    reloadProblems();
                }
            });
        }

        public void categoryRefreshAll(CategoryEvent event) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    reloadProblems();
                }
            });
        }
        
    }

    private boolean isThisSite(int siteNumber) {
        return siteNumber == getContest().getSiteNumber();
    }
    
    private void showMessage(String string) {
        JOptionPane.showMessageDialog(this, string);
    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        super.setContestAndController(inContest, inController);

        this.log = getController().getLog();
        
        initializePermissions();
        getContest().addAccountListener(new AccountListenerImplementation());
        getContest().addContestTimeListener(new ContestTimeListenerImplementation());
        getContest().addProblemListener(new ProblemListenerImplementation());
        getContest().addCategoryListener(new CategoryListenerImplementation());

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                populateGUI();
//                setVisible(true);
            }
        });
    }
    
    /**
     * 
     * @author pc2@ecs.csus.edu
     */
    public class AccountListenerImplementation implements IAccountListener {

        public void accountAdded(AccountEvent accountEvent) {
            // ignore, doesn't affect this pane
        }

        public void accountModified(AccountEvent event) {
            // check if is this account
            Account account = event.getAccount();
            /**
             * If this is the account then update the GUI display per the potential change in Permissions.
             */
            if (getContest().getClientId().equals(account.getClientId())) {
                // They modified us!!
                initializePermissions();
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        updateGUIperPermissions();
                    }
                });
            }
        }

        public void accountsAdded(AccountEvent accountEvent) {
            // ignore, does not affect this pane
        }

        public void accountsModified(AccountEvent accountEvent) {
            // check if it included this account
            boolean theyModifiedUs = false;
            for (Account account : accountEvent.getAccounts()) {
                /**
                 * If this is the account then update the GUI display per the potential change in Permissions.
                 */
                if (getContest().getClientId().equals(account.getClientId())) {
                    theyModifiedUs = true;
                    initializePermissions();
                }
            }
            final boolean finalTheyModifiedUs = theyModifiedUs;
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    if (finalTheyModifiedUs) {
                        updateGUIperPermissions();
                    }
                }
            });
        }

        public void accountsRefreshAll(AccountEvent accountEvent) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    updateGUIperPermissions();
                }
            });
        }
    }
    
    private void updateGUIperPermissions() {
        submitClarificationButton.setVisible(isAllowed(Permission.Type.SUBMIT_CLARIFICATION));
    }
} // @jve:decl-index=0:visual-constraint="10,10"
