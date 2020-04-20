package se.lu.maxiv.mx.dewarscan.ui.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import se.lu.maxiv.mx.dewarscan.IntentArgs;
import se.lu.maxiv.mx.dewarscan.R;
import se.lu.maxiv.mx.dewarscan.ui.ScanActivity;
import se.lu.maxiv.mx.dewarscan.data.DuoSession;
import se.lu.maxiv.mx.dewarscan.ui.AskPermissions;
import se.lu.maxiv.mx.dewarscan.ui.login.LoginActivity;

public class MainActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onStart()
    {
        super.onStart();

        if (!haveCameraPermission())
        {
            startAskPermissionsActivity();
            return;
        }

        if (!DuoSession.loggedIn())
        {
            startLoginActivity(false);
            return;
        }

        final TextView username = findViewById(R.id.username);
        username.setText(DuoSession.getUser().getUsername());
    }

    boolean haveCameraPermission()
    {
        int perm = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        return perm == PackageManager.PERMISSION_GRANTED;
    }

    void startAskPermissionsActivity()
    {
        startActivity(new Intent(this, AskPermissions.class));
    }

    void startLoginActivity(boolean forgetPassword)
    {
        Intent i = new Intent(this, LoginActivity.class);

        IntentArgs.setForgetPassword(this, i, forgetPassword);

        startActivity(i);
    }

    public void logout(View v)
    {
        DuoSession.logout();
        startLoginActivity(true);
    }

    public void scanArrived(View v)
    {
        startScanActivity(true);
    }

    public void scanLeaving(View v)
    {
        startScanActivity(false);
    }

    public void startScanActivity(boolean arrived)
    {
        Intent i = new Intent(this, ScanActivity.class);

        IntentArgs.setArrivedDewar(this, i, arrived);

        startActivity(i);
    }
}
