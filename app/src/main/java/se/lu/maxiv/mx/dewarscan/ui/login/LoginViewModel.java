package se.lu.maxiv.mx.dewarscan.ui.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import android.util.Patterns;

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

    public void login(String username, String password) {
        // can be launched in a separate asynchronous job
        Result<LoggedInUser> result = loginRepository.login(username, password);

        if (result instanceof Result.Success) {
            LoggedInUser data = ((Result.Success<LoggedInUser>) result).getData();
            loginResult.setValue(new LoginResult(new LoggedInUserView(data.getUsername())));
        } else {
            loginResult.setValue(new LoginResult(R.string.login_failed));
        }
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

    // A placeholder username validation check
    private boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }
        if (username.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(username).matches();
        } else {
            return !username.trim().isEmpty();
        }
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }
}
