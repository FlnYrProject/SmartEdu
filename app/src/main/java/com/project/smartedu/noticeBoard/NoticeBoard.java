package com.project.smartedu.noticeBoard;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.project.smartedu.R;

import java.util.ArrayList;

public class NoticeBoard extends Fragment {


    ArrayList<String> noticeBoardList;
    ListView noticeList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_notification_bar, container, false);
       noticeList=(ListView) layout.findViewById(R.id.notice_board_list);
        return layout;
    }

    public void setList(ArrayList<String> noticeBoardList)
    {
        this.noticeBoardList=noticeBoardList;
    }
}
