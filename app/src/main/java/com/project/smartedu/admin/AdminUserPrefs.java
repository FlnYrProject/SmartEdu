package com.project.smartedu.admin;

import android.content.Context;
import android.content.SharedPreferences;

import com.project.smartedu.database.Allotments;
import com.project.smartedu.database.Class;
import com.project.smartedu.database.Schedule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by Shubham Bhasin on 25-Feb-17.
 */

public class AdminUserPrefs {

    SharedPreferences pref;

    SharedPreferences.Editor editor;


    Context _context;

    int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "AdminUserDetails";




    public static ArrayList<String> teacherLt;
    public static ArrayList<String> teacheruseridLt;
    public static HashMap<String,String> teachersusermap; //to map teacher to its user id
    public static HashMap<String,String> teachersuserreversemap; //to map user id to teacher




    public static ArrayList<Class> classes;
    public static ArrayList<Allotments> allotmments;



    static {


        teacherLt=new ArrayList<>();
        teacheruseridLt=new ArrayList<>();
        teachersusermap=new HashMap<>();
        teachersuserreversemap=new HashMap<>();
       // schedulesmaplt=new HashMap<>();


        classes=new ArrayList<Class>();
        allotmments=new ArrayList<>();

    }

    public AdminUserPrefs(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void clearAdminData(){
        teacherLt.clear();
        teacheruseridLt.clear();
        teachersusermap.clear();
        // schedulesmaplt=new HashMap<>();


        classes.clear();
        allotmments.clear();

    }



}
