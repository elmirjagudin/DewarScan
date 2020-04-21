package se.lu.maxiv.mx.dewarscan;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.NoCache;

public class NetworkReqs
{
    static RequestQueue reqQueueInstance;

    static RequestQueue setupReqQueue()
    {
        RequestQueue requestQueue;

        requestQueue = new RequestQueue(
                /* 'disable' cache, as we not going to do any cache-able requests */
                new NoCache(),
                /* use the 'standard' network implementation, copy-pasted from docs example */
                new BasicNetwork(new HurlStack()));
        requestQueue.start();

        return requestQueue;
    }

    synchronized static RequestQueue getRequestQueue()
    {
        if (reqQueueInstance == null)
        {
            reqQueueInstance = setupReqQueue();
        }

        return reqQueueInstance;
    }

    public static <T> void enqueue(Request<T> req)
    {
        getRequestQueue().add(req);
    }
}
