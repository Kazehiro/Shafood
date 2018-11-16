package com.example.root.shafood;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class Donatur_Main extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GoogleMap.OnMapLongClickListener, OnMapReadyCallback {

    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private long backPressedTime;
    private Toast backToast;
    private String userID;
    private TextView cari;
    private GoogleMap mMap;
    private static int REQUEST_CODE = 0;
    public static final int PICK_UP = 1;

    private static final String TAG = "AddToDatabase";

    TextView namaDonatur;
    TextView emailDonatur;
    TextView etBarang;
    TextView etKuantitas;
    private ImageView imageProfile;
    private Button btnCari;
    public LatLng indonesia;
    private static final int LOCATION_REQUEST = 500;
    private String NamaDonatur, LatDonatur, LngDonatur;
    private NavigationView nav_view;
    ArrayList<LatLng> lispoints;
    public LatLng alamatLatLng = null;
    public Double alamatLatitude, alamatLongitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donatur__main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapDonatur);
        mapFragment.getMapAsync(this);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);

        namaDonatur = (TextView) header.findViewById(R.id.namaDonatur);
        emailDonatur = (TextView) header.findViewById(R.id.emailDonatur);
        etBarang = (TextView) findViewById(R.id.editTextNamaBarang);
        etKuantitas = (TextView) findViewById(R.id.editTextKuantitas);
        imageProfile = (ImageView) header.findViewById(R.id.imageProfile);
        btnCari = (Button) header.findViewById(R.id.buttonKirim);
        cari = (TextView) findViewById(R.id.cari);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        userID = user.getUid();
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                showData(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        lispoints = new ArrayList<>();

        emailDonatur.setText(user.getEmail());
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl("gs://shafood93.appspot.com");


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    toastMessage("Successfully signed in with: " + user.getEmail());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    toastMessage("Successfully signed out.");
                }
                // ...
            }
        };

        final String userID = user.getUid();
        storageRef.child("Donatur/FotoProfil/" + userID).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                System.out.println(uri);
                Glide.with(getApplicationContext()).load(uri).into(imageProfile);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });

        cari.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPlaceAutoComplete(PICK_UP);
            }
        });

    }

    public void showPlaceAutoComplete(int typeLocation) {
        REQUEST_CODE = typeLocation;
        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder().setCountry("ID").build();
        try {
            Intent mIntent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                    .setFilter(typeFilter)
                    .build(this);
            startActivityForResult(mIntent, REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
            Toast.makeText(this, "Layanan Tidak Tersedia", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            //Toast.makeText(this, "Sini Gaes2", Toast.LENGTH_SHORT).show();
            // Tampung Data tempat ke variable
            try {
                Place placeData = PlaceAutocomplete.getPlace(this, data);
                if (placeData.isDataValid()) {
                    // Show in Log Cat
                    Log.d("autoCompletePlace Data", placeData.toString());

                    // Dapatkan Detail
                    String placeAddress = placeData.getAddress().toString();
                    LatLng placeLatLng = placeData.getLatLng();
                    String placeName = placeData.getName().toString();

                    // Cek user milih titik jemput atau titik tujuan
                    switch (REQUEST_CODE) {
                        case PICK_UP:
                            // Set ke widget lokasi asal
                            cari.setText(placeAddress);
                            alamatLatLng = placeData.getLatLng();
                            break;
                    }
                    if (alamatLatLng != null) {
                        onMapLongClick(alamatLatLng);
                        lispoints.add(alamatLatLng);
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(alamatLatLng));
                        CameraUpdateFactory.newLatLng(alamatLatLng);
                        CameraUpdateFactory.newLatLngZoom(alamatLatLng, 16);
                        mMap.addMarker(new MarkerOptions().position(alamatLatLng).title(placeAddress));
                    }

                } else {
                    // Data tempat tidak valid
                    Toast.makeText(this, "Invalid Place !", Toast.LENGTH_SHORT).show();
                }
            }catch (RuntimeException e){
                e.printStackTrace();
            }
        }
    }

    private void showData(DataSnapshot dataSnapshot) {
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            ProfileDonatur uInfo = new ProfileDonatur();
            uInfo.setNama(ds.child("USER").child("DONATUR").child(userID).getValue(ProfileDonatur.class).getNama());
            uInfo.setLatitude(ds.child("USER").child("DONATUR").child(userID).getValue(ProfileDonatur.class).getLatitude());
            uInfo.setLongitude(ds.child("USER").child("DONATUR").child(userID).getValue(ProfileDonatur.class).getLongitude());
            namaDonatur.setText(uInfo.getNama());
            NamaDonatur = uInfo.getNama();
            LatDonatur = uInfo.getLatitude();
            LngDonatur = uInfo.getLongitude();

            alamatLatitude = Double.parseDouble(LatDonatur);
            alamatLongitude = Double.parseDouble(LngDonatur);
            LatLng imah = new LatLng(alamatLatitude,alamatLongitude);
            mMap.addMarker(new MarkerOptions().position(imah).title(NamaDonatur));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(imah));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(imah, 16));
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (backPressedTime + 2000 > System.currentTimeMillis()) {
            backToast.cancel();
            super.onBackPressed();
            Intent intent = new Intent(Donatur_Main.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("EXIT", true);
            startActivity(intent);
        } else {
            backToast = Toast.makeText(getBaseContext(), "Tekan Lagi Untuk Keluar", Toast.LENGTH_SHORT);
            backToast.show();
        }
        backPressedTime = System.currentTimeMillis();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.donatur__main, menu);
        return true;
    }

    public void cari(View v) {
        if (TextUtils.isEmpty(etBarang.getText())) {
            showSnackbar(v, "Jumlah Barang", 3000);
            return;
        }
        if (TextUtils.isEmpty(etKuantitas.getText())) {
            showSnackbar(v, "Masukan Kuantitas", 3000);
            return;
        }
        Intent search = new Intent(Donatur_Main.this, ShowPenerima.class);
        search.putExtra("Barang", etBarang.getText().toString());
        search.putExtra("Kuantitas", etKuantitas.getText().toString());
        search.putExtra("Nama Donatur", NamaDonatur);
        search.putExtra("Latitude Donatur", alamatLatitude.toString());
        search.putExtra("Longitude Donatur", alamatLongitude.toString());
        search.putExtra("Id Donatur", userID);
        startActivity(search);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_account) {
            Intent mIntent = new Intent(Donatur_Main.this, Donatur_Account.class);
            startActivity(mIntent);
        } else if (id == R.id.nav_kirim_barang) {
            Intent mIntent = new Intent(Donatur_Main.this, Donatur_Main.class);
            startActivity(mIntent);
        } else if (id == R.id.nav_history) {
            Intent mIntent = new Intent(Donatur_Main.this, Donatur_History.class);
            startActivity(mIntent);
        } else if (id == R.id.nav_logout) {
            mAuth.signOut();
            Intent mIntent = new Intent(Donatur_Main.this, MainActivity.class);
            startActivity(mIntent);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void showSnackbar(View view, String message, int duration) {
        Snackbar.make(view, message, duration).show();
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        if (lispoints.size() >= 1) {
            mMap.clear();
            lispoints.clear();
        } else {
            lispoints.add(latLng);
            MarkerOptions mMarkerOptions = new MarkerOptions();
            mMarkerOptions.position(latLng);
            mMap.addMarker(new MarkerOptions().position(latLng).title(latLng.toString()));
            alamatLatitude = latLng.latitude;
            alamatLongitude = latLng.longitude;
        }

        Log.d("Latitude = ", alamatLatitude.toString());
        Log.d("Longitude = ", alamatLongitude.toString());
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST);
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setAllGesturesEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        String[] indo = "-6.175 , 106.828333".split(",");
        Double lat = Double.parseDouble(indo[0]);
        Double lng = Double.parseDouble(indo[1]);
        indonesia = new LatLng(lat, lng);
        mMap.setOnMapLongClickListener(this);

    }
}
