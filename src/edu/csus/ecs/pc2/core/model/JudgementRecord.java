// Copyright (C) 1989-2025 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.model;

import java.io.Serializable;
import java.util.Date;

/**
 * A set of Judgement information for a run.
 *
 * <br>
 * This contains all the information about a single judgement, who
 * judged the run, what the judgement was, how long it took to judge,
 * and whether the judgement {@link #isActive() is Active}
 *
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class JudgementRecord implements Serializable, IGetDate {

    /**
     *
     */
    private static final long serialVersionUID = -2043715842465711645L;

    /**
     * A Unique contest-wide identifier the judgement.
     *
     * Will match Judgement.getElementId();
     */
    private ElementId judgementId = null;

    private long time = new Date().getTime();

    /**
     * A string that identifies this type.
     *
     * This string also is used in naming files on disk.
     *
     */
    public static final String JUDGEMENT_RECORD_ID = "JudgementRecord";

    /**
     * A Unique contest-wide identifier this JudgementRecord instance.
     *
     */


    private ElementId elementId = new ElementId (JUDGEMENT_RECORD_ID);

    /**
     * Who entered this judgement.
     */
    private ClientId judgerClientId;

    /**
     * Is this judgement from a validator.
     */
    private boolean usedValidator = false;

    private boolean acceptButtonHit = false;

    /**
     * Is this "the" judgement for a run.
     *
     * @see #isActive
     */
    private boolean active = true;

    /**
     * Number of second it took the judge to judge this run.
     */
    private long judgedSeconds = 0;

    /**
     * Number of seconds it took to execute the run.
     */
    private long executeMS = 0;

    /**
     * Comment for team only.
     */
    private JudgeComment commentForTeam = null;

    /**
     * Comment for judges only.
     */
    private JudgeComment commentForJudge = null;

    /**
     * The time when the judgement was received by the server.
     */
    private long whenJudgedTime;

    /**
     * The number of seconds it took to judge.
     *
     * This time is the number of seconds between the time when the Select Judgement dialog appears and when the Judge (person)
     * selects a judgement.
     */
    private long howLongToJudgeInSeconds;

    /**
     * Is this judgement a Yes ?.
     */
    private boolean solved = false;

    /**
     * Send this judgement to team ?.
     */
    private boolean sendToTeam = true;

    private String validatorResultString = null;

    private ElementId runResultsElementId = null;

    /**
     *
     */
    private boolean computerJudgement = false;

    private ElementId previousComputerJudgementId = null;

    private boolean preliminaryJudgement = false;

    // Copied from submission when judgement record is complete
    private Date judgeStartDate = null;


    /**
     * Create a Judgement Record.
     * @param judgementId elementId for the run
     * @param judgerClientId which judge judged this run
     * @param solved solved this problem
     * @param usedValidator did the judge accept the validator.
     */
    public JudgementRecord(ElementId judgementId, ClientId judgerClientId, boolean solved, boolean usedValidator) {
        this.judgementId = judgementId;
        this.judgerClientId = judgerClientId;
        this.usedValidator = usedValidator;
        this.solved = solved;
    }

    public JudgementRecord(ElementId judgementId, ClientId judgerClientId, boolean solved, boolean usedValidator, boolean computerJudgement) {
        this(judgementId, judgerClientId, solved, usedValidator);
        this.computerJudgement = computerJudgement;
    }

    /**
     * Is this "the" Judgement. ?
     *
     * There may be many judgements for a run, if this returns true then this is the judgement shown to the teams and scoreboard and
     * is the official judgement which is used to rank teams.
     *
     * @return true if this is "the" judgement.
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Set this as "the" judgement.
     *
     * @see #isActive()
     * @param active
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * @return Returns the judgedSeconds.
     */
    public long getJudgedSeconds() {
        return judgedSeconds;
    }

    /**
     * @param judgedSeconds
     *            the number of seconds to judge run.
     */
    public void setJudgedSeconds(long judgedSeconds) {
        this.judgedSeconds = judgedSeconds;
    }

    /**
     * @return number of minutes to judge run.
     */
    public long getJudgedMinutes() {
        return getJudgedSeconds() / 60;
    }

    public ElementId getJudgementId() {
        return judgementId;
    }

    public void setJudgementId(ElementId judgementId) {
        this.judgementId = judgementId;
    }

    /**
     * get who entered this judgement.
     */
    public ClientId getJudgerClientId() {
        return judgerClientId;
    }

    public JudgeComment getCommentForJudge() {
        return commentForJudge;
    }

    public void setCommentForJudge(JudgeComment commentForJudge) {
        this.commentForJudge = commentForJudge;
    }

    public JudgeComment getCommentForTeam() {
        return commentForTeam;
    }

    public void setCommentForTeam(JudgeComment commentForTeam) {
        this.commentForTeam = commentForTeam;
    }

    /**
     * Elapsed time on server when this judgement was registered.
     *
     * @return the time
     */
    public long getWhenJudgedTime() {
        return whenJudgedTime;
    }

    /**
     *
     * @param whenJudgedTime
     */
    public void setWhenJudgedTime(long whenJudgedTime) {
        this.whenJudgedTime = whenJudgedTime;
    }

    /**
     * is this problem solved, marked as Yes? .
     */
    public boolean isSolved() {
        return solved;
    }

    public void setSolved(boolean solved) {
        this.solved = solved;
    }

    public boolean isSendToTeam() {
        return sendToTeam;
    }

    public void setSendToTeam(boolean sendToTeams) {
        this.sendToTeam = sendToTeams;
    }

    public void setJudgerClientId(ClientId judgerClientId) {
        this.judgerClientId = judgerClientId;
    }

    // TODO code equals
    // public boolean equals(JudgementRecord judgementRecord) {
    //
    // try {
    // if (judgementId != judgementRecord.getJudgementId()) {
    // return false;
    // }
    // if (!judgerClientId.equals(judgementRecord.getJudgerClientId())) {
    // return false;
    // }
    // if (usedValidator != judgementRecord.usedValidator) {
    // return false;
    // }
    // if (solved != judgementRecord.isSolved()) {
    // return false;
    // }
    // if (active != judgementRecord.isActive()) {
    // return false;
    // }
    // if (judgedSeconds != judgementRecord.getJudgedSeconds()) {
    // return false;
    // }
    // if (whenJudgedTime != judgementRecord.getWhenJudgedTime()) {
    // return false;
    // }
    // if (sendToTeam != judgementRecord.isSendToTeam()) {
    // return false;
    // }
    // if (!commentForJudge.equals(judgementRecord.getCommentForJudge())) {
    // return false;
    // }
    // if (!commentForTeam.equals(judgementRecord.getCommentForTeam())) {
    // return false;
    // }
    //
    // return true;
    // } catch (Exception e) {
    // // TODO print to static Exception Log
    // return false;
    // }
    // }

    public long getHowLongToJudgeInSeconds() {
        return howLongToJudgeInSeconds;
    }

    public void setHowLongToJudgeInSeconds(long howLongToJudgeInSeconds) {
        this.howLongToJudgeInSeconds = howLongToJudgeInSeconds;
    }

    public boolean isUsedValidator() {
        return usedValidator;
    }

    public void setUsedValidator(boolean usedValidator) {
        this.usedValidator = usedValidator;
    }

    /**
     * get time in ms that it took to execute the team's solution.
     */
    public long getExecuteMS() {
        return executeMS;
    }

    /**
     * Set time in ms that it took to execute the team's solution.
     * @param executeMS
     */
    public void setExecuteMS(long executeMS) {
        this.executeMS = executeMS;
    }

    @Override
    public String toString() {
        String infoString = "No";
        if (isSolved() ) {
            infoString = "Yes";
        }
        return infoString +" by "+judgerClientId + " judgement " + judgementId + " id "+getElementId();
    }

    public ElementId getElementId() {
        return elementId;
    }

    public int versionNumber() {
        return elementId.getVersionNumber();
    }

    /**
     *
     * @return null if no results, else a string result.
     */
    public String getValidatorResultString() {
        return validatorResultString;
    }

    public void setValidatorResultString(String validatorResultString) {
        this.validatorResultString = validatorResultString;
    }

    public boolean isAcceptButtonHit() {
        return acceptButtonHit;
    }

    public void setAcceptButtonHit(boolean acceptButtonHit) {
        this.acceptButtonHit = acceptButtonHit;
    }

    public ElementId getRunResultsElementId() {
        return runResultsElementId;
    }

    public void setRunResultsElementId(ElementId runResultsElementId) {
        this.runResultsElementId = runResultsElementId;
    }

    /**
     * Was this judged by a computer (not a human) ?
     * @return true if judged by computer.
     */
    public boolean isComputerJudgement() {
        return computerJudgement;
    }

    public void setPreviousComputerJudgementId(ElementId previousComputerJudgementId) {
        this.previousComputerJudgementId = previousComputerJudgementId;
    }

    public ElementId getPreviousComputerJudgementId() {
        return previousComputerJudgementId;
    }


    /**
     * Is this judgement record a preliminary.
     *
     * @return true if preliminary judgement, false if a final judgement.
     */
    public boolean isPreliminaryJudgement() {
        return preliminaryJudgement;
    }

    public void setPreliminaryJudgement(boolean preliminaryJudgement) {
        this.preliminaryJudgement = preliminaryJudgement;
    }


    /**
     * Get wall clock time for submission.
     *
     * @return
     */
    @Override
    public  Date getDate() {
        return new Date(time);
    }

    /**
     * Set submission date.
     *
     * This field does not affect {@link #getElapsedMS()} or {@link #getElapsedMins()}.
     *
     * @param date Date, if null then sets Date long value to zero
     */
    @Override
    public void setDate (Date date){
        time = 0;
        if (date != null){
            time = date.getTime();
        }
    }

    /**
     * When a judge starts judging (set's state to being judged), this date gets set to the current time
     *
     */
    public void setJudgeStartDate(Date startDate) {
        judgeStartDate = startDate;
    }

    /**
     *
     * @return date when judge started judging
     */
    public Date getJudgeStartDate() {
        return judgeStartDate;
    }
}
