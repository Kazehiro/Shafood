package com.example.root.shafood;


import android.content.Intent;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;


import com.mukeshsolanki.sociallogin.google.GoogleHelper;
import com.mukeshsolanki.sociallogin.google.GoogleListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MainActivity extends AppCompatActivity implements GoogleListener {
    GoogleHelper mGoogle;
    private static final String TAG = "MainActivity";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private ProgressDialog progressDialog;
    private long backPressedTime;
    private Toast backToast;
    private int progressStatus = 0;

    // UI references.
    private EditText mEmail, mPassword;
    private Button btnSignIn;
    private FirebaseAuth firebaseAuth;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        mGoogle.onActivityResult(requestCode,resultCode,data);
    }

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
        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        mGoogle = new GoogleHelper(this, this, null);


        Button btnGoogle;
        btnGoogle = (Button) findViewById(R.id.btnGoogle);
        btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGoogle.performSignIn(MainActivity.this);
            }
        });


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            private View view;

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
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
                String email = mEmail.getText().toString();
                String pass = mPassword.getText().toString();
                if (!email.equals("") && !pass.equals("")) {
                    mAuth.signInWithEmailAndPassword(email, pass);
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
    public void onGoogleAuthSignIn(String authToken, String userId) {
        Toast.makeText(this, ""+userId, Toast.LENGTH_SHORT).show();
        System.out.println(userId);
    }

    @Override
    public void onGoogleAuthSignInFailed(String errorMessage) {
        Toast.makeText(this, ""+errorMessage, Toast.LENGTH_SHORT).show();
        System.out.println("GAGAl Login uy");
    }

    @Override
    public void onGoogleAuthSignOut() {
        Toast.makeText(this, "Sign Out", Toast.LENGTH_SHORT).show();
        System.out.println("signOut Wa");
    }
}