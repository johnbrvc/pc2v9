// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2;

import java.awt.Component;
import java.util.Arrays;

import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

import edu.csus.ecs.pc2.core.InternalController;
import edu.csus.ecs.pc2.core.model.InternalContest;
import edu.csus.ecs.pc2.core.model.IInternalContest;

/**
 * Starter class.
 * 
 * The Starter class is the main driver for all PC<sup>2</sup> modules.
 * <P>
 * This class creates a contest data {@link edu.csus.ecs.pc2.core.model.IInternalContest}, then
 * a  controller {@link edu.csus.ecs.pc2.core.IInternalController}.   Then it passes the
 * command line arguments to {@link edu.csus.ecs.pc2.core.InternalController#start(String[])} and
 * that starts a Login Frame. 
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public final class Starter  {

    private Starter(){
        // constructor per checkstyle suggestion.
    }

    /**
     * Start a contest module.
     * 
     * @param args
     */
    public static void main(String[] args) {
        
        IInternalContest model = new InternalContest();
        InternalController controller = new InternalController (model);
        
        try {
            LookAndFeel feel = UIManager.getLookAndFeel();
            System.err.println("The current look and feel is: " + feel.toString());
            UIManager.setLookAndFeel(new NimbusLookAndFeel() {
                @Override
                public void provideErrorFeedback(Component component) {
    
                    System.err.println("Got a beep from " + component.toString() + ":");
                    System.err.println(Arrays.toString(Thread.currentThread().getStackTrace()).replace( ',', '\n' ));
                    // You want error feedback 
                    super.provideErrorFeedback(component);
    
                }
            });
        } catch (Exception e) {
            System.err.println("Can't set look and feel " + e.toString());
        }
        
        if (args.length > 0 && args[0].equals(AppConstants.TEAM1_OPTION_STRING)){
            try {
                controller.setUsingMainUI(false);
                controller.start(args);
                @SuppressWarnings("unused") 
                IInternalContest contest = controller.clientLogin(model, "t1", "");
//                System.out.println("Logged in as "+contest.getClientId()+" length = "+contest.getSites().length);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            controller.start(args);
        }
    }
}
