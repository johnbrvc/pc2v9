package edu.csus.ecs.pc2.core.model;

import java.util.Enumeration;
import java.util.Vector;

import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.transport.ConnectionHandlerID;

/**
 * Specifies methods used to manipulate contest data.
 * 
 * @see edu.csus.ecs.pc2.Starter
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
public interface IModel {

    /**
     * Add Run into TeamModel's data or receive run from Server.
     * 
     * runFiles will not be null if this is a run submitted from
     * a team or if it is a run to be fetched for a judge to execute
     * and judge this run.
     * 
     * @param run
     * @param runFiles this may be null.
     * @return
     */
    Run addRun(Run run, RunFiles runFiles);

    void addLanguage(Language language);

    void addProblem(Problem problem);

    void addContestTime(ContestTime contestTime, int siteNumber);

    void addJudgement(Judgement judgement);

    /**
     * Start Contest Clock at site.
     * 
     * @param siteNumber
     */
    void startContest(int siteNumber);

    /**
     * Stop Contest Clock at site.
     * 
     * @param siteNumber
     */
    void stopContest(int siteNumber);

    /**
     * Add a run into the contest data, return updated Submitted Run.
     * 
     * @param submittedRun
     * @return Submitted Run with id and timestamps
     */
    Run acceptRun(Run submittedRun, RunFiles runFiles) throws Exception;

    /**
     * Add new accounts.
     * 
     * @param clientTypeName
     *            name of client type, "team", "judge", etc.
     * @param count
     *            number of accounts to add
     * @param active
     *            set to True if the accounts are active
     */
    void generateNewAccounts(String clientTypeName, int count, boolean active);

    /**
     * Add a new account listener.
     * 
     * @param accountListener
     */
    void addAccountListener(IAccountListener accountListener);

    /**
     * Remove a account listener.
     * 
     * @param accountListener
     */
    void removeAccountListener(IAccountListener accountListener);

    /**
     * Fetch all defined problems.
     * 
     * @return array of Problem
     */
    Problem[] getProblems();

    /**
     * Fetch all defined judgements.
     * 
     * @return
     */
    Judgement[] getJudgements();

    /**
     * Fetch all defined languages.
     * 
     * @return array of Language
     */
    Language[] getLanguages();

    /**
     * The name of the class to display after login.
     * 
     * @return class name
     */
    String getFrameName();

    /**
     * get title for this logged in client.
     * 
     * @return
     */
    String getTitle();

    /**
     * @return ClientId for the logged in client.
     */
    ClientId getClientId();

    void addRunListener(IRunListener runListener);

    void removeRunListener(IRunListener runListener);

    void addProblemListener(IProblemListener problemListener);

    void removeProblemListener(IProblemListener problemListener);

    void addLanguageListener(ILanguageListener languageListener);

    void removeLanguageListener(ILanguageListener languageListener);

    void addLoginListener(ILoginListener loginListener);

    void removeLoginListener(ILoginListener loginListener);

    void addContestTimeListener(IContestTimeListener contestTimeListener);

    void removeContestTimeListener(IContestTimeListener contestTimeListener);

    void addJudgementListener(IJudgementListener judgementListener);

    void removeJudgementListener(IJudgementListener judgementListener);

    Run getRun(ElementId id);

    Vector<Account> getAccounts(Type type, int siteNumber);

    Vector<Account> getAccounts(Type type);

    /**
     * return true if account exists and valid password matches.
     * 
     * @param clientId
     * @param password
     * @return true if valid password for input clientId
     */
    boolean isValidLoginAndPassword(ClientId clientId, String password);

    /**
     * add a already validated login and connection to list.
     * 
     * @param clientId
     * @param connectionHandlerID
     */
    void addLogin(ClientId clientId, ConnectionHandlerID connectionHandlerID);

    /**
     * Lookup a client id given a ConnectionHandlerID.
     * 
     * @param connectionHandlerID
     * @return ClientId or null if not found.
     */
    ClientId getLoginClientId(ConnectionHandlerID connectionHandlerID);

    /**
     * 
     * @param sourceId
     * @return true if logged in.
     */
    boolean isLoggedIn(ClientId sourceId);

    /**
     * Lookup ConnectionHandlerID for a given ClientId.
     * 
     * @param clientId
     * @return ClientId or null if not found.
     */
    ConnectionHandlerID getConnectionHandleID(ClientId clientId);

    /**
     * Logoff, remove user from login list.
     * 
     * @param clientId
     */
    void removeLogin(ClientId clientId);

    int getSiteNumber();

    ContestTime getContestTime();

    ContestTime getContestTime(int siteNumber);

    void setClientId(ClientId clientId);

    void setSiteNumber(int number);

    Enumeration<ClientId> getLoggedInClients(Type type);

}
