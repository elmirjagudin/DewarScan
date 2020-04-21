package se.lu.maxiv.mx.dewarscan.ui.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import se.lu.maxiv.mx.dewarscan.data.LoginCredentials;
import se.lu.maxiv.mx.dewarscan.data.LoginRepository;
import se.lu.maxiv.mx.dewarscan.data.Result;
import se.lu.maxiv.mx.dewarscan.data.model.LoggedInUser;
import se.lu.maxiv.mx.dewarscan.R;

public class LoginViewModel extends ViewModel
{
    MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();
    LoginRepository loginRepository;

    LoginViewModel(LoginRepository loginRepository)
    {
        this.loginRepository = loginRepository;
    }

    LoginCredentials getLoginCredentials()
    {
        return loginRepository.getLoginCredentials();
    }

    LiveData<LoginFormState> getLoginFormState()
    {
        return loginFormState;
    }

    LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    public void forgetPassword()
    {
        loginRepository.forgetPassword();
    }

    public void login(String username, String password)
    {
        loginRepository.login(username, password,
            new LoginRepository.Listener()
            {
                @Override
                public void onLoginResult(Result<LoggedInUser> result)
                {
                    if (result instanceof Result.Error)
                    {
                        String err = ((Result.Error) result).getError();
                        loginResult.setValue(new LoginResult(err));
                        return;
                    }
                    LoggedInUser data = ((Result.Success<LoggedInUser>) result).getData();
                    loginResult.setValue(new LoginResult(new LoggedInUserView(data.getUsername())));
                }
            });
    }

    public void loginDataChanged(String username, String password) {
        if (!isUserNameValid(username)) {
            loginFormState.setValue(new LoginFormState(R.string.invalid_username, null));
        } else if (!isPasswordValid(password)) {
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_password));
        } else {
            loginFormState.setValue(new LoginFormState(true));
        }
    }

    private boolean isUserNameValid(String username)
    {
        if (username == null)
        {
            return false;
        }

        return !username.trim().isEmpty();
    }

    private boolean isPasswordValid(String password)
    {
        return password != null && !password.trim().isEmpty();
    }
}
