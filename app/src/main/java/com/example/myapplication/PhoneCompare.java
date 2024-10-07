package com.example.myapplication;

import java.util.Comparator;

public class PhoneCompare implements Comparator<Contact> {
    @Override
    public int compare(Contact o1, Contact o2) {
        return (Character.compare(o1.getName().toLowerCase().charAt(0), o2.getName().toLowerCase().charAt(0)));
    }
}
