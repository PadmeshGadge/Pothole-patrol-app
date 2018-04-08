package com.example.cleo.camera;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by Cleo on 4/3/2018.
 */

public class MySingleton {
    private static MySingleton mInstance;
    private RequestQueue requestQueue;
    private static Context mCtx;

    private MySingleton(Context context)
    {
        mCtx = context;
        requestQueue = getRequestQueue();
    }

    public static synchronized MySingleton getInstance(Context context)
    {
        if(mInstance==null)
        {
            mInstance=new MySingleton(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue()
    {
        if(requestQueue==null)
        {
            requestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return  requestQueue;
    }

    public <T>void addTorequestque(Request<T> request)
    {
        requestQueue.add(request);
    }
}
