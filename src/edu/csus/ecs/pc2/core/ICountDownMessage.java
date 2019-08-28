// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core;

import edu.csus.ecs.pc2.ui.UIPlugin;

/**
 * Countdown timer.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public interface ICountDownMessage extends UIPlugin {

    void setTitle(String string);

    void setExitOnClose(boolean b);

    /**
     * Start countdown timer.
     * 
     * @param seconds 
     * @param prefixForCount prefix to countdown count.
     */
    void start(String prefixForCount, int seconds);

}
