package com.example.root.shafood;

import android.app.Dialog;
import android.app.Notification;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.lang.ref.ReferenceQueue;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by User on 2/8/2017.
 */

public class ShowPenerima extends AppCompatActivity {
    private static final String TAG = "ViewDatabase";

    //add Firebase Database stuff
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    private String userID;
    private TextView etNama,etAlamat,etNoTelepon,etNarasi;

    private ListView mListViewPenerima;
    private ImageView ivProfil;


    private FirebaseStorage storage;
    private StorageReference storageRef;

    //Deklarasi Biodata Penerima
    private String Barang, Kuantitas, NamaDonatur, Id_Donatur, Lat_Donatur, Lng_Donatur, Id_Penerima, Lat_Penerima, Lng_Penerima, Nama_Penerima, Alamat_Penerima, TeleponPenerima, Narasi;

    Dialog myDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_penerima);

        System.out.println("HAHA INI CREATE");

        myDialog = new Dialog(this);

        mListViewPenerima = (ListView) findViewById(R.id.listviewPenerima);

        //declare the database reference object. This is what we use to access the database.
        //NOTE: Unless you are signed in, this will not be useable.
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference().child("SHAFOOD").child("USER").child("PENERIMA");
        FirebaseUser user = mAuth.getCurrentUser();
        userID = user.getUid();

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl("gs://shafood93.appspot.com");

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };


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



    }

    public void ShowPopup(View v) {
        System.out.println(Id_Penerima);
        TextView txtclose;
        Button btnFollow;
        myDialog.setContentView(R.layout.penerma_popup);
        ivProfil = (ImageView) myDialog.findViewById(R.id.imageViewFotoPenerima);
        etNama = (TextView) myDialog.findViewById(R.id.editTextNamaPenerimaPopup);
        etAlamat = (TextView) myDialog.findViewById(R.id.editTextAlamatPenerimaPopup);
        etNoTelepon = (TextView) myDialog.findViewById(R.id.editTextNoTelpPopup);
        etNarasi = (TextView) myDialog.findViewById(R.id.editTextDescPopup);


        storageRef.child("Penerima/FotoProfil/" + Id_Penerima).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                System.out.println(uri);
                Glide.with(getApplicationContext()).load(uri).into(ivProfil);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });

        txtclose =(TextView) myDialog.findViewById(R.id.txtclose);
        txtclose.setText("X");
        btnFollow = (Button) myDialog.findViewById(R.id.btnfollow);
        etNama.setText(Nama_Penerima);
        etAlamat.setText(Alamat_Penerima);
        etNoTelepon.setText(TeleponPenerima);
        etNarasi.setText(Narasi);
        btnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(ShowPenerima.this, ShowKurir.class);
                mIntent.putExtra("Barang", Barang);
                mIntent.putExtra("Kuantitas", Kuantitas);
                mIntent.putExtra("Id Penerima", Id_Penerima);
                mIntent.putExtra("Id Donatur", Id_Donatur);
                mIntent.putExtra("Latitude Penerima", Lat_Penerima);
                mIntent.putExtra("Longitude Penerima", Lng_Penerima);
                mIntent.putExtra("Latitude Donatur", Lat_Donatur);
                mIntent.putExtra("Longitude Donatur", Lng_Donatur);
                mIntent.putExtra("Nama Donatur", NamaDonatur);
                mIntent.putExtra("Nama Penerima", Nama_Penerima);
                startActivity(mIntent);
            }
        });
        txtclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }

    private void showData(Map<String, Object> dataSnapshot) {
        final ArrayList<String> Nama = new ArrayList<>();
        for (Map.Entry<String, Object> entry : dataSnapshot.entrySet()) {
            Map nama = (Map) entry.getValue();
            Nama.add((String) nama.get("nama"));
        }
        ArrayList<String> Id_penerima = new ArrayList<>();
        for (Map.Entry<String, Object> entry : dataSnapshot.entrySet()) {
            Map id_penerima = (Map) entry.getValue();
            Id_penerima.add((String) id_penerima.get("id_user"));
        }
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
        ArrayList<String> Request = new ArrayList<>();
        for (Map.Entry<String, Object> entry : dataSnapshot.entrySet()) {
            Map request = (Map) entry.getValue();
            Request.add((String) request.get("request"));
        }
        ArrayList<String> Transaksi = new ArrayList<>();
        for (Map.Entry<String, Object> entry : dataSnapshot.entrySet()) {
            Map transaksi = (Map) entry.getValue();
            Transaksi.add((String) transaksi.get("transaksi"));
        }
        ArrayList<String> Alamat = new ArrayList<>();
        for (Map.Entry<String, Object> entry : dataSnapshot.entrySet()) {
            Map alamat = (Map) entry.getValue();
            Alamat.add((String) alamat.get("alamat"));
        }
        ArrayList<String> NoTelepon = new ArrayList<>();
        for (Map.Entry<String, Object> entry : dataSnapshot.entrySet()) {
            Map notelepon = (Map) entry.getValue();
            NoTelepon.add((String) notelepon.get("nohp"));
        }
        final ArrayList<String> Desc = new ArrayList<>();
        for (Map.Entry<String, Object> entry : dataSnapshot.entrySet()) {
            Map desc = (Map) entry.getValue();
            Desc.add((String) desc.get("narasi"));
        }
        final ArrayList<String> Verifikasi = new ArrayList<>();
        for (Map.Entry<String, Object> entry : dataSnapshot.entrySet()) {
            Map verifikasi = (Map) entry.getValue();
            Verifikasi.add((String) verifikasi.get("verifikasi"));
        }
        int i = 0;
        final ArrayList<String> listNama = new ArrayList<>();
        final ArrayList<String> listId = new ArrayList<>();
        final ArrayList<String> listLat = new ArrayList<>();
        final ArrayList<String> listLng = new ArrayList<>();
        final ArrayList<String> listalamat = new ArrayList<>();
        final ArrayList<String> listtelepon = new ArrayList<>();
        final ArrayList<String> listNarasi= new ArrayList<>();
        if (Nama != null) {
            while (Nama.size() > i) {
                if (Request.get(i).equals("true")) {
                    if (Transaksi.get(i).equals("false")) {
                        if (Verifikasi.get(i).equals("true")){
                            listNama.add(Nama.get(i));
                            listId.add(Id_penerima.get(i));
                            listLat.add(Lat.get(i));
                            listLng.add(Lng.get(i));
                            listalamat.add(Alamat.get(i));
                            listtelepon.add(NoTelepon.get(i));
                            listNarasi.add(Desc.get(i));
                        }
                    }
                }
                i++;
            }
            i = 0;
            while (Nama.size() > i) {
                if (Request.get(i).equals("false")) {
                    if (Transaksi.get(i).equals("false")) {
                        if (Verifikasi.get(i).equals("true")) {
                            listNama.add(Nama.get(i));
                            listId.add(Id_penerima.get(i));
                            listLat.add(Lat.get(i));
                            listLng.add(Lng.get(i));
                            listalamat.add(Alamat.get(i));
                            listtelepon.add(NoTelepon.get(i));
                            listNarasi.add(Desc.get(i));
                        }
                    }
                }
                i++;
            }
            mListViewPenerima.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Barang = getIntent().getStringExtra("Barang");
                    Kuantitas = getIntent().getStringExtra("Kuantitas");
                    NamaDonatur = getIntent().getStringExtra("Nama Donatur");
                    Id_Donatur = getIntent().getStringExtra("Id Donatur");
                    Lat_Donatur = getIntent().getStringExtra("Latitude Donatur");
                    Lng_Donatur = getIntent().getStringExtra("Longitude Donatur");
                    Id_Penerima = listId.get(position);
                    Lat_Penerima = listLat.get(position);
                    Lng_Penerima = listLng.get(position);
                    Nama_Penerima = listNama.get(position);
                    Alamat_Penerima = listalamat.get(position);
                    TeleponPenerima = listtelepon.get(position);
                    Narasi = listNarasi.get(position);
                    ShowPopup(view);

                }
            });
            ArrayAdapter namaUser = new ArrayAdapter(this, android.R.layout.simple_list_item_1, listNama);
            mListViewPenerima.setAdapter(namaUser);
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);

        System.out.println("HAHA INI START");
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }

        System.out.println("HAHA INI STOP");
    }

    @Override
    protected void onResume() {
        super.onResume();

        System.out.println("HAHA INI RESUME");
    }
    @Override
    public void onPause() {
        super.onPause();
        System.out.println("HAHA INI PAUSE");
    }

    /**
     * customizable toast
     *
     * @param message
     */
    private void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void showLatLng(DataSnapshot dataSnapshot) {
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            ProfileDonatur PDonatur = new ProfileDonatur();
            PDonatur.setLatitude(ds.child("USER").child("DONATUR").child(userID).getValue(ProfileDonatur.class).getLatitude());
            PDonatur.setLongitude(ds.child("USER").child("DONATUR").child(userID).getValue(ProfileDonatur.class).getLongitude());
            System.out.println(PDonatur.getLatitude());
            System.out.println(PDonatur.getLongitude());
        }
    }
}
