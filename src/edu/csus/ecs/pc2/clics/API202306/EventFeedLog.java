// Copyright (C) 1989-2025 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.clics.API202306;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.IInternalContest;

/**
 * Event fee log.
 *
 * Contains all event feed log entries for current contest.
 *
 * @author Douglas A. Lane, PC^2 Team, pc2@ecs.csus.edu
 */
public class EventFeedLog {

    private String[] fileLines = new String[0];

    private OutputStreamWriter outStream;

    private static String logsDirectory = Log.LOG_DIRECTORY_NAME;

    private String logFileName = null;

    private String filename;

    private long oldFileSize;

    /**
     * Load all events from events log
     *
     * @param contest
     * @throws FileNotFoundException
     * @throws UnsupportedEncodingException
     */
    public EventFeedLog(IInternalContest contest) throws FileNotFoundException, UnsupportedEncodingException {

        filename = getEventFeedLogName(contest.getContestIdentifier());
        setLogFileName(filename);

        // First read log

        readLog();

        // open log file for write/append
        outStream = new OutputStreamWriter(new FileOutputStream(filename, true), "UTF8");
    }

    private void readLog() {
        try {
            fileLines = Utilities.loadFile(filename);
            oldFileSize = new File(filename).length();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getEventFeedLogName(String id) {
        return logsDirectory + File.separator + "Eventfeed_2023_06_" + id + ".log";
    }

    public String[] getLogLines() {
        synchronized (filename) {
            long newFileSize = new File(filename).length();
            // only need to re-read log if the file size changed
            if (newFileSize != oldFileSize) {
                // this will also update oldFileSize
                readLog();
            }
        }
        return fileLines;
    }

    /**
     * Append event to event log.
     *
     * @param eventString
     * @throws IOException
     */
    public void writeEvent(String eventString) throws IOException {
        outStream.write(eventString);
        outStream.flush();
    }

    public static void setLogsDirectory(String logsDirectory) {
        EventFeedLog.logsDirectory = logsDirectory;
    }

    void setLogFileName(String logFileName) {
        this.logFileName = logFileName;
    }

    public String getLogFileName() {
        return logFileName;
    }

    public void close() throws IOException {
        outStream.flush();
        outStream.close();
    }

}
