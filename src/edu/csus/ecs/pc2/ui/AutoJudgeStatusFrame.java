// Copyright (C) 1989-2023 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.execute.IExecutableNotify;
import edu.csus.ecs.pc2.core.execute.IExecutableMonitor;
import edu.csus.ecs.pc2.core.model.IInternalContest;

/**
 * Auto Judge Status Frame.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class AutoJudgeStatusFrame extends javax.swing.JFrame implements AutoJudgeNotifyMessages, IExecutableMonitor {

    /**
     * 
     */
    private static final long serialVersionUID = -4357639159960022863L;
    
    private static final String BACKGROUND_PROPERTIES_FILE = "ajbackground.properties";
    private static final String SHOWBIGLABEL_KEY = "showbiglabel";

    private String clientName = null;
    private boolean showBigLabel = true;
    
    private JPanel mainAJStatusPane = null;

    private JPanel buttonPane = null;

    private JBackgroundImagePanel centerPane = null;

    private JPanel messagePanel = null;

    private JButton stopAutoJudgingButton = null;

    private JLabel messageLabel = null;

    private JLabel bigAutoJudgeStatusLabel = null;
    
    private AutoJudgingMonitor autoJudgingMonitor = null;

    private IExecutableNotify notifyFrame = null;
    
    /**
     * This method initializes
     * 
     */
    public AutoJudgeStatusFrame() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setSize(new java.awt.Dimension(519, 251));
        this.setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
        this.setContentPane(getMainAJStausPane());
        this.setTitle("Auto Judge Status");
        getCenterPane().resizeParentToFit(this);
    }

    /**
     * This method initializes mainAJStausPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getMainAJStausPane() {
        if (mainAJStatusPane == null) {
            mainAJStatusPane = new JPanel();
            mainAJStatusPane.setLayout(new BorderLayout());
            mainAJStatusPane.add(getButtonPane(), java.awt.BorderLayout.SOUTH);
            mainAJStatusPane.add(getCenterPane(), java.awt.BorderLayout.CENTER);
            mainAJStatusPane.add(getMessagePanel(), java.awt.BorderLayout.NORTH);
        }
        return mainAJStatusPane;
    }

    /**
     * This method initializes buttonPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getButtonPane() {
        if (buttonPane == null) {
            buttonPane = new JPanel();
            buttonPane.setPreferredSize(new java.awt.Dimension(45, 45));
            buttonPane.add(getStopAutoJudgingButton(), null);
        }
        return buttonPane;
    }
        
    /**
     * This method initializes jPanel
     * 
     * @return javax.swing.JPanel
     */
    private JBackgroundImagePanel getCenterPane() {
        if (centerPane == null) {
            centerPane = new JBackgroundImagePanel();
            centerPane.setPropertiesFileName(BACKGROUND_PROPERTIES_FILE);
            showBigLabel = true;
            
            Properties props = centerPane.getPanelProperties();
            if(props != null) {
                String showLabelVal = props.getProperty(SHOWBIGLABEL_KEY);
                if(showLabelVal != null) {;
                    showBigLabel = Boolean.valueOf(showLabelVal);
                }
            }
            bigAutoJudgeStatusLabel = new JLabel();
            if(showBigLabel) {
                bigAutoJudgeStatusLabel.setText("Waiting for runs");
            }
            bigAutoJudgeStatusLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            bigAutoJudgeStatusLabel.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 36));

            centerPane.setLayout(new BorderLayout());
            centerPane.add(bigAutoJudgeStatusLabel, java.awt.BorderLayout.CENTER);
        }
        return centerPane;
    }

    /**
     * This method initializes messagePanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getMessagePanel() {
        if (messagePanel == null) {
            messageLabel = new JLabel();
            messageLabel.setText("## - Problem Title  (Run NN, Site YY)");
            messageLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            messageLabel.setFont(new java.awt.Font("monospaced", java.awt.Font.BOLD, 18));
            messagePanel = new JPanel();
            messagePanel.setLayout(new BorderLayout());
            messagePanel.setPreferredSize(new java.awt.Dimension(45, 45));
            messagePanel.add(messageLabel, java.awt.BorderLayout.CENTER);
        }
        return messagePanel;
    }

    /**
     * This method initializes stopAutoJudgingButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getStopAutoJudgingButton() {
        if (stopAutoJudgingButton == null) {
            stopAutoJudgingButton = new JButton();
            stopAutoJudgingButton.setText("Stop AutoJudging");
            stopAutoJudgingButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if(notifyFrame != null) {
                        notifyFrame.executeFrameTerminated();
                    } else {
                        startStopAutoJudging();
                    }
                }
            });
        }
        return stopAutoJudgingButton;
    }

    protected void startStopAutoJudging() {
        if (autoJudgingMonitor.isAutoJudgeDisabledLocally()) {
            // Locally turned off, turn it ON
            getStopAutoJudgingButton().setText("Stop AutoJudging");
            new Thread(new Runnable() {
                public void run() {
                    autoJudgingMonitor.setAutoJudgeDisabledLocally(false);
                    autoJudgingMonitor.startAutoJudging();
                }
            }).start();

        } else {
            // Local turned ON, turn it off
            getStopAutoJudgingButton().setText("Start Auto Judging");
            new Thread(new Runnable() {
                public void run() {
                    autoJudgingMonitor.setAutoJudgeDisabledLocally(true);
                    autoJudgingMonitor.stopAutoJudging();
                }
            }).start();

        }
    }

    /**
     * Show message at top of frame.
     * 
     * @param message
     */
    public void updateMessage(final String message) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                messageLabel.setText(message);
                messageLabel.setToolTipText(message);
            }
        });
    }

    /**
     * Update big message in center of frame.
     * 
     * @param bigMessage
     */
    public void updateStatusLabel(final String bigMessage) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if(showBigLabel) {
                    bigAutoJudgeStatusLabel.setText(bigMessage);
                }
                bigAutoJudgeStatusLabel.setToolTipText(bigMessage);
            }
        });
    }

    /**
     * Returns client name (eg. judge1) associated with this status window. Currently used to
     * select which bg image to display
     * @param name the client name to set
     */
    public void setClientName(String name) {
        clientName = name;
        getCenterPane().setImageFromKey(clientName);
        getCenterPane().resizeParentToFit(this);
    }
    
    /**
     * Gets current client name for this frame
     * @return the client name
     */
    public String getClientName() {
        return clientName;
    }
    public void setAutoJudgeMonitor(AutoJudgingMonitor monitor) {
        autoJudgingMonitor = monitor;
    }

    protected AutoJudgingMonitor getAutoJudgingMonitor() {
        return autoJudgingMonitor;
    }

    public void setAutoJudgingMonitor(AutoJudgingMonitor autoJudgingMonitor) {
        this.autoJudgingMonitor = autoJudgingMonitor;
    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
       
        // not used
        
    }

    public String getPluginTitle() {
        return "Auto Judge Status Frame";
    }

    @Override
    public void resetFrame() {
        setTimerCountLabelColor(Color.black);
    }

    @Override
    public void setTimerFrameVisible(boolean bHow)
    {
        // The AJ Status frame is not controlled by the execution timer, but by the AJ itself
        return;
    }
    
    @Override
    public void setTimerCountLabelColor(Color fg) {
        if(bigAutoJudgeStatusLabel != null) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    bigAutoJudgeStatusLabel.setForeground(fg);
                }
            });
           
        }
    }

    @Override
    public void setTimerCountLabelText(String msg) {
        updateStatusLabel(msg);
        
    }

    @Override
    public void setExecuteTimerLabel(String msg) {
        updateMessage(msg);
        
    }

    @Override
    public void setTerminateButtonNotify(IExecutableNotify ntfy) {
        notifyFrame = ntfy;
        if(ntfy != null) {
            this.getStopAutoJudgingButton().setText("Terminate");
        } else {
            // We had to be auto-judging to get here
            this.getStopAutoJudgingButton().setText("Stop AutoJudging");
        }
        
    }

} // @jve:decl-index=0:visual-constraint="10,10"
