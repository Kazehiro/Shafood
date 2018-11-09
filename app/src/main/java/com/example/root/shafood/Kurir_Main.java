package com.example.root.shafood;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.root.shafood.Responses.Distance;
import com.example.root.shafood.Responses.Duration;
import com.example.root.shafood.Responses.LegsItem;
import com.example.root.shafood.Responses.ResponseRoute;
import com.example.root.shafood.network.*;
import com.example.root.shafood.Responses.*;
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
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.PolyUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Kurir_Main extends FragmentActivity implements OnMapReadyCallback {

    private long backPressedTime;
    private Toast backToast;
    private GoogleMap mMap;
    String API_KEY = "AIzaSyAdXOOHbTv9D2DwmZ2o5M7VbyhLrd8Y8mw";
    public static final int PICK_UP = 0;
    public static final int DEST_LOC = 1;
    private static int REQUEST_CODE = 0;
    public LatLng pickUpLatLng = null;
    public LatLng locationLatLng = null;
    private static final int LOCATION_REQUEST = 500;

    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;

    ImageButton imageBtnScan;

    TextView etTitikAwal, etTitikAkhir;
    private PlaceAutocomplete mPlaceAutocomplete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kurir__main);

        imageBtnScan = (ImageButton) findViewById(R.id.imageBtnScan);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference().child("SHAFOOD").child("USER").child("DONATUR");
        widgetInit();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                showData((Map<String, Object>) dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        imageBtnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(Kurir_Main.this, Kurir_Verifikasi.class);
                startActivity(mIntent);
            }
        });
    }


    private void showData(Map<String, Object> dataSnapshot) {
        ArrayList<String> Lat = new ArrayList<>();
        for (Map.Entry<String, Object> entry : dataSnapshot.entrySet()) {
            Map lat = (Map) entry.getValue();
            Lat.add((String) lat.get("latitude"));
        }
        ArrayList<String> Lng = new ArrayList<>();
        for (Map.Entry<String, Object> entry : dataSnapshot.entrySet()) {
            Map lng = (Map) entry.getValue();
            Lng.add((String) lng.get("longitude"));
        }
        ArrayList<String> Nama = new ArrayList<>();
        for (Map.Entry<String, Object> entry : dataSnapshot.entrySet()) {
            Map nama = (Map) entry.getValue();
            Nama.add((String) nama.get("nama"));
        }
        int i = 0;
        while (Lat.size() > i){
            System.out.println(Lat.get(i)+","+Lng.get(i));
            Double lat = Double.parseDouble(Lat.get(i));
            Double lng = Double.parseDouble(Lng.get(i));
            LatLng mLatLng = new LatLng(lat,lng);

            mMap.addMarker(new MarkerOptions().position(mLatLng).title(Nama.get(i)));
            i++;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //minta permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST);
            return;
        }
        //deklarasi widget
        widgetInit();
        //setting UI MAPS
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setAllGesturesEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        String[] indo = "-6.175 , 106.828333".split(",");
        Double lat = Double.parseDouble(indo[0]);
        Double lng = Double.parseDouble(indo[1]);
        LatLng  indonesia = new LatLng(lat,lng);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(indonesia));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(indonesia,5));

    }

    public void widgetInit() {
        etTitikAkhir = findViewById(R.id.etTitikAkhir);
        etTitikAwal = findViewById(R.id.etTitikAwal);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mMap.setMyLocationEnabled(true);
                }
                break;
        }
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Toast.makeText(this, "Sini Gaes", Toast.LENGTH_SHORT).show();
        // Pastikan Resultnya OK
        if (resultCode == RESULT_OK) {
            //Toast.makeText(this, "Sini Gaes2", Toast.LENGTH_SHORT).show();
            // Tampung Data tempat ke variable
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
                        etTitikAwal.setText(placeAddress);
                        pickUpLatLng = placeData.getLatLng();
                        break;
                    case DEST_LOC:
                        // Set ke widget lokasi tujuan
                        etTitikAkhir.setText(placeAddress);
                        locationLatLng = placeData.getLatLng();
                        break;
                }
                if (pickUpLatLng != null && locationLatLng != null) {
                }

            } else {
                // Data tempat tidak valid
                Toast.makeText(this, "Invalid Place !", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void etTitikAwal(View v) {
        mAuth.signOut();
        Intent mIntent = new Intent(Kurir_Main.this, MainActivity.class);
        startActivity(mIntent);
    }

    public void etTitikAkhir(View v) {
        showPlaceAutoComplete(DEST_LOC);
    }

    @Override
    public void onBackPressed(){
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            backToast.cancel();
            super.onBackPressed();
            Intent intent = new Intent(Kurir_Main.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("EXIT", true);
            startActivity(intent);
        }
        else{
            backToast = Toast.makeText(getBaseContext(), "Tekan Lagi Untuk Keluar", Toast.LENGTH_SHORT);
            backToast.show();
        }
        backPressedTime = System.currentTimeMillis();
    }
}
