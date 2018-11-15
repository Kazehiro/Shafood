package com.example.root.shafood;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

public class lengkapidata_penerima extends AppCompatActivity implements OnMapReadyCallback ,GoogleMap.OnMapLongClickListener{

    private static final String TAG = "Penerima";

    EditText editTextNama;
    EditText editTextEmail;
    EditText editTextNohp;
    TextView editTextAlamat;
    EditText editTextTanggalLahir;
    EditText editTextIdentitas;
    Button btnTambah;
    private GoogleMap mMap;
    ArrayList<LatLng> lispoints;
    public LatLng alamatLatLng = null;
    public Double alamatLatitude,alamatLongitude;
    public LatLng indonesia;
    private static final int LOCATION_REQUEST = 500;

    //Gambar
    private Button btnChooseIdentitasDonatur;
    private Button btnChooseFotoDonatur;
    private ImageView imageViewIdentitasDonatur,imgViewFotoDonatur;
    private Uri filePath1;
    private Uri filePath2;
    private final int PICK_IMAGE_REQUEST_1 = 1;
    private final int PICK_IMAGE_REQUEST_2 = 2;

    //add Firebase Database stuff
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    public static final int ALAMAT = 1;
    private static int REQUEST_CODE = 0;
    FirebaseStorage storage;
    StorageReference storageReference;

    @Nullable
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lengkapidata_penerima);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        editTextNama = (EditText) findViewById(R.id.EditTextnama);
        editTextNohp = (EditText) findViewById(R.id.EditTextnohp);
        editTextAlamat = (TextView) findViewById(R.id.EditTextalamat);
        editTextTanggalLahir = (EditText) findViewById(R.id.EditTexttanggallahir);
        btnTambah = (Button) findViewById(R.id.tambahDonatur);
        editTextIdentitas = (EditText) findViewById(R.id.EditTextidentitas);

        //Initialize Views
        btnChooseFotoDonatur = (Button) findViewById(R.id.btnChooseFotoDonatur);
        btnChooseIdentitasDonatur = (Button) findViewById(R.id.btnChooseIdentitasDonatur);
        imageViewIdentitasDonatur = (ImageView) findViewById(R.id.imgViewIdentitasDonatur);
        imgViewFotoDonatur = (ImageView) findViewById(R.id.imgViewFotoDonatur);

        //declare the database reference object. This is what we use to access the database.
        //NOTE: Unless you are signed in, this will not be useable.
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();

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
                // ...
            }
        };

        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Object value = dataSnapshot.getValue();
                Log.d(TAG, "Value is: " + value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        btnChooseIdentitasDonatur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImageIdentitasDonatur();
            }
        });

        btnChooseFotoDonatur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImageFotoDonatur();
            }
        });

        btnTambah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Attempting to add object to database.");
                String nama = editTextNama.getText().toString().toUpperCase().trim();
                String nohp = editTextNohp.getText().toString().trim();
                String alamat = editTextAlamat.getText().toString().toUpperCase().trim();
                String tanggallahir = editTextTanggalLahir.getText().toString().trim();
                String noIdentitas = editTextIdentitas.getText().toString().trim();
                String latitude = alamatLatitude.toString().trim();
                String longitude = alamatLongitude.toString().trim();

                Log.d("ISI", nama + " , " + nohp + " , " + alamat + " , " + tanggallahir + " , " + noIdentitas);
                uploadImageIdentitasDonatur();
                uploadImageFotoDonatur();
                if (!nama.equals("")) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    String userID = user.getUid();
                    UserPenerima newUser = new UserPenerima(userID, nama, noIdentitas, nohp, alamat, tanggallahir,latitude,longitude,"false", "false", "false",4);
                    myRef.child("SHAFOOD").child("USER").child("PENERIMA").child(userID).setValue(newUser);
                    Intent i = new Intent(lengkapidata_penerima.this, Berhasil.class);
                    startActivity(i);
                }

            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        editTextAlamat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPlaceAutoComplete(ALAMAT);
            }
        });
        lispoints = new ArrayList<>();

    }

    public void chooseImageIdentitasDonatur() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST_1);
    }

    public void chooseImageFotoDonatur() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST_2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null && data.getData() != null) {
            if (requestCode == PICK_IMAGE_REQUEST_1) {
                filePath1 = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath1);
                    imageViewIdentitasDonatur.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                filePath2 = data.getData();
                try {
                    Bitmap bitmip = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath2);
                    imgViewFotoDonatur.setImageBitmap(bitmip);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        else if (resultCode == RESULT_OK) {
            //Toast.makeText(this, "Sini Gaes2", Toast.LENGTH_SHORT).show();
            // Tampung Data tempat ke variable
            Place placeData = PlaceAutocomplete.getPlace(this, data);

            if (placeData.isDataValid()) {
                // Show in Log Cat
                Log.d("autoCompletePlace Data", placeData.toString());

                // Dapatkan Detail
                String placeAddress = placeData.getAddress().toString();
                LatLng placeLatLng = placeData.getLatLng();
                String placeName = placeData.getName().toString();

                // Cek user milih titik jemput atau titik tujuan
                switch (REQUEST_CODE) {
                    case ALAMAT:
                        // Set ke widget lokasi asal
                        editTextAlamat.setText(placeAddress);
                        alamatLatLng = placeData.getLatLng();
                        break;
                }
                if (alamatLatLng != null) {
                    onMapLongClick(alamatLatLng);
                    lispoints.add(alamatLatLng);
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(alamatLatLng));
                    CameraUpdateFactory.newLatLng(alamatLatLng);
                    CameraUpdateFactory.newLatLngZoom(alamatLatLng,16);
                    mMap.addMarker(new MarkerOptions().position(alamatLatLng).title(placeAddress));
                }

            } else {
                // Data tempat tidak valid
                Toast.makeText(this, "Invalid Place !", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void uploadImageIdentitasDonatur() {

        if (filePath1 != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            FirebaseUser user = mAuth.getCurrentUser();
            String userID = user.getUid();
            StorageReference ref = storageReference.child("Penerima/KTP/" + userID);
            ref.putFile(filePath1)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(lengkapidata_penerima.this, "Uploaded Berhasil", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(lengkapidata_penerima.this, "Upload Gagal " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
            Toast.makeText(lengkapidata_penerima.this, "Gagal uyyyyyy", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadImageFotoDonatur() {

        if (filePath2 != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            FirebaseUser user = mAuth.getCurrentUser();
            String userID = user.getUid();
            StorageReference ref = storageReference.child("Penerima/FotoProfil/" + userID);
            ref.putFile(filePath2)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(lengkapidata_penerima.this, "Uploaded Berhasil", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(lengkapidata_penerima.this, "Upload Gagal " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
            Toast.makeText(lengkapidata_penerima.this, "Gagal uyyyyyy", Toast.LENGTH_SHORT).show();
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



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //minta permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST);
            return;
        }
        //deklarasi widget
        //setting UI MAPS
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setAllGesturesEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        String[] indo = "-6.175 , 106.828333".split(",");
        Double lat = Double.parseDouble(indo[0]);
        Double lng = Double.parseDouble(indo[1]);
        indonesia = new LatLng(lat,lng);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(indonesia));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(indonesia,16));
        mMap.setOnMapLongClickListener(this);
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        if (lispoints.size() >= 1){
            mMap.clear();
            lispoints.clear();
        }
        else {
            lispoints.add(latLng);
            MarkerOptions mMarkerOptions = new MarkerOptions();
            mMarkerOptions.position(latLng);
            mMap.addMarker(new MarkerOptions().position(latLng).title(latLng.toString()));
            alamatLatitude = latLng.latitude;
            alamatLongitude = latLng.longitude;
        }

        Log.d("Latitude = ", alamatLatitude.toString());
        Log.d("Longitude = ", alamatLongitude.toString());
    }

    public void showPlaceAutoComplete(int typeLocation) {
        REQUEST_CODE = typeLocation;
        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder().setCountry("ID").build();
        AutocompleteFilter filter = new AutocompleteFilter.Builder().setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES).setCountry("ID").build();
        try {
            Intent mIntent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                    .setFilter(typeFilter)
                    .build(this);
            startActivityForResult(mIntent, REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
            Toast.makeText(this, "Layanan Tidak Tersedia", Toast.LENGTH_SHORT).show();
        }
    }

    //add a toast to show when successfully signed in
    /**
     * customizable toast
     * @param message
     */
}
