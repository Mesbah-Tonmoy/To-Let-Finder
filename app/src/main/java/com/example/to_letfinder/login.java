package com.example.to_letfinder;

import android.arch.core.executor.TaskExecutor;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

import java.util.concurrent.TimeUnit;

public class login extends AppCompatActivity {

    private Button next;
    private EditText edittext;
    private CountryCodePicker ccp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        next = (Button)findViewById(R.id.next);
        edittext =(EditText)findViewById(R.id.edittext);
        ccp = (CountryCodePicker)findViewById(R.id.ccp);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = edittext.getText().toString().trim();

                if(phone.isEmpty() || phone.length()!=10){
                    edittext.setError("Enter a valid phone number");
                    edittext.requestFocus();
                    return;
                }
                String phoneNumber = '+' + ccp.getSelectedCountryCode() + phone;
                Intent intent = new Intent(login.this, verifyPhone.class);
                intent.putExtra("phoneNumber", phoneNumber);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            Intent intent = new Intent(this, MapsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }
}
