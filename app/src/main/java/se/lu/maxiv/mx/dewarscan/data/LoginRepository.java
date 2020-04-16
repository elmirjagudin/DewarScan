package se.lu.maxiv.mx.dewarscan.data;

import se.lu.maxiv.mx.dewarscan.PersistedState;
import se.lu.maxiv.mx.dewarscan.data.model.LoggedInUser;

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */
public class LoginRepository
{
    private static volatile LoginRepository instance;

    PersistedState persistedState;
    LoginDataSource dataSource;
    LoginCredentials loginCredentials = null;

    /* private constructor : singleton access */
    private LoginRepository(PersistedState persistedState, LoginDataSource dataSource)
    {
        this.persistedState = persistedState;
        this.dataSource = dataSource;
    }

    public static LoginRepository getInstance(PersistedState persistedState, LoginDataSource dataSource)
    {
        if (instance == null)
        {
            instance = new LoginRepository(persistedState, dataSource);
        }
        return instance;
    }

    public void forgetPassword()
    {
        persistedState.forgetPassword();
        loginCredentials = null;
    }

    LoginCredentials loadPersistedCredentials()
    {
        String uname = persistedState.getUsername();
        String passwd = persistedState.getPassword();

        if (uname == null && passwd == null)
        {
            /* no persisted credentials */
            return null;
        }

        return new LoginCredentials(uname, passwd);
    }

    public LoginCredentials getLoginCredentials()
    {
        if (loginCredentials == null)
        {
            loginCredentials = loadPersistedCredentials();
        }

        return loginCredentials;
    }

    public Result<LoggedInUser> login(String username, String password)
    {
        Result<LoggedInUser> result = dataSource.login(username, password);
        if (result instanceof Result.Success)
        {
            loginCredentials = null;
            persistedState.setUsernamePassword(username, password);
            DuoSession.setUser(((Result.Success<LoggedInUser>) result).getData());
        }
        return result;
    }
}
