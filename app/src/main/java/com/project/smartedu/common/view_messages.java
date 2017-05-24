package com.project.smartedu.common;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.project.smartedu.BaseActivity;
import com.project.smartedu.Constants;
import com.project.smartedu.LoginActivity;
import com.project.smartedu.R;
import com.project.smartedu.UserPrefs;
import com.project.smartedu.database.Messages;
import com.project.smartedu.navigation.FragmentDrawer;
import com.project.smartedu.notification.NotificationBar;
import com.project.smartedu.teacher.Classes;
import com.project.smartedu.teacher.Home;
import com.project.smartedu.teacher.TeacherUserPrefs;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class view_messages extends BaseActivity {
    private Toolbar mToolbar;
    private FragmentDrawer drawerFragment;


    // Students students = new Students();
    //ArrayList<Task> myList;
    ListView messageList;
    NotificationBar noti_bar;
    TextView message;
    TextView messageFrom;
    Button delete;
    Button ok;
    TextView messagedate;
    String _for;
    TextView title;
    TextView change_mode;
    TextView new_message;
    String classGradeId;
    String studentId;
    Button reply;
    EditText reply_message;
    Button reply_button;

    UserPrefs userPrefs;


    DatabaseReference databaseReference;


SwipeRefreshLayout swipeRefreshLayout;




    private class ReceivedMessageItems extends AsyncTask<Void, Void, Void> {

        private Context async_context;
        private ProgressDialog pd;

        public ReceivedMessageItems(Context context){
            this.async_context = context;
            pd = new ProgressDialog(async_context);

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setMessage("Fetching Received Messages...");
            pd.setCancelable(false);
            pd.show();
            UserPrefs.receivedmessagemap.clear();
            databaseReference = Constants.databaseReference.child(Constants.MESSAGES_TABLE).child(firebaseAuth.getCurrentUser().getUid()).child("received");

        }

        @Override
        protected Void doInBackground(Void... params) {
            final Object lock = new Object();

            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    synchronized (lock) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {

                            Log.d("ds.key",ds.getKey());             //user id


                            String id=ds.getKey();
                            String msg_id="";
                            String name="";
                            String content="";
                            String time="";
                            for(DataSnapshot dataSnapshot1:ds.getChildren()){           //traversing each message related to a user
                                Log.d("dsc",dataSnapshot1.getKey());        //message id

msg_id=dataSnapshot1.getKey();

                                for(DataSnapshot dataSnapshot2:dataSnapshot1.getChildren()) {
                                    Log.d("dscc",dataSnapshot2.getKey());
                                    if (dataSnapshot2.getKey().equalsIgnoreCase("name")) {

                                        name = dataSnapshot2.getValue().toString();
                                    }

                                    if (dataSnapshot2.getKey().equalsIgnoreCase("content")) {
                                        content = dataSnapshot2.getValue().toString();
                                    }

                                    if (dataSnapshot2.getKey().equalsIgnoreCase("time")) {
                                        time = dataSnapshot2.getValue().toString();
                                    }
                                }

                            }

                            Messages messages=new Messages(name,content,time,id,msg_id);

                            UserPrefs.receivedmessagemap.put(name+". "+time,messages);



                        }



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

            if(UserPrefs.receivedmessagemap.size()!=0) {
                setReceived();
            }
            //Show the log in progress_bar for at least a few milliseconds
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    pd.dismiss();
                    pd=null;
                }
            }, 500);  // 100 milliseconds
        }
        //end firebase_async_class
    }






    private class SentMessageItems extends AsyncTask<Void, Void, Void> {

        private Context async_context;
        private ProgressDialog pd;

        public SentMessageItems(Context context){
            this.async_context = context;
            pd = new ProgressDialog(async_context);

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setMessage("Fetching Sent Messages...");
            pd.setCancelable(false);
            pd.show();
            UserPrefs.sentmessagemap.clear();
            databaseReference = Constants.databaseReference.child(Constants.MESSAGES_TABLE).child(firebaseAuth.getCurrentUser().getUid()).child("sent");
            Log.d("check","1");
        }

        @Override
        protected Void doInBackground(Void... params) {
            final Object lock = new Object();
            Log.d("check","2");
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    synchronized (lock) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {

                            Log.d("ds.key",ds.getKey());             //user id


                            String id=ds.getKey();
                            String msg_id="";
                            String name="";
                            String content="";
                            String time="";
                            for(DataSnapshot dataSnapshot1:ds.getChildren()){           //traversing each message related to a user
                                Log.d("dsc",dataSnapshot1.getKey());        //message id

                                msg_id=dataSnapshot1.getKey();

                                for(DataSnapshot dataSnapshot2:dataSnapshot1.getChildren()) {
                                    Log.d("dscc",dataSnapshot2.getKey());
                                    if (dataSnapshot2.getKey().equalsIgnoreCase("name")) {

                                        name = dataSnapshot2.getValue().toString();
                                    }

                                    if (dataSnapshot2.getKey().equalsIgnoreCase("content")) {
                                        content = dataSnapshot2.getValue().toString();
                                    }

                                    if (dataSnapshot2.getKey().equalsIgnoreCase("time")) {
                                        time = dataSnapshot2.getValue().toString();
                                    }
                                }

                            }

                            Messages messages=new Messages(name,content,time,id,msg_id);

                            UserPrefs.sentmessagemap.put(name+". "+time,messages);



                        }



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
            if(UserPrefs.sentmessagemap.size()!=0) {
                setSent();
            }
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    pd.dismiss();
                    pd=null;
                }
            }, 500);  // 100 milliseconds
        }
        //end firebase_async_class
    }








    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_messages);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setTitle("Messages");
        final Intent from_student = getIntent();
        role = from_student.getStringExtra("role");
        _for = from_student.getStringExtra("_for");
        //classGradeId= from_student.getStringExtra("classGradeId");
        //studentId= from_student.getStringExtra("studentId");
        institutionName= from_student.getStringExtra("institution_name");


        userPrefs=new UserPrefs(view_messages.this);
        noti_bar = (NotificationBar) getSupportFragmentManager().findFragmentById(R.id.noti);
        noti_bar.setTexts(userPrefs.getUserName(), role,institutionName);


        drawerFragment = (FragmentDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar, role);
        drawerFragment.setDrawerListener(this);


        swipeRefreshLayout=(SwipeRefreshLayout)findViewById(R.id.swipe_layout);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefresh();
            }
        });

        change_mode=(TextView)findViewById(R.id.change_mode);
        new_message=(TextView)findViewById(R.id.new_message);
        messageList = (ListView) findViewById(R.id.messageList);


        if(role.equalsIgnoreCase("Teacher")) {
            new_message.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent message_intent = new Intent(view_messages.this, Classes.class);
                    message_intent.putExtra("institution_name", institutionName);
                    message_intent.putExtra("role", role);
                    message_intent.putExtra("for", "message");
                    startActivity(message_intent);
                }
            });
        }



        if(role.equalsIgnoreCase("Student") || role.equalsIgnoreCase("Parent"))
        {
            new_message.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent message_intent = new Intent(view_messages.this, message_to_teacher.class);
                    message_intent.putExtra("role", role);
                    message_intent.putExtra("institution",institutionName);

                    message_intent.putExtra("for", "message");
                    startActivity(message_intent);
                }
            });
        }


        if(_for.equals("received")) {
            getSupportActionBar().setTitle("INBOX");
            change_mode.setText("SEE OUTBOX");

            change_mode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent read_message_intent = new Intent(view_messages.this, view_messages.class);
                    read_message_intent.putExtra("role", role);

                    if (role.equals("Parent") || role.equals("Student")) {
                        //see later classId = from_student.getStringExtra("classId");
                        studentId = from_student.getStringExtra("studentId");
                        //sl read_message_intent.putExtra("classId", classId);
                        read_message_intent.putExtra("studentId", studentId);
                    }
                    read_message_intent.putExtra("institution_name", institutionName);

                    read_message_intent.putExtra("_for", "sent");
                    startActivity(read_message_intent);
                }
            });


            if (userPrefs.isFirstMessageLoading()) {
                userPrefs.setFirstLoading(false);
                ReceivedMessageItems receivedMessageItems = new ReceivedMessageItems(view_messages.this);
                receivedMessageItems.execute();
            }else {
                setReceived();
            }




            messageList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String item = ((TextView) view).getText().toString();
                    String values[] = item.split(" at ");
                    final String name = values[0];
                    String dateString = values[1];

                    SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss aa");

                    Date d = null;
                    try {
                        d = f.parse(dateString.trim());
                    } catch (java.text.ParseException x) {
                        x.printStackTrace();
                    }
                    java.util.Calendar calendar = Calendar.getInstance();
                    Log.d("user", String.valueOf(d));
                    calendar.setTime(d);
                    final String time = String.valueOf(d.getTime());


                    final Dialog dialog = new Dialog(view_messages.this);
                    dialog.setContentView(R.layout.messsage_info);
                    dialog.setTitle("Message");

                    setDialogSize(dialog);

                    Log.d("mcheck",name+" "+time);

                    title = (TextView) dialog.findViewById(R.id.title);
                    message = (TextView) dialog.findViewById(R.id.message);
                    messageFrom = (TextView) dialog.findViewById(R.id.message_from);
                    messagedate = (TextView) dialog.findViewById(R.id.date);
                    delete = (Button) dialog.findViewById(R.id.delButton);
                    ok = (Button) dialog.findViewById(R.id.doneButton);
                    reply = (Button) dialog.findViewById(R.id.replyButton);


                    title.setText("From:");
                    messageFrom.setText(name);
                    messagedate.setText(dateString);


                    reply.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            sendReply(name + ". " + time);
                            dialog.dismiss();
                        }
                    });


                    ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });


                    delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Messages messages = UserPrefs.receivedmessagemap.get(name + ". " + time);
                            String key = messages.getMessage_id();
                            databaseReference = Constants.databaseReference.child(Constants.MESSAGES_TABLE).child(firebaseAuth.getCurrentUser().getUid()).child("received").child(messages.getName_id()).child(key);
                            databaseReference.removeValue();

                            UserPrefs.receivedmessagemap.remove(name + ". " + time);
                            setReceived();
                            dialog.dismiss();

                        }
                    });


                    Messages messages = UserPrefs.receivedmessagemap.get(name + ". " + time);
                    message.setText(messages.getContent());


                    dialog.show();


                }
            });


        }

        if(_for.equals("sent")){
            getSupportActionBar().setTitle("OUTBOX");
            change_mode.setText("SEE INBOX");

            change_mode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent read_message_intent = new Intent(view_messages.this, view_messages.class);
                    read_message_intent.putExtra("role", role);
                    read_message_intent.putExtra("_for", "received");
                    read_message_intent.putExtra("institution_name", institutionName);
                    startActivity(read_message_intent);
                }
            });



            //see sent
            if(userPrefs.isFirstMessageLoading()) {
                userPrefs.setFirstMessageLoading(false);
                SentMessageItems sentMessageItems = new SentMessageItems(view_messages.this);
                sentMessageItems.execute();

            }else {
                setSent();
            }





            messageList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String item = ((TextView) view).getText().toString();
                    String values[] = item.split(" at ");
                    final String name = values[0];
                    String dateString = values[1];

                    SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss aa");

                    Date d = null;
                    try {
                        d = f.parse(dateString.trim());
                    } catch (java.text.ParseException x) {
                        x.printStackTrace();
                    }
                    java.util.Calendar calendar = Calendar.getInstance();
                    Log.d("user", String.valueOf(d));
                    calendar.setTime(d);
                    final String time = String.valueOf(d.getTime());


                    final Dialog dialog = new Dialog(view_messages.this);
                    dialog.setContentView(R.layout.messsage_info);
                    dialog.setTitle("Message");

                    setDialogSize(dialog);


                    title = (TextView) dialog.findViewById(R.id.title);
                    message = (TextView) dialog.findViewById(R.id.message);
                    messageFrom = (TextView) dialog.findViewById(R.id.message_from);
                    messagedate = (TextView) dialog.findViewById(R.id.date);
                    delete = (Button) dialog.findViewById(R.id.delButton);
                    ok = (Button) dialog.findViewById(R.id.doneButton);
                    reply = (Button) dialog.findViewById(R.id.replyButton);
                    reply.setVisibility(View.INVISIBLE);


                    title.setText("To:");
                    messageFrom.setText(name);
                    messagedate.setText(dateString);


                    reply.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                           // sendReply(name + ". " + time);
                            dialog.dismiss();
                        }
                    });


                    ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });


                    delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Messages messages = UserPrefs.sentmessagemap.get(name + ". " + time);
                            String key = messages.getMessage_id();
                            databaseReference = Constants.databaseReference.child(Constants.MESSAGES_TABLE).child(firebaseAuth.getCurrentUser().getUid()).child("sent").child(messages.getName_id()).child(key);
                            databaseReference.removeValue();

                            UserPrefs.sentmessagemap.remove(name + ". " + time);
                            setSent();
                            dialog.dismiss();

                        }
                    });


                    Messages messages = UserPrefs.sentmessagemap.get(name + ". " + time);
                    message.setText(messages.getContent());


                    dialog.show();


                }
            });
        }
    }


    protected void sendReply(final String key)            //key is name+". "+time
    {
        final Dialog send_reply=new Dialog(view_messages.this);
        send_reply.setContentView(R.layout.sending_message_to_teacher);
        reply_message=(EditText)send_reply.findViewById(R.id.message);
        reply_button=(Button)send_reply.findViewById(R.id.send_message);
        send_reply.show();


        reply_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(reply_message.getText().equals(""))
                {
                    Toast.makeText(view_messages.this, "Empty message", Toast.LENGTH_LONG).show();
                }else
                {


                    Messages messages=UserPrefs.receivedmessagemap.get(key);

                    String client_userid = messages.getName_id();


                    databaseReference= Constants.databaseReference.child(Constants.MESSAGES_TABLE).child(firebaseAuth.getCurrentUser().getUid()).child("sent").child(client_userid).push();
                    databaseReference.child("content").setValue(reply_message.getText().toString());
                    java.util.Calendar calendar = Calendar.getInstance();
                    databaseReference.child("time").setValue(String.valueOf((calendar.getTimeInMillis()/1000)*1000));
                    databaseReference.child("name").setValue(messages.getName());




                    databaseReference= Constants.databaseReference.child(Constants.MESSAGES_TABLE).child(client_userid).child("received").child(firebaseAuth.getCurrentUser().getUid()).push();
                    databaseReference.child("content").setValue(reply_message.getText().toString());
                    databaseReference.child("time").setValue(String.valueOf((calendar.getTimeInMillis()/1000)*1000));
                    databaseReference.child("name").setValue(userPrefs.getUserName());



                    send_reply.dismiss();

                    Toast.makeText(view_messages.this, "Reply Sent", Toast.LENGTH_LONG).show();


                }
            }
        });
    }


    protected void sleep(int time)
    {
        for(int x=0;x<time;x++)
        {}
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Intent to_view_messages = new Intent(view_messages.this, view_messages.class);
        to_view_messages.putExtra("institution_name",institutionName);
        to_view_messages.putExtra("role", role);
        to_view_messages.putExtra("_for",_for);
        startActivity(to_view_messages);
        finish();
    }


    public void setReceived(){

        ArrayList<String> messageLt = new ArrayList<String>();

        for (String key : UserPrefs.receivedmessagemap.keySet()) {      //key is name+". "+time

            String values[] = key.split("\\. ");
            String name = values[0];
            String time = values[1];

            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss aa");
            String dateString = formatter.format(new Date(Long.parseLong(time)));

            messageLt.add(name + " at " + dateString);

        }


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                getApplicationContext(), R.layout.simple_list_item, messageLt);

        messageList.setAdapter(adapter);

    }


    public void setSent(){

        ArrayList<String> messageLt = new ArrayList<String>();

        for (String key : UserPrefs.sentmessagemap.keySet()) {      //key is name+". "+time

            String values[] = key.split("\\. ");
            String name = values[0];
            String time = values[1];

            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss aa");
            String dateString = formatter.format(new Date(Long.parseLong(time)));

            messageLt.add(name + " at " + dateString);

        }


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                getApplicationContext(), R.layout.simple_list_item, messageLt);

        messageList.setAdapter(adapter);
    }

    public void swipeRefresh(){
        userPrefs.setFirstMessageLoading(true);
        Intent to_view_messages = new Intent(view_messages.this, view_messages.class);
        to_view_messages.putExtra("institution_name",institutionName);
        to_view_messages.putExtra("role", role);
        to_view_messages.putExtra("_for",_for);
        startActivity(to_view_messages);

    }


    @Override
    protected void onPostResume() {
        super.onPostResume();
        if(firebaseAuth.getCurrentUser()==null)
        {
            Intent nouser=new Intent(view_messages.this,LoginActivity.class);
            startActivity(nouser);
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();


        if(role.equalsIgnoreCase("Teacher")) {
            Intent toHome = new Intent(view_messages.this, Home.class);
            toHome.putExtra("role", role);
            toHome.putExtra("institution_name", institutionName);
            startActivity(toHome);
        }

        if(role.equalsIgnoreCase("student")){
            Intent toHome = new Intent(view_messages.this, com.project.smartedu.student.Home.class);
            toHome.putExtra("role", role);
            toHome.putExtra("institution_name", institutionName);
            startActivity(toHome);
        }


        if(role.equalsIgnoreCase("parent")){
            Intent toHome = new Intent(view_messages.this, com.project.smartedu.parent.Home.class);
            toHome.putExtra("role", role);
            toHome.putExtra("institution_name", institutionName);
            startActivity(toHome);
        }
    }

}
