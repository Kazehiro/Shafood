package com.example.root.shafood;

import android.Manifest;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
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
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;

public class Donatur_Main extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GoogleMap.OnMapLongClickListener, OnMapReadyCallback ,Dialog.OnDismissListener {

    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef,myRef1,myRef2;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    StorageReference storageReference;
    private long backPressedTime;
    private Toast backToast;
    private String userID;
    private TextView cari,kiriman;
    private GoogleMap mMap;
    private static int REQUEST_CODE = 0;
    public static final int PICK_UP = 1;
    private ListView listViewBelumTerkirim;

    private static final String TAG = "AddToDatabase";

    TextView namaDonatur;
    TextView emailDonatur;
    TextView etBarang;
    TextView etKuantitas;
    private ImageView fotoHistory;
    private TextView etNamaPenerima, etNamaPengirim, etAlamatPenerima, etNamaDonatur, etPesan;
    private Dialog more,myNotif;
    private ImageView imageProfile;
    private Button btnCari;
    public LatLng indonesia;
    private String NamaPenerima, NamaPengirim, IdPenerima , NamaDonaturPopup, Status;
    private static final int LOCATION_REQUEST = 500;
    private String NamaDonatur, LatDonatur, LngDonatur, Ngaran, Waktos;
    private NavigationView nav_view;
    ArrayList<LatLng> lispoints;
    public LatLng alamatLatLng = null;
    public Double alamatLatitude, alamatLongitude;

    //Gambar
    private Button BtnFotoBarang;
    private ImageView ImgViewBarang;
    private Uri filePath1;
    public static final int REQUEST_CODE_CAMERA_BARANG = 0022;
    private String[] items = {"Camera"};
    private final static int REQUEST_CODE_ASK_PERMISSIONS = 1;
    private static final String[] REQUIRED_SDK_PERMISSIONS = new String[] {
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donatur__main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        more = new Dialog(this);
        myNotif = new Dialog(this);
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
        etPesan = (TextView) findViewById(R.id.editTextPesan);
        imageProfile = (ImageView) header.findViewById(R.id.imageProfile);
        btnCari = (Button) header.findViewById(R.id.buttonKirim);
        cari = (TextView) findViewById(R.id.cari);
        listViewBelumTerkirim = (ListView) findViewById(R.id.listViewBelumTerkirim);
        BtnFotoBarang = (Button) findViewById(R.id.BtnFotoBarang);
        ImgViewBarang = (ImageView) findViewById(R.id.ImgViewBarang);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        userID = user.getUid();
        myRef1 = mFirebaseDatabase.getReference().child("SHAFOOD").child("TRANSAKSI");
        myRef2 = mFirebaseDatabase.getReference().child("SHAFOOD").child("NOTIFIKASI");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_CAMERA_BARANG);

        /////////

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

        myRef1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try{
                    showData1((Map<String, Object>) dataSnapshot.getValue());
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        myRef2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try{
                    showData2((Map<String, Object>) dataSnapshot.getValue());
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        BtnFotoBarang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImageBarang();
            }
        });

    }

    public void chooseImageBarang() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Options");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (items[i].equals("Camera")) {
                    EasyImage.openCamera(Donatur_Main.this, REQUEST_CODE_CAMERA_BARANG);
                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showData1(Map<String, Object> dataSnapshot) {
        final ArrayList<String> Id_Donatur = new ArrayList<>();
        for (Map.Entry<String, Object> entry : dataSnapshot.entrySet()) {
            Map id_donatur = (Map) entry.getValue();
            Id_Donatur.add((String) id_donatur.get("id_donatur"));
        }
        final ArrayList<String> Id_Penerima = new ArrayList<>();
        for (Map.Entry<String, Object> entry : dataSnapshot.entrySet()) {
            Map id_penerima = (Map) entry.getValue();
            Id_Penerima.add((String) id_penerima.get("id_penerima"));
        }
        final ArrayList<String> Nm_Penerima = new ArrayList<>();
        for (Map.Entry<String, Object> entry : dataSnapshot.entrySet()) {
            Map nm_penerima = (Map) entry.getValue();
            Nm_Penerima.add((String) nm_penerima.get("nama_penerima"));
        }
        final ArrayList<String> Nm_Pengirim = new ArrayList<>();
        for (Map.Entry<String, Object> entry : dataSnapshot.entrySet()) {
            Map nm_pengirim = (Map) entry.getValue();
            Nm_Pengirim.add((String) nm_pengirim.get("nama_kurir"));
        }
        final ArrayList<String> Success = new ArrayList<>();
        for (Map.Entry<String, Object> entry : dataSnapshot.entrySet()) {
            Map success = (Map) entry.getValue();
            Success.add((String) success.get("success"));
        }
        final ArrayList<String> Id_Transaksi = new ArrayList<>();
        for (Map.Entry<String, Object> entry : dataSnapshot.entrySet()) {
            Map id_transaksi = (Map) entry.getValue();
            Id_Transaksi.add((String) id_transaksi.get("id_transaksi"));
        }
        final ArrayList<String> Nm_Donatur = new ArrayList<>();
        for (Map.Entry<String, Object> entry : dataSnapshot.entrySet()) {
            Map nm_donatur = (Map) entry.getValue();
            Nm_Donatur.add((String) nm_donatur.get("nama_donatur"));
        }
        int i = 0;
        final ArrayList<String> listId = new ArrayList<>();
        final ArrayList<String> listIdPenerima = new ArrayList<>();
        final ArrayList<String> listNamaPenerima = new ArrayList<>();
        final ArrayList<String> listNamaPengirim = new ArrayList<>();
        final ArrayList<String> listNamaDonatur = new ArrayList<>();
        
        if (Id_Donatur != null) {
            while (Id_Donatur.size() > i) {
                if (Id_Donatur.get(i).equals(userID)) {
                    if (Success.get(i).equals("false")) {
                        listId.add(Id_Transaksi.get(i));
                        listIdPenerima.add(Id_Penerima.get(i));
                        listNamaPengirim.add(Nm_Pengirim.get(i));
                        listNamaDonatur.add(Nm_Donatur.get(i));
                        listNamaPenerima.add(Nm_Penerima.get(i));
                    }
                }
                i++;
            }
            listViewBelumTerkirim.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    IdPenerima = listIdPenerima.get(position);
                    NamaPenerima = listNamaPenerima.get(position);
                    NamaPengirim = listNamaPengirim.get(position);
                    NamaDonaturPopup = listNamaDonatur.get(position);
                    ShowPopupHistory(view);
                }
            });
            ArrayAdapter namaUser = new ArrayAdapter(this, android.R.layout.simple_list_item_1, listNamaPenerima);
            listViewBelumTerkirim.setAdapter(namaUser);
        }
    }
    private void showData2(Map<String, Object> dataSnapshot) {

        final ArrayList<String> Nm_Penerima = new ArrayList<>();
        for (Map.Entry<String, Object> entry : dataSnapshot.entrySet()) {
            Map nm_penerima = (Map) entry.getValue();
            Nm_Penerima.add((String) nm_penerima.get("nama_penerima"));
        }
        final ArrayList<String> Show = new ArrayList<>();
        for (Map.Entry<String, Object> entry : dataSnapshot.entrySet()) {
            Map show = (Map) entry.getValue();
            Show.add((String) show.get("show"));
        }
        final ArrayList<String> Waktu = new ArrayList<>();
        for (Map.Entry<String, Object> entry : dataSnapshot.entrySet()) {
            Map waktu = (Map) entry.getValue();
            Waktu.add((String) waktu.get("waktu"));
        }

        int i = 0;
        final ArrayList<String> listNmPenerima = new ArrayList<>();
        final ArrayList<String> listWaktu= new ArrayList<>();

        if (Nm_Penerima != null) {
            while (Nm_Penerima.size() > i) {
                if (Show.get(i).equals("true")) {
                    Ngaran = (Nm_Penerima.get(i));
                    Waktos = (Waktu.get(i));
                    notif(Ngaran,Waktos);
                    showNotification();
                }
                i++;
            }
        }
    }
    public void showNotification(){
        myNotif.setContentView(R.layout.notification_popup);
        TextView txtclose,ty,waktu;
        txtclose = (TextView) myNotif.findViewById(R.id.txtclose);
        txtclose.setText("X");
        ty = (TextView) myNotif.findViewById(R.id.editTextTyPopup);
        waktu  = (TextView) myNotif.findViewById(R.id.editTextWaktuPopup);
        ty.setText("Terimakasih Kiriman Anda Sudah Diterima oleh " + Ngaran);
        waktu.setText("Pada " + Waktos);


        txtclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myNotif.dismiss();
            }
        });

        myNotif.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myNotif.show();
        myNotif.setOnDismissListener(this);
    }

    public void ShowPopupHistory(View v) {
        TextView txtclose;
        more.setContentView(R.layout.donatur_notif);
        fotoHistory = (ImageView) more.findViewById(R.id.imageViewHistoryFotoPenerima);
        etNamaPenerima = (TextView) more.findViewById(R.id.editTextHistoryNamaPenerimaPopup);
        etNamaPengirim = (TextView) more.findViewById(R.id.editTextHistoryNamaPengirim);
        etAlamatPenerima = (TextView) more.findViewById(R.id.editTextHistoryAlamatPenerimaPopup);
        etNamaDonatur = (TextView) more.findViewById(R.id.editTextHistoryNamaDonaturPopup);


        storageRef.child("Penerima/FotoProfil/" + IdPenerima).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                System.out.println(uri);
                Glide.with(getApplicationContext()).load(uri).into(fotoHistory);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });

        txtclose =(TextView) more.findViewById(R.id.txtclose);
        txtclose.setText("X");
        etNamaPenerima.setText("Penerima : " + NamaPenerima);
        etNamaPengirim.setText("Kurir : " + NamaPengirim);
        etNamaDonatur.setText("Donatur : "+NamaDonaturPopup);
        etAlamatPenerima.setText("Status : Belum Terkirim");
        txtclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                more.dismiss();
            }
        });
        more.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        more.show();
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

        EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
            @Override
            public void onImagePicked(File imageFile, EasyImage.ImageSource source, int type) {
                switch (type) {
                    case REQUEST_CODE_CAMERA_BARANG:
                        Glide.with(Donatur_Main.this)
                                .load(imageFile)
                                .centerCrop()
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(ImgViewBarang);
                        filePath1 = Uri.fromFile(imageFile);
                        System.out.println("PATH ============== " + filePath1);
                        System.out.println("PATH ============== " + DiskCacheStrategy.ALL.toString());
                        break;

                }
            }
        });

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
        search.putExtra("Pesan", etPesan.getText().toString());
        search.setData(filePath1);
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
        }else if (id == R.id.nav_Tentang) {
            Intent mIntent = new Intent(Donatur_Main.this, Authors.class);
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
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_REQUEST);
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

    @Override
    public void onDismiss(DialogInterface dialog) {
        myRef2.child(userID).child("show").setValue("false");
    }


    public void notif(String namapenerima, String waktu) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this);

        //Create the intent that’ll fire when the user taps the notification//

        Intent intent = new Intent(Donatur_Main.this, Donatur_Main.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        mBuilder.setContentIntent(pendingIntent);
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher_round);
        mBuilder.setContentTitle("Kiriman Anda Telah Sampai");
        mBuilder.setContentText("Pesanan Anda Diterima Oleh " + namapenerima + " Pada " + waktu);
        mBuilder.setPriority(Notification.PRIORITY_MAX);
        long[] v = {100,200,400,800,400,800};
        mBuilder.setVibrate(v);
        mBuilder.setSound(uri);
        mBuilder.setOnlyAlertOnce(true);
        mBuilder.setAutoCancel(true);
        NotificationManager mNotificationManager =

                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(001, mBuilder.build());

    }
}
