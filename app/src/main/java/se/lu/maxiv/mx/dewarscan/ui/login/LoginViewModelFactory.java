package se.lu.maxiv.mx.dewarscan.ui.login;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.annotation.NonNull;

import se.lu.maxiv.mx.dewarscan.PersistedState;
import se.lu.maxiv.mx.dewarscan.data.LoginDataSource;
import se.lu.maxiv.mx.dewarscan.data.LoginRepository;

/**
 * ViewModel provider factory to instantiate LoginViewModel.
 * Required given LoginViewModel has a non-empty constructor
 */
public class LoginViewModelFactory implements ViewModelProvider.Factory
{
    PersistedState persistedState;

    public LoginViewModelFactory(PersistedState persistedState)
    {
        this.persistedState = persistedState;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass)
    {
        if (modelClass.isAssignableFrom(LoginViewModel.class))
        {
            LoginRepository lrepo = LoginRepository.getInstance(persistedState, new LoginDataSource());
            return (T) new LoginViewModel(lrepo);
        }

        /* how can this happpens? */
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
