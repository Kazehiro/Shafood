package com.example.root.shafood;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Penerima_Main extends AppCompatActivity {


    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    private String userID;

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

    private Button BtnQrcode;
    private ImageButton BtnRequest;
    private ImageButton ImgBtnProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_penerima__main);

        ImgBtnProfile = (ImageButton) findViewById(R.id.imgBtnProfile);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        FirebaseUser user = mAuth.getCurrentUser();
        userID = user.getUid();

        BtnQrcode = (Button) findViewById(R.id.BtnQrcode);
        BtnRequest = (ImageButton) findViewById(R.id.imageButtonRequest);

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

    private void showData(DataSnapshot dataSnapshot) {
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            ProfilePenerima uInfo = new ProfilePenerima();
            uInfo.setRequest(ds.child("USER").child("PENERIMA").child(userID).getValue(ProfilePenerima.class).getRequest());

            request = uInfo.getRequest();
            if (request.equals("true")){
                BtnRequest.setImageResource(R.drawable.req);
            }else {
                BtnRequest.setImageResource(R.drawable.kurir);
            }
        }
    }
    public void out(View view){
        mAuth.signOut();
        Intent mIntent = new Intent(Penerima_Main.this, MainActivity.class);
        startActivity(mIntent);
    }
}
