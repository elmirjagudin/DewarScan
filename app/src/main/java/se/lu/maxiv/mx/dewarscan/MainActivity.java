package se.lu.maxiv.mx.dewarscan;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import se.lu.maxiv.mx.dewarscan.ui.login.LoginActivity;
import static se.lu.maxiv.mx.dewarscan.LogTag.TAG;

public class MainActivity extends AppCompatActivity {
    static final int PERM_REQ = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
    }

    public void scan(View v) {
        /*
         * request camera permission,
         * if the app already have camera permission,
         * the onRequestPermissionsResult() will be called
         * without showing the 'grant permissions' dialog
         */
        ActivityCompat.requestPermissions(
                this,
                new String[]{ Manifest.permission.CAMERA },
                PERM_REQ);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions,
                                           int[] grantResults) {
        if (requestCode != PERM_REQ)
        {
            Log.w(TAG, "got unexpected permission result code " + requestCode + ", ignoring");
            return;
        }

        /* we only expect one permission grant result for CAMERA */
        int grantRes = grantResults[0];

        if (grantRes == PackageManager.PERMISSION_DENIED)
        {
            Toast.makeText(this, "camera permissions required\nto scan codes",
                    Toast.LENGTH_LONG).show();
            return;
        }

        /* we know that we have CAMERA permission now, goto scan activity */
        startActivity(new Intent(this, ScanActivity.class));
    }

    public void login(View v) {
        startActivity(new Intent(this, LoginActivity.class));
    }
}
