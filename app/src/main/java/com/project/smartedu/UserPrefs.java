package com.project.smartedu;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

/**
 * Created by Shubham Bhasin on 24-Feb-17.
 */

public class UserPrefs {

    SharedPreferences pref;

    SharedPreferences.Editor editor;


    Context _context;

    int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "UserDetails";


    private static final String IS_LOGIN = "IsLoggedIn";

    public static final String USER_NAME = "name";

    public static final String USER_EMAIL = "email";

    public static final String USER_PASSWORD = "password";

    public static final String IS_ADMIN= "isadmin";

    public static final String USER_ID= "userid";

    public static final String INSTITUION= "adminInst";

    public UserPrefs(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }


    public void setUserDetails(String userid, String name, String email, String password){
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);

        // Storing name in pref
        editor.putString(USER_NAME, name);

        // Storing email in pref
        editor.putString(USER_EMAIL, email);

        // Storing password in pref
        editor.putString(USER_PASSWORD, password);

        // Storing user id in pref
        editor.putString(USER_ID, userid);

        // commit changes
        editor.commit();
    }


    public void setIfAdmin(Boolean isadmin,String institutionName){
        // Storing flag for admin in pref
        editor.putBoolean(IS_ADMIN, isadmin);
        editor.putString(INSTITUION,institutionName);
        // commit changes
        editor.commit();
    }



    public void clearUserDetails(){
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();

        // After logout redirect user to Loing Activity
        Intent i = new Intent(_context, LoginActivity.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        _context.startActivity(i);
    }



    public boolean isAdmin(){
       return pref.getBoolean(IS_ADMIN,false);
    }

    public String getInstitution(){
        return pref.getString(INSTITUION,null);
    }


}