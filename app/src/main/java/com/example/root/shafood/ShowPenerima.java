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
                showData((Map<String, Object>) dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mListViewPenerima.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                toastMessage("Posisi = " + position + "id = " + id);

            }
        });
    }

    private void showData(Map<String, Object> dataSnapshot) {
        ArrayList<String> Nama = new ArrayList<>();
        for (Map.Entry<String, Object> entry : dataSnapshot.entrySet()) {
            Map nama = (Map) entry.getValue();
            Nama.add((String) nama.get("nama"));
        }
        ArrayList<String> Request = new ArrayList<>();
        for (Map.Entry<String, Object> entry : dataSnapshot.entrySet()) {
            Map request = (Map) entry.getValue();
            Request.add((String) request.get("request"));
        }
        int i = 0;
        ArrayList<String> list = new ArrayList<>();
        if (Nama != null) {
            while (Nama.size() > i) {
                if (Request.get(i).equals("true")) {
                    list.add(Nama.get(i));
                }
                i++;
            }
            i = 0;
            while (Nama.size() > i) {
                if (Request.get(i).equals("false")) {
                    list.add(Nama.get(i));
                }
                i++;
            }
            ArrayAdapter namaUser = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);
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
