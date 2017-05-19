package com.project.smartedu.teacher;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.project.smartedu.LoginActivity;
import com.project.smartedu.database.*;
import com.project.smartedu.database.Class;
import com.project.smartedu.database.Students;

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

    public static final String INSTITUION= "Inst";




    public static ArrayList<String> allotments;

    public static HashMap<String,ArrayList<String>> subjectallotmentmap;

    public static ArrayList<String> studentsuseridLt;
    public static HashMap<String,Students> studentsHashMap;                 //user id to student details




    static {

        allotments=new ArrayList<>();
        subjectallotmentmap=new HashMap<>();
        studentsuseridLt=new ArrayList<>();
        studentsHashMap=new HashMap<>();
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

        allotments.clear();
        subjectallotmentmap.clear();
        studentsuseridLt.clear();
        studentsHashMap.clear();

    }



}
