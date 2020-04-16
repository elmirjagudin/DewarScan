package se.lu.maxiv.mx.dewarscan.data;

import se.lu.maxiv.mx.dewarscan.data.model.LoggedInUser;

public class DuoSession
{
    private static LoggedInUser user;

    public static boolean loggedIn() { return user != null; }

    /**
     * @return null if no logged in user
     */
    public static LoggedInUser getUser() { return user; }

    public static void setUser(LoggedInUser user) { DuoSession.user = user; }

    public static void logout() { DuoSession.user = null; }
}
