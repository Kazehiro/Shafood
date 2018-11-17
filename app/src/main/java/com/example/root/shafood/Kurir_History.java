package com.example.root.shafood;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

import java.util.ArrayList;
import java.util.Map;

public class Kurir_History extends AppCompatActivity {

    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private String userID;
    private String NamaPenerima, NamaPengirim, IdPenerima , NamaDonatur;


    private FirebaseStorage storage;
    private StorageReference storageRef;

    private ImageButton ImgBtnBack;
    private ListView mListViewHistoryKurir;
    private Dialog kurirHistory;
    private ImageView fotoHistory;
    private TextView etNamaPenerima, etNamaPengirim, etAlamatPenerima, etNamaDonatur;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kurir_history);

        kurirHistory = new Dialog(this);
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl("gs://shafood93.appspot.com");

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference().child("SHAFOOD").child("TRANSAKSI");
        FirebaseUser user = mAuth.getCurrentUser();
        userID = user.getUid();

        ImgBtnBack = findViewById(R.id.imageButton);
        mListViewHistoryKurir = findViewById(R.id.listviewHistoryKurir);

        ImgBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(Kurir_History.this, Kurir_Main_MAIN.class);
                startActivity(mIntent);
            }
        });
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

    private void showData(Map<String, Object> dataSnapshot) {
        final ArrayList<String> Id_Kurir = new ArrayList<>();
        for (Map.Entry<String, Object> entry : dataSnapshot.entrySet()) {
            Map id_kurir = (Map) entry.getValue();
            Id_Kurir.add((String) id_kurir.get("id_kurir"));
        }
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
        final ArrayList<String> Nm_Donatur = new ArrayList<>();
        for (Map.Entry<String, Object> entry : dataSnapshot.entrySet()) {
            Map nm_donatur = (Map) entry.getValue();
            Nm_Donatur.add((String) nm_donatur.get("nama_donatur"));
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
        int i = 0;
        final ArrayList<String> listId = new ArrayList<>();
        final ArrayList<String> listIdPenerima = new ArrayList<>();
        final ArrayList<String> listNamaPenerima = new ArrayList<>();
        final ArrayList<String> listNamaDonatur = new ArrayList<>();
        final ArrayList<String> listNamaPengirim = new ArrayList<>();

        if (Id_Kurir != null) {
            while (Id_Kurir.size() > i) {
                if (Id_Kurir.get(i).equals(userID)) {
                    if (Success.get(i).equals("true")) {
                        listId.add(Id_Transaksi.get(i));
                        listIdPenerima.add(Id_Penerima.get(i));
                        listNamaPengirim.add(Nm_Pengirim.get(i));
                        listNamaDonatur.add(Nm_Donatur.get(i));
                        listNamaPenerima.add(Nm_Penerima.get(i));
                    }
                }
                i++;
            }
            mListViewHistoryKurir.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    IdPenerima = listIdPenerima.get(position);
                    NamaPenerima = listNamaPenerima.get(position);
                    NamaPengirim = listNamaPengirim.get(position);
                    NamaDonatur = listNamaDonatur.get(position);
                    ShowPopupHistory(view);
                }
            });
            ArrayAdapter namaUser = new ArrayAdapter(this, android.R.layout.simple_list_item_1, listNamaPenerima);
            mListViewHistoryKurir.setAdapter(namaUser);
        }
    }

    public void ShowPopupHistory(View v) {
        TextView txtclose;
        kurirHistory.setContentView(R.layout.history_kurir_popup);
        fotoHistory = (ImageView) kurirHistory.findViewById(R.id.imageViewHistoryFotoPenerima);
        etNamaPenerima = (TextView) kurirHistory.findViewById(R.id.editTextHistoryNamaPenerimaPopup);
        etNamaPengirim = (TextView) kurirHistory.findViewById(R.id.editTextHistoryNamaPengirim);
        etAlamatPenerima = (TextView) kurirHistory.findViewById(R.id.editTextHistoryAlamatPenerimaPopup);
        etNamaDonatur = (TextView) kurirHistory.findViewById(R.id.editTextHistoryNamaDonaturPopup);


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

        txtclose =(TextView) kurirHistory.findViewById(R.id.txtclose);
        txtclose.setText("X");
        etNamaPenerima.setText("Penerima : " + NamaPenerima);
        etNamaPengirim.setText("Kurir : " + NamaPengirim);
        etNamaDonatur.setText("Donatur : "+NamaDonatur);
        txtclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                kurirHistory.dismiss();
            }
        });
        kurirHistory.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        kurirHistory.show();
    }
    
}
