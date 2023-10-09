// Copyright (C) 1989-2023 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.ui;

import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.log.StaticLog;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class JBackgroundImagePanel extends JPanel {

    private static final String DEFAULT_IMAGEFILE_KEY = "default";
    
    private String profileFileName = null;

    private Properties panelProperties = null;
    
    private Image backgroundImage = null;
    
    /**
     * In case we have to serialize this object
     */
    private static final long serialVersionUID = 1L;
    
    public JBackgroundImagePanel() {
        
    }

    /**
     * Gets the loaded properties for the panel
     * @return properties object for the panel
     */
    Properties getPanelProperties() {
        return panelProperties;
    }
    
    /**
     * Accessor for profile file name
     * @return name of properties file, if any; null otherwise
     */
    public String getPropertiesFileName() {
        return profileFileName;
    }
    
    /**
     * Sets and loads property information from supplied file.
     * Specifically, loads the names of image files that can be used on a per-client name basis
     * @param fileName - properties file
     */
    public void setPropertiesFileName(String fileName) {
        try {
            if(new File(fileName).exists()) {
                panelProperties = new Properties();
                panelProperties.load(new FileInputStream(fileName));
                
                profileFileName = fileName;
                setImageFromKey(DEFAULT_IMAGEFILE_KEY);
            }                   
        } catch (Exception e) {
            // Can't load bg properties, just log a message
            StaticLog.getLog().log(Log.INFO, "Error loading BG properties from " + fileName + " in setPropertiesFileName()", e);
        }
        
    }
    
    /**
     * Looks up the key (which is typically a client name) to determine which background image to use for this JPanel
     * @param key Key in the properties file that contains the image to use for the background
     * @return true on success
     */
    public boolean setImageFromKey(String key)
    {
        boolean result = false;
        
        if(panelProperties != null) {
            String imageFileName = panelProperties.getProperty(key);
            
            if(imageFileName == null || imageFileName.isEmpty()) {
                imageFileName = panelProperties.getProperty(DEFAULT_IMAGEFILE_KEY);
            }
            if(imageFileName != null && !imageFileName.isEmpty()) {
                try {
                    Image newImage = ImageIO.read(new File(imageFileName));
                    backgroundImage = newImage;
                    result = true;
                    repaint();
                } catch(IOException e) {
                    StaticLog.getLog().log(Log.INFO, "Error loading image for key " + key + " in setImageFromKey()", e);
                }
            }
        }
        return result;
    }
    
    /**
     * Makes the parent frame a nice size to preserve aspect ratio of background image
     * @param frame
     */
    public void resizeParentToFit(JFrame frame) {
        if(backgroundImage != null) {
            double fw = frame.getWidth();
            double fh = frame.getHeight();
            double nw = backgroundImage.getWidth(null);
            double nh = backgroundImage.getHeight(null);
            Dimension screenDims = Toolkit.getDefaultToolkit().getScreenSize();
            
//            System.err.println("image wid=" + nw + " ht=" + nh + " fr wid=" + fw + " ht=" + fh);
            // get aspect ratio of the image
            double ar = nw / nh;
            // if the wid < height, it's portrait mode (cell phone picture?)
            if(ar < 1.0) {
                //Image is portrait - calculate new frame height using image ar
                fh = fw / ar;
                //if too big to fit on screen, adjust to max, then re-calc the frame width
                //based on the new height
                if(fh > screenDims.getHeight()) {
                    fh = screenDims.getHeight();
                    fw = ar * fh;
                }
            } else {
                //Image is landscape - calculate new frame width using image ar
                fw = ar * fh;
                //if too big to fit on the screen, adjust to max, then re-calc the frame height
                //basedon the new width
                if(fw > screenDims.getWidth()) {
                    fw = screenDims.getWidth();
                    fh = fw / ar;
                }
            }
            
//            System.err.println("ar=" + ar + " New fw=" + fw + " fh=" + fh);
            frame.setSize((int)fw, (int)fh);
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if(backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, this.getWidth(), this.getHeight(), null);
        }
    }
    
}
