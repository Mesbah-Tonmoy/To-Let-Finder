package com.example.to_letfinder;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class Post_Detail extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private CircleImageView PP;
    private TextView name, userPhoneNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post__detail);

        Intent intent = getIntent();
        String imageUrl = intent.getStringExtra("Image");
        String address = intent.getStringExtra("Address");
        String rent = intent.getStringExtra("Rent");
        String userNmae = intent.getStringExtra("UserName");
        String phoneNo = intent.getStringExtra("PhoneNo");
        String userImage = intent.getStringExtra("UserImage");
        String date = intent.getStringExtra("Deadline");

        ImageView owner_image = findViewById(R.id.owner_pic);
        TextView owner_name = findViewById(R.id.owner_name);
        TextView owner_phone = findViewById(R.id.owner_phone);

        ImageView imageView = findViewById(R.id.imageView);
        TextView address1 = findViewById(R.id.textView);
        TextView rent1 = findViewById(R.id.textView2);
        TextView date1 = findViewById(R.id.textView3);

        Picasso.with(this).load(imageUrl).fit().centerInside().into(imageView);
        address1.setText(address);
        rent1.setText(rent);
        date1.setText(date);
        owner_name.setText(userNmae);
        owner_phone.setText(phoneNo);
        Picasso.with(getApplicationContext()).load(userImage).fit().centerInside().into(owner_image);

        //navigation drawer code::::::::::::::::::::::::::::::
        android.support.v7.widget.Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
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
                Toast.makeText(Post_Detail.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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
            startActivity(new Intent(this, MapsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_pro_update) {
            startActivity(new Intent(this, newProfile.class));
        }
        else if (id == R.id.nav_adpost) {
            Intent intent = new Intent(this, AdPost.class);
            startActivity(intent);

        }else if (id == R.id.nav_my_post) {
            startActivity(new Intent(this, MyPosts.class));

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
