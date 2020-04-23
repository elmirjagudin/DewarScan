package se.lu.maxiv.mx.dewarscan;

import android.content.Context;
import android.content.SharedPreferences;

public class PersistedState
{
    static final String USERNAME = "USERNAME";
    static final String PASSWORD = "PASSWORD";

    Context context;
    Encryptor encryptor;

    public PersistedState(Context context)
    {
        this.context = context;
    }

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

    Encryptor getEncryptor()
    {
        if (encryptor == null)
        {
            encryptor = new Encryptor(context);
        }

        return encryptor;
    }

    public void setUsernamePassword(String username, String password)
    {
        SharedPreferences.Editor editor = getEditor();


        editor.putString(PersistedState.USERNAME, username);
        editor.putString(PersistedState.PASSWORD, getEncryptor().encrypt(password));

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
        String encrypted_password = getPrefs().getString(PASSWORD, null);
        if (encrypted_password == null)
        {
            return null;
        }

        return getEncryptor().decrypt(encrypted_password);
    }
}
