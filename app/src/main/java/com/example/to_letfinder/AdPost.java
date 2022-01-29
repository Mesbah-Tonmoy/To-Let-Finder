package com.example.to_letfinder;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
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

import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdPost extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private EditText address, noofrooms, description, rent;
    private Button done, choose, dateChooser, selectLocation;
    private TextView dateView, strLocation, name, userPhoneNo;
    private DatePickerDialog.OnDateSetListener dateSetListener;
    private ImageView imageView;
    private FirebaseDatabase db = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference;
    private FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    private StorageReference storageReference;
    private static int PICK_IMAGE = 123;
    private Uri imagePath;
    private String text, text2, imageURL, userName, date, phoneNo, ppUrl, latitude, longitude;
    private CircleImageView PP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_post);

        address = findViewById(R.id.address);
        noofrooms = findViewById(R.id.noofrooms);
        description = findViewById(R.id.description);
        rent = findViewById(R.id.rent);
        done = findViewById(R.id.done);
        choose = findViewById(R.id.choose);
        dateChooser = findViewById(R.id.date_chooser);
        selectLocation = findViewById(R.id.slctLocation);
        strLocation = findViewById(R.id.str_location);
        dateView = findViewById(R.id.date_view);
        imageView = (ImageView)findViewById(R.id.imgeView);
        storageReference = firebaseStorage.getReference();

        databaseReference = db.getReference("Data").child("Post Detail");


        Spinner spinner = findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.thanas, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new areaSelection());

        Spinner spinner2 = findViewById(R.id.spinner2);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this, R.array.category, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(adapter2);
        spinner2.setOnItemSelectedListener(new categorySelection());

        choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageChooser();
            }
        });

        done.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                sendData();
            }
        });

        selectLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(AdPost.this, SelectLocation.class), 1);
            }
        });

        dateChooser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog = new DatePickerDialog(AdPost.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        dateSetListener, year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });
        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                date = dayOfMonth + "/" + month + "/" + year + "";
                dateView.setText(date);
            }
        };

        //navigation drawer code::::::::::::::::::::::::::::::
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);

        PP = headerView.findViewById(R.id.pp);
        name = headerView.findViewById(R.id.name);
        userPhoneNo = headerView.findViewById(R.id.phoneNo);
        DatabaseReference headerRef = FirebaseDatabase.getInstance().getReference("Data").child("User Info")
                .child(FirebaseAuth.getInstance().getUid());
        headerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String userName = dataSnapshot.child("userName").getValue().toString();
                String phone = dataSnapshot.child("phoneNo").getValue().toString();
                name.setText(userName);
                userPhoneNo.setText(phone);
                if(dataSnapshot.child("ppUrl").getValue() != null){
                    String image = dataSnapshot.child("ppUrl").getValue().toString();
                    Picasso.with(getApplicationContext()).load(image).fit().centerInside().into(PP);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AdPost.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showImageChooser() {
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
            imageView.setImageURI(imagePath);
        }

        if(requestCode == 1 && resultCode == RESULT_OK){
            String location = data.getStringExtra("Address");
            latitude = data.getStringExtra("latitude");
            longitude = data.getStringExtra("longitude");
            strLocation.setText(location);
        }
    }

    class areaSelection implements AdapterView.OnItemSelectedListener{
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            text = parent.getItemAtPosition(position).toString();
            ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    class categorySelection implements AdapterView.OnItemSelectedListener{
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            text2 = parent.getItemAtPosition(position).toString();
            ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
            //Toast.makeText(AdPost.this, text2, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    public void sendData(){

        final String UID = FirebaseAuth.getInstance().getUid();
        final String Darea = text;
        final String category = text2;
        final String Daddress = address.getText().toString();
        final String Dnoofrooms = noofrooms.getText().toString();
        final String Ddescription = description.getText().toString();
        final String Drent = rent.getText().toString();
        final String imageUrl = imageURL;

        DatabaseReference userData = FirebaseDatabase.getInstance().getReference("Data").child("User Info")
                .child(FirebaseAuth.getInstance().getUid());
        userData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userName = dataSnapshot.child("userName").getValue().toString();
                phoneNo = dataSnapshot.child("phoneNo").getValue().toString();
                ppUrl = dataSnapshot.child("ppUrl").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if(!TextUtils.isEmpty(Daddress)&&!TextUtils.isEmpty(Dnoofrooms)&&!TextUtils.isEmpty(Ddescription)&&!TextUtils.isEmpty(Drent)){

            final String id = databaseReference.push().getKey();

            if(imagePath != null){

                final ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setMessage("Uploading...");
                progressDialog.show();

                final StorageReference imageReference = storageReference.child("images").child(FirebaseAuth.getInstance().getUid()).child(id).child("Room_Pic");
                imageReference.putFile(imagePath).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(AdPost.this, "Inforamtion Uploading Failed!", Toast.LENGTH_SHORT).show();
                    }
                })
                        .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        imageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    public void run() {
                                        progressDialog.dismiss();
                                    }
                                }, 1000);

                                Uri downloadUrl = uri;
                                Data data = new Data(UID, Daddress, Dnoofrooms, Ddescription, Drent, Darea, downloadUrl.toString(), userName, phoneNo, ppUrl, date, latitude, longitude, category);
                                databaseReference.child(id).setValue(data);
                                address.setText("");
                                noofrooms.setText("");
                                description.setText("");
                                rent.setText("");
                                Toast.makeText(AdPost.this, "Information Uploaded Successfully!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(AdPost.this, MainActivity.class);
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
                Data data = new Data(UID, Daddress, Dnoofrooms, Ddescription, Drent, Darea, imageUrl, userName, phoneNo, ppUrl, date, latitude, longitude, category);
                databaseReference.child(id).setValue(data);
                address.setText("");
                noofrooms.setText("");
                description.setText("");
                rent.setText("");
                Toast.makeText(this, "Data saved successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            }
        }else{
            Toast.makeText(this, "Please fill up all the fields", Toast.LENGTH_SHORT).show();
        }

        }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, MainActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        startActivity(new Intent(this, newProfile.class));
        int id = item.getItemId();

        if (id == R.id.nav_pro_update) {
            startActivity(new Intent(this, newProfile.class));
            finish();
            // Handle the camera action
        } else if (id == R.id.nav_adpost) {
            Intent intent = new Intent(this, AdPost.class);
            startActivity(intent);
            finish();

        }else if (id == R.id.nav_my_post) {
            startActivity(new Intent(this, MyPosts.class));
            finish();

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
