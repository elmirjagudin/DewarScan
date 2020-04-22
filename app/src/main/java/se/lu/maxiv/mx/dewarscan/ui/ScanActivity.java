package se.lu.maxiv.mx.dewarscan.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
import se.lu.maxiv.mx.dewarscan.Dewar;
import se.lu.maxiv.mx.dewarscan.IntentArgs;
import se.lu.maxiv.mx.dewarscan.R;

public class ScanActivity extends AppCompatActivity  implements ZXingScannerView.ResultHandler {
    ZXingScannerView mScannerView;
    boolean arrivedDewar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        arrivedDewar = IntentArgs.getArrivedDewar(this);

        ViewGroup contentFrame = findViewById(R.id.content_frame);
        mScannerView = new ZXingScannerView(this);
        contentFrame.addView(mScannerView);
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    void ShowToast(String msg)
    {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    void registrationDone(String result)
    {
        ShowToast(result);

        // Note:
        // * Wait 3 seconds to resume the preview.
        // * On older devices continuously stopping and resuming camera preview can result in freezing the app.
        // * I don't know why this is the case but I don't have the time to figure out.
        Handler handler = new Handler();
        handler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                ShowToast("ready to scan");
                mScannerView.resumeCameraPreview(ScanActivity.this);
            }
        }, 3000);

    }

    @Override
    public void handleResult(Result rawResult)
    {
        Dewar.Register(rawResult.getText(), arrivedDewar, new Dewar.Listener()
        {
            @Override
            public void onRegistrationDone(String result)
            {
                registrationDone(result);
            }
        });
    }
}
