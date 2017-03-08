package com.project.smartedu.teacher;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.project.smartedu.LoginActivity;
import com.project.smartedu.database.Allotments;
import com.project.smartedu.database.Class;

import java.util.ArrayList;
import java.util.HashMap;

import static com.project.smartedu.admin.AdminUserPrefs.allotmments;

/**
 * Created by Shubham Bhasin on 08-Mar-17.
 */

public class TeacherUserPrefs {

    SharedPreferences pref;

    SharedPreferences.Editor editor;


    Context _context;

    int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "TeacherUserDetails";

    public static final String INSTITUION= "adminInst";




    public static ArrayList<String> allotments;



    static {

        allotments=new ArrayList<>();


    }

    public TeacherUserPrefs(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setInstituion(String instituion){

        // Storing name in pref
        editor.putString(INSTITUION, instituion);

        // commit changes
        editor.commit();
    }

    public void clearTeacherDetails(){
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();
      //  clearAllSavedData();

        // After logout redirect user to Loing Activity
        Intent i = new Intent(_context, LoginActivity.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        _context.startActivity(i);
    }



}
