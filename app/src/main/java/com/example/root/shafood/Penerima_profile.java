package com.example.root.shafood;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

import java.io.File;
import java.util.Calendar;

import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;

public class Penerima_profile extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private ImageButton ImgBtnBack;
    private long backPressedTime;
    private Toast backToast;
    private ImageView fotoProfile;
    private TextView namaProfile, alamatProfile, noTelp, tanggalLahir;
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef, myRef2;
    private String userID;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private TextView desc;
    public static final int REQUEST_CODE_CAMERA_FOTO = 0020;
    public static final int REQUEST_CODE_GALLERY_FOTO = 0021;
    private String[] items = {"Camera", "Gallery"};
    private Button btnEditPassword, btnEditFoto, btnSave, selesai, btnEditPro,Cancel;
    private Uri filePath2;
    StorageReference storageReference;
    private Dialog editProfile;
    public String namaEdit, alamatEdit, nomorTelp, tglLahir, deskripsi;
    public EditText namaEditText, etNotelp, desk;
    public TextView tvAlamat, tvTanggal;
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_penerima_profile);
        editProfile = new Dialog(this);
        editProfile.setContentView(R.layout.update_profile);
        storage = FirebaseStorage.getInstance();

        ImgBtnBack = findViewById(R.id.imageButton);
        desc = (TextView) findViewById(R.id.desc);
        btnEditPassword = (Button) findViewById(R.id.btnEditPassword);
        btnEditFoto = (Button) findViewById(R.id.btnEditFoto);
        btnSave = (Button) findViewById(R.id.btnSave);
        btnEditPro = (Button) findViewById(R.id.btnEditPro);
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        myRef2 = mFirebaseDatabase.getReference();
        FirebaseUser user = mAuth.getCurrentUser();
        userID = user.getUid();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl("gs://shafood93.appspot.com");
        namaEditText = (EditText) editProfile.findViewById(R.id.namaEdit);
        /*tvAlamat = (TextView) editProfile.findViewById(R.id.TvAlamat);*/
        etNotelp = (EditText) editProfile.findViewById(R.id.etTelepon);
        tvTanggal = (TextView) editProfile.findViewById(R.id.tvTanggal);
        desk = (EditText) editProfile.findViewById(R.id.etDesk);
        selesai = (Button) editProfile.findViewById(R.id.Selesai);
        Cancel = (Button) editProfile.findViewById(R.id.Cancel);

        btnEditPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSnackbar(view, "Layanan Belum Tersedia", 3000);
            }
        });
        btnEditPro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gantiProfile();
            }
        });
        tvTanggal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        Penerima_profile.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });
        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                String date = day + "/" + month + "/" + year;
                tvTanggal.setText(date);
            }
        };
        
        /*SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);*/

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


        ImgBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(Penerima_profile.this, Penerima_Main.class);
                startActivity(mIntent);
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deletePhoto();
            }
        });


        namaProfile = (TextView) findViewById(R.id.namaProfile);
        alamatProfile = (TextView) findViewById(R.id.alamatProfile);
        noTelp = (TextView) findViewById(R.id.noTelp);
        tanggalLahir = (TextView) findViewById(R.id.tanggalLahir);
        fotoProfile = (ImageView) findViewById(R.id.fotoProfile);
        final String userID = user.getUid();

        storageRef.child("Penerima/FotoProfil/" + userID).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                System.out.println(uri);
                Glide.with(getApplicationContext()).load(uri).into(fotoProfile);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });

        btnEditFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImageFotoPenerima();
            }
        });
    }

    public void chooseImageFotoPenerima() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Options");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (items[i].equals("Camera")) {
                    EasyImage.openCamera(Penerima_profile.this, REQUEST_CODE_CAMERA_FOTO);
                } else if (items[i].equals("Gallery")) {
                    EasyImage.openGallery(Penerima_profile.this, REQUEST_CODE_GALLERY_FOTO);
                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showData(DataSnapshot dataSnapshot) {
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            ProfilePenerima uInfo = new ProfilePenerima();
            uInfo.setNama(ds.child("USER").child("PENERIMA").child(userID).getValue(ProfilePenerima.class).getNama());
            uInfo.setAlamat(ds.child("USER").child("PENERIMA").child(userID).getValue(ProfilePenerima.class).getAlamat());
            uInfo.setTanggallahir(ds.child("USER").child("PENERIMA").child(userID).getValue(ProfilePenerima.class).getTanggallahir());
            uInfo.setNohp(ds.child("USER").child("PENERIMA").child(userID).getValue(ProfilePenerima.class).getNohp());
            uInfo.setLevel(ds.child("USER").child("PENERIMA").child(userID).getValue(ProfilePenerima.class).getLevel());
            uInfo.setLongitude(ds.child("USER").child("PENERIMA").child(userID).getValue(ProfilePenerima.class).getLongitude());
            uInfo.setLatitude(ds.child("USER").child("PENERIMA").child(userID).getValue(ProfilePenerima.class).getLatitude());
            uInfo.setNoktp(ds.child("USER").child("PENERIMA").child(userID).getValue(ProfilePenerima.class).getNoktp());
            uInfo.setNarasi(ds.child("USER").child("PENERIMA").child(userID).getValue(ProfilePenerima.class).getNarasi());

            namaProfile.setText("Nama : " + uInfo.getNama());
            alamatProfile.setText("Alamat : " + uInfo.getAlamat());
            noTelp.setText("Nomor Telepon : " + uInfo.getNohp());
            tanggalLahir.setText("Tanggal Lahir : " + uInfo.getTanggallahir());
            desc.setText("Deskripsi : " + uInfo.getNarasi());
            //dialog
            namaEditText.setText(uInfo.getNama());
            etNotelp.setText(uInfo.getNohp());
            desk.setText(uInfo.getNarasi());
            tvTanggal.setText(uInfo.getTanggallahir());
        }
    }

    public void showSnackbar(View view, String message, int duration) {
        Snackbar.make(view, message, duration).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
            @Override
            public void onImagePicked(File imageFile, EasyImage.ImageSource source, int type) {
                switch (type) {
                    case REQUEST_CODE_CAMERA_FOTO:
                        Glide.with(Penerima_profile.this)
                                .load(imageFile)
                                .centerCrop()
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(fotoProfile);
                        filePath2 = Uri.fromFile(imageFile);
                        break;
                    case REQUEST_CODE_GALLERY_FOTO:
                        Glide.with(Penerima_profile.this)
                                .load(imageFile)
                                .centerCrop()
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(fotoProfile);
                        filePath2 = Uri.fromFile(imageFile);
                        break;
                }
            }
        });
    }

    private void uploadImageFotoPenerima() {

        if (filePath2 != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            FirebaseUser user = mAuth.getCurrentUser();
            String userID = user.getUid();
            StorageReference ref = storageRef.child("Penerima/FotoProfil/" + userID);
            ref.putFile(filePath2)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(Penerima_profile.this, "Uploaded Berhasil", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Penerima_profile.this, "Upload Gagal " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
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
            Toast.makeText(Penerima_profile.this, "Gagal uyyyyyy", Toast.LENGTH_SHORT).show();
        }
    }

    private void deletePhoto() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading...");
        progressDialog.show();
        FirebaseUser user = mAuth.getCurrentUser();
        String userID = user.getUid();
        StorageReference ref = storageRef.child("Penerima/FotoProfil/" + userID);
        ref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                uploadImageFotoPenerima();
                progressDialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                uploadImageFotoPenerima();
                progressDialog.dismiss();
            }
        }).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                uploadImageFotoPenerima();
                progressDialog.dismiss();
            }
        });
    }

    public void gantiProfile() {

        selesai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                namaEdit = namaEditText.getText().toString().trim();
                nomorTelp = etNotelp.getText().toString().trim();
                deskripsi = desk.getText().toString().trim();
                tglLahir = tvTanggal.getText().toString().trim();

                myRef2.child("SHAFOOD").child("USER").child("PENERIMA").child(userID).child("nama").setValue(namaEdit);
                myRef2.child("SHAFOOD").child("USER").child("PENERIMA").child(userID).child("nohp").setValue(nomorTelp);
                myRef2.child("SHAFOOD").child("USER").child("PENERIMA").child(userID).child("narasi").setValue(deskripsi);
                myRef2.child("SHAFOOD").child("USER").child("PENERIMA").child(userID).child("tanggallahir").setValue(tglLahir);

                editProfile.dismiss();
            }
        });

        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editProfile.dismiss();
            }
        });

        editProfile.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        editProfile.show();

    }

    @Override
    public void onMapLongClick(LatLng latLng) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }

}
