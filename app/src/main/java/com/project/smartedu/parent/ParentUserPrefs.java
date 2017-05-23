package com.project.smartedu.parent;

import android.content.Context;
import android.content.SharedPreferences;

import com.project.smartedu.database.Children;
import com.project.smartedu.database.Students;
import com.project.smartedu.database.Teachers;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Shubham Bhasin on 31-Mar-17.
 */

public class ParentUserPrefs {


    SharedPreferences pref;

    SharedPreferences.Editor editor;


    Context _context;

    int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "ParentUserDetails";
    private static final String SELECTED_CHILD_ID = "SelectedChildId";
    private static final String SELECTED_CHILD_INSTITUTE = "SelectedInstitute";
    private static final String SELECTED_CHILD_NAME = "SelectedChildName";



    private static final String SELECTED_CHILD_CLASS = "ChildClass";
    private static final String SELECTED_CHILD_ROLL_NUMBER = "ChildRollNumber";




    public static ArrayList<String> childuseridLt;
    public static HashMap<String,Children> childinsitutionmap;                 //child user id to list of insitutions




    static {
        childuseridLt=new ArrayList<>();
        childinsitutionmap=new HashMap<>();
    }



    public ParentUserPrefs(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }



    public void setSelectedInstituion(String instituion){

        // Storing name in pref
        editor.putString(SELECTED_CHILD_INSTITUTE, instituion);

        // commit changes
        editor.commit();
    }


    public void setSelectedChildId(String childId){

        // Storing name in pref
        editor.putString(SELECTED_CHILD_ID, childId);

        // commit changes
        editor.commit();
    }



    public void setSelectedChildClass(String classId){

        // Storing name in pref
        editor.putString(SELECTED_CHILD_CLASS, classId);

        // commit changes
        editor.commit();
    }




    public void setSelectedChildRollNumber(String rollNumber){

        // Storing name in pref
        editor.putString(SELECTED_CHILD_ROLL_NUMBER, rollNumber);

        // commit changes
        editor.commit();
    }



    public void setSelectedChildNameString(String name){

        // Storing name in pref
        editor.putString(SELECTED_CHILD_NAME, name);

        // commit changes
        editor.commit();
    }






    public String getSelectedChildName(){
        return pref.getString(SELECTED_CHILD_NAME,"");
    }






    public String getSelectedChildRollNumber(){
        return pref.getString(SELECTED_CHILD_ROLL_NUMBER,"");
    }


    public String getChildClass(){
        return pref.getString(SELECTED_CHILD_CLASS,"");
    }


    public String getSelectedInstitution(){
        return pref.getString(SELECTED_CHILD_INSTITUTE,"");
    }

    public String getSelectedChildId(){
        return pref.getString(SELECTED_CHILD_ID,"");
    }



    public void clearParentDetails(){
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();
        //  clearAllSavedData();


    }


}
