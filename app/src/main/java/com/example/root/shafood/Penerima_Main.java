package com.example.root.shafood;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Map;

public class Penerima_Main extends AppCompatActivity {


    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef, myRef1;
    private String userID;
    private Dialog notif;
    private TextView etNama, etAlamat, etNoTelepon, etNarasi, etJarak;
    private ImageView fotoHistory;
    private StorageReference storageRef;
    private FirebaseStorage storage;

    private String nama;
    private String noktp;
    private String nohp;
    private String alamat;
    private String tanggallahir;
    private String latitude;
    private String longitude;
    private String transaksi;
    private String verifikasi;
    private int level;
    private String request;
    private ListView listViewBelumTerkirim;
    private TextView etNamaPenerima, etNamaPengirim, etAlamatPenerima, etNamaDonatur;
    private String NamaPenerima, NamaPengirim, IdPenerima , NamaDonaturPopup, Status;

    private Button BtnQrcode;
    private ImageButton BtnRequest;
    private ImageButton ImgBtnProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_penerima__main);
        notif = new Dialog(this);
        ImgBtnProfile = (ImageButton) findViewById(R.id.imgBtnProfile);

        listViewBelumTerkirim = (ListView) findViewById(R.id.BelumDiterima);
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        myRef1 = mFirebaseDatabase.getReference().child("SHAFOOD").child("TRANSAKSI");
        FirebaseUser user = mAuth.getCurrentUser();
        userID = user.getUid();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl("gs://shafood93.appspot.com");

        BtnQrcode = (Button) findViewById(R.id.BtnQrcode);
        BtnRequest = (ImageButton) findViewById(R.id.imageButtonRequest);

        myRef1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                showData1((Map<String, Object>) dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

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



        ImgBtnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(Penerima_Main.this, Penerima_profile.class);
                startActivity(mIntent);
            }
        });

        BtnRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = mAuth.getCurrentUser();
                String userID = user.getUid();
                myRef.child("SHAFOOD").child("USER").child("PENERIMA").child(userID).child("request").setValue("true");
            }
        });

        BtnQrcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(Penerima_Main.this, Penerima_Verifikasi.class);
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
        int i = 0;
        final ArrayList<String> listId = new ArrayList<>();
        final ArrayList<String> listIdPenerima = new ArrayList<>();
        final ArrayList<String> listNamaPenerima = new ArrayList<>();
        final ArrayList<String> listNamaPengirim = new ArrayList<>();
        final ArrayList<String> listNamaDonatur = new ArrayList<>();

        if (Id_Penerima != null) {
            while (Id_Penerima.size() > i) {
                if (Id_Penerima.get(i).equals(userID)) {
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
            ArrayAdapter namaUser = new ArrayAdapter(this, android.R.layout.simple_list_item_1, listNamaDonatur);
            listViewBelumTerkirim.setAdapter(namaUser);
        }
    }


    private void showData(DataSnapshot dataSnapshot) {
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            ProfilePenerima uInfo = new ProfilePenerima();
            uInfo.setRequest(ds.child("USER").child("PENERIMA").child(userID).getValue(ProfilePenerima.class).getRequest());

            request = uInfo.getRequest();
            if (request.equals("true")) {
                BtnRequest.setImageResource(R.drawable.kurir);
            } else {
                BtnRequest.setImageResource(R.drawable.req);
            }
        }
    }

    public void out(View view) {
        mAuth.signOut();
        Intent mIntent = new Intent(Penerima_Main.this, MainActivity.class);
        startActivity(mIntent);
    }

    public void ShowPopupHistory(View v) {
        TextView txtclose;
        notif.setContentView(R.layout.kurir_notif);
        fotoHistory = (ImageView) notif.findViewById(R.id.imageViewHistoryFotoPenerima);
        etNamaPenerima = (TextView) notif.findViewById(R.id.editTextHistoryNamaPenerimaPopup);
        etNamaPengirim = (TextView) notif.findViewById(R.id.editTextHistoryNamaPengirim);
        etAlamatPenerima = (TextView) notif.findViewById(R.id.editTextHistoryAlamatPenerimaPopup);
        etNamaDonatur = (TextView) notif.findViewById(R.id.editTextHistoryNamaDonaturPopup);

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

        txtclose =(TextView) notif.findViewById(R.id.txtclose);
        txtclose.setText("X");
        etNamaPenerima.setText("Penerima : " + NamaPenerima);
        etNamaPengirim.setText("Kurir : " + NamaPengirim);
        etNamaDonatur.setText("Donatur : "+NamaDonaturPopup);
        etAlamatPenerima.setText("Status : Belum Terkirim");
        txtclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notif.dismiss();
            }
        });
        notif.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        notif.show();
    }
    
}
