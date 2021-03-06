package com.example.root.shafood;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
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
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

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
    private DatabaseReference myRef,myRefUpload,myRef1;
    private String userID;

    private ListView mListViewKurir;
    FirebaseStorage storage;
    StorageReference storageReference;
    private String Pesan,pathfile, Barang, SKuantitas, NamaDonatur, NamaPenerima, Id_Donatur, Lat_Donatur, Lng_Donatur, Id_Penerima, Lat_Penerima, Lng_Penerima, Nama_Penerima, Alamat_Penerima, TeleponPenerima, Narasi;
    private Uri filePath1;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_kurir);

        mListViewKurir = (ListView) findViewById(R.id.listviewKurir);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        Barang = getIntent().getStringExtra("Barang");
        SKuantitas = getIntent().getStringExtra("Kuantitas");
        NamaDonatur = getIntent().getStringExtra("Nama Donatur");
        NamaPenerima = getIntent().getStringExtra("Nama Penerima");
        Id_Donatur = getIntent().getStringExtra("Id Donatur");
        Id_Penerima = getIntent().getStringExtra("Id Penerima");
        Lat_Donatur = getIntent().getStringExtra("Latitude Donatur");
        Lng_Donatur = getIntent().getStringExtra("Longitude Donatur");
        Lat_Penerima = getIntent().getStringExtra("Latitude Penerima");
        Lng_Penerima = getIntent().getStringExtra("Longitude Penerima");
        Pesan = getIntent().getStringExtra("Pesan");
        filePath1 = getIntent().getData();
        try {
            System.out.println("INI FILEPATH KURIR =======" + filePath1.toString());
        }catch (NullPointerException e){
            e.printStackTrace();
        }

        //declare the database reference object. This is what we use to access the database.
        //NOTE: Unless you are signed in, this will not be useable.
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference().child("SHAFOOD").child("USER").child("KURIR");
        myRef1 = mFirebaseDatabase.getReference();
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
        Location currentUser = new Location("");
        currentUser.setLatitude(Double.parseDouble(Lat_Donatur));
        currentUser.setLongitude(Double.parseDouble(Lng_Donatur));

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
        final ArrayList<String> Status = new ArrayList<>();
        for (Map.Entry<String, Object> entry : dataSnapshot.entrySet()) {
            Map status = (Map) entry.getValue();
            Status.add((String) status.get("status"));
        }
        final ArrayList<String> Lat = new ArrayList<>();
        for (Map.Entry<String, Object> entry : dataSnapshot.entrySet()) {
            Map lat = (Map) entry.getValue();
            Lat.add((String) lat.get("latitude"));
        }
        final ArrayList<String> Lng = new ArrayList<>();
        for (Map.Entry<String, Object> entry : dataSnapshot.entrySet()) {
            Map lng = (Map) entry.getValue();
            Lng.add((String) lng.get("longitude"));
        }
        final ArrayList<String> Narik = new ArrayList<>();
        for (Map.Entry<String, Object> entry : dataSnapshot.entrySet()) {
            Map narik = (Map) entry.getValue();
            Narik.add((String) narik.get("narik"));
        }
        final ArrayList<String> listNama = new ArrayList<>();
        final ArrayList<String> listId = new ArrayList<>();
        int i = 0;
        System.out.println(Nama + " | " + Id_kurir);
        while(Nama.size() > i){
            if(Status.get(i).equals("true")){
                if(Narik.get(i).equals("false"))
                {
                    Location friend = new Location("");
                    friend.setLatitude(Double.parseDouble(Lat.get(i)));
                    friend.setLongitude(Double.parseDouble(Lng.get(i)));
                    if (((currentUser.distanceTo(friend)) / 1000) <= 5) {
                        listNama.add(Nama.get(i));
                        listId.add(Id_kurir.get(i));
                    }
                }
            }
            i++;
        }
        listNama.add("Request Kurir Ke Kantor");
        listId.add("request");
        mListViewKurir.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {



                String NamaKurir = listNama.get(position);
                String Id_Kurir = listId.get(position);
                int Kuantitas = Integer.parseInt(SKuantitas);
                Long tsLong = System.currentTimeMillis() / 1000;
                String ts = tsLong.toString();
                Transaksi newTransaksi = new Transaksi(userID + ts, userID, Id_Penerima, Id_Kurir, Lat_Penerima, Lng_Penerima, Lat_Donatur, Lng_Donatur, NamaDonatur, NamaKurir, NamaPenerima, Barang, Pesan, Kuantitas, "false","0","kosong");
                myRefUpload.child("SHAFOOD").child("TRANSAKSI").child(userID + ts).setValue(newTransaksi);
                if (Id_Kurir.equals("request")){ }
                else{
                    myRef1.child("SHAFOOD").child("USER").child("KURIR").child(Id_Kurir).child("narik").setValue("true");
                }
                toastMessage("Terima Kasih");
                uploadImageBarang();
                Intent mIntent = new Intent(ShowKurir.this,Donatur_Main.class);
                startActivity(mIntent);
            }
        });
        ArrayAdapter namaUser = new ArrayAdapter(this, android.R.layout.simple_list_item_1, listNama);
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

    private void uploadImageBarang() {

        if (filePath1 != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            StorageReference ref = storageReference.child("Transaksi/" + Id_Penerima);
            ref.putFile(filePath1)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(ShowKurir.this, "Uploaded Berhasil", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(ShowKurir.this, "Upload Gagal " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        }
                    });
        } else {
            Toast.makeText(ShowKurir.this, "Gagal uyyyyyy", Toast.LENGTH_SHORT).show();
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
