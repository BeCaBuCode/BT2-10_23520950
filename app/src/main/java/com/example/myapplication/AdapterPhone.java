package com.example.myapplication;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;

public class AdapterPhone extends ArrayAdapter<Contact> {
    Activity content;
    int idLayout;
    ArrayList<Contact> myList;
    private boolean isDisplay;
    public AdapterPhone(Activity content, int idLayout, ArrayList<Contact> myList) {
        super(content, idLayout,myList);
        this.content = content;
        this.idLayout = idLayout;
        this.myList = myList;
        this.isDisplay=false;
    }

    public boolean isDisplay() {
        return isDisplay;
    }

    public void setDisplay(boolean display) {
        isDisplay = display;
        for (int i=0;i<myList.size();i++){
            myList.get(i).setSelected(false);
        }
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater myInflater= content.getLayoutInflater();
        convertView=myInflater.inflate(idLayout,null);
        Contact myContact =myList.get(position);
        TextView t1=convertView.findViewById(R.id.textView);
        t1.setText(myContact.getId());
        TextView t2=convertView.findViewById(R.id.textView2);
        t2.setText(myContact.getName());
        TextView t3=convertView.findViewById(R.id.textView3);
        t3.setText(myContact.getPhonenumber());
        CheckBox b=convertView.findViewById(R.id.checkBox);
        b.setVisibility(isDisplay ? CheckBox.VISIBLE : CheckBox.GONE);
        b.setChecked(myContact.isSelected());
        b.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                getItem(position).setSelected(isChecked);
                notifyDataSetChanged();
            }
        });
        return convertView;
    }
}