package com.project.smartedu;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.project.smartedu.database.Messages;
import com.project.smartedu.database.Schedule;
import com.project.smartedu.database.Uploads;

import java.util.ArrayList;
import java.util.HashMap;

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

    public static final String USER_DOB="dateofbirth";

    public static final String USER_ADDRESS="address";

    public static final String USER_CONTACT="contact";

    public static final String USER_PARENT_NAME="parentname";

    public static final String USER_EMAIL = "email";

    public static final String USER_PASSWORD = "password";

    public static final String IS_ADMIN= "isadmin";

    public static final String USER_ID= "userid";

    public static final String INSTITUION= "adminInst";

    public static final String FIRST_LOADING= "isfirstloading";

    public static final String FIRST_MESSAGE_LOADING= "isfirstmessageloading";


    public static HashMap<String,ArrayList<String>> roleslistmap;
    public static HashMap<String,ArrayList<String>> parentchildinstmap;

    public static ArrayList<String> taskItems;
    public static HashMap<String,String> taskidmap; //to map task to its id

    //public static ArrayList<String> uploadItems;
    //public static HashMap<String,String> uploadidmap; //to map task to its id
    public static HashMap<Uploads,String> uploadkeymap;        ///to map schedule to key

    // public static HashMap<String,ArrayList<Schedule>> schedulesmaplt; // a map from day to its schedules
    public static HashMap<Schedule,String> schedulekeymap;        ///to map schedule to key









    public static HashMap<String,Messages> receivedmessagemap;      //name+time--->message
    public static HashMap<String,Messages> sentmessagemap;      //name+time--->message

    static {
        roleslistmap=new HashMap<>();           //role type mapped to list of institutions
        parentchildinstmap=new HashMap<>();     //child user id to list of institutions
        taskItems=new ArrayList<>();
        taskidmap=new HashMap<>();
        schedulekeymap=new HashMap<>();
        uploadkeymap=new HashMap<>();
        receivedmessagemap=new HashMap<>();
        sentmessagemap=new HashMap<>();
    }

    public UserPrefs(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }


    public void setUserDetails(String userid, String name,String email, String password){
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

    public Boolean isFirstLoading(){
        // Storing flag for admin in pref
       return pref.getBoolean(FIRST_LOADING,true);
    }


    public void setFirstLoading(Boolean isfirstloading){
        // Storing flag for admin in pref
        editor.putBoolean(FIRST_LOADING, isfirstloading);

        // commit changes
        editor.commit();
    }


    public Boolean isFirstMessageLoading(){
        // Storing flag for admin in pref
        return pref.getBoolean(FIRST_MESSAGE_LOADING,true);
    }


    public void setFirstMessageLoading(Boolean isfirstmessageloading){
        // Storing flag for admin in pref
        editor.putBoolean(FIRST_MESSAGE_LOADING, isfirstmessageloading);

        // commit changes
        editor.commit();
    }


    public void clearUserDetails(){
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();
        clearAllSavedData();

        FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
        firebaseAuth.signOut();
        Toast.makeText(_context,"Signed Out",Toast.LENGTH_LONG).show();
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
    public String getUserName(){
        return pref.getString(USER_NAME,null);
    }
    public String getUserDob(){ return pref.getString(USER_DOB,null);}
    public String getUserAddress(){ return pref.getString(USER_ADDRESS,null);}
    public String getUserContact(){ return pref.getString(USER_CONTACT,null);}
    public String getUserParentName(){ return pref.getString(USER_PARENT_NAME,null);}
    public String getUserEmail() { return pref.getString(USER_EMAIL,null);}
    public String getUserPassword() { return  pref.getString(USER_PASSWORD,null);}


    public void setUserName(String name){
        editor.putString(USER_NAME, name);
        editor.commit();
    }

    public void setUserDob(String dob){
        editor.putString(USER_DOB, dob);
        editor.commit();
    }

    public void setUserAddress(String address){
        editor.putString(USER_ADDRESS, address);
        editor.commit();
    }

    public void setUserContact(String contact){
        editor.putString(USER_CONTACT, contact);
        editor.commit();
    }

    public void setUserParentName(String parentName){
        editor.putString(USER_PARENT_NAME, parentName);
        editor.commit();
    }

    public void clearAllSavedData(){
        roleslistmap.clear();
        parentchildinstmap.clear();
        taskidmap.clear();
        taskItems.clear();
        schedulekeymap.clear();

    }


}
