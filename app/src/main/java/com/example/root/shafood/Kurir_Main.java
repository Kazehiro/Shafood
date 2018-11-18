package com.example.root.shafood;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.PolyUtil;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.journeyapps.barcodescanner.CaptureActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Kurir_Main extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private long backPressedTime;
    private Toast backToast;
    private GoogleMap mMap;
    private String userID;
    private String nama;
    private String noktp;
    private String nohp;
    private String alamat;
    private String tanggallahir;
    private String latitude;
    private String longitude;
    private String transaksi;
    private String request;
    private String verifikasi;
    private int jumlah_narik;

    private String alamat_penerima_lat;
    private String alamat_penerima_lng;
    private String alamat_donatur_lat;
    private String alamat_donatur_lng;
    private String nama_donatur;
    private String nama_kurir;
    private String nama_penerima;
    private String nama_barang;
    private int kuantitas;
    private Double LatPenerima, LngPenerima, LatDonatur, LngDonatur, lat, lng;

    private String QrVerifikasi, email;
    private int level;
    private String Id_Donatur, Id_Penerima;
    String text2Qr;

    /*String API_KEY = "AIzaSyAdXOOHbTv9D2DwmZ2o5M7VbyhLrd8Y8mw";*/
    public static final int PICK_UP = 0;
    public static final int DEST_LOC = 1;
    private static int REQUEST_CODE = 0;
    public LatLng pickUpLatLng = null;
    public LatLng locationLatLng = null;
    private LatLng latlngPenerima, latlngDonatur;
    private static final int LOCATION_REQUEST = 500;
    private FloatingActionButton fab_Scan;

    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef, myRef1, myRef2, myRef3;
    private final int REQUEST_CODE_CAMERA_IDENTITAS = 001;

    ImageButton imageBtnScan;
    private RelativeLayout tempatDonatur, tempatPenerima;
    TextView etTitikAwal, etTitikAkhir;
    private PlaceAutocomplete mPlaceAutocomplete;
    private static final int MY_PERMISSION_REQUEST_CODE = 7171;
    private static final int PLAY_SERVICES_RES_REQUEST = 7172;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;

    private static int UPDATE_INTERVAL = 2000;
    private static int FASTEST_INTERVAL = 2000;
    private static int DISTANCE = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kurir__main);
        System.out.println("Cycle CREATE");

        fab_Scan = (FloatingActionButton) findViewById(R.id.fab_Scan);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference().child("SHAFOOD").child("USER").child("PENERIMA");
        myRef1 = mFirebaseDatabase.getReference().child("SHAFOOD").child("TRANSAKSI");
        myRef3 = mFirebaseDatabase.getReference().child("USER").child("KURIR");
        myRef2 = mFirebaseDatabase.getReference().child("SHAFOOD").child("USER").child("KURIR");
        FirebaseUser user = mAuth.getCurrentUser();
        userID = user.getUid();
        tempatDonatur = (RelativeLayout) findViewById(R.id.tempatDonatur);
        tempatPenerima = (RelativeLayout) findViewById(R.id.tempatPenerima);
        widgetInit();

        myRef1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                showData1((Map<String, Object>) dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        myRef3.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                showData3(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        fab_Scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentIntegrator integrator = new IntentIntegrator(Kurir_Main.this);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                integrator.setPrompt("Verifikasi");
                integrator.setCameraId(0);
                integrator.setBeepEnabled(false);
                integrator.setCaptureActivity(Capture_Scanner.class);
                integrator.setBarcodeImageEnabled(false);
                integrator.initiateScan();
            }
        });


        tempatDonatur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (latlngDonatur != null) {
                    Uri nav = Uri.parse("google.navigation:q=" + latlngDonatur.latitude + "," + latlngDonatur.longitude + "&avoid=tf");
                    Intent mIntent = new Intent(Intent.ACTION_VIEW, nav);
                    mIntent.setPackage("com.google.android.apps.maps");
                    System.out.println("INI NAV Donatur ======== " + nav);
                    startActivity(mIntent);
                } else {
                    Toast.makeText(Kurir_Main.this, "Anda Belum Memiliki Order", Toast.LENGTH_SHORT).show();
                    Snackbar.make(view, "Anda Belum Memiliki Order", 3000);
                }
            }
        });

        tempatPenerima.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (latlngPenerima != null) {
                    Uri nav = Uri.parse("google.navigation:q=" + latlngPenerima.latitude + "," + latlngPenerima.longitude + "&avoid=tf");
                    Intent mIntent = new Intent(Intent.ACTION_VIEW, nav);
                    mIntent.setPackage("com.google.android.apps.maps");
                    System.out.println("INI NAV Penerima ======== " + nav);
                    startActivity(mIntent);
                } else {
                    Toast.makeText(Kurir_Main.this, "Anda Belum Memiliki Order", Toast.LENGTH_SHORT).show();
                    Snackbar.make(view, "Anda Belum Memiliki Order", 3000);
                }
            }
        });


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        /*myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                showData((Map<String, Object>) dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/
        buildGoogleApiClient();
        showMarker(latlngDonatur, latlngPenerima);
        updateLokasi();
        createLocationRequest();
    }

    private void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
        mGoogleApiClient.connect();
    }

    private void updateLokasi() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            //Update to firebase
            String lat = String.valueOf(mLastLocation.getLatitude());
            String lng = String.valueOf(mLastLocation.getLongitude());
            myRef2.child(userID)
                    .child("latitude").setValue(lat);
            myRef2.child(userID)
                    .child("longitude").setValue(lng);
        } else {
            //Toast.makeText(this, "Couldn't get the location",Toast.LENGTH_SHORT).show();
            Log.d("TEST", "Couldn't load location");
        }
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setSmallestDisplacement(DISTANCE);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /*private void showData(Map<String, Object> dataSnapshot) {
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
        while (Lat.size() > i) {
            System.out.println(Lat.get(i) + "," + Lng.get(i));
            Double lat = Double.parseDouble(Lat.get(i));
            Double lng = Double.parseDouble(Lng.get(i));
            LatLng mLatLng = new LatLng(lat, lng);cd

            mMap.addMarker(new MarkerOptions().position(mLatLng).title(Nama.get(i)));
            i++;
        }
    }*/

    public void showData3(DataSnapshot dataSnapshot) {
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            ProfileKurir mUpdate_penerima = new ProfileKurir();
            mUpdate_penerima.setJumlah_narik(ds.child(userID).getValue(ProfileKurir.class).getJumlah_narik());

            jumlah_narik = mUpdate_penerima.getJumlah_narik();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //minta permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_REQUEST);
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
        LatLng indonesia = new LatLng(lat, lng);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(indonesia));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(indonesia, 5));

        //


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
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "You cancelled the scanning", Toast.LENGTH_LONG).show();
                return;
            } else {
                QrVerifikasi = result.getContents();
                System.out.println("Hasil Scan === " + QrVerifikasi);
                System.out.println("Hasil DB === " + text2Qr);
                if (QrVerifikasi.equals(text2Qr)) {
                    Toast.makeText(Kurir_Main.this, "Selesai", Toast.LENGTH_SHORT).show();
                    myRef1.child(text2Qr).child("success").setValue("true");
                    myRef.child(Id_Penerima).child("transaksi").setValue("true");
                    myRef2.child(userID).child("jumlah_narik").setValue(jumlah_narik + 1);
                    myRef2.child(userID).child("narik").setValue("false");
                    Intent mIntent = new Intent(Kurir_Main.this,Kurir_Main_MAIN.class);
                    startActivity(mIntent);
                    return;
                } else if (QrVerifikasi.equals(Id_Penerima)) {
                    myRef1.child(text2Qr).child("success").setValue("true");
                    myRef.child(Id_Penerima).child("transaksi").setValue("true");
                    myRef2.child(userID).child("narik").setValue("false");
                    myRef2.child(userID).child("jumlah_narik").setValue(jumlah_narik + 1);
                    Toast.makeText(Kurir_Main.this, "Selesai", Toast.LENGTH_SHORT).show();
                    Intent mIntent = new Intent(Kurir_Main.this,Kurir_Main_MAIN.class);
                    startActivity(mIntent);
                    return;
                } else {
                    Toast.makeText(Kurir_Main.this, "Kode Tidak Sesuai", Toast.LENGTH_SHORT).show();
                    Intent mIntent = new Intent(Kurir_Main.this,Kurir_Main_MAIN.class);
                    startActivity(mIntent);
                    return;
                }
            }
        }
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

    private void showData1(Map<String, Object> dataSnapshot) {
        ArrayList<String> Nm_Donatur = new ArrayList<>();
        for (Map.Entry<String, Object> entry : dataSnapshot.entrySet()) {
            Map nm_donatur = (Map) entry.getValue();
            Nm_Donatur.add((String) nm_donatur.get("nama_donatur"));
        }
        ArrayList<String> Nm_Kurir = new ArrayList<>();
        for (Map.Entry<String, Object> entry : dataSnapshot.entrySet()) {
            Map nm_kurir = (Map) entry.getValue();
            Nm_Kurir.add((String) nm_kurir.get("nama_kurir"));
        }
        ArrayList<String> Nm_penerima = new ArrayList<>();
        for (Map.Entry<String, Object> entry : dataSnapshot.entrySet()) {
            Map nm_penerima = (Map) entry.getValue();
            Nm_penerima.add((String) nm_penerima.get("nama_penerima"));
        }
        ArrayList<String> Lat_donatur = new ArrayList<>();
        for (Map.Entry<String, Object> entry : dataSnapshot.entrySet()) {
            Map lat_donatur = (Map) entry.getValue();
            Lat_donatur.add((String) lat_donatur.get("alamat_donatur_lat"));
        }
        ArrayList<String> Lng_donatur = new ArrayList<>();
        for (Map.Entry<String, Object> entry : dataSnapshot.entrySet()) {
            Map lng_donatur = (Map) entry.getValue();
            Lng_donatur.add((String) lng_donatur.get("alamat_donatur_lng"));
        }
        ArrayList<String> Lat_Penerima = new ArrayList<>();
        for (Map.Entry<String, Object> entry : dataSnapshot.entrySet()) {
            Map lat_penerima = (Map) entry.getValue();
            Lat_Penerima.add((String) lat_penerima.get("alamat_penerima_lat"));
        }
        ArrayList<String> Lng_Penerima = new ArrayList<>();
        for (Map.Entry<String, Object> entry : dataSnapshot.entrySet()) {
            Map lng_penerima = (Map) entry.getValue();
            Lng_Penerima.add((String) lng_penerima.get("alamat_penerima_lng"));
        }
        ArrayList<Long> Kuantitas = new ArrayList<>();
        for (Map.Entry<String, Object> entry : dataSnapshot.entrySet()) {
            Map kuantitas = (Map) entry.getValue();
            Kuantitas.add((Long) kuantitas.get("kuantitas"));
        }
        ArrayList<String> Barang = new ArrayList<>();
        for (Map.Entry<String, Object> entry : dataSnapshot.entrySet()) {
            Map barang = (Map) entry.getValue();
            Barang.add((String) barang.get("nama_barang"));
        }
        ArrayList<String> Id_donatur = new ArrayList<>();
        for (Map.Entry<String, Object> entry : dataSnapshot.entrySet()) {
            Map id_donatur = (Map) entry.getValue();
            Id_donatur.add((String) id_donatur.get("id_donatur"));
        }
        ArrayList<String> Id_penerima = new ArrayList<>();
        for (Map.Entry<String, Object> entry : dataSnapshot.entrySet()) {
            Map id_penerima = (Map) entry.getValue();
            Id_penerima.add((String) id_penerima.get("id_penerima"));
        }
        ArrayList<String> Id_transaksi = new ArrayList<>();
        for (Map.Entry<String, Object> entry : dataSnapshot.entrySet()) {
            Map id_transaksi = (Map) entry.getValue();
            Id_transaksi.add((String) id_transaksi.get("id_transaksi"));
        }
        ArrayList<String> Id_kurir = new ArrayList<>();
        for (Map.Entry<String, Object> entry : dataSnapshot.entrySet()) {
            Map id_kurir = (Map) entry.getValue();
            Id_kurir.add((String) id_kurir.get("id_kurir"));
        }
        ArrayList<String> Success = new ArrayList<>();
        for (Map.Entry<String, Object> entry : dataSnapshot.entrySet()) {
            Map success = (Map) entry.getValue();
            Success.add((String) success.get("success"));
        }
        System.out.println(Id_donatur + " | " + Success);
        int i = 0;
        ArrayList<String> id = new ArrayList<>();
        FirebaseUser user = mAuth.getCurrentUser();
        String userID = user.getUid();
        if (Id_transaksi.get(i) != null) {
            while (Id_transaksi.size() > i) {
                if (userID.equals(Id_kurir.get(i))) {
                    if (Success.get(i).equals("false")) {
                        text2Qr = Id_transaksi.get(i);
                        Id_Donatur = Id_donatur.get(i);
                        Id_Penerima = Id_penerima.get(i);
                        alamat_donatur_lat = Lat_donatur.get(i);
                        alamat_donatur_lng = Lng_donatur.get(i);
                        alamat_penerima_lat = Lat_Penerima.get(i);
                        alamat_penerima_lng = Lng_Penerima.get(i);
                        kuantitas = Integer.parseInt(String.valueOf(Kuantitas.get(i)));
                        nama_barang = Barang.get(i);
                        nama_donatur = Nm_Donatur.get(i);
                        nama_kurir = Nm_Kurir.get(i);
                        nama_penerima = Nm_penerima.get(i);
                    }
                }
                i++;
            }
        }
        try {
            LatPenerima = Double.parseDouble(alamat_penerima_lat);
            LngPenerima = Double.parseDouble(alamat_penerima_lng);
            LatDonatur = Double.parseDouble(alamat_donatur_lat);
            LngDonatur = Double.parseDouble(alamat_donatur_lng);
            latlngDonatur = new LatLng(LatDonatur, LngDonatur);
            latlngPenerima = new LatLng(LatPenerima, LngPenerima);
            System.out.println("HAI DADANGGGGGG ======= " + latlngDonatur + " | " + latlngPenerima + " | " + nama_donatur + " | " + nama_penerima);
            showMarker(latlngDonatur, latlngPenerima);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }

    /*private void showData2(DataSnapshot dataSnapshot) {
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            Update_Penerima mUpdate_penerima = new Update_Penerima();
            mUpdate_penerima.setAlamat(ds.child(Id_Penerima).getValue(Update_Penerima.class).getAlamat());
            mUpdate_penerima.setLatitude(ds.child(Id_Penerima).getValue(Update_Penerima.class).getLatitude());
            mUpdate_penerima.setLongitude(ds.child(Id_Penerima).getValue(Update_Penerima.class).getLongitude());
            mUpdate_penerima.setNama(ds.child(Id_Penerima).getValue(Update_Penerima.class).getNama());
            mUpdate_penerima.setNohp(ds.child(Id_Penerima).getValue(Update_Penerima.class).getNohp());
            mUpdate_penerima.setNoktp(ds.child(Id_Penerima).getValue(Update_Penerima.class).getNoktp());
            mUpdate_penerima.setTanggallahir(ds.child(Id_Penerima).getValue(Update_Penerima.class).getTanggallahir());
            mUpdate_penerima.setVerifikasi(ds.child(Id_Penerima).getValue(Update_Penerima.class).getVerifikasi());
            mUpdate_penerima.setRequest(ds.child(Id_Penerima).getValue(Update_Penerima.class).getRequest());

            alamat = mUpdate_penerima.getAlamat();
            latitude = mUpdate_penerima.getLatitude();
            longitude = mUpdate_penerima.getLongitude();
            nama = mUpdate_penerima.getNama();
            nohp = mUpdate_penerima.getNohp();
            noktp = mUpdate_penerima.getNoktp();
            tanggallahir = mUpdate_penerima.getTanggallahir();
            verifikasi = mUpdate_penerima.getVerifikasi();
            request = mUpdate_penerima.getRequest();
        }
    }*/

    public LatLng showMarker(LatLng latlngDonatur, LatLng latlngPenerima) {
        try {
            System.out.println("MANTABSSSSSSSSS ======= " + latlngDonatur + " | " + latlngPenerima + " | ");
            mMap.addMarker(new MarkerOptions().position(latlngPenerima).title("Penerima").snippet(nama_penerima));
            mMap.addMarker(new MarkerOptions().position(latlngDonatur).title("Donatur").snippet(nama_donatur));
        } catch (IllegalArgumentException | NullPointerException e) {
            e.printStackTrace();
        }
        return latlngDonatur;
    }

    @Override
    protected void onStart() {
        super.onStart();
        showMarker(latlngDonatur, latlngPenerima);
        if (mGoogleApiClient != null)
            mGoogleApiClient.connect();
        System.out.println("Cycle Start");
    }


    @Override
    protected void onResume() {
        super.onResume();
        showMarker(latlngDonatur, latlngPenerima);
        checkPlayServices();
        System.out.println("Cycle RESUME");
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RES_REQUEST).show();
            } else {
                Toast.makeText(this, "This Device is not supported", Toast.LENGTH_SHORT).show();
                finish();
            }
            return false;
        }
        return true;
    }

    @Override
    protected void onStop() {
        if (mGoogleApiClient != null)
            mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        updateLokasi();
        startLocationUpdates();

    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        updateLokasi();
    }
}
