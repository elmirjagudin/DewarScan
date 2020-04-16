package se.lu.maxiv.mx.dewarscan;

import android.content.Context;
import android.content.SharedPreferences;

public class PersistedState
{
    static final String USERNAME = "USERNAME";
    static final String PASSWORD = "PASSWORD";

    Context context;

    SharedPreferences getPrefs()
    {
        return context.getSharedPreferences(
                context.getString(R.string.preference_file_key),
                Context.MODE_PRIVATE);
    }

    SharedPreferences.Editor getEditor()
    {
        return getPrefs().edit();
    }

    public PersistedState(Context context)
    {
        this.context = context;
    }

    // If user credentials will be cached in local storage, it is recommended it be encrypted
    // @see https://developer.android.com/training/articles/keystore
    public void setUsernamePassword(String username, String password)
    {
        SharedPreferences.Editor editor = getEditor();

        editor.putString(PersistedState.USERNAME, username);
        editor.putString(PersistedState.PASSWORD, password);

        editor.commit();
    }

    public void forgetPassword()
    {
        SharedPreferences.Editor editor = getEditor();
        editor.remove(PersistedState.PASSWORD);
        editor.commit();
    }

    public String getUsername()
    {
        return getPrefs().getString(USERNAME, null);
    }
    public String getPassword()
    {
        return getPrefs().getString(PASSWORD, null);
    }
}
