package se.lu.maxiv.mx.dewarscan.data.model;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
public class LoggedInUser
{
    private String authToken;
    private String username;

    public LoggedInUser(String authToken, String username)
    {
        this.authToken = authToken;
        this.username = username;
    }

    public String getAuthToken() { return authToken; }
    public String getUsername() { return username; }
}
