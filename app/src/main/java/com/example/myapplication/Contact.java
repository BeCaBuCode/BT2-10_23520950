package com.example.myapplication;

import java.util.Comparator;

public class Contact{
    private String id;
    private String phonenumber;
    private String name;
    private boolean isSelected;
    private String contactId;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

    public Contact(String contactId,String id, String name, String phonenumber) {
        this.contactId=contactId;
        this.id = id;
        this.phonenumber = phonenumber;
        this.name = name;
        this.isSelected=false;
    }

}

