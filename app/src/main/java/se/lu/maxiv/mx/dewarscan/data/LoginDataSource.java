package se.lu.maxiv.mx.dewarscan.data;

import se.lu.maxiv.mx.dewarscan.data.model.LoggedInUser;

import java.io.IOException;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
// TODO: rename Authenticator or some such
public class LoginDataSource
{
    boolean isValidCredentials(String username, String password)
    {
        return password.equals("passwd");
    }

    public Result<LoggedInUser> login(String username, String password)
    {
        try
        {
            if (isValidCredentials(username, password))
            {
                // TODO: handle loggedInUser authentication
                LoggedInUser fakeUser =
                        new LoggedInUser(
                                java.util.UUID.randomUUID().toString(),
                                username);
                return new Result.Success<>(fakeUser);
            }
            return new Result.Error(new Exception("invalid credentials"));
        }
        catch (Exception e)
        {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    public void logout()
    {
        // TODO: revoke authentication
    }
}
