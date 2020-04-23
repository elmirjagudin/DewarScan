package se.lu.maxiv.mx.dewarscan;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import se.lu.maxiv.mx.dewarscan.data.DuoSession;
import se.lu.maxiv.mx.dewarscan.data.model.LoggedInUser;

/**
 * keeps track of barcodes that have been registered in this
 * session
 */
class Barcodes
{
    Set<String> registeredBarcodes = new HashSet<>();

    /**
     * check if the specified barcode have been previously registered,
     *
     * if we have seen the barcode perviusly returns true,
     * if it's first time, remember the barcode and return false
     */
    boolean isRegistered(String barcode)
    {
        if (registeredBarcodes.contains(barcode))
        {
            return true;
        }

        /* new code, remember for the future */
        registeredBarcodes.add(barcode);
        return false;
    }
}

public class Dewar
{
    static final String MAXIV_BARCODE_REGEX = "^MAXIV\\d\\d\\d\\d\\d$";

    static Barcodes registeredArrived = new Barcodes();
    static Barcodes registeredLeaving = new Barcodes();

    public interface Listener
    {
        void onRegistrationDone(String result);
    }

    static class UpdateRequest extends Request<String>
    {
        String barCode;
        String username;
        boolean arrived;
        Listener listener;

        UpdateRequest(String token, String barCode, String username, boolean arrived, final Listener listener)
        {
            super(Method.POST, ISPyB.getDewarUpdateUrl(token), new Response.ErrorListener()
            {
                @Override
                public void onErrorResponse(VolleyError error)
                {
                    listener.onRegistrationDone("ERROR: " + ISPyB.ErrorMsg(error));
                }
            });

            this.barCode = barCode;
            this.username = username;
            this.arrived = arrived;
            this.listener = listener;
        }

        @Override
        protected Map<String, String> getParams()
        {
            Map<String, String> params = new HashMap<>();

            params.put("username", this.username);
            params.put("barCode", this.barCode);
            params.put("location", this.arrived ? "BioMAX" : "");

            return params;
        }

        @Override
        protected Response<String> parseNetworkResponse(NetworkResponse response)
        {
            return Response.success(
                    String.format("Registered %s as %s", this.barCode, DewarStatus(this.arrived)),
                    null);
        }

        @Override
        protected void deliverResponse(String response)
        {
            listener.onRegistrationDone(response);
        }
    }

    static Barcodes getRegisteredBarcodes(boolean arrived)
    {
        if (arrived)
        {
            return registeredArrived;
        }

        return registeredLeaving;
    }

    static String DewarStatus(boolean arrived)
    {
        return arrived ? "arrived" : "sent to User";
    }

    static boolean validMAXIVBarcode(String barcode)
    {
        Matcher matcher = Pattern.compile(MAXIV_BARCODE_REGEX).matcher(barcode);
        return matcher.matches();
    }

    public static void Register(String barcode, boolean arrived, Listener listener)
    {
        Barcodes registered = getRegisteredBarcodes(arrived);

        /* filter out unrecognized/partly scanned barcodes */
        if (!validMAXIVBarcode(barcode))
        {
            String msg = String.format("'%s' is not a valid MAXIV barcode", barcode);
            listener.onRegistrationDone(msg);
            return;
        }

        /* avoid registering same Deware multiple times */
        if (registered.isRegistered(barcode))
        {
            String msg = String.format("%s already registered as '%s'", barcode, DewarStatus(arrived));
            listener.onRegistrationDone(msg);
            return;
        }

        LoggedInUser user = DuoSession.getUser();
        UpdateRequest request =
                new UpdateRequest(user.getAuthToken(), barcode, user.getUsername(), arrived, listener);
        NetworkReqs.enqueue(request);
    }
}
