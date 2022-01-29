package com.example.to_letfinder;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyPosts extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,ViewAdapter.OnItemClickListener{
    private RecyclerView mRecycler;
    private ViewAdapter viewAdapter;
    private DatabaseReference mref;
    private List<Data> dataSetList;
    private ProgressBar progressBar;
    private CircleImageView PP;
    private TextView name, phoneNo;

    private Data dataSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_posts);

        progressBar = (ProgressBar)findViewById(R.id.probar_circle);
        mRecycler = findViewById(R.id.recycler2);
        mRecycler.setHasFixedSize(true);
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        dataSetList = new ArrayList<>();
        mref = FirebaseDatabase.getInstance().getReference("Data").child("Post Detail");
        mref.keepSynced(true);
        final String myId = FirebaseAuth.getInstance().getUid();

        mref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                            if(postSnapshot.child("uid").getValue() != null){
                                mref.orderByChild("uid").equalTo(myId).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for(DataSnapshot post : dataSnapshot.getChildren()){
                                            if(post.getValue() != null){
                                                Toast.makeText(MyPosts.this, "My Id " + myId, Toast.LENGTH_SHORT).show();
                                                dataSet = post.getValue(Data.class);
                                                dataSetList.add(dataSet);
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        }
                        viewAdapter = new ViewAdapter(MyPosts.this, dataSetList);
                        mRecycler.setAdapter(viewAdapter);
                        progressBar.setVisibility(View.INVISIBLE);
                        viewAdapter.setOnItemClickListener(MyPosts.this);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(MyPosts.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });



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
        phoneNo = headerView.findViewById(R.id.phoneNo);
        DatabaseReference headerRef = FirebaseDatabase.getInstance().getReference("Data").child("User Info")
                .child(FirebaseAuth.getInstance().getUid());
        headerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String userName = dataSnapshot.child("userName").getValue().toString();
                String phone = dataSnapshot.child("phoneNo").getValue().toString();
                name.setText(userName);
                phoneNo.setText(phone);
                if(dataSnapshot.child("ppUrl").getValue() != null){
                    String image = dataSnapshot.child("ppUrl").getValue().toString();
                    Picasso.with(getApplicationContext()).load(image).fit().centerInside().into(PP);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MyPosts.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
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
            startActivity(new Intent(this, MainActivity.class));
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
            finish();
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

    @Override
    public void OnItemClick(int position) {
        Intent intent = new Intent(this, Post_Detail.class);
        Data clickedItem = dataSetList.get(position);
        intent.putExtra("UserName",clickedItem.getUserName());
        intent.putExtra("PhoneNo",clickedItem.getPhoneNo());
        intent.putExtra("UserImage",clickedItem.getPpUrl());
        intent.putExtra("Address", clickedItem.getDaddress());
        intent.putExtra("Rent", clickedItem.getDrent());
        intent.putExtra("Description", clickedItem.getDdescription());
        intent.putExtra("Image", clickedItem.getImageUrl());
        intent.putExtra("Deadline", clickedItem.getDeadline());
        startActivity(intent);
    }
}
