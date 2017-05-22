package com.project.smartedu;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Shubham Bhasin on 22-May-17.
 */






public class MarksListAdapter extends BaseAdapter {

    ArrayList<String> list;
    Context context;
    ArrayList<Integer> marksLt;

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    private class ViewHolder {
        TextView textView1;
        EditText editText1;
        int ref;
    }


    public MarksListAdapter(Context context, ArrayList<String> list,ArrayList<Integer> marks) {
       // super(context, R.layout.list_row_marks, resource);
        // TODO Auto-generated constructor stub
        this.context = context;
        this.list = list;
        this.marksLt=marks;


    }


    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        if(list != null && list.size() != 0){
            return list.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        //ViewHolder holder = null;
        final ViewHolder holder;
        if (convertView == null) {

            holder = new ViewHolder();
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.list_row_marks, null);
            holder.textView1 = (TextView) convertView.findViewById(R.id.textView1);
            holder.editText1 = (EditText) convertView.findViewById(R.id.marks);

            convertView.setTag(holder);

        } else {

            holder = (ViewHolder) convertView.getTag();
        }

        holder.ref = position;

        holder.textView1.setText(list.get(position));
        holder.editText1.setText(String.valueOf(marksLt.get(position)));

        holder.editText1.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub

                if(arg0.toString().equals("") || arg0.toString().equals(null)){

                    marksLt.add(holder.ref,0);
                }else {
                    Log.d("test","here");
                    marksLt.add(holder.ref, Integer.parseInt(arg0.toString()));
                }
                notifyDataSetChanged();
            }
        });

        return convertView;
    }



}
