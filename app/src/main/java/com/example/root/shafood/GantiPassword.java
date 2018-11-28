package com.example.root.shafood;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;

public class GantiPassword extends AppCompatActivity {

    private EditText editTextPassword, editTextRePassword, editTextPasswordLama;
    private Button btnEditPassword;
    private String email, name, uid, providerId, newPassword, passLama, rePass;
    private int level;
    private FirebaseUser user;
    private AuthCredential credential;
    private long backPressedTime;
    private Toast backToast;
    private ProgressDialog progressDialog;
    private int progressStatus = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ganti_password);

        editTextPasswordLama = (EditText) findViewById(R.id.editTextPasswordLama);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        editTextRePassword = (EditText) findViewById(R.id.editTextRePassword);
        btnEditPassword = (Button) findViewById(R.id.btnEditPassword);
        level = getIntent().getIntExtra("level", level);

        progressDialog = new ProgressDialog(this);
        user = FirebaseAuth.getInstance().getCurrentUser();
        email = user.getEmail();
        btnEditPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(GantiPassword.this, "Mantap", Toast.LENGTH_SHORT);
                newPassword = editTextPassword.getText().toString().trim();
                rePass = editTextRePassword.getText().toString().trim();
                if (newPassword.equals(rePass) && !newPassword.equals("") && !rePass.equals("")) {
                    passLama = editTextPasswordLama.getText().toString().trim();
                    credential = EmailAuthProvider.getCredential(email, passLama);
                    user.reauthenticate(credential).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(GantiPassword.this,"Password Sebelumnya Salah",Toast.LENGTH_LONG).show();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            gantiPassword();
                        }
                    });
                } else {
                    Snackbar.make(view, "Password Tidak Sama", 5000);
                    return;
                }
            }
        });
    }

    public void gantiPassword() {
        user.updatePassword(newPassword).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(GantiPassword.this,"Gagal Mengganti Password",Toast.LENGTH_LONG).show();
            }
        }).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d("User password updated.", newPassword);
                    System.out.println("LEVEL BOIIII ====== " + level);
                    /*if (level == 2) {
                        Intent mIntent = new Intent(GantiPassword.this, Donatur_Main.class);
                        startActivity(mIntent);
                    } else if (level == 3) {
                        Intent mIntent = new Intent(GantiPassword.this, Kurir_Main_MAIN.class);
                        startActivity(mIntent);
                    } else {
                        Intent mIntent = new Intent(GantiPassword.this, Penerima_Main.class);
                        startActivity(mIntent);
                    }*/
                    Intent mIntent = new Intent(GantiPassword.this, lengkapidata_penerima.class);
                    startActivity(mIntent);
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
            }
        });
    }
    @Override
    public void onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            backToast.cancel();
            super.onBackPressed();
            Intent intent = new Intent(GantiPassword.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("EXIT", true);
            startActivity(intent);
        } else {
            backToast = Toast.makeText(getBaseContext(), "Tekan Lagi Untuk Keluar", Toast.LENGTH_SHORT);
            backToast.show();
        }
        backPressedTime = System.currentTimeMillis();
    }

}
