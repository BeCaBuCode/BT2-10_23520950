package com.example.myapplication;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AddContact extends AppCompatActivity {
    EditText e1,e2;
    Button b;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_contact);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        androidx.appcompat.widget.Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar1);
        setSupportActionBar(myToolbar);
        Intent r=getIntent();
        getSupportActionBar().setTitle("THEM THONG TIN");
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#4CAF50"));
        getSupportActionBar().setBackgroundDrawable(colorDrawable);
        e1=findViewById(R.id.edt_Name);
        e2=findViewById(R.id.edt_Number);
        b=findViewById(R.id.btn_Submit);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name=e1.getText().toString();
                String number=e2.getText().toString();
                if (!number.matches("[0-9()+ -]+")){
                    Toast.makeText(AddContact.this,"INVALID PHONE NUMBER",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (name.isEmpty() || number.isEmpty()){
                    Toast.makeText(AddContact.this,"Invalid",Toast.LENGTH_SHORT).show();
                    return;
                }
                r.putExtra("name",name);
                r.putExtra("number",number);
                setResult(314,r);
                finish();
            }
        });
    }
}