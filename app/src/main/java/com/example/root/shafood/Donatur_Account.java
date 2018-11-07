package com.example.root.shafood;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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

public class Donatur_Account extends AppCompatActivity {

    private ImageButton ImgBtnBack;
    private long backPressedTime;
    private Toast backToast;
    private ImageView fotoProfile;
    private TextView namaProfile,alamatProfile,noTelp,tanggalLahir;
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    private String userID;
    private FirebaseStorage storage;
    private StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donatur__account);

        ImgBtnBack = findViewById(R.id.imageButton);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        FirebaseUser user = mAuth.getCurrentUser();
        userID = user.getUid();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl("gs://shafood93.appspot.com");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                showData(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        ImgBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(Donatur_Account.this, Donatur_Main.class);
                startActivity(mIntent);
            }
        });


        namaProfile = (TextView) findViewById(R.id.namaProfile);
        alamatProfile = (TextView) findViewById(R.id.alamatProfile);
        noTelp = (TextView) findViewById(R.id.noTelp);
        tanggalLahir = (TextView) findViewById(R.id.tanggalLahir);
        fotoProfile = (ImageView) findViewById(R.id.fotoProfile);
        final String userID = user.getUid();
        storageRef.child("Donatur/FotoProfil/" + userID).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                System.out.println(uri);
                Glide.with(getApplicationContext()).load(uri).into(fotoProfile);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
    }

    private void showData(DataSnapshot dataSnapshot) {
        for(DataSnapshot ds : dataSnapshot.getChildren()){
            ProfileDonatur uInfo = new ProfileDonatur();
            uInfo.setNama(ds.child("USER").child("DONATUR").child(userID).getValue(ProfileDonatur.class).getNama());
            uInfo.setAlamat(ds.child("USER").child("DONATUR").child(userID).getValue(ProfileDonatur.class).getAlamat());
            uInfo.setTanggallahir(ds.child("USER").child("DONATUR").child(userID).getValue(ProfileDonatur.class).getTanggallahir());
            uInfo.setNohp(ds.child("USER").child("DONATUR").child(userID).getValue(ProfileDonatur.class).getNohp());
            uInfo.setLevel(ds.child("USER").child("DONATUR").child(userID).getValue(ProfileDonatur.class).getLevel());
            uInfo.setLongitude(ds.child("USER").child("DONATUR").child(userID).getValue(ProfileDonatur.class).getLongitude());
            uInfo.setLatitude(ds.child("USER").child("DONATUR").child(userID).getValue(ProfileDonatur.class).getLatitude());
            uInfo.setNoktp(ds.child("USER").child("DONATUR").child(userID).getValue(ProfileDonatur.class).getNoktp());

            namaProfile.setText("Nama : "+uInfo.getNama());
            alamatProfile.setText("Alamat : "+uInfo.getAlamat());
            noTelp.setText("Nomor Telepon : "+uInfo.getNohp());
            tanggalLahir.setText("Tanggal Lahir : "+uInfo.getTanggallahir());
        }
    }
}
