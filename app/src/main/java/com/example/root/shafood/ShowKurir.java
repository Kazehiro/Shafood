package com.example.root.shafood;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.auth.api.credentials.IdentityProviders;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.ref.ReferenceQueue;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by User on 2/8/2017.
 */

public class ShowKurir extends AppCompatActivity {
    private static final String TAG = "ViewDatabase";

    //add Firebase Database stuff
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef,myRefUpload;
    private String userID;

    private ListView mListViewKurir;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_kurir);

        mListViewKurir = (ListView) findViewById(R.id.listviewKurir);

        //declare the database reference object. This is what we use to access the database.
        //NOTE: Unless you are signed in, this will not be useable.
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference().child("SHAFOOD").child("USER").child("KURIR");
        myRefUpload = mFirebaseDatabase.getReference();
        FirebaseUser user = mAuth.getCurrentUser();
        userID = user.getUid();

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

    private void showData(Map<String, Object> dataSnapshot) {
        final ArrayList<String> Nama = new ArrayList<>();
        for (Map.Entry<String, Object> entry : dataSnapshot.entrySet()) {
            Map nama = (Map) entry.getValue();
            Nama.add((String) nama.get("nama"));
        }
        final ArrayList<String> Id_kurir = new ArrayList<>();
        for (Map.Entry<String, Object> entry : dataSnapshot.entrySet()) {
            Map id_kurir = (Map) entry.getValue();
            Id_kurir.add((String) id_kurir.get("id_user"));
        }
        System.out.println(Nama + " | " + Id_kurir);
        mListViewKurir.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String Barang = getIntent().getStringExtra("Barang");
                String SKuantitas = getIntent().getStringExtra("Kuantitas");
                String NamaDonatur = getIntent().getStringExtra("Nama Donatur");
                String NamaPenerima = getIntent().getStringExtra("Nama Penerima");
                String Id_Donatur = getIntent().getStringExtra("Id Donatur");
                String Id_Penerima = getIntent().getStringExtra("Id Penerima");
                String Lat_Donatur = getIntent().getStringExtra("Latitude Donatur");
                String Lng_Donatur = getIntent().getStringExtra("Longitude Donatur");
                String Lat_Penerima = getIntent().getStringExtra("Latitude Penerima");
                String Lng_Penerima = getIntent().getStringExtra("Longitude Penerima");
                String NamaKurir = Nama.get(position);
                String Id_Kurir = Id_kurir.get(position);
                int Kuantitas = Integer.parseInt(SKuantitas);
                System.out.println(Barang + " | " + Kuantitas + " | " + NamaDonatur + " | " + NamaPenerima + " | " + Id_Donatur + " | " + Id_Penerima + " | " + Lat_Donatur + " | " + Lng_Donatur + " | " + Lat_Penerima + " | " + Lng_Penerima + " | " + NamaKurir + " | " + Id_Kurir);
                toastMessage(Barang + " | " + Kuantitas + " | " + NamaDonatur + " | " + NamaPenerima + " | " + Id_Donatur + " | " + Id_Penerima + " | " + Lat_Donatur + " | " + Lng_Donatur + " | " + Lat_Penerima + " | " + Lng_Penerima + " | " + NamaKurir + " | " + Id_Kurir);
                Long tsLong = System.currentTimeMillis() / 1000;
                String ts = tsLong.toString();
                Transaksi newTransaksi = new Transaksi(userID + ts, userID, Id_Penerima, Id_Kurir, Lat_Penerima, Lng_Penerima, Lat_Donatur, Lng_Donatur, NamaDonatur, NamaKurir, NamaPenerima, Barang, Kuantitas, "false");
                myRefUpload.child("SHAFOOD").child("TRANSAKSI").child(userID + ts).setValue(newTransaksi);
                toastMessage("Adding User to database...");
            }
        });
        ArrayAdapter namaUser = new ArrayAdapter(this, android.R.layout.simple_list_item_1, Nama);
        mListViewKurir.setAdapter(namaUser);
    }


    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }


    /**
     * customizable toast
     *
     * @param message
     */
    private void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
