// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
/**
 * IExecuteTimerFrame interface
 * This interface should be implemented by classes that use the Executable class.
 * Typically, this is a Frame, such as AutoJudgeStatusFrame or ExecuteTimerFrame
 */
package edu.csus.ecs.pc2.core.execute;

import java.awt.Color;

/**
 * @author John Buck
 *
 */
public interface IExecuteTimerFrame {

    // sets the frame to it "known" state
    public void resetFrame();
    
    // controls whether the frame is visible or not.
    public void setTimerFrameVisible(boolean bVis);
    
    // change the color of the timer text (Test button if execute time gets too big
    public void setTimerCountLabelColor(Color fg);
    
    // change the timer text
    public void setTimerCountLabelText(String msg);
    
    // change the label indicating what we are doing
    public void setExecuteTimerLabel(String msg);
    
    // set who gets notified if the Terminate button is pressed (null to clear)
    public void setTerminateButtonNotify(IExecuteFrameNotify ntfy);
}
