package com.project.smartedu.common;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.widget.DrawerLayout;
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

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.project.smartedu.BaseActivity;
import com.project.smartedu.LoginActivity;
import com.project.smartedu.R;
import com.project.smartedu.navigation.FragmentDrawer;
import com.project.smartedu.notification.NotificationBar;
import com.project.smartedu.teacher.Classes;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class view_messages extends BaseActivity {
    /*private Toolbar mToolbar;
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
        classGradeId= from_student.getStringExtra("classGradeId");
        studentId= from_student.getStringExtra("studentId");
        institutionName= from_student.getStringExtra("institution_name");

        noti_bar = (NotificationBar) getSupportFragmentManager().findFragmentById(R.id.noti);
        noti_bar.setTexts(ParseUser.getCurrentUser().getUsername(), role,institutionName);


        drawerFragment = (FragmentDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar, role);
        drawerFragment.setDrawerListener(this);

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



        if(role.equals("Student") || role.equals("Parent"))
        {
            new_message.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //classId=from_student.getStringExtra("classId");
                    studentId=from_student.getStringExtra("studentId");
                    // Log.d("test",classId);
                    Log.d("test",studentId);
                    Intent message_intent = new Intent(view_messages.this, message_to_teacher.class);
                    message_intent.putExtra("role", role);
                    message_intent.putExtra("institution",institutionName);
                    message_intent.putExtra("classGradeId", classGradeId);
                    message_intent.putExtra("studentId", studentId);
                    message_intent.putExtra("for", "message");
                    startActivity(message_intent);
                }
            });
        }


        if(_for.equals("received")){
            getSupportActionBar().setTitle("Received Messages");
            change_mode.setText("SEE INBOX");

            change_mode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent read_message_intent = new Intent(view_messages.this, view_messages.class);
                    read_message_intent.putExtra("role", role);
                    if (role.equals("Parent") || role.equals("Student")) {
                        classId = from_student.getStringExtra("classId");
                        studentId = from_student.getStringExtra("studentId");
                        read_message_intent.putExtra("classId", classId);
                        read_message_intent.putExtra("studentId", studentId);
                        read_message_intent.putExtra("institution_name", institutionName);
                    }
                    read_message_intent.putExtra("_for", "sent");
                    startActivity(read_message_intent);
                }
            });

            ParseQuery<ParseObject> messageQuery = ParseQuery.getQuery(MessageTable.TABLE_NAME);
            messageQuery.whereEqualTo(MessageTable.TO_USER_REF, ParseUser.getCurrentUser());
            messageQuery.whereEqualTo(MessageTable.DELETED_BY_RECEIVER, false);
            messageQuery.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> messageListRet, ParseException e) {
                    if (e == null) {
                        if (messageListRet.size() != 0) {

                            ArrayList<String> messageLt = new ArrayList<String>();

                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                                    getApplicationContext(), android.R.layout.simple_list_item_1, messageLt) {

                                @Override
                                public View getView(int position, View convertView,
                                                    ViewGroup parent) {
                                    View view = super.getView(position, convertView, parent);

                                    TextView textView = (TextView) view.findViewById(android.R.id.text1);

            *//*YOUR CHOICE OF COLOR*//*
                                    textView.setTextColor(Color.WHITE);

                                    return view;
                                }
                            };


                            Log.d("user", "Retrieved " + messageListRet.size() + " messages");
                            //Toast.makeText(getApplicationContext(), studentListRet.toString(), Toast.LENGTH_LONG).show();
                            for (int i = 0; i < messageListRet.size(); i++) {
                                ParseObject u = messageListRet.get(i);
                                //  if(u.getString("class").equals(id)) {
                                ParseUser senderuser = (ParseUser) u.get(MessageTable.FROM_USER_REF);

                                String from = null;
                                try {
                                    from = senderuser.fetchIfNeeded().getUsername();
                                } catch (ParseException e1) {
                                    e1.printStackTrace();
                                }
                                //name += "\n";
                                // name += u.getInt("age");
                                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss aa");

                                final String dateString = formatter.format(new Date(u.getLong(MessageTable.SENT_AT)));
                                Log.d("user", dateString);

                                ParseObject institute = (ParseObject) u.get(MessageTable.INSTITUTION);
                                String insti= null;
                                if (institute != null) {
                                    try {
                                        insti = institute.fetchIfNeeded().get(InstitutionTable.INSTITUTION_NAME).toString();
                                        Log.d("institute: ", insti);
                                    } catch (ParseException e1) {
                                        e1.printStackTrace();
                                    }
                                }
                                String name = from + "\nat " + dateString + "\n" + insti;
                                adapter.add(name);
                                // }

                            }


                            messageList.setAdapter(adapter);


                            messageList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                                    String item = ((TextView) view).getText().toString();
                                    String[] itemVal= item.split("\n");
                                    Log.d("user", "1 " + itemVal[0].trim() + " 2 " + itemVal[1]);

                                    final String[] details = new String[2];
                                    details[0]= itemVal[0];

                                    String[] itemValues = itemVal[1].split("at ");
                                    details[1]=itemValues[1];

                            *//*
                            int j = 0;

                            for (String x : itemValues) {
                                details[j++] = x;
                            } *//*

                                    Log.d("user", "from " + details[0].trim() + " at " + details[1]);  //extracts Chit as Chi and query fails???
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


                                    title.setText("From:");
                                    messageFrom.setText(details[0]);
                                    messagedate.setText(details[1]);


                                    reply.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            sendReply(details[0]);
                                            dialog.dismiss();
                                        }
                                    });


                                    ok.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            dialog.dismiss();
                                        }
                                    });

                                    SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss aa");
                                    Log.d("user", details[1]);
                                    Date d = null;
                                    try {
                                        d = f.parse(details[1].trim());
                                    } catch (java.text.ParseException x) {
                                        x.printStackTrace();
                                    }
                                    final java.util.Calendar calendar = Calendar.getInstance();
                                    Log.d("user", String.valueOf(d));
                                    calendar.setTime(d);

                                    delete.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            ParseQuery<ParseUser> user = ParseUser.getQuery();
                                            user.whereEqualTo("username", details[0]);
                                            user.findInBackground(new FindCallback<ParseUser>() {
                                                @Override
                                                public void done(List<ParseUser> objects, ParseException e) {
                                                    if (e == null) {
                                                        if (objects.size() != 0) {
                                                            ParseQuery<ParseObject> getMessageQuery = ParseQuery.getQuery(MessageTable.TABLE_NAME);
                                                            getMessageQuery.whereEqualTo(MessageTable.FROM_USER_REF, objects.get(0));
                                                            getMessageQuery.whereEqualTo(MessageTable.TO_USER_REF, ParseUser.getCurrentUser());
                                                            getMessageQuery.whereEqualTo(MessageTable.INSTITUTION, ParseObject.createWithoutData(InstitutionTable.TABLE_NAME, institution_code));

                                                            final long milliseconds = calendar.getTimeInMillis();
                                                            Log.d("user", String.valueOf(milliseconds));
                                                            getMessageQuery.whereEqualTo(MessageTable.SENT_AT, milliseconds);
                                                            getMessageQuery.findInBackground(new FindCallback<ParseObject>() {
                                                                @Override
                                                                public void done(List<ParseObject> objects, ParseException e) {
                                                                    if (e == null) {
                                                                        if (objects.size() != 0) {
                                                                            objects.get(0).put(MessageTable.DELETED_BY_RECEIVER, true);
                                                                            objects.get(0).saveInBackground();
                                                                            sleep(1000);
                                                                            if (objects.get(0).getBoolean(MessageTable.DELETED_BY_SENDER)) {
                                                                                objects.get(0).deleteEventually();
                                                                            }
                                                                            dialog.dismiss();
                                                                            onRestart();
                                                                        } else {
                                                                            Log.d("user", "query logic in deleting messages");
                                                                        }
                                                                    } else {
                                                                        Log.d("user", "no message retrived");
                                                                    }
                                                                }
                                                            });
                                                        } else {
                                                            Log.d("user", "query logic error");
                                                        }
                                                    } else {
                                                        Log.d("user", "error in getting user");
                                                    }
                                                }
                                            });


                                        }
                                    });


                                    ParseQuery<ParseUser> user = ParseUser.getQuery();
                                    user.whereEqualTo("username", details[0]);
                                    user.findInBackground(new FindCallback<ParseUser>() {
                                        @Override
                                        public void done(List<ParseUser> objects, ParseException e) {
                                            if (e == null) {
                                                if (objects.size() != 0) {
                                                    ParseQuery<ParseObject> getMessageQuery = ParseQuery.getQuery(MessageTable.TABLE_NAME);
                                                    getMessageQuery.whereEqualTo(MessageTable.FROM_USER_REF, objects.get(0));
                                                    getMessageQuery.whereEqualTo(MessageTable.TO_USER_REF, ParseUser.getCurrentUser());
                                                    getMessageQuery.whereEqualTo(MessageTable.INSTITUTION, ParseObject.createWithoutData(InstitutionTable.TABLE_NAME, institution_code));
                                                    final long milliseconds = calendar.getTimeInMillis();
                                                    Log.d("user", String.valueOf(milliseconds));
                                                    getMessageQuery.whereEqualTo(MessageTable.SENT_AT, milliseconds);
                                                    getMessageQuery.findInBackground(new FindCallback<ParseObject>() {
                                                        @Override
                                                        public void done(List<ParseObject> objects, ParseException e) {
                                                            if (e == null) {
                                                                if (objects.size() != 0) {

                                                                    message.setText(objects.get(0).getString(MessageTable.MESSAGE_CONTENT));
                                                                } else {
                                                                    Log.d("user", "query logic in getting messages");
                                                                }
                                                            } else {
                                                                Log.d("user", "no message retrived");
                                                            }
                                                        }
                                                    });
                                                } else {
                                                    Log.d("user", "query logic error");
                                                }
                                            } else {
                                                Log.d("user", "error in getting user");
                                            }
                                        }
                                    });

                                    dialog.show();


                                }
                            });

                        } else {
                            Toast.makeText(view_messages.this, "No messsages", Toast.LENGTH_LONG).show();
                        }

                    } else {
                        Toast.makeText(view_messages.this, "error", Toast.LENGTH_LONG).show();
                        Log.d("user", "Error: " + e.getMessage());
                    }
                }
            });
        }



        if(_for.equals("sent")){
            getSupportActionBar().setTitle("Sent Messages");
            change_mode.setText("SEE OUTBOX");

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

            ParseQuery<ParseObject> messageQuery = ParseQuery.getQuery(MessageTable.TABLE_NAME);
            messageQuery.whereEqualTo(MessageTable.FROM_USER_REF, ParseUser.getCurrentUser());
            messageQuery.whereEqualTo(MessageTable.INSTITUTION, ParseObject.createWithoutData(InstitutionTable.TABLE_NAME, institution_code));
            messageQuery.whereEqualTo(MessageTable.DELETED_BY_SENDER, false);
            messageQuery.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> messageListRet, ParseException e) {
                    if (e == null) {
                        if (messageListRet.size() != 0) {

                            ArrayList<String> messageLt = new ArrayList<String>();

                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                                    getApplicationContext(), android.R.layout.simple_list_item_1, messageLt) {

                                @Override
                                public View getView(int position, View convertView,
                                                    ViewGroup parent) {
                                    View view = super.getView(position, convertView, parent);

                                    TextView textView = (TextView) view.findViewById(android.R.id.text1);

            *//*YOUR CHOICE OF COLOR*//*
                                    textView.setTextColor(Color.WHITE);

                                    return view;
                                }
                            };


                            Log.d("user", "Retrieved " + messageListRet.size() + " messages");
                            //Toast.makeText(getApplicationContext(), studentListRet.toString(), Toast.LENGTH_LONG).show();
                            for (int i = 0; i < messageListRet.size(); i++) {
                                ParseObject u = messageListRet.get(i);
                                //  if(u.getString("class").equals(id)) {
                                ParseUser receiveruser = (ParseUser) u.get(MessageTable.TO_USER_REF);

                                String to = null;
                                try {
                                    to = receiveruser.fetchIfNeeded().getUsername();
                                } catch (ParseException e1) {
                                    e1.printStackTrace();
                                }
                                //name += "\n";
                                // name += u.getInt("age");
                                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss aa");

                                final String dateString = formatter.format(new Date(u.getLong(MessageTable.SENT_AT)));
                                Log.d("user", dateString);
                                String name = to + "\nat " + dateString;
                                adapter.add(name);
                                // }

                            }


                            messageList.setAdapter(adapter);


                            messageList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                                    String item = ((TextView) view).getText().toString();

                                    String[] itemValues = item.split("\nat ");

                                    final String[] details = new String[2];
                                    int j = 0;

                                    for (String x : itemValues) {
                                        details[j++] = x;
                                    }

                                    Log.d("user", "from " + details[0].trim() + " at " + details[1]);  //extracts Chit as Chi and query fails???
                                    final Dialog dialog = new Dialog(view_messages.this);
                                    dialog.setContentView(R.layout.messsage_info);
                                    dialog.setTitle("Message");

                                    setDialogSize(dialog);

                                    title = (TextView) dialog.findViewById(R.id.title);
                                    message = (TextView) dialog.findViewById(R.id.message);
                                    messageFrom = (TextView) dialog.findViewById(R.id.message_from);
                                    messagedate = (TextView) dialog.findViewById(R.id.date);
                                    delete = (Button) dialog.findViewById(R.id.doneButton);
                                    ok = (Button) dialog.findViewById(R.id.doneButton);
                                    reply=(Button)dialog.findViewById(R.id.replyButton);
                                    reply.setVisibility(View.INVISIBLE);
                                    title.setText("To:");
                                    messageFrom.setText(details[0]);
                                    messagedate.setText(details[1]);

                                    ok.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            dialog.dismiss();
                                        }
                                    });

                                    SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss aa");
                                    Log.d("user", details[1]);
                                    Date d = null;
                                    try {
                                        d = f.parse(details[1].trim());
                                    } catch (java.text.ParseException x) {
                                        x.printStackTrace();
                                    }
                                    final java.util.Calendar calendar = Calendar.getInstance();
                                    Log.d("user", String.valueOf(d));
                                    calendar.setTime(d);

                                    delete.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            ParseQuery<ParseUser> user = ParseUser.getQuery();
                                            user.whereEqualTo("username", details[0]);
                                            user.findInBackground(new FindCallback<ParseUser>() {
                                                @Override
                                                public void done(List<ParseUser> objects, ParseException e) {
                                                    if (e == null) {
                                                        if (objects.size() != 0) {
                                                            ParseQuery<ParseObject> getMessageQuery = ParseQuery.getQuery(MessageTable.TABLE_NAME);
                                                            getMessageQuery.whereEqualTo(MessageTable.FROM_USER_REF, ParseUser.getCurrentUser());
                                                            getMessageQuery.whereEqualTo(MessageTable.TO_USER_REF, objects.get(0));
                                                            getMessageQuery.whereEqualTo(MessageTable.INSTITUTION, ParseObject.createWithoutData(InstitutionTable.TABLE_NAME, institution_code));

                                                            final long milliseconds = calendar.getTimeInMillis();
                                                            Log.d("user", String.valueOf(milliseconds));
                                                            getMessageQuery.whereEqualTo(MessageTable.SENT_AT, milliseconds);
                                                            getMessageQuery.findInBackground(new FindCallback<ParseObject>() {
                                                                @Override
                                                                public void done(List<ParseObject> objects, ParseException e) {
                                                                    if (e == null) {
                                                                        if (objects.size() != 0) {
                                                                            objects.get(0).put(MessageTable.DELETED_BY_SENDER, true);
                                                                            objects.get(0).saveInBackground();
                                                                            sleep(1000);
                                                                            if (objects.get(0).getBoolean(MessageTable.DELETED_BY_RECEIVER)) {
                                                                                objects.get(0).deleteEventually();
                                                                            }
                                                                            dialog.dismiss();
                                                                            onRestart();
                                                                        } else {
                                                                            Log.d("user", "query logic in deleting messages");
                                                                        }
                                                                    } else {
                                                                        Log.d("user", "no message retrived");
                                                                    }
                                                                }
                                                            });
                                                        } else {
                                                            Log.d("user", "query logic error");
                                                        }
                                                    } else {
                                                        Log.d("user", "error in getting user");
                                                    }
                                                }
                                            });


                                        }
                                    });


                                    ParseQuery<ParseUser> user = ParseUser.getQuery();
                                    user.whereEqualTo("username", details[0]);
                                    user.findInBackground(new FindCallback<ParseUser>() {
                                        @Override
                                        public void done(List<ParseUser> objects, ParseException e) {
                                            if (e == null) {
                                                if (objects.size() != 0) {
                                                    ParseQuery<ParseObject> getMessageQuery = ParseQuery.getQuery(MessageTable.TABLE_NAME);
                                                    getMessageQuery.whereEqualTo(MessageTable.FROM_USER_REF, ParseUser.getCurrentUser());
                                                    getMessageQuery.whereEqualTo(MessageTable.TO_USER_REF, objects.get(0));
                                                    getMessageQuery.whereEqualTo(MessageTable.INSTITUTION, ParseObject.createWithoutData(InstitutionTable.TABLE_NAME, institution_code));
                                                    final long milliseconds = calendar.getTimeInMillis();
                                                    Log.d("user", String.valueOf(milliseconds));
                                                    getMessageQuery.whereEqualTo(MessageTable.SENT_AT, milliseconds);
                                                    getMessageQuery.findInBackground(new FindCallback<ParseObject>() {
                                                        @Override
                                                        public void done(List<ParseObject> objects, ParseException e) {
                                                            if (e == null) {
                                                                if (objects.size() != 0) {

                                                                    message.setText(objects.get(0).getString(MessageTable.MESSAGE_CONTENT));
                                                                } else {
                                                                    Log.d("user", "query logic in getting messages");
                                                                }
                                                            } else {
                                                                Log.d("user", "no message retrived");
                                                            }
                                                        }
                                                    });
                                                } else {
                                                    Log.d("user", "query logic error");
                                                }
                                            } else {
                                                Log.d("user", "error in getting user");
                                            }
                                        }
                                    });

                                    dialog.show();


                                }
                            });

                        } else {
                            Toast.makeText(view_messages.this, "No messsages", Toast.LENGTH_LONG).show();
                        }

                    } else {
                        Toast.makeText(view_messages.this, "error", Toast.LENGTH_LONG).show();
                        Log.d("user", "Error: " + e.getMessage());
                    }
                }
            });
        }
    }


    protected void sendReply(final String sender)
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
                    ParseQuery<ParseUser> reply_to_query=ParseUser.getQuery();
                    reply_to_query.whereEqualTo("username",sender);
                    reply_to_query.findInBackground(new FindCallback<ParseUser>() {
                        @Override
                        public void done(List<ParseUser> objects, ParseException e) {
                            if(e==null)
                            {
                                ParseObject new_message=new ParseObject(MessageTable.TABLE_NAME);
                                new_message.put(MessageTable.FROM_USER_REF,ParseUser.getCurrentUser());
                                new_message.put(MessageTable.TO_USER_REF,objects.get(0));
                                new_message.put(MessageTable.MESSAGE_CONTENT,reply_message.getText().toString());
                                new_message.put(MessageTable.DELETED_BY_SENDER,false);
                                new_message.put(MessageTable.DELETED_BY_RECEIVER,false);
                                new_message.put(MessageTable.INSTITUTION,ParseObject.createWithoutData(InstitutionTable.TABLE_NAME,institution_code));

                                java.util.Calendar calendar= Calendar.getInstance();
                                SimpleDateFormat format=new SimpleDateFormat("dd/MM/yyyy hh:mm:ss aa");
                                String date= format.format(new Date(calendar.getTimeInMillis()));
                                Date d=null;
                                try {
                                    d=format.parse(date);
                                } catch (java.text.ParseException e1) {
                                    e1.printStackTrace();
                                }

                                new_message.put(MessageTable.SENT_AT,d.getTime());
                                new_message.saveEventually();
                                Toast.makeText(view_messages.this, "Reply Sent", Toast.LENGTH_LONG).show();
                                send_reply.dismiss();
                            }else
                            {
                                Log.d("user","sender not found");
                            }
                        }
                    });
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

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if(firebaseAuth.getCurrentUser()==null)
        {
            Intent nouser=new Intent(view_messages.this,LoginActivity.class);
            startActivity(nouser);
        }
    }
*/
}
