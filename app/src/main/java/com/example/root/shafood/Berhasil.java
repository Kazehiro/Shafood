package com.example.root.shafood;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by User on 2/8/2017.
 */

public class Berhasil extends AppCompatActivity {
    private static final String TAG = "ViewDatabase";

    //add Firebase Database stuff
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    private String userID;

    private ListView mListView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_berhasil);

        mListView = (ListView) findViewById(R.id.listview);

        //declare the database reference object. This is what we use to access the database.
        //NOTE: Unless you are signed in, this will not be useable.
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        FirebaseUser user = mAuth.getCurrentUser();
        userID = user.getUid();
        System.out.println("User = " + userID);


        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                showData(dataSnapshot);
                showData1(dataSnapshot);
                showData2(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

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

    }

    private NullPointerException showData(DataSnapshot dataSnapshot) {
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            ProfileDonatur uInfo = new ProfileDonatur();
            try {
                uInfo.setLevel(ds.child("USER").child("DONATUR").child(userID).getValue(ProfileDonatur.class).getLevel());
                System.out.println("uInfo = " + uInfo.getLevel());
                if (uInfo.getLevel() == 2) {
                    Intent mIntent = new Intent(Berhasil.this, Donatur_Main.class);
                    startActivity(mIntent);
                }
            } catch (NullPointerException e) {
                return e;
            }
        }
        return null;
    }

    private NullPointerException showData1(DataSnapshot dataSnapshot) {
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            ProfileKurir kuInfo = new ProfileKurir();
            try {

                kuInfo.setLevel(ds.child("USER").child("KURIR").child(userID).getValue(ProfileKurir.class).getLevel());
                System.out.println("kuInfo = " + kuInfo.getLevel());

                if (kuInfo.getLevel() == 3) {
                    Intent mIntent = new Intent(Berhasil.this, Kurir_Main_MAIN.class);
                    startActivity(mIntent);
                }


            } catch (NullPointerException e) {
                return e;
            }
        }
        return null;
    }

    private NullPointerException showData2(DataSnapshot dataSnapshot) {
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            ProfilePenerima peInfo = new ProfilePenerima();
            try {

                peInfo.setLevel(ds.child("USER").child("PENERIMA").child(userID).getValue(ProfilePenerima.class).getLevel());
                peInfo.setVerifikasi(ds.child("USER").child("PENERIMA").child(userID).getValue(ProfilePenerima.class).getVerifikasi());
                System.out.println("kuInfo = " + peInfo.getLevel());

                if (peInfo.getLevel() == 4) {
                    if(peInfo.getVerifikasi().equals("true")){
                        Intent mIntent = new Intent(Berhasil.this, Penerima_Main.class);
                        startActivity(mIntent);
                    } else{
                        toastMessage("Akun Anda Belum Ter-Verifikasi");
                    }
                }


            } catch (NullPointerException e) {
                return e;
            }
        }
        return null;
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
