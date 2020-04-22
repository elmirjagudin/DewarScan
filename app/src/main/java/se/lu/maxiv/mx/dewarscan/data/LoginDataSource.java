package se.lu.maxiv.mx.dewarscan.data;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;

import se.lu.maxiv.mx.dewarscan.ISPyB;
import se.lu.maxiv.mx.dewarscan.NetworkReqs;
import se.lu.maxiv.mx.dewarscan.data.model.LoggedInUser;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource
{
    static final String EXPECTED_ERR_PATTERN = "^JBAS011843: Failed instantiate.*ldap.*ispyb.*";

    public interface Listener
    {
        void onLoginResult(Result<LoggedInUser> result);
    }


    class AuthRequest extends Request<String>
    {
        String username;
        String password;

        Listener listener;

        AuthRequest(String username, String password, final Listener listener)
        {
            super(Method.POST, ISPyB.getAuthUrl(), new Response.ErrorListener()
            {
                @Override
                public void onErrorResponse(VolleyError error)
                {
                     listener.onLoginResult(new Result.Error(ISPyB.ErrorMsg(error)));
                }
            });

            this.username = username;
            this.password = password;
            this.listener = listener;
        }

        @Override
        protected Map<String, String> getParams()
        {
            Map<String, String> params = new HashMap<>();

            params.put("login", this.username);
            params.put("password", this.password);

            return params;
        }

        protected void deliverResponse(String token)
        {
            listener.onLoginResult(new Result.Success<>(new LoggedInUser(token, username)));
        }

        boolean expectedError(String replyBody)
        {
            Matcher matcher = Pattern.compile(EXPECTED_ERR_PATTERN).matcher(replyBody);
            return matcher.matches();
        }

        protected Response<String> parseNetworkResponse(NetworkResponse response)
        {
            String charset = HttpHeaderParser.parseCharset(response.headers, "utf-8");
            String body;

            try
            {
                body = new String(response.data, charset);
            }
            catch (UnsupportedEncodingException e)
            {
                return Response.error(new VolleyError("unsupported charset '" + charset + "' in server reply", e));
            }

            try
            {
                JSONObject obj = new JSONObject(body);
                return Response.success(obj.getString("token"), null);
            }
            catch (JSONException e)
            {
                if (expectedError(body))
                {
                    return Response.error(new VolleyError("invalid credentials"));
                }

                String msg = String.format("unparsable json from server '%s'", body);
                return Response.error(new VolleyError(msg));
            }
        }
    }

    public void login(final String username, String password, final Listener listener)
    {
        AuthRequest request = new AuthRequest(username, password, listener);
        NetworkReqs.enqueue(request);
    }
}
