package com.farmtofamily.ecommerce;

import android.content.Context;
import android.content.Intent;


public class UserAuth {

    public static boolean isUserLoggedIn(Context context, String userName, String userEmailId) {
        if (userEmailId == null || userEmailId == "" || userName == null || userName == "") {
            Intent theLoginIntent = new Intent(context, LoginActivity.class);
            //theLoginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            theLoginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            theLoginIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            context.startActivity(theLoginIntent);
            return false;
        }
        return true;
    }

    public static boolean isUserLoggedIn(Context context) {
        String theUserEmailId = SessionManager.Instance().getUserEmailId();
        String theUserName = SessionManager.Instance().getUserFName();
        return isUserLoggedIn(context, theUserName, theUserEmailId);
    }

    public static boolean isUserLoggedIn() {
        String theUserEmailId = SessionManager.Instance().getUserEmailId();
        String theUserName = SessionManager.Instance().getUserFName();
        //String theUserPhotoURL = SessionManager.Instance().getUserPhotoUrl();

        if (theUserEmailId == null || theUserEmailId == "" || theUserName == null || theUserName == "") {
            return false;
        }
        return true;
    }

    public void saveAuthenticationInfo(UserDbDTO userInfo, final Context context) {
        if (userInfo == null)
            return;

        if (userInfo.getEmail() == null || userInfo.getEmail() == "" ||
                userInfo.getFname() == null || userInfo.getFname() == "")
            return;

        SessionManager theSessionManager = SessionManager.getInstance(context);
        theSessionManager.setUserId(userInfo.getUserid());
        theSessionManager.setUserFName(userInfo.getFname());
        theSessionManager.setUserFName(userInfo.getFname());
        theSessionManager.setUserLName(userInfo.getLname());
        theSessionManager.setUserApart(userInfo.getApart());
        theSessionManager.setUserEmailId(userInfo.getEmail());
        theSessionManager.setUserAddr1(userInfo.getAddr1());
        theSessionManager.setUserAddr2(userInfo.getAddr2());
        theSessionManager.setUserCity(userInfo.getCity());
        theSessionManager.setUserCode(userInfo.getCode());
        theSessionManager.setUserPhone(userInfo.getPhone());
    }

    public static boolean CleanAuthenticationInfo() {

        SessionManager theSessionManager = SessionManager.Instance();
        theSessionManager.setUserId(null);
        theSessionManager.setUserFName(null);
        theSessionManager.setUserLName(null);
        theSessionManager.setUserEmailId(null);
        theSessionManager.setUserAddr1(null);
        theSessionManager.setUserAddr2(null);
        theSessionManager.setUserApart(null);
        theSessionManager.setUserCode(null);
        theSessionManager.setUserCity(null);
        theSessionManager.setUserPhone(null);

        return true;
    }

}
