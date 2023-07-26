package com.tournaments.grindbattles.utils;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by sanat on 4/29/2018.
 */

public class MySingleton {

    private static MySingleton mInstance;
    private RequestQueue requestQueue;
    static Context context;

    private MySingleton(Context context){
        this.context=context;
        this.requestQueue = getRequestQueue();
    }

    public RequestQueue getRequestQueue(){
        if (requestQueue==null){
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return  requestQueue;
    }

    public static synchronized MySingleton getInstance(Context context){
        if (mInstance == null){
            mInstance = new MySingleton(context);
        }
        return mInstance;
    }

    public <T>void addToRequestque(Request<T> request){
        requestQueue.add(request);
    }
}








