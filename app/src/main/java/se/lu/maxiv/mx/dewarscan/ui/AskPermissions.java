package se.lu.maxiv.mx.dewarscan.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import se.lu.maxiv.mx.dewarscan.R;

public class AskPermissions extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_permissions);
        requestPermissions();
    }

    public void requestPermissions()
    {
        /*
         * request camera permission
         */
        ActivityCompat.requestPermissions(
                this,
                new String[]{ Manifest.permission.CAMERA },
                0);
    }

    void handleDenied()
    {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA))
        {
            /*
             * user has denied permission (but not permanently!)
             * ask again!
             */
            requestPermissions();
            return;
        }

        /*
         * user have permanently denied camera permission,
         * this application is now useless.
         *
         * the only thing to do is to plead with user to grant
         * camera permission from system wide config page
         */
        final TextView message = findViewById(R.id.message);
        message.setVisibility(View.VISIBLE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        /* we only expect one permission grant result for CAMERA */
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED)
        {
            handleDenied();
            return;
        }

        /* permission granted, we are done with this ehh.. 'activity' */
        finish();
    }
}
