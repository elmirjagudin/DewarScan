package se.lu.maxiv.mx.dewarscan;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import se.lu.maxiv.mx.dewarscan.ui.login.LoginActivity;

public class MainActivity extends AppCompatActivity {

    static boolean loggedIn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i("FOO", "loggedIn " + loggedIn + " this " + this);

        setContentView(R.layout.activity_main);

        if (!loggedIn)
        {
            loggedIn = true;
            startActivity(new Intent(this, LoginActivity.class));
        }
    }

    public void scan(View v) {
        Log.i("FOO", "SCAN");
    }

    public void login(View v) {
        Log.i("FOO", "LOGIN");
        startActivity(new Intent(this, LoginActivity.class));
    }
}
