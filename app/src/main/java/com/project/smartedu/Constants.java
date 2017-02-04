package com.project.smartedu;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Shubham Bhasin on 04-Feb-17.
 */

public class Constants {

  public static final DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference().child(Constants.DATABASE);

    public static final String DATABASE = "SmartEdu_DB";
    public static final String USER_DETAILS_TABLE= "user_details";
    public static final String INSTITUTION_TABLE= "institution";










}
