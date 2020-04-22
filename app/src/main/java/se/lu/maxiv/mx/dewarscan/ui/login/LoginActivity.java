package se.lu.maxiv.mx.dewarscan.ui.login;

import android.app.Activity;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import se.lu.maxiv.mx.dewarscan.IntentArgs;
import se.lu.maxiv.mx.dewarscan.PersistedState;
import se.lu.maxiv.mx.dewarscan.R;
import se.lu.maxiv.mx.dewarscan.data.LoginCredentials;

public class LoginActivity extends AppCompatActivity
{
    LoginViewModel loginViewModel;

    void finish_success()
    {
        setResult(Activity.RESULT_OK);
        finish();
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        /* setup our view model */
        LoginViewModelFactory factory = new LoginViewModelFactory(new PersistedState(this));
        loginViewModel = new ViewModelProvider(this, factory).get(LoginViewModel.class);

        if (IntentArgs.getForgetPassword(this))
        {
            loginViewModel.forgetPassword();
        }

        setContentView(R.layout.activity_login);

        final EditText usernameEditText = findViewById(R.id.username);
        final EditText passwordEditText = findViewById(R.id.password);

        final Button loginButton = findViewById(R.id.login);
        final ProgressBar loadingProgressBar = findViewById(R.id.loading);

        loginViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>()
        {
            @Override
            public void onChanged(@Nullable LoginFormState loginFormState)
            {
                if (loginFormState == null)
                {
                    return;
                }
                loginButton.setEnabled(loginFormState.isDataValid());
                if (loginFormState.getUsernameError() != null) {
                    usernameEditText.setError(getString(loginFormState.getUsernameError()));
                }
                if (loginFormState.getPasswordError() != null) {
                    passwordEditText.setError(getString(loginFormState.getPasswordError()));
                }
            }
        });

        loginViewModel.getLoginResult().observe(this, new Observer<LoginResult>() {
            @Override
            public void onChanged(@Nullable LoginResult loginResult) {
                if (loginResult == null) {
                    return;
                }
                loadingProgressBar.setVisibility(View.GONE);
                if (loginResult.getError() != null)
                {
                    showLoginFailed(loginResult.getError());
                }

                if (loginResult.getSuccess() != null)
                {
                    /* complete and destroy login activity once successful */
                    finish_success();
                }
            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { /* nop */ }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { /* nop */ }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    loginViewModel.login(usernameEditText.getText().toString(),
                            passwordEditText.getText().toString());
                }
                return false;
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                login();
            }
        });

        showPersistedCredentials(usernameEditText, passwordEditText, loginViewModel.getLoginCredentials());
        loginWithPersisted();
    }

    /**
     * if we have persisted credentials, 'click' the login button
     */
    void loginWithPersisted()
    {
        LoginCredentials creds = loginViewModel.getLoginCredentials();
        if (creds == null)
        {
            /* no persisted credentials */
            return;
        }

        if (creds.getPassword() == null)
        {
            /* password not persisted */
            return;
        }

        login();
    }

    void login()
    {
        final ProgressBar loadingProgressBar = findViewById(R.id.loading);
        final EditText usernameEditText = findViewById(R.id.username);
        final EditText passwordEditText = findViewById(R.id.password);

        loadingProgressBar.setVisibility(View.VISIBLE);
        loginViewModel.login(usernameEditText.getText().toString(),
                passwordEditText.getText().toString());
    }

    /**
     * update text widget with persisted username and password, if we have persisted state
     */
    void showPersistedCredentials(EditText username, EditText password, LoginCredentials credentials)
    {
        if (credentials == null)
        {
            return;
        }

        username.setText(credentials.getUsername());
        password.setText(credentials.getPassword());
    }

    private void showLoginFailed(String errorDetails)
    {
        String text = String.format("%s\n%s", getString(R.string.login_failed), errorDetails);
        Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG);

        /* center text inside the toast widget */
        LinearLayout layout = (LinearLayout) toast.getView();
        if (layout.getChildCount() > 0)
        {
            TextView tv = (TextView) layout.getChildAt(0);
            tv.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        }

        toast.show();
    }
}
