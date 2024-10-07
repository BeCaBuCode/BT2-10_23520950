package com.example.myapplication;
import android.annotation.SuppressLint;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MainActivity extends AppCompatActivity {
    private ArrayList<Contact> contactList= new ArrayList<>();
    ListView lv;
    Button b;
    private AdapterPhone myAdapter;
    private Handler handler = new Handler(Looper.getMainLooper());
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private int numList;
    int selectedindex;
    String nameFromIntent;
    String numberFromIntent;
    String nameupdate;
    String phoneupdate;
    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_CONTACTS}, 1);
        } else {
            getContacts();
        }
        lv=findViewById(R.id.lv);
        myAdapter=new AdapterPhone(MainActivity.this, R.layout.phone_num_layout,contactList);
        lv.setAdapter(myAdapter);
        androidx.appcompat.widget.Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("DANH SACH SO DIEN THOAI");
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#4CAF50"));
        myToolbar.setBackgroundDrawable(colorDrawable);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedindex=position;
                Intent r=new Intent(MainActivity.this, ContactInfo.class);
                r.putExtra("phonename",contactList.get(position).getName());
                r.putExtra("phonenumber",contactList.get(position).getPhonenumber());
                for (Contact contact:contactList){
                    contact.setSelected(false);
                }
                startActivityForResult(r,100);
            }
        });
        b=findViewById(R.id.button2);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent n=new Intent(MainActivity.this, AddContact.class);
                startActivityForResult(n,99);
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        myAdapter.setDisplay(false);
        if (requestCode==100 && resultCode==RESULT_OK){
            contactList.get(selectedindex).setSelected(true);
            deleteSelectedContacts();
        }
        if (requestCode==99 && resultCode==314){
            nameFromIntent=data.getStringExtra("name");
            numberFromIntent=data.getStringExtra("number");
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_CONTACTS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.WRITE_CONTACTS}, 3);
            }
            else{
                addUser(nameFromIntent,numberFromIntent);
            }
        }
        if (requestCode==100 && resultCode==2608){
            nameupdate=data.getStringExtra("nameupdate");
            phoneupdate =data.getStringExtra("phoneupdate");
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_CONTACTS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.WRITE_CONTACTS}, 4);
            }
            else{
                updateContact(contactList.get(selectedindex).getName(),nameupdate,phoneupdate);
            }
            contactList.get(selectedindex).setName(nameupdate);
            contactList.get(selectedindex).setPhonenumber(phoneupdate);
            myAdapter.notifyDataSetChanged();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                initializeAdapterAsync();
            } else {
                Toast.makeText(MainActivity.this,"Failed",Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode==2){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                deleteSelectedContacts();
            } else {

                Toast.makeText(MainActivity.this,"Failed",Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode==3){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                addUser(nameFromIntent,numberFromIntent);
            }
            else{
                Toast.makeText(MainActivity.this,"Failed",Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode==4){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                updateContact(contactList.get(selectedindex).getName(),nameupdate,phoneupdate);
            }
            else{
                Toast.makeText(MainActivity.this,"Failed",Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void initializeAdapterAsync() {
        Future<?> future = executorService.submit(() ->  {
            handler.post(() -> {
                getContacts();
                myAdapter=new AdapterPhone(MainActivity.this, R.layout.phone_num_layout,contactList);
                if (lv==null){
                    lv=findViewById(R.id.lv);
                }
                lv.setAdapter(myAdapter);

            });
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.options_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==R.id.item1)
        {
            contactList.sort(new Comparator<Contact>() {
                @Override
                public int compare(Contact o1, Contact o2) {
                    return o1.getName().compareToIgnoreCase(o2.getName());
                }
            });
            myAdapter.notifyDataSetChanged();
        }
        else if (item.getItemId()==R.id.item2)
        {
            contactList.sort(new Comparator<Contact>() {
                @Override
                public int compare(Contact o1, Contact o2) {
                    return o2.getName().compareToIgnoreCase(o1.getName());  // Reverse the comparison for descending order
                }
            });
            myAdapter.notifyDataSetChanged();
        }
        else if (item.getItemId()==R.id.item3){
            boolean show=myAdapter.isDisplay();
            myAdapter.setDisplay(!show);
        }
        else{
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_CONTACTS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.WRITE_CONTACTS}, 2);
            } else
            {
                deleteSelectedContacts();
            }
        }
        return super.onOptionsItemSelected(item);
    }
    private void getContacts() {
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            numList=1;
            while (cursor.moveToNext()) {
                String contactId = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.RawContacts._ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
                String phoneNumber="";
                if (cursor.getInt(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor phoneCursor = contentResolver.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{contactId},
                            null);
                    if (phoneCursor != null) {
                        String phoneId = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone._ID));
                        while (phoneCursor.moveToNext()) {
                            phoneNumber = phoneCursor.getString(
                                    phoneCursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            contactList.add(new Contact(phoneId,"#"+numList,name,phoneNumber));
                            numList++;
                        }
                        phoneCursor.close();
                    }
                }
            }
            cursor.close();
        }
    }

    public void deleteSelectedContacts() {
        ContentResolver contentResolver = getContentResolver();

        for (int i=0;i<contactList.size();i++)
        {
            if (contactList.get(i).isSelected())
            {
                String phoneNumber=contactList.get(i).getPhonenumber();
                Uri phoneUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
                String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone.CONTACT_ID};
                String selection = ContactsContract.CommonDataKinds.Phone.NUMBER + " = ?";
                String[] selectionArgs = new String[]{phoneNumber};

                Cursor cursor = contentResolver.query(phoneUri, projection, selection, selectionArgs, null);

                if (cursor != null && cursor.moveToFirst()) {
                    // Get the Contact ID for the provided phone number
                    String contactId = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
                    cursor.close();

                    int phoneNumberCount = getPhoneNumberCount(contactId);

                    if (phoneNumberCount > 1) {
                        deletePhoneNumber(phoneNumber);
                    } else if (phoneNumberCount == 1) {
                        deleteContact(contactId);
                    }
                } else {
                    Toast.makeText(MainActivity.this, "No contact found with phone number: " + phoneNumber,Toast.LENGTH_SHORT).show();
                }
                contactList.remove(i);
                i--;
            }
            myAdapter.notifyDataSetChanged();
        }
    }
    private int getPhoneNumberCount(String contactId) {
        ContentResolver contentResolver = getContentResolver();
        Uri phoneUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone._ID};
        String selection = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?";
        String[] selectionArgs = new String[]{contactId};
        Cursor cursor = contentResolver.query(phoneUri, projection, selection, selectionArgs, null);
        int count = 0;
        if (cursor != null) {
            count = cursor.getCount();
            cursor.close();
        }
        return count;
    }
    private void deletePhoneNumber(String phoneNumber) {
        ContentResolver contentResolver = getContentResolver();
        Uri uri = ContactsContract.Data.CONTENT_URI;
        String selection = ContactsContract.CommonDataKinds.Phone.NUMBER + " = ?";
        String[] selectionArgs = new String[]{phoneNumber};
        Cursor cursor = contentResolver.query(uri, new String[]{ContactsContract.Data._ID, ContactsContract.RawContacts.CONTACT_ID}, selection, selectionArgs, null);
        if (cursor != null && cursor.moveToFirst()) {
            String dataId = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Data._ID));
            String contactId = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.RawContacts.CONTACT_ID));
            Uri deleteUri = Uri.withAppendedPath(ContactsContract.Data.CONTENT_URI, dataId);
            int rowsDeleted = contentResolver.delete(deleteUri, null, null);
            cursor.close();
        } else {
            Log.d("MainActivity", "No contact found with phone number: " + phoneNumber);
        }
    }
    private void deleteContact(String contactId) {
        ContentResolver contentResolver = getContentResolver();
        Uri deleteUri = Uri.withAppendedPath(ContactsContract.RawContacts.CONTENT_URI, contactId);
        int rowsDeleted = contentResolver.delete(deleteUri, null, null);
    }


    private void addUser(String contactName, String newPhoneNumber) {
        ContentResolver contentResolver = getContentResolver();

        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        String[] projection = new String[]{ContactsContract.Contacts._ID};
        String selection = ContactsContract.Contacts.DISPLAY_NAME + " = ?";
        String[] selectionArgs = new String[]{contactName};

        Cursor cursor = contentResolver.query(uri, projection, selection, selectionArgs, null);

        if (cursor != null && cursor.moveToFirst()) {
            String contactId = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
            cursor.close();
            addPhoneNumberToExistingContact(contactId, newPhoneNumber);
        } else {
            cursor.close();
            addNewContact(contactName, newPhoneNumber);
        }
        contactList.clear();
        getContacts();
        myAdapter.notifyDataSetChanged();
    }

    private void addPhoneNumberToExistingContact(String contactId, String phoneNumber) {
        ContentResolver contentResolver = getContentResolver();
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValue(ContactsContract.Data.RAW_CONTACT_ID, contactId)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phoneNumber)
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                .build());
        try {
            contentResolver.applyBatch(ContactsContract.AUTHORITY, ops);
        } catch (Exception e) {
            Toast.makeText(MainActivity.this, "Failed to add phone number to existing contact.",Toast.LENGTH_SHORT).show();
        }
    }

    private void addNewContact(String contactName, String phoneNumber) {
        ContentResolver contentResolver = getContentResolver();
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build());

        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, contactName)
                .build());

        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phoneNumber)
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                .build());

        try {
            contentResolver.applyBatch(ContactsContract.AUTHORITY, ops);
        } catch (Exception e) {
            Toast.makeText(MainActivity.this, "Failed to add new contact.",Toast.LENGTH_SHORT).show();
        }
    }

    private void updateContact(String currentName, String nameUpdate, String phoneUpdate) {
        ContentResolver contentResolver = getContentResolver();

        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        String[] projection = new String[]{ContactsContract.Contacts._ID};
        String selection = ContactsContract.Contacts.DISPLAY_NAME + " = ?";
        String[] selectionArgs = new String[]{currentName};
        Cursor cursor = contentResolver.query(uri, projection, selection, selectionArgs, null);
        if (cursor != null && cursor.moveToFirst()) {
            String contactId = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
            cursor.close();
            updateContactNameAndPhone(contactId, nameUpdate, phoneUpdate);
        } else {
            Toast.makeText(MainActivity.this, "No contact found with name: " + currentName,Toast.LENGTH_SHORT).show();
            if (cursor != null) {
                cursor.close();
            }
        }
    }
    private void updateContactNameAndPhone(String contactId, String newName, String newPhoneNumber) {
        ContentResolver contentResolver = getContentResolver();
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        if (newName!=contactList.get(selectedindex).getName()) {
            ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                    .withSelection(ContactsContract.Data.CONTACT_ID + " = ? AND " +
                                    ContactsContract.Data.MIMETYPE + " = ?",
                            new String[]{contactId, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE})
                    .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, newName)
                    .build());
        }
        if (newPhoneNumber != contactList.get(selectedindex).getPhonenumber()) {
            ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                    .withSelection(ContactsContract.Data.CONTACT_ID + " = ? AND " +
                                    ContactsContract.Data.MIMETYPE + " = ?",
                            new String[]{contactId, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE})
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, newPhoneNumber)
                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)  // Assuming mobile phone type
                    .build());
        }
        try {
            contentResolver.applyBatch(ContactsContract.AUTHORITY, ops);
        } catch (Exception e) {
            Toast.makeText(MainActivity.this, "Failed",Toast.LENGTH_SHORT).show();
        }
    }

}
