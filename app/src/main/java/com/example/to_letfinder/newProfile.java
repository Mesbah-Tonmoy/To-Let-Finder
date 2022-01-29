package com.example.to_letfinder;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class newProfile extends AppCompatActivity {

    private ImageView pic;
    private EditText name;
    private Button button, done;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private static int PICK_IMAGE = 123;
    private Uri imagePath;
    private String phoneNumber;
    private ProgressBar progressBar;
    private Context context;
    private String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_profile);

        phoneNumber = getIntent().getStringExtra("phoneNumber2");
        //context = this;

        pic = (ImageView)findViewById(R.id.pic);
        name = (EditText)findViewById(R.id.name);
        button = (Button)findViewById(R.id.button);
        done = (Button)findViewById(R.id.done);
        progressBar = findViewById(R.id.pro_circle);
        storageReference = FirebaseStorage.getInstance().getReference();

        databaseReference = FirebaseDatabase.getInstance().getReference("Data").child("User Info");
        final String myId = FirebaseAuth.getInstance().getUid();

        databaseReference.orderByChild("uid").equalTo(myId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            DataSnapshot postSnapshot = dataSnapshot.child(myId);
                            if(postSnapshot.child("userName").getValue() != null && postSnapshot.child("phoneNo").getValue() != null){
                                String Name = postSnapshot.child("userName").getValue().toString();
                                name.setText(Name);
                                phone = postSnapshot.child("phoneNo").getValue().toString();
                            }
                            if(postSnapshot.child("ppUrl").getValue() != null){
                                String PPurl = postSnapshot.child("ppUrl").getValue().toString();
                                Picasso.with(getApplicationContext()).load(PPurl).fit().centerInside().into(pic);
                            }
                            progressBar.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(newProfile.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(newProfile.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(phoneNumber != null){
                    sendData(phoneNumber);
                }else{
                    phoneNumber = phone;
                    sendData(phoneNumber);
                }
            }
        });
    }

    private void showFileChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null){
            imagePath = data.getData();
            pic.setImageURI(imagePath);
        }
    }

   private void sendData(final String phoneNumber){
        final String Name = name.getText().toString();
        final String id = FirebaseAuth.getInstance().getUid();

        if(Name.isEmpty()){
            name.setError("Enter your name");
            name.requestFocus();
            return;
        }else{

            if(imagePath != null){

            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Uploading...");
            progressDialog.show();

            final StorageReference imageReference = storageReference.child("images").child(FirebaseAuth.getInstance().getUid()).child("Profile pic");
            imageReference.putFile(imagePath).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(newProfile.this, "Inforamtion Uploading Failed!", Toast.LENGTH_SHORT).show();
                }
            }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                public void run() {
                                    progressDialog.dismiss();
                                }
                            }, 1000);
                            imageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    Uri downloadUrl = uri;
                                    UserInfo userInfo = new UserInfo(Name, phoneNumber, downloadUrl.toString(), id);
                                    databaseReference.child(FirebaseAuth.getInstance().getUid()).setValue(userInfo);
                                    Toast.makeText(newProfile.this, "Information Uploaded Successfully!", Toast.LENGTH_SHORT).show();

                                    Intent intent = new Intent(newProfile.this, MapsActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                }
                            });

                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100*taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                    progressDialog.setMessage(((int) progress) + "%" + "Uploaded...");
                }
            });
        }
        else{
                String ppUrl = null;
                UserInfo userinfo = new UserInfo(Name, phoneNumber, ppUrl, id);
                databaseReference.child(FirebaseAuth.getInstance().getUid()).setValue(userinfo);

                Intent intent = new Intent(newProfile.this, MapsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        }
   }
}
