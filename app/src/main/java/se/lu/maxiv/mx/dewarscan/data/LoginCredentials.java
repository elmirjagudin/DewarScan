package se.lu.maxiv.mx.dewarscan.data;

import androidx.annotation.Nullable;

public class LoginCredentials
{
    @Nullable
    String username;

    @Nullable
    String password;

    LoginCredentials(@Nullable String username, @Nullable String password)
    {
        this.username = username;
        this.password = password;
    }

    @Nullable
    public String getUsername() {
        return username;
    }

    @Nullable
    public String getPassword() {
        return password;
    }
}
