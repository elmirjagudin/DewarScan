package se.lu.maxiv.mx.dewarscan;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.HashMap;
import java.util.Map;

import se.lu.maxiv.mx.dewarscan.data.DuoSession;
import se.lu.maxiv.mx.dewarscan.data.model.LoggedInUser;

public class Dewar
{
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
            String adj = this.arrived ? "arrived" : "sent to User";
            return Response.success(
                    String.format("Registered %s as %s", this.barCode, adj),
                    null);
        }

        @Override
        protected void deliverResponse(String response)
        {
            listener.onRegistrationDone(response);
        }
    }

    public static void Register(String barcode, boolean arrived, Listener listener)
    {
        LoggedInUser user = DuoSession.getUser();
        UpdateRequest request =
                new UpdateRequest(user.getAuthToken(), barcode, user.getUsername(), arrived, listener);
        NetworkReqs.enqueue(request);
    }
}
