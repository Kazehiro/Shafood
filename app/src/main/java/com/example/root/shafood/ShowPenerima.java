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

public class ShowPenerima extends AppCompatActivity {
    private static final String TAG = "ViewDatabase";

    //add Firebase Database stuff
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    private String userID;

    private ListView mListViewPenerima;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_penerima);

        mListViewPenerima = (ListView) findViewById(R.id.listviewPenerima);

        //declare the database reference object. This is what we use to access the database.
        //NOTE: Unless you are signed in, this will not be useable.
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference().child("SHAFOOD").child("USER").child("PENERIMA");
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
        int i = 0;
        final ArrayList<String> listNama = new ArrayList<>();
        final ArrayList<String> listId = new ArrayList<>();
        final ArrayList<String> listLat = new ArrayList<>();
        final ArrayList<String> listLng = new ArrayList<>();
        if (Nama != null) {
            while (Nama.size() > i) {
                if (Request.get(i).equals("true")) {
                    if (Transaksi.get(i).equals("false")) {
                        listNama.add(Nama.get(i));
                        listId.add(Id_penerima.get(i));
                        listLat.add(Lat.get(i));
                        listLng.add(Lng.get(i));
                    }
                }
                i++;
            }
            i = 0;
            while (Nama.size() > i) {
                if (Request.get(i).equals("false")) {
                    if (Transaksi.get(i).equals("false")) {
                        listNama.add(Nama.get(i));
                        listId.add(Id_penerima.get(i));
                        listLat.add(Lat.get(i));
                        listLng.add(Lng.get(i));
                    }
                    i++;
                }
            }
            mListViewPenerima.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String Barang = getIntent().getStringExtra("Barang");
                    String Kuantitas = getIntent().getStringExtra("Kuantitas");
                    String NamaDonatur = getIntent().getStringExtra("Nama Donatur");
                    String Id_Donatur = getIntent().getStringExtra("Id Donatur");
                    String Lat_Donatur = getIntent().getStringExtra("Latitude Donatur");
                    String Lng_Donatur = getIntent().getStringExtra("Longitude Donatur");
                    toastMessage("Nama = " + listNama.get(position) + " id = " + listId.get(position) + " Barang = " + Barang + " Kuantitas = " + Kuantitas + " Lat Penerima = " + listLat.get(position) + " Lng Penerima = " + listLng.get(position));
                    Intent mIntent = new Intent(ShowPenerima.this, ShowKurir.class);
                    mIntent.putExtra("Barang", Barang);
                    mIntent.putExtra("Kuantitas", Kuantitas);
                    mIntent.putExtra("Id Penerima", listId.get(position));
                    mIntent.putExtra("Id Donatur", Id_Donatur);
                    mIntent.putExtra("Latitude Penerima", listLat.get(position));
                    mIntent.putExtra("Longitude Penerima", listLng.get(position));
                    mIntent.putExtra("Latitude Donatur", Lat_Donatur);
                    mIntent.putExtra("Longitude Donatur", Lng_Donatur);
                    mIntent.putExtra("Nama Donatur", NamaDonatur);
                    mIntent.putExtra("Nama Penerima", listNama.get(position));
                    startActivity(mIntent);

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
