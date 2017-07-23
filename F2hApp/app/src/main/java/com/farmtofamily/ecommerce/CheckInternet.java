package com.farmtofamily.ecommerce;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Datta on 8/26/2016.
 */
public class CheckInternet {
     Context context;
    CheckInternet(){

    }

    public Boolean isInternetConnected(Context context) {
        this.context = context;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null || (!networkInfo.isConnected())) {
            return false;
        } else {
            return true;

        }

    }

}
