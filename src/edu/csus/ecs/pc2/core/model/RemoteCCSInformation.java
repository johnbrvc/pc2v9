// Copyright (C) 1989-2024 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.

package edu.csus.ecs.pc2.core.model;

/**
 * @author John Buck
 *
 * Contains per-remote CCS connection information
 */
public class RemoteCCSInformation {

    public enum RemoteCCSType {
        /**
         * Unset or Unknown.
         */
        SHADOW,
        /**
         * All accounts.
         */
        COMBINESCOREBOARD
    };

    //Shadow Mode settings
    private boolean enabled = false;
    private RemoteCCSType type = RemoteCCSType.SHADOW;
    private String CCS_URL = null;
    private String CCS_user_login = "";
    private String CCS_user_pw = "";
    private String lastShadowEventID = "";
    private String accountName = "";

    public RemoteCCSInformation(String accountName, RemoteCCSType type, boolean enabled, String url, String login, String password) {
        this.accountName = accountName;
        this.type = type;
        this.enabled = enabled;
        this.CCS_URL = url;
        this.CCS_user_login = login;
        this.CCS_user_pw = password;
    }

    /**
     * Returns the String representation for the account to which this entry applies.
     * @return a String containing the account name
     */
    public String getAccountName() {
        return accountName;
    }

    /**
     * Sets the String representation for the account for which the entry applies
     * @param acctName a String giving the account name
     */
    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public RemoteCCSType getType() {
        return type;
    }

    public void SetType(RemoteCCSType type) {
        this.type = type;
    }

    /**
     * Returns the String representation for a "CCS" URL (that is, the URL of a Remote CCS being shadowed);
     * only relevant when operating this instance of the PC2 CCS as a "Shadow CCS".
     * @return a String containing the URL of the CCS which we're shadowing
     */
    public String getCCS_URL() {
        return CCS_URL;
    }

    /**
     * Sets the String representation for a "CCS" URL (that is, the URL of a Remote CCS being shadowed);
     * only relevant when operating this instance of the PC2 CCS as a "Shadow CCS".
     * @param CCS_URL a String giving the URL of the (remote) CCS (the CCS being shadowed)
     */
    public void setCCS_URL(String CCS_URL) {
        this.CCS_URL = CCS_URL;
    }

    /**
     * Returns a String containing the user login account to be used when connecting
     * to a CCS (only useful when operating this instance of PC2 as a "Shadow CCS" or "Combined scoreboard").
     * @return a String containing the CCS user account name
     */
    public String getCCS_user_login() {
        return CCS_user_login;
    }

    /**
     * Sets the value of the user login (account) for the CCS (only useful
     * when operating this instance of PC2 as a "Shadow CCS" or "Combined scoreboard").
     * @param CCS_user_login the CCS login account name
     */
    public void setCCS_user_login(String CCS_user_login) {
        this.CCS_user_login = CCS_user_login;
    }

    /**
     * Returns a String containing the password used for logging in to the
     * CCS (only useful when operating this instance of PC2 as a
     * "Shadow CCS" or "Combined scoreboard").
     * @return a String containing a password
     */
    public String getCCS_user_pw() {
        //TODO: consider some method of encrypting the password
        return CCS_user_pw;
    }

    /**
     * Sets the value of the password to be used when connecting to a CCS
     * (only useful when operating this instance of PC2 as a "Shadow CCS" or "Combined scoreboard").
     * @param CCS_user_pw a String containing a password
     */
    public void setCCS_user_pw(String CCS_user_pw) {
        this.CCS_user_pw = CCS_user_pw;
    }

    /**
     * Returns a String containing the "CLICS ID" for the last event retrieved from
     * a remote CCS being shadowed (only useful when operating this instance of PC2 as a
     * "Shadow CCS" or "Combined scoreboard").
     * @return a String containing a remote event id
     */
    public String getLastShadowEventID() {
        return lastShadowEventID;
    }

    /**
     * Sets the value of the String containing the "CLICS since_id" for the last event retrieved from
     * a remote CCS being shadowed; that is, the id from which reconnections to the remote CCS event
     * feed should proceed (only useful when operating this instance of PC2 as a
     * "Shadow CCS" or "Combined scoreboard").
     * @param lastShadowEventID a String identifying the last event from the remote CCS
     */
     public void setLastShadowEventID(String lastShadowEventID) {
        this.lastShadowEventID = lastShadowEventID;
    }
}
