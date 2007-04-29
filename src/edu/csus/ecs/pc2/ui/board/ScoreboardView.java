package edu.csus.ecs.pc2.ui.board;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.IController;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.IModel;
import edu.csus.ecs.pc2.core.util.XSLTransformer;
import edu.csus.ecs.pc2.ui.FrameUtilities;
import edu.csus.ecs.pc2.ui.JPanePlugin;
import edu.csus.ecs.pc2.ui.LogWindow;
import edu.csus.ecs.pc2.ui.OptionsPanel;
import edu.csus.ecs.pc2.ui.StandingsPane;
import edu.csus.ecs.pc2.ui.UIPlugin;
import javax.swing.JTabbedPane;

/**
 * This class is the default scoreboard view (frame).
 * 
 * @author pc2@ecs.csus.edu
 * 
 */
// $HeadURL$
public class ScoreboardView extends JFrame implements UIPlugin {

    /**
     * 
     */
    private static final long serialVersionUID = -8071477348056424178L;

    private IModel model;

    private IController controller;

    private LogWindow logWindow = null;

    private JTabbedPane mainTabbedPane = null;

    private String xslDir;
    
    private String outputDir = "html";

    private Log log;

    /**
     * This method initializes
     * 
     */
    public ScoreboardView() {
        super();
        initialize();
    }

    /**
     * 
     * @author pc2@ecs.csus.edu
     *
     */
    public class PropertyChangeListenerImplementation implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent evt) {
            if(evt.getPropertyName().equalsIgnoreCase("standings")) {
                if (evt.getNewValue() != null && !evt.getNewValue().equals(evt.getOldValue())) {
                    // standings have changed
                    // TODO take this off the awt thread
                    generateOutput((String)evt.getNewValue());
                }
            }
        }
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setSize(new java.awt.Dimension(405, 296));
        this.setContentPane(getMainTabbedPane());
        this.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        this.setTitle("Scoreboard");

        this.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                promptAndExit();
            }
        });

        FrameUtilities.centerFrame(this);
    }

    protected void promptAndExit() {
        int result = FrameUtilities.yesNoCancelDialog("Are you sure you want to exit PC^2?", "Exit PC^2");

        if (result == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    public void setModelAndController(IModel inModel, IController inController) {
        this.model = inModel;
        this.controller = inController;

        VersionInfo versionInfo = new VersionInfo();
        // TODO xslDir should be configurable this is just one possible default
        xslDir = versionInfo.locateHome() + File.separator + "data" + File.separator + "xsl";

        log = controller.getLog();
        
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                setTitle("PC^2 " + model.getTitle() + " Build " + new VersionInfo().getBuildNumber());
        
                if (logWindow == null) {
                    logWindow = new LogWindow();
                }
                logWindow.setModelAndController(model, controller);
                logWindow.setTitle("Log " + model.getClientId().toString());
        
                StandingsPane standingsPane = new StandingsPane();
                addUIPlugin(getMainTabbedPane(), "Standings", standingsPane);
                standingsPane.addPropertyChangeListener("standings", new PropertyChangeListenerImplementation());
                OptionsPanel optionsPanel = new OptionsPanel();
                addUIPlugin(getMainTabbedPane(), "Options", optionsPanel);
                optionsPanel.setLogWindow(logWindow);
        
                setVisible(true);
            }
        });
    }

    public String getPluginTitle() {
        return "Scoreboard View";
    }

    protected void addUIPlugin(JTabbedPane tabbedPane, String tabTitle, JPanePlugin plugin) {
        plugin.setModelAndController(model, controller);
        tabbedPane.add(plugin, tabTitle);
    }

    private void generateOutput(String xmlString) {
        File inputDir = new File(xslDir);
        if (!inputDir.isDirectory()) {
            log.warning("xslDir is not a directory");
            return;
        }
        File outputDirFile = new File(outputDir);
        if (!outputDirFile.exists() && !outputDirFile.mkdirs()) {
            log.warning("Could not create " + outputDirFile.getAbsolutePath() + ", defaulting to current directory");
            outputDir = ".";
            outputDirFile = new File(outputDir);
        }
        if (!outputDirFile.isDirectory()) {
            log.warning(outputDir + " is not a directory.");
            return;
        } else {
            log.fine("Sending output to " + outputDirFile.getAbsolutePath());
        }
        // TODO consider changing this to use a filenameFilter
        String[] inputFiles = inputDir.list();
        XSLTransformer transformer = new XSLTransformer();
        for (int i = 0; i < inputFiles.length; i++) {
            String xslFilename = inputFiles[i];
            if (xslFilename.endsWith(".xsl")) {
                String outputFilename = xslFilename.substring(0, xslFilename.length() - 4) + ".html";
                try {
                    File output = File.createTempFile("__t", ".htm", outputDirFile);
                    FileOutputStream outputStream = new FileOutputStream(output);
                    transformer.transform(xslDir + File.separator + xslFilename, new ByteArrayInputStream(xmlString.getBytes()), outputStream);
                    outputStream.close();
                    if (output.length() > 0) {
                        File outputFile = new File(outputDir + File.separator + outputFilename);
                        // behaviour of renameTo is platform specific, try the possibly atomic 1st
                        if (!output.renameTo(outputFile)) {
                            // otherwise fallback to the delete then rename
                            outputFile.delete();
                            if (!output.renameTo(outputFile)) {
                                log.warning("Could not create " + outputFile.getCanonicalPath());
                            } else {
                                log.finest("rename2 to " + outputFile.getCanonicalPath() + " succeeded.");
                            }
                        } else {
                            log.finest("rename to " + outputFile.getCanonicalPath() + " succeeded.");
                        }
                    } else {
                        // 0 length file
                        log.warning("output from tranformation " + xslFilename + " was empty");
                        output.delete();
                    }
                    output = null;
                } catch (IOException e) {
                    // TODO re-visit this log message
                    log.log(Log.WARNING, "Trouble generating output", e);
                    e.printStackTrace();
                } catch (Exception e) {
                    // TODO re-visit this log message
                    log.log(Log.WARNING, "Trouble generating output", e);
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * This method initializes mainTabbedPane
     * 
     * @return javax.swing.JTabbedPane
     */
    private JTabbedPane getMainTabbedPane() {
        if (mainTabbedPane == null) {
            mainTabbedPane = new JTabbedPane();
        }
        return mainTabbedPane;
    }

} // @jve:decl-index=0:visual-constraint="10,10"
