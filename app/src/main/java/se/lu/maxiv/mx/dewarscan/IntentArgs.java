package se.lu.maxiv.mx.dewarscan;

import android.app.Activity;
import android.content.Intent;

public class IntentArgs
{
    static String forgetPasswordName(Activity activity)
    {
        return activity.getPackageName() + ".ForgetPassword";
    }

    static String arrivedDewarName(Activity activity)
    {
        return activity.getPackageName() + ".ArrivedDewar";
    }

    public static void setForgetPassword(Activity activity, Intent intent, boolean argValue)
    {
        intent.putExtra(forgetPasswordName(activity), argValue);
    }

    public static boolean getForgetPassword(Activity activity)
    {
        return activity.getIntent().getBooleanExtra(forgetPasswordName(activity), false);
    }

    public static void setArrivedDewar(Activity activity, Intent intent, boolean argValue)
    {
        intent.putExtra(arrivedDewarName(activity), argValue);
    }

    public static boolean getArrivedDewar(Activity activity)
    {
        return activity.getIntent().getBooleanExtra(arrivedDewarName(activity), true);
    }
}
