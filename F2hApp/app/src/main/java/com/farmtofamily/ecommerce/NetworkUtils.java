package com.farmtofamily.ecommerce;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


public class NetworkUtils {


    /* Method to check network availability
    * */
    public static boolean isActiveNetworkAvailable(Context aContext){

        boolean theStatus = false;
        ConnectivityManager theConManager = (ConnectivityManager)aContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo theNetInfo = theConManager.getActiveNetworkInfo();
        if(theNetInfo != null) {
            theStatus = theNetInfo.isConnected();
        }
        return theStatus;

    }

}
