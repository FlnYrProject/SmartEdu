package com.project.smartedu;

import android.widget.CheckBox;
import android.widget.TextView;

/**
 * Created by Shubham Bhasin on 26-Mar-17.
 */

public class ModelViewHolder {

    private CheckBox checkBox;
    private TextView textView;

    public ModelViewHolder()
    {
    }

    public ModelViewHolder(TextView textView, CheckBox checkBox)
    {
        this.checkBox = checkBox;
        this.textView = textView;
    }

    public CheckBox getCheckBox()
    {
        return checkBox;
    }

    public void setCheckBox(CheckBox checkBox)
    {
        this.checkBox = checkBox;
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
