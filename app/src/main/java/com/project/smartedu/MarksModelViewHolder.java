package com.project.smartedu;

import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by Shubham Bhasin on 26-Mar-17.
 */

public class MarksModelViewHolder {

    private TextView editText;
    private TextView textView;

    public MarksModelViewHolder()
    {
    }

    public MarksModelViewHolder(TextView textView,TextView editText)
    {
        this.editText = editText;
        this.textView = textView;
    }

    public TextView getEditText()
    {
        return editText;
    }

    public void setEditText(TextView editText)
    {
        this.editText = editText;
    }

    public TextView getTextView()
    {
        return textView;
    }

    public void setTextView(TextView textView)
    {
        this.textView = textView;
    }
}
