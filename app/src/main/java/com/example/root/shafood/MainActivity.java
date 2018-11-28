package com.example.root.shafood;


import android.content.Intent;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthProvider;
import com.mukeshsolanki.sociallogin.google.GoogleHelper;
import com.mukeshsolanki.sociallogin.google.GoogleListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;


public class MainActivity extends AppCompatActivity {
    GoogleHelper mGoogle;
    private static final String TAG = "MainActivity";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private ProgressDialog progressDialog;
    private long backPressedTime;
    private Toast backToast;
    private int progressStatus = 0;
    private final static int LOGIN_PERMISSION = 1000;
    private String email, pass;
    private FirebaseUser user1;
    private FirebaseAuthException mFirebaseAuthException;

    // UI references.
    private EditText mEmail, mPassword;
    private Button btnSignIn;
    private Button btnGoogle;
    ;
    private FirebaseAuth firebaseAuth;
    private static final int LOCATION_REQUEST = 500;
    public static final int REQUEST_CODE_CAMERA_FOTO = 0020;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
        }
        mEmail = (EditText) findViewById(R.id.email);
        mPassword = (EditText) findViewById(R.id.password);
        btnSignIn = (Button) findViewById(R.id.email_sign_in_button);
        btnGoogle = (Button) findViewById(R.id.btnGoogle);
        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*startActivityForResult(
                        AuthUI.getInstance().createSignInIntentBuilder()
                                .setIsSmartLockEnabled(true)
                                .build(),LOGIN_PERMISSION);*/
                showSnackbar(v, "Layanan Belum Tersedia", 3000);

            }
        });


        int currentApiVersion = Build.VERSION.SDK_INT;
        if (currentApiVersion >= Build.VERSION_CODES.M) {
            if (checkPermissionCamera()) {
            } else {
                requestPermissionCamera();
            }
            if (checkPermissionLocation()) {
            } else {
                requestPermissionLocation();
            }
        } else {
            if (checkPermissionCamera()) {
            } else {
                requestPermissionCamera();
            }
            if (checkPermissionLocation()) {
            } else {
                requestPermissionLocation();
            }
        }

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            private View view;

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user1 = firebaseAuth.getCurrentUser();
                if (user1 != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user1.getUid());
                    Intent i = new Intent(MainActivity.this, Berhasil.class);
                    MainActivity.this.startActivity(i);
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = mEmail.getText().toString();
                pass = mPassword.getText().toString();
                if (!email.equals("") && !pass.equals("")) {
                    mAuth.signInWithEmailAndPassword(email, pass).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            Toast.makeText(MainActivity.this,"Masuk Sebagai "+ email,Toast.LENGTH_LONG).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this,"Login Failed !!!",Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    showSnackbar(view, "Harap isi semua kolom", 3000);
                    return;
                }

                progressDialog.setMax(100);
                progressDialog.setMessage("Tunggu Sebentar");
                progressDialog.setTitle("Login");

                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setIndeterminate(false);
                progressDialog.show();
                progressStatus = 0;
                final Handler handle = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);
                        progressDialog.incrementProgressBy(1);
                    }
                };
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (progressStatus < 100) {
                            progressStatus += 1;
                            try {
                                Thread.sleep(10);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            handle.post(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.setProgress(progressStatus);
                                    if (progressStatus == 100) {
                                        progressDialog.dismiss();
                                    }
                                }
                            });
                        }
                    }
                }).start();
            }
        });
    }


    public void register(View view) {
        Intent register = new Intent(MainActivity.this, Register.class);
        MainActivity.this.startActivity(register);
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

    //add a toast to show when successfully signed in

    /**
     * customizable toast
     *
     * @param message
     */
    private void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            backToast.cancel();
            super.onBackPressed();
            Intent intent = new Intent(MainActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("EXIT", true);
            startActivity(intent);
        } else {
            backToast = Toast.makeText(getBaseContext(), "Tekan Lagi Untuk Keluar", Toast.LENGTH_SHORT);
            backToast.show();
        }
        backPressedTime = System.currentTimeMillis();
    }

    public void showSnackbar(View view, String message, int duration) {
        Snackbar.make(view, message, duration).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == LOGIN_PERMISSION) {
            startNewActivity(resultCode, data);
        } else {
            Toast.makeText(this, "Login Failed !!!", Toast.LENGTH_LONG).show();
        }
        if (user1 == null) {
            Toast.makeText(MainActivity.this, "Login Failed !!!", Toast.LENGTH_LONG).show();
        }
    }

    private void startNewActivity(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Intent intent = new Intent(MainActivity.this, Berhasil.class);
            intent.putExtra("email", email);
            intent.putExtra("pass", pass);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Login Failed !!!", Toast.LENGTH_SHORT).show();
        }
        if (user1 == null) {
            Toast.makeText(MainActivity.this, "Login Failed !!!", Toast.LENGTH_LONG).show();
        }
    }

    private boolean checkPermissionLocation() {
        return (ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermissionLocation() {
        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION}, LOCATION_REQUEST);

    }

    private boolean checkPermissionCamera() {
        return (ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermissionCamera() {
        ActivityCompat.requestPermissions(this, new String[]{CAMERA}, REQUEST_CODE_CAMERA_FOTO);
    }

    @Override
    protected void onResume() {
        super.onResume();
        int currentApiVersion = Build.VERSION.SDK_INT;
        if (currentApiVersion >= Build.VERSION_CODES.M) {
            if (checkPermissionCamera()) {
            } else {
                requestPermissionCamera();
            }
            if (checkPermissionLocation()) {
            } else {
                requestPermissionLocation();
            }
        } else {
            if (checkPermissionCamera()) {
            } else {
                requestPermissionCamera();
            }
            if (checkPermissionLocation()) {
            } else {
                requestPermissionLocation();
            }
        }
    }


}