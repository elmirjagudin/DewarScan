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

    // If user credentials will be cached in local storage, it is recommended it be encrypted
    // @see https://developer.android.com/training/articles/keystore
    private LoggedInUser user = null;

    // private constructor : singleton access
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

    public LoginCredentials getLoginCredentials()
    {
        if (loginCredentials == null)
        {
            loginCredentials = new LoginCredentials(
                    persistedState.getUsername(), persistedState.getPassword());
        }

        return loginCredentials;
    }

    public boolean isLoggedIn() {
        return user != null;
    }

    public void logout() {
        user = null;
        dataSource.logout();
    }

    private void setLoggedInUser(LoggedInUser user) {
        this.user = user;
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
    }

    public Result<LoggedInUser> login(String username, String password)
    {
        // handle login
        Result<LoggedInUser> result = dataSource.login(username, password);
        if (result instanceof Result.Success)
        {
            loginCredentials = null;
            persistedState.setUsernamePassword(username, password);

            setLoggedInUser(((Result.Success<LoggedInUser>) result).getData());
        }
        return result;
    }
}
