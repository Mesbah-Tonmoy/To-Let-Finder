package com.example.to_letfinder;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class verifyPhone extends AppCompatActivity {

    private String verificationId;
    private Button verify;
    private EditText vcode;
    private FirebaseAuth mAuth;
    private ProgressBar probar;

    String phoneNumber;

    FirebaseDatabase db = FirebaseDatabase.getInstance();
    DatabaseReference dbreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_phone);

        phoneNumber = getIntent().getStringExtra("phoneNumber");
        sendVerificationCode(phoneNumber);
        mAuth = FirebaseAuth.getInstance();
        probar = (ProgressBar)findViewById(R.id.probar);

        dbreference = db.getReference("Data").child("User Info");

        verify = (Button)findViewById(R.id.verify);
        vcode = (EditText)findViewById(R.id.vcode);

        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = vcode.getText().toString().trim();

                if(code.isEmpty() || code.length() != 6){
                    vcode.setError("Enter the valid code");
                    vcode.requestFocus();
                    return;
                }
                verifynCode(code);
            }
        });
    }
    private void verifynCode(String code){
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithCredential(credential);
    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            checkUser(phoneNumber);
                        }else{
                            Toast.makeText(verifyPhone.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void checkUser(final String phoneNumber){

        dbreference.orderByChild("phoneNo").equalTo(phoneNumber).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.getValue() != null){
                    //it means user already registered
                    //Add code to show your prompt
                    Intent intent = new Intent(verifyPhone.this, MapsActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }else{
                    //It is new users
                    //write an entry to your user table
                    //writeUserEntryToDB();
                    Toast.makeText(verifyPhone.this, "New User", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(verifyPhone.this, newProfile.class);
                    intent.putExtra("phoneNumber2", phoneNumber);
                    startActivity(intent);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void sendVerificationCode(String phoneNumber){
        //probar.setVisibility(View.VISIBLE);
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallBack
        );
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationId = s;
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if(code != null){
                vcode.setText(code);
                verifynCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(verifyPhone.this, "Verification Failed", Toast.LENGTH_SHORT).show();
            //1906672790
            //1751517406
        }
    };
}
