package com.project.smartedu.noticeBoard;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.project.smartedu.Constants;
import com.project.smartedu.R;
import com.project.smartedu.UserPrefs;
import com.project.smartedu.admin.Classes;
import com.project.smartedu.common.Tasks;
import com.project.smartedu.database.Notice;
import com.project.smartedu.parent.ParentUserPrefs;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class NoticeBoard extends Fragment {


    ArrayList<String> noticeBoardList;

    ListView noticeList;
    TextView addnewnoti;
    TextView notice_board_text;



    Button okButton;
    EditText new_notication_text;


    DatabaseReference databaseReference;
    UserPrefs userPrefs;
    ParentUserPrefs parentUserPrefs;

    String institutionName;
    String role;

    FirebaseAuth firebaseAuth;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_notice_board, container, false);
        notice_board_text=(TextView)layout.findViewById(R.id.notice_board_text);

        userPrefs=new UserPrefs(getActivity());
        parentUserPrefs=new ParentUserPrefs(getActivity());
        firebaseAuth=FirebaseAuth.getInstance();

        return layout;
    }

    public void setData(final String institutionName, final String role)
    {

        this.institutionName=institutionName;
        this.role=role;





        if (UserPrefs.noticeLt.size() == 0) {
            notice_board_text.setText("No Notice put up");

        } else {
            notice_board_text.setText("Notice Available");
            blink();
        }



        notice_board_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Dialog notification_dialog = new Dialog(getActivity());
                notification_dialog.setContentView(R.layout.notification_dialog);
                notification_dialog.setTitle("NOTICE BOARD");

                noticeList = (ListView) notification_dialog.findViewById(R.id.notification_list);
                addnewnoti = (TextView) notification_dialog.findViewById(R.id.add_new_notification);
                notification_dialog.show();

                if (UserPrefs.noticeLt.size() != 0) {




                    makeNoticeList();



                }



                addnewnoti.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Dialog new_notification_dialog = new Dialog(getActivity());
                        new_notification_dialog.setContentView(R.layout.enter_notification_dialog);
                        new_notification_dialog.setTitle("NEW NOTICE");
                        new_notification_dialog.show();
                        okButton = (Button) new_notification_dialog.findViewById(R.id.okButton);
                        new_notication_text = (EditText) new_notification_dialog.findViewById(R.id.new_notification_text);

                        okButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String new_notice = new_notication_text.getText().toString();

                                if (new_notice.equalsIgnoreCase("") || new_notice.equalsIgnoreCase(null)) {
                                    Toast.makeText(getActivity(), "Add text to notice", Toast.LENGTH_LONG).show();
                                } else {
                                    java.util.Calendar calendar = Calendar.getInstance();

                                    String time_in_millis = String.valueOf((calendar.getTimeInMillis() / 1000) * 1000);
                                    String bystring = "";

                                    databaseReference = Constants.databaseReference.child(Constants.NOTIFICATION_TABLE).child(institutionName).push();
                                    databaseReference.child("time").setValue(time_in_millis);
                                    databaseReference.child("content").setValue(new_notice);
                                    if (role.equalsIgnoreCase("parent")) {
                                        bystring = userPrefs.getUserName() + ", " + role + " of " + parentUserPrefs.getSelectedChildName() + ", " + parentUserPrefs.getChildClass();


                                    } else {
                                        bystring = userPrefs.getUserName() + ", " + role + " at " + institutionName;

                                    }
                                    databaseReference.child("by").setValue(bystring);
                                    databaseReference.child("user_id").setValue(firebaseAuth.getCurrentUser().getUid());
                                    databaseReference.child("user_name").setValue(userPrefs.getUserName());


                                    Notice notice = new Notice(databaseReference.getKey(), new_notice, time_in_millis, bystring, firebaseAuth.getCurrentUser().getUid(), userPrefs.getUserName());
                                    UserPrefs.noticeLt.add(databaseReference.getKey());
                                    UserPrefs.noticemap.put(databaseReference.getKey(), notice);
                                    new_notification_dialog.dismiss();
                                    makeNoticeList();
                                }
                            }
                        });
                    }
                });


            }

            });




    }




    private void makeNoticeList(){
        ArrayList<String> noticeEntryList = new ArrayList<>();

        for (int x = 0; x < UserPrefs.noticeLt.size(); x++) {

            Notice notice = UserPrefs.noticemap.get(UserPrefs.noticeLt.get(x));
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss aa");
            String dateString = formatter.format(new Date(Long.parseLong(notice.getTime())));
            noticeEntryList.add(notice.getContent() + "\n" + notice.getBystring() + "\n" + dateString);


        }

        ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, noticeEntryList);
        noticeList.setAdapter(adapter);

    }



    private void blink() {
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                final int timeToBlink = 500;    //in milissegunds
                try {
                    Thread.sleep(timeToBlink);
                } catch (Exception e) {
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        if (notice_board_text.getVisibility() == View.VISIBLE) {
                            notice_board_text.setVisibility(View.INVISIBLE);

                        } else {
                            notice_board_text.setVisibility(View.VISIBLE);
                        }
                        blink();
                    }
                });
            }
        }).start();


    }






















    }


