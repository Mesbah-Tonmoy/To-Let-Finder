package com.example.to_letfinder;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener, AdapterView.OnItemSelectedListener
        , NavigationView.OnNavigationItemSelectedListener {
    
    private CircleImageView PP;
    private TextView name, userPhoneNo;
    private GoogleMap mMap;
    private GoogleApiClient apiClient;
    private Location location;
    private LocationManager locationManager;
    private LocationRequest locationRequest;
    private com.google.android.gms.location.LocationListener listener;
    private long UPDATE_INTERVAL = 2000;
    private long FASTEST_INTERVAL = 5000;
    private LatLng latLng;
    private boolean isPermission;
    private DatabaseReference mref;
    private Marker marker, currentMarker, marker2;
    private String text, category, flag = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Spinner spinner = findViewById(R.id.spinner3);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.category, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        if (requestSinglePermission()) {
            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);

            apiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API).build();
            locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
            checkLocation();
        }
        mref = FirebaseDatabase.getInstance().getReference("Data").child("Post Detail");
        mref.push().setValue(marker);

        /*Button ok = findViewById(R.id.ok);
        Button family = findViewById(R.id.family);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MapsActivity.this, "Bachelor", Toast.LENGTH_SHORT).show();
                showLocation("Bachelor");
            }
        });
        family.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MapsActivity.this, "Family", Toast.LENGTH_SHORT).show();
                showLocation("Family");
            }
        });*/

        FloatingActionButton fab = findViewById(R.id.mfab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MapsActivity.this, AdPost.class));
            }
        });

        //navigation drawer code::::::::::::::::::::::::::::::
        android.support.v7.widget.Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar2);
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
                Toast.makeText(MapsActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        text = parent.getItemAtPosition(position).toString();
        ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
        ((TextView) parent.getChildAt(0)).setTextSize(20);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private boolean checkLocation() {
        if (!isLocationEnabled()) {
            showAlert();
        }
        return isLocationEnabled();
    }

    private void showAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Enable Location").setMessage("Your Location Setting is Off\nPlease Enable Location to Use This App")
                .setPositiveButton("Location Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);

                    }
                }).setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dialog.show();
    }

    private boolean isLocationEnabled() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private boolean requestSinglePermission() {
        Dexter.withActivity(this).withPermission(Manifest.permission.ACCESS_FINE_LOCATION).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse response) {
                isPermission = true;
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse response) {
                if (response.isPermanentlyDenied()) {
                    isPermission = false;
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

            }
        }).check();
        return isPermission;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (latLng != null) {
            mMap.addMarker(new MarkerOptions().position(latLng).title("Your Current Location")).showInfoWindow();
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14F));
        }
        //googleMap.setOnMarkerClickListener((GoogleMap.OnMarkerClickListener) this);
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        showLocation("All");
    }

    private void showLocation(final String text) {
        mref.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            for (DataSnapshot shot : dataSnapshot.getChildren()) {
                if (shot.child("latitude").getValue() != null && shot.child("longitude")
                        .getValue() != null && shot.child("category").getValue() != null) {
                    String lat = shot.child("latitude").getValue().toString();
                    String lng = shot.child("longitude").getValue().toString();
                    String ctg = shot.child("category").getValue().toString();
                    Double latitude = Double.parseDouble(lat);
                    Double longitude = Double.parseDouble(lng);
                    LatLng toLet = new LatLng(latitude, longitude);
                    if(text != null){
                        switch (text){
                            case "Family":
                                switch (ctg){
                                    case "Family":
                                        MarkerOptions options = new MarkerOptions().position(toLet).title("To-let Here").snippet(ctg);
                                        currentMarker = mMap.addMarker(options);
                                        currentMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
                                        if(marker2 != null){
                                            marker2.remove();
                                        }
                                        marker2 = currentMarker;
                                        break;
                                    case "Bachelor":
                                        mMap.addMarker(new MarkerOptions().position(toLet).title("To-Let Here").snippet(ctg).visible(false))
                                                .setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
                                        break;
                                    default:
                                        mMap.addMarker(new MarkerOptions().position(toLet).title("To-Let Here").snippet(ctg).visible(false))
                                                .setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                                        break;
                                }
                                break;
                            case "Bachelor":
                                switch (ctg){
                                    case "Family":
                                        mMap.addMarker(new MarkerOptions().position(toLet).title("To-Let Here").snippet(ctg).visible(false))
                                                .setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
                                        break;
                                    case "Bachelor":
                                        if(marker2 != null){
                                            marker2.remove();
                                        }
                                        MarkerOptions options = new MarkerOptions().position(toLet).title("To-let Here").snippet(ctg);
                                        currentMarker = mMap.addMarker(options);
                                        currentMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
                                        marker2 = currentMarker;
                                        break;
                                    default:
                                        mMap.addMarker(new MarkerOptions().position(toLet).title("To-Let Here").snippet(ctg).visible(false))
                                                .setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                                        break;
                                }
                                break;
                            default:
                                switch (ctg){
                                    case "Family":
                                        MarkerOptions options = new MarkerOptions().position(toLet).title("To-let Here").snippet(ctg);
                                        currentMarker = mMap.addMarker(options);
                                        currentMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
                                        break;
                                    case "Bachelor":
                                        MarkerOptions options2 = new MarkerOptions().position(toLet).title("To-let Here").snippet(ctg);
                                        currentMarker = mMap.addMarker(options2);
                                        currentMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
                                        break;
                                    default:
                                        MarkerOptions options3 = new MarkerOptions().position(toLet).title("To-let Here").snippet(ctg);
                                        currentMarker = mMap.addMarker(options3);
                                        currentMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                                        break;
                                }
                                break;
                        }
                    }
                    //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(toLet, 14F));

                    mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                        @Override
                        public void onInfoWindowClick(Marker marker2) {
                            Toast.makeText(MapsActivity.this, marker2.getId(), Toast.LENGTH_SHORT).show();
                            LatLng markerPosition = marker2.getPosition();
                            String markerLatitude = String.valueOf(markerPosition.latitude);
                            mref.orderByChild("latitude").equalTo(markerLatitude).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for(DataSnapshot post : dataSnapshot.getChildren()) {
                                        if (post.getValue() != null) {
                                            Data data2 = post.getValue(Data.class);
                                            Intent intent = new Intent(MapsActivity.this, Post_Detail.class);
                                            intent.putExtra("UserName", data2.getUserName());
                                            intent.putExtra("PhoneNo", data2.getPhoneNo());
                                            intent.putExtra("UserImage", data2.getPpUrl());
                                            intent.putExtra("Address", data2.getDaddress());
                                            intent.putExtra("Rent", data2.getDrent());
                                            intent.putExtra("Description", data2.getDdescription());
                                            intent.putExtra("Image", data2.getImageUrl());
                                            intent.putExtra("Deadline", data2.getDeadline());
                                            startActivity(intent);

                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                            }


                        });
                } else {
                    //Toast.makeText(MapsActivity.this, "LatLng Null", Toast.LENGTH_LONG).show();
                }
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    });
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        startLocationUpdates();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        location = LocationServices.FusedLocationApi.getLastLocation(apiClient);
        if (location == null) {
            startLocationUpdates();
        } else {
            Toast.makeText(this, "Location Not Detected", Toast.LENGTH_SHORT).show();
        }
    }

    private void startLocationUpdates() {
        locationRequest = LocationRequest.create().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(apiClient, locationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        String msg = "Updated Location" + Double.toString(location.getLatitude()) + "," + Double.toString(location.getLongitude());
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        latLng = new LatLng(location.getLatitude(), location.getLongitude());
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(apiClient != null){
            apiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(apiClient.isConnected()){
            apiClient.disconnect();
        }
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
