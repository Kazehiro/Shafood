package com.example.root.shafood;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
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
import java.util.Map;

public class Kurir_Main_MAIN extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private TextView nama, tvNotif, jumlahNarik;
    private ImageView check;
    private RelativeLayout buka_maps;
    private LinearLayout mulai;
    private long backPressedTime;
    private Toast backToast;
    private ImageView fotoHistory;
    private Button btn_buka_maps, kerja, btn_buka_history, btn_account,btnGo;
    private ImageView foto, icon_maps, icon_history, icon_account;
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef,myRef1, myRef2;
    private String userID, namaProfile;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private String status = "true";
    private static final int MY_PERMISSION_REQUEST_CODE = 7171;
    private static final int PLAY_SERVICES_RES_REQUEST = 7172;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private FloatingActionButton fab_Logout;
    private ListView listViewBelumTerkirim;
    private String NamaPenerima, NamaPengirim, IdPenerima , NamaDonaturPopup, pesen;
    private Dialog buka;
    private TextView etNamaPenerima, etNamaPengirim, etAlamatPenerima, etNamaDonatur,etPesan;
    private static int UPDATE_INTERVAL = 2000;
    private static int FASTEST_INTERVAL = 2000;
    private static int DISTANCE = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kurir_main_main);
        buka = new Dialog(this);
        nama = (TextView) findViewById(R.id.nama);
        check = (ImageView) findViewById(R.id.check);
        mulai = (LinearLayout) findViewById(R.id.mulai);
        foto = (ImageView) findViewById(R.id.foto);
        btn_buka_maps = (Button) findViewById(R.id.btn_buka_maps);
        icon_maps = (ImageView) findViewById(R.id.icon_maps);
        fab_Logout = (FloatingActionButton) findViewById(R.id.fab_Logout);
        kerja = (Button) findViewById(R.id.kerja);
        listViewBelumTerkirim = (ListView) findViewById(R.id.listViewBelumTerkirim);
        btn_buka_history = (Button) findViewById(R.id.btn_buka_history);
        btn_account = (Button) findViewById(R.id.btn_account);
        icon_account = (ImageView) findViewById(R.id.icon_account);
        icon_history = (ImageView) findViewById(R.id.icon_history);
        jumlahNarik = (TextView) findViewById(R.id.jumlahNarik);
        

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        myRef2 = mFirebaseDatabase.getReference().child("SHAFOOD").child("USER").child("KURIR");
        myRef1 = mFirebaseDatabase.getReference().child("SHAFOOD").child("TRANSAKSI");
        FirebaseUser user = mAuth.getCurrentUser();
        userID = user.getUid();
        System.out.println("INI UID ======== " + userID);
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl("gs://shafood93.appspot.com");


        myRef1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                showData1((Map<String, Object>) dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        
        btn_buka_maps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mIntent = new Intent(Kurir_Main_MAIN.this, Kurir_Main.class);
                startActivity(mIntent);
            }
        });

        icon_maps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mIntent = new Intent(Kurir_Main_MAIN.this, Kurir_Main.class);
                startActivity(mIntent);
            }
        });

        btn_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mIntent = new Intent(Kurir_Main_MAIN.this, Kurir_Account.class);
                startActivity(mIntent);
            }
        });

        icon_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mIntent = new Intent(Kurir_Main_MAIN.this, Kurir_Account.class);
                startActivity(mIntent);
            }
        });

        btn_buka_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mIntent = new Intent(Kurir_Main_MAIN.this, Kurir_History.class);
                startActivity(mIntent);
            }
        });

        icon_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mIntent = new Intent(Kurir_Main_MAIN.this, Kurir_History.class);
                startActivity(mIntent);
            }
        });

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                showData(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        System.out.println("INI STATUS LOHHHH ==== " + status);

        kerja.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (status.equals("true")) {
                    myRef.child("SHAFOOD").child("USER").child("KURIR").child(userID).child("status").setValue("false");
                } else {
                    myRef.child("SHAFOOD").child("USER").child("KURIR").child(userID).child("status").setValue("true");
                }
            }
        });

        storageRef.child("Kurir/IdentitasKurir/" + userID).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                System.out.println("INI URI LOHHHHH ===== " + uri);
                Glide.with(getApplicationContext()).load(uri).into(foto);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
        buildGoogleApiClient();
        updateLokasi();
        createLocationRequest();

        fab_Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                Intent mIntent = new Intent(Kurir_Main_MAIN.this, MainActivity.class);
                startActivity(mIntent);
            }
        });

    }

    private void showData1(Map<String, Object> dataSnapshot) {
        final ArrayList<String> Id_Donatur = new ArrayList<>();
        for (Map.Entry<String, Object> entry : dataSnapshot.entrySet()) {
            Map id_donatur = (Map) entry.getValue();
            Id_Donatur.add((String) id_donatur.get("id_donatur"));
        }
        final ArrayList<String> Id_Kurir = new ArrayList<>();
        for (Map.Entry<String, Object> entry : dataSnapshot.entrySet()) {
            Map id_kurir = (Map) entry.getValue();
            Id_Kurir.add((String) id_kurir.get("id_kurir"));
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
        final ArrayList<String> Pesan = new ArrayList<>();
        for (Map.Entry<String, Object> entry : dataSnapshot.entrySet()) {
            Map pesan = (Map) entry.getValue();
            Pesan.add((String) pesan.get("pesan"));
        }
        int i = 0;
        final ArrayList<String> listId = new ArrayList<>();
        final ArrayList<String> listIdPenerima = new ArrayList<>();
        final ArrayList<String> listNamaPenerima = new ArrayList<>();
        final ArrayList<String> listNamaPengirim = new ArrayList<>();
        final ArrayList<String> listNamaDonatur = new ArrayList<>();
        final ArrayList<String> listPesan = new ArrayList<>();

        if (Id_Kurir != null) {
            while (Id_Kurir.size() > i) {
                if (Id_Kurir.get(i).equals(userID)) {
                    if (Success.get(i).equals("false")) {
                        listId.add(Id_Transaksi.get(i));
                        listIdPenerima.add(Id_Penerima.get(i));
                        listNamaPengirim.add(Nm_Pengirim.get(i));
                        listNamaDonatur.add(Nm_Donatur.get(i));
                        listNamaPenerima.add(Nm_Penerima.get(i));
                        listPesan.add(Pesan.get(i));
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
                    pesen = listPesan.get(position);
                    ShowPopupHistory(view);
                }
            });
            ArrayAdapter namaUser = new ArrayAdapter(this, android.R.layout.simple_list_item_1, listNamaPenerima);
            listViewBelumTerkirim.setAdapter(namaUser);
        }
    }

    public void ShowPopupHistory(View v) {
        TextView txtclose;
        buka.setContentView(R.layout.kurir_notif);
        fotoHistory = (ImageView) buka.findViewById(R.id.imageViewHistoryFotoPenerima);
        etNamaPenerima = (TextView) buka.findViewById(R.id.editTextHistoryNamaPenerimaPopup);
        etNamaPengirim = (TextView) buka.findViewById(R.id.editTextHistoryNamaPengirim);
        etAlamatPenerima = (TextView) buka.findViewById(R.id.editTextHistoryAlamatPenerimaPopup);
        etNamaDonatur = (TextView) buka.findViewById(R.id.editTextHistoryNamaDonaturPopup);
        etPesan = (TextView) buka.findViewById(R.id.editTextHistoryPesan);
        btnGo = (Button) buka.findViewById(R.id.btnGo);

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

        txtclose =(TextView) buka.findViewById(R.id.txtclose);
        txtclose.setText("X");
        etNamaPenerima.setText("Penerima : " + NamaPenerima.toUpperCase());
        etNamaPengirim.setText("Kurir : " + NamaPengirim.toUpperCase());
        etNamaDonatur.setText("Donatur : "+NamaDonaturPopup.toUpperCase());
        etPesan.setText("Pesan Dari Donatur : "+pesen.toUpperCase());
        etAlamatPenerima.setText("Status : Belum Terkirim");
        txtclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buka.dismiss();
            }
        });
        btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mIntent = new Intent(Kurir_Main_MAIN.this,Kurir_Main.class);
                startActivity(mIntent);
            }
        });
        buka.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        buka.show();
    }

    private void showData(DataSnapshot dataSnapshot) {
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            ProfileKurir uInfo = new ProfileKurir();
            uInfo.setNama(ds.child("USER").child("KURIR").child(userID).getValue(ProfileKurir.class).getNama());
            uInfo.setAlamat(ds.child("USER").child("KURIR").child(userID).getValue(ProfileKurir.class).getAlamat());
            uInfo.setTanggallahir(ds.child("USER").child("KURIR").child(userID).getValue(ProfileKurir.class).getTanggallahir());
            uInfo.setNohp(ds.child("USER").child("KURIR").child(userID).getValue(ProfileKurir.class).getNohp());
            uInfo.setLevel(ds.child("USER").child("KURIR").child(userID).getValue(ProfileKurir.class).getLevel());
            uInfo.setNoplat(ds.child("USER").child("KURIR").child(userID).getValue(ProfileKurir.class).getNoplat());
            uInfo.setStatus(ds.child("USER").child("KURIR").child(userID).getValue(ProfileKurir.class).getStatus());
            uInfo.setJumlah_narik(ds.child("USER").child("KURIR").child(userID).getValue(ProfileKurir.class).getJumlah_narik());

            status = uInfo.getStatus();
            namaProfile = uInfo.getNama();
            int jmlNarik = uInfo.getJumlah_narik();
            nama.setText(namaProfile);
            jumlahNarik.setText("Jumlah Narik : " + jmlNarik);

            System.out.println(namaProfile);
            if (status.equals("true")) {
                check.setImageResource(R.drawable.ic_check_black_24dp);
                kerja.setBackgroundResource(R.color.green);
            } else {
                check.setImageResource(R.drawable.ic_close_black_24dp);
                kerja.setBackgroundResource(R.color.red);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            backToast.cancel();
            super.onBackPressed();
            Intent intent = new Intent(Kurir_Main_MAIN.this, MainActivity.class);
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
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null)
            mGoogleApiClient.connect();
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                showData(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        myRef.removeEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                showData(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServices();
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                showData(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
        super.onStop();
        if (mGoogleApiClient != null)
            mGoogleApiClient.disconnect();
        System.out.println("INI ON STOP");
        myRef.removeEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                showData(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.out.println("INI ON Destroy");
        myRef.removeEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                showData(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
