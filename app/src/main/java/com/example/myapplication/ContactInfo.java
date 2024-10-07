package com.example.myapplication;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ContactInfo extends AppCompatActivity {
    EditText t1,t2;
    ImageButton b1,b2;
    Button b3;
    Intent r;
    Boolean haveEdit=false;
    String name,number;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_contact_info);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        t1=findViewById(R.id.text_name);
        t2=findViewById(R.id.text_Number);
        t1.setEnabled(false);
        t2.setEnabled(false);
        r=getIntent();
        name=r.getStringExtra("phonename");
        number=r.getStringExtra("phonenumber");
        t1.setText(name);
        t2.setText(number);
        b1=findViewById(R.id.imageButton3);
        b2=findViewById(R.id.imageButton4);
        b3=findViewById(R.id.button_Submit);
        b3.setEnabled(haveEdit);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callintent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+t2.getText().toString()));
                if (ActivityCompat.checkSelfPermission(ContactInfo.this,
                        android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions(ContactInfo.this, new String[]{android.Manifest.permission.CALL_PHONE},1);
                    return;
                }
                startActivity(callintent);
            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:"+t2.getText().toString()));
                startActivity(intent);
            }
        });
        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!number.matches("[0-9() -]+")){
                    Toast.makeText(ContactInfo.this,"INVALID PHONE NUMBER",Toast.LENGTH_SHORT).show();
                    return;
                }
                r.putExtra("nameupdate",name);
                r.putExtra("phoneupdate",number);
                setResult(2608,r);
                finish();
            }
        });
        t1.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (!t1.isFocused()){
                    t1.setEnabled(false);
                    return true;
                }
                if (actionId == EditorInfo.IME_ACTION_NEXT ||
                        (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    if (!t1.getText().toString().equals(name)){
                        name=t1.getText().toString();
                        if (!haveEdit) haveEdit=true;
                    }

                    b3.setEnabled(haveEdit);
                    t1.setEnabled(false);
                    return true;
                }
                return false;
            }
        });
        t2.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT ||
                        (event!=null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)){
                    if (!t2.getText().toString().equals(number)){
                        if (!haveEdit) haveEdit=true;
                        number=t2.getText().toString();
                    }
                    b3.setEnabled(haveEdit);
                    t2.setEnabled(false);
                    return true;
                }
                return false;
            }
        });
        androidx.appcompat.widget.Toolbar myToolbar=findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("THONG TIN CA NHAN");
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#4CAF50"));
        getSupportActionBar().setBackgroundDrawable(colorDrawable);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=new MenuInflater(ContactInfo.this);
        inflater.inflate(R.menu.option_info,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==R.id.delete){
            r.putExtra("request","delete");
            setResult(RESULT_OK,r);
            finish();
        }
        else{
            t1.setEnabled(true);
            t2.setEnabled(true);
            t1.requestFocus();
        }
        return super.onOptionsItemSelected(item);
    }
}