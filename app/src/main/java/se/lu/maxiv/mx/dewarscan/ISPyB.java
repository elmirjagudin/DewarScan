package se.lu.maxiv.mx.dewarscan;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;

public class ISPyB
{
    static final String ISPYB_AUTH_HOST = "ispyb.maxiv.lu.se";
    static final String ISPYB_AUTH_SITE = "MAXIV";

    /**
     * Convert ValleyError exception to an error message for the UI
     */
    public static String ErrorMsg(VolleyError error)
    {
        String msg = error.getMessage();
        if (msg != null)
        {
            /* we have hopefully a useful error message for the user */
            return msg;
        }

        NetworkResponse resp = error.networkResponse;
        if (resp != null)
        {
            /*
             * unexpected reply from the server,
             * show reply to the user, to help with troubleshooting
             */
            return String.format("status code: %d\nbody %s", resp.statusCode, new String(resp.data));
        }

        /* last ditch effort, dump the exception to the user */
        return error.toString();
    }


    public static String getAuthUrl()
    {
        return String.format(
                "https://%s/ispyb/ispyb-ws/rest/authenticate?site=%s",
                ISPYB_AUTH_HOST, ISPYB_AUTH_SITE);
    }

    public static String getDewarUpdateUrl(String token)
    {
        return String.format(
                "https://%s/ispyb/ispyb-ws/rest/%s/dewar/updateStatus",
                ISPYB_AUTH_HOST, token);
    }
}
