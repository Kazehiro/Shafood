package com.example.root.shafood;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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

import java.util.ArrayList;

/**
 * Created by User on 2/8/2017.
 */

public class ViewDatabase extends AppCompatActivity {
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
        setContentView(R.layout.activity_view_database);

        mListView = (ListView) findViewById(R.id.listview);

        //declare the database reference object. This is what we use to access the database.
        //NOTE: Unless you are signed in, this will not be useable.
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        FirebaseUser user = mAuth.getCurrentUser();
        userID = user.getUid();

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
            }
        };


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


            //display all the information
            Log.d(TAG, "showData: Nama                  : " + uInfo.getNama());
            Log.d(TAG, "showData: Alamat                : " + uInfo.getAlamat());
            Log.d(TAG, "showData: Tempat Tanggal Lahihr : " + uInfo.getTanggallahir());
            Log.d(TAG, "showData: No. HP                : " + uInfo.getNohp());
            Log.d(TAG, "showData: Level                 : " + uInfo.getLevel());

            ArrayList<String> array  = new ArrayList<>();
            array.add(uInfo.getNama());
            array.add(uInfo.getAlamat());
            array.add(uInfo.getTanggallahir());
            array.add(uInfo.getNohp());
            array.add(uInfo.getLatitude());
            array.add(uInfo.getLongitude());
            array.add(uInfo.getNoktp());
            System.out.println(array);
            ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,array);
            System.out.println(adapter);
            mListView.setAdapter(adapter);
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
     * @param message
     */
    private void toastMessage(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }
}
