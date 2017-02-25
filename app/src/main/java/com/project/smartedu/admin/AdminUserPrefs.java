package com.project.smartedu.admin;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Shubham Bhasin on 25-Feb-17.
 */

public class AdminUserPrefs {

    SharedPreferences pref;

    SharedPreferences.Editor editor;


    Context _context;

    int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "AdminUserDetails";


    public static ArrayList<String> taskItems;
    public static ArrayList<String> teacherLt;
    public static ArrayList<String> teacheruseridLt;
    public static HashMap<String,String> teachersusermap; //to map teacher to its user id


    static {
       taskItems=new ArrayList<>();
        teacherLt=new ArrayList<>();
        teacheruseridLt=new ArrayList<>();
        teachersusermap=new HashMap<>();

    }

    public AdminUserPrefs(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }



}
