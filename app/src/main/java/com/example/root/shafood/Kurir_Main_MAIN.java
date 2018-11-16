package com.example.root.shafood;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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

public class Kurir_Main_MAIN extends AppCompatActivity {

    private TextView nama;
    private ImageView check;
    private RelativeLayout buka_maps;
    private LinearLayout mulai;
    private long backPressedTime;
    private Toast backToast;
    private Button btn_buka_maps, kerja;
    private ImageView foto, icon_maps;
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    private String userID, namaProfile;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private String status = "true";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kurir_main_main);

        nama = (TextView) findViewById(R.id.nama);
        check = (ImageView) findViewById(R.id.check);
        mulai = (LinearLayout) findViewById(R.id.mulai);
        foto = (ImageView) findViewById(R.id.foto);
        btn_buka_maps = (Button) findViewById(R.id.btn_buka_maps);
        icon_maps = (ImageView) findViewById(R.id.icon_maps);
        kerja = (Button) findViewById(R.id.kerja);
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        FirebaseUser user = mAuth.getCurrentUser();
        userID = user.getUid();
        System.out.println("INI UID ======== " + userID);
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl("gs://shafood93.appspot.com");

        btn_buka_maps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mIntent = new Intent(Kurir_Main_MAIN.this, Kurir_Main.class);
                startActivity(mIntent);
            }
        });

        icon_maps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mIntent = new Intent(Kurir_Main_MAIN.this, Kurir_Main.class);
                startActivity(mIntent);
            }
        });

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                showData(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        System.out.println("INI STATUS LOHHHH ==== " + status);

        kerja.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (status.equals("true")) {
                    myRef.child("SHAFOOD").child("USER").child("KURIR").child(userID).child("status").setValue("false");
                } else {
                    myRef.child("SHAFOOD").child("USER").child("KURIR").child(userID).child("status").setValue("true");
                }
            }
        });

        storageRef.child("Kurir/IdentitasKurir/" + userID).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                System.out.println("INI URI LOHHHHH ===== " + uri);
                Glide.with(getApplicationContext()).load(uri).into(foto);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private void showData(DataSnapshot dataSnapshot) {
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            ProfileKurir uInfo = new ProfileKurir();
            uInfo.setNama(ds.child("USER").child("KURIR").child(userID).getValue(ProfileKurir.class).getNama());
            uInfo.setAlamat(ds.child("USER").child("KURIR").child(userID).getValue(ProfileKurir.class).getAlamat());
            uInfo.setTanggallahir(ds.child("USER").child("KURIR").child(userID).getValue(ProfileKurir.class).getTanggallahir());
            uInfo.setNohp(ds.child("USER").child("KURIR").child(userID).getValue(ProfileKurir.class).getNohp());
            uInfo.setLevel(ds.child("USER").child("KURIR").child(userID).getValue(ProfileKurir.class).getLevel());
            uInfo.setNoplat(ds.child("USER").child("KURIR").child(userID).getValue(ProfileKurir.class).getNoplat());
            uInfo.setStatus(ds.child("USER").child("KURIR").child(userID).getValue(ProfileKurir.class).getStatus());

            status = uInfo.getStatus();
            namaProfile = uInfo.getNama();
            nama.setText(namaProfile);
            System.out.println(namaProfile);
            if (status.equals("true")) {
                check.setImageResource(R.drawable.ic_check_black_24dp);
                kerja.setBackgroundResource(R.color.green);
            } else {
                check.setImageResource(R.drawable.ic_close_black_24dp);
                kerja.setBackgroundResource(R.color.red);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            backToast.cancel();
            super.onBackPressed();
            Intent intent = new Intent(Kurir_Main_MAIN.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("EXIT", true);
            startActivity(intent);
        } else {
            backToast = Toast.makeText(getBaseContext(), "Tekan Lagi Untuk Keluar", Toast.LENGTH_SHORT);
            backToast.show();
        }
        backPressedTime = System.currentTimeMillis();
    }

    @Override
    protected void onStart() {
        super.onStart();
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                showData(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        myRef.removeEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                showData(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                showData(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        System.out.println("INI ON STOP");
        myRef.removeEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                showData(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.out.println("INI ON Destroy");
        myRef.removeEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                showData(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
