package com.farmtofamily.ecommerce;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Class helps to retrive or set values from shared preferences.
 * In case there are no values, it feeds in values from app.properties from assets directory
 * Created by anand on 29-10-2015.
 */
public class SessionManager {
    private static final String PROJECT_PREFERENCES = "com.nearbypets";

    private static SessionManager mSessionManager;
    private static SharedPreferences mProjectSharedPref = null;
    private static Context mContext = null;
    private static PropertyFileReader mPropertyFileReader = null;

    /**
     * Method is supposed to called once at beginning of the APP.
     *
     * @param context Context of any base or current activity, Needs to be called at only once.
     * @return SessionManager session manager instance
     */

    public static SessionManager getInstance(Context context) {
        if (mSessionManager != null)
            return mSessionManager;

        mContext = context;
        mPropertyFileReader = PropertyFileReader.getInstance(context);
        loadProjectSharedPreferences();
        mSessionManager = new SessionManager();

        return mSessionManager;
    }

    /**
     * Gets singleton instance of session manager class
     *
     * @return Singleton Instance of Session Manager
     */

    public static SessionManager Instance() {
        if (mSessionManager != null)
            return mSessionManager;
        else
            throw new IllegalArgumentException("No instance is yet created");
    }

    /**
     * Loads all the App.Properties file values into shared preferences.
     * In case of version upgrade, replaces all the values in the shared preferences.
     */

    private static void loadProjectSharedPreferences() {
        if (mProjectSharedPref == null) {
            mProjectSharedPref = mContext.getSharedPreferences(PROJECT_PREFERENCES, Context.MODE_PRIVATE);
        }

        setValuesInSharedPrefs(PropertyTypeConstants.API_ENDPOINT_URI, mPropertyFileReader.getEndPointUri());
    }

    private SessionManager() {
    }

    public String getEndpointUrl() {
        return mProjectSharedPref.getString(PropertyTypeConstants.API_ENDPOINT_URI, null);
    }

    public void setEndpointUrl(String endpointUrl) {
        setValuesInSharedPrefs(PropertyTypeConstants.API_ENDPOINT_URI, endpointUrl);
    }


    public String getUserId() {
        return mProjectSharedPref.getString(PropertyTypeConstants.USER_ID, null);
    }

    public void setUserId(String userId) {
        setValuesInSharedPrefs(PropertyTypeConstants.USER_ID, userId);
    }

    public String getUserFName() {
        return mProjectSharedPref.getString(PropertyTypeConstants.USER_FNAME, null);
    }

    public void setUserFName(String firstName) {
        setValuesInSharedPrefs(PropertyTypeConstants.USER_FNAME, firstName);
    }
    public String getUserLName() {
        return mProjectSharedPref.getString(PropertyTypeConstants.USER_LNAME, null);
    }

    public void setUserLName(String lastName) {
        setValuesInSharedPrefs(PropertyTypeConstants.USER_LNAME,lastName);
    }

    public String getUserEmailId() {
        return mProjectSharedPref.getString(PropertyTypeConstants.USER_EMAIL_ID, null);
    }

    public void setUserEmailId(String userEmailId) {
        setValuesInSharedPrefs(PropertyTypeConstants.USER_EMAIL_ID, userEmailId);
    }

    public String getUserAddr1() {
        return mProjectSharedPref.getString(PropertyTypeConstants.USER_ADDR1, null);
    }

    public void setUserAddr1(String userAddr1) {
        setValuesInSharedPrefs(PropertyTypeConstants.USER_ADDR1, userAddr1);
    }
    public String getUserAddr2() {
        return mProjectSharedPref.getString(PropertyTypeConstants.USER_ADDR2, null);
    }
    public void setUserAddr2(String userAddr2) {
        setValuesInSharedPrefs(PropertyTypeConstants.USER_ADDR2, userAddr2);
    }
    public String getUserApart() {
        return mProjectSharedPref.getString(PropertyTypeConstants.USER_APART, null);
    }
    public void setUserApart(String userApart) {
        setValuesInSharedPrefs(PropertyTypeConstants.USER_APART, userApart);
    }

    public String getUserCode() {
        return mProjectSharedPref.getString(PropertyTypeConstants.USER_CODE, null);
    }

    public void setUserCode(String userCode) {
        setValuesInSharedPrefs(PropertyTypeConstants.USER_CODE, userCode);
    }

    public void setUserCity(String city) {
        setValuesInSharedPrefs(PropertyTypeConstants.USER_CITY, String.valueOf(city));
    }

    public String getUserCity() {
        return mProjectSharedPref.getString(PropertyTypeConstants.USER_CITY, null);

    }
    public void setUserPhone(String phone) {
        setValuesInSharedPrefs(PropertyTypeConstants.USER_PHONE, String.valueOf(phone));
    }

    public String getUserPhone() {
        return mProjectSharedPref.getString(PropertyTypeConstants.USER_PHONE, null);

    }

    private static void setValuesInSharedPrefs(String sharedPrefKey, String sharedPrefValue) {
        SharedPreferences.Editor editor = mProjectSharedPref.edit();
        editor.putString(sharedPrefKey, sharedPrefValue);
        editor.apply();
    }
}
