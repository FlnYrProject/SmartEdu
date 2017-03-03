package com.project.smartedu;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.project.smartedu.admin.AdminUserPrefs;
import com.project.smartedu.admin.Home;
import com.project.smartedu.common.parent_choose_child;
import com.project.smartedu.common.select_institution;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class ChooseRole extends BaseActivity {

    Button student;
    Button parent;
    Button teacher;

    boolean foundRole;

    DatabaseReference databaseReference;







    private class RoleItems extends AsyncTask<Void, Void, Void> {

        private Context async_context;
        private ProgressDialog pd;

        public RoleItems(Context context){
            this.async_context = context;
            pd = new ProgressDialog(async_context);
            databaseReference = Constants.databaseReference.child(Constants.USER_DETAILS_TABLE).child(firebaseAuth.getCurrentUser().getUid()).child("role");
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setMessage("Fetching Roles");
            pd.setCancelable(false);
            pd.show();
            UserPrefs.roleslistmap.clear();
        }

        @Override
        protected Void doInBackground(Void... params) {
            final Object lock = new Object();

            final ArrayList<String> parentinst=new ArrayList<>();
            final ArrayList<String> teacherinst=new ArrayList<>();
            final ArrayList<String> studentinst=new ArrayList<>();

            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    synchronized (lock) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            HashMap<String,String> retRolesList = (HashMap<String, String>) ds.getValue();

                            Log.d("mainkey",ds.getKey());       //defining which kind of role
                            for ( String key : retRolesList.keySet() ) {

                                Log.d("role",ds.getKey() + " at " + retRolesList.get(key));

                                if(ds.getKey().equalsIgnoreCase("parent")){
                                    parentinst.add(retRolesList.get(key));
                                }else if(ds.getKey().equalsIgnoreCase("student")){
                                    studentinst.add(retRolesList.get(key));
                                }else if(ds.getKey().equalsIgnoreCase("teacher")){
                                    teacherinst.add(retRolesList.get(key));
                                }


                            }



                        }
                        UserPrefs.roleslistmap.put("parent",parentinst);
                        UserPrefs.roleslistmap.put("student",studentinst);
                        UserPrefs.roleslistmap.put("teacher",teacherinst);

                        lock.notifyAll();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }

            });

            synchronized (lock){
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            //Handles the stuff after the synchronisation with the firebase listener has been achieved
            //The main UI is already idle by this moment
            super.onPostExecute(aVoid);

            //Show the log in progress_bar for at least a few milliseconds

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    pd.dismiss();
                }
            }, 500);  // 100 milliseconds
        }
        //end firebase_async_class
    }








    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_role);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //change to add role button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with add role", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                firebaseAuth.signOut();
                UserPrefs userPrefs=new UserPrefs(ChooseRole.this);
                userPrefs.clearUserDetails();
            }
        });


        foundRole=false;

        student=(Button)findViewById(R.id.button_student);
        parent=(Button)findViewById(R.id.button_parent);
        teacher=(Button)findViewById(R.id.button_teacher);

        RoleItems roleItemsasync=new RoleItems(ChooseRole.this);
        roleItemsasync.execute();

        teacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                roleChosen("teacher");

            }
        });

        parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                roleChosen("parent");

            }
        });

        student.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                roleChosen("student");

            }
        });


    }




    public void roleChosen(final String role) {
        Log.d("rl",UserPrefs.roleslistmap.get("parent").size() + " in parent ");
        Log.d("rl",UserPrefs.roleslistmap.get("student").size() + " in student ");
        Log.d("rl",UserPrefs.roleslistmap.get("teacher").size() + " in teacher ");

        if( (!UserPrefs.roleslistmap.containsKey(role)) || (UserPrefs.roleslistmap.get(role).size()==0) ){
            Toast.makeText(getApplicationContext(), "Role not added", Toast.LENGTH_LONG).show();
        }else{

            if(role.equalsIgnoreCase("parent")) {
                Intent j = new Intent(ChooseRole.this, parent_choose_child.class);
                j.putExtra("role", role);
                startActivity(j);
            }else
            {
                Intent j = new Intent(ChooseRole.this, select_institution.class);
                j.putExtra("role", role);
                startActivity(j);
            }

            Toast.makeText(getApplicationContext(), role + " Module", Toast.LENGTH_LONG)
                    .show();
        }


    }



}
