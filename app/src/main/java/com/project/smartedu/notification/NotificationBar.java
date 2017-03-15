package com.project.smartedu.notification;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.project.smartedu.R;


public class NotificationBar extends Fragment {
    public static TextView user;
    public static TextView role;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_notification_bar, container, false);
        user=(TextView)layout.findViewById(R.id.user);
        role=(TextView)layout.findViewById(R.id.role);
        return layout;
    }

    public void setTexts(String username,String userrole,String institution)
    {
        user.setText("Hi,"+username);
        role.setText("(as "+ userrole + " at " + institution +")");
    }
}
