package com.example.root.shafood;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Register extends AppCompatActivity {

    //defining view objects
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editReTextPassword;
    private Button buttonSignup;
    private ProgressDialog progressDialog;
    private Spinner spPilih;

    //defining firebaseauth object
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //initializing firebase auth object
        firebaseAuth = FirebaseAuth.getInstance();

        //initializing views
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        editReTextPassword = (EditText) findViewById(R.id.editTextRePassword);
        buttonSignup = (Button) findViewById(R.id.buttonSignup);

        progressDialog = new ProgressDialog(this);
        TextView textViewSignIn = (TextView) findViewById(R.id.textViewSignin);
        textViewSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(Register.this, MainActivity.class);
                startActivity(mIntent);
            }
        });

        spPilih = (Spinner) findViewById(R.id.sp_Pilih);
    }

    public void registerUser(View view) {

        //getting email and password from edit texts
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String repassword = editReTextPassword.getText().toString().trim();

        //checking if email and passwords are empty
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter email", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(password) && TextUtils.isEmpty(repassword)) {
            Toast.makeText(this, "Please enter password", Toast.LENGTH_LONG).show();
            return;
        }
        if (password.equals(repassword)) {
        }
        else{
            Toast.makeText(this, "Password Tidak Sesuai", Toast.LENGTH_LONG).show();
            return;
        }

        //if the email and password are not empty
        //displaying a progress dialog

        progressDialog.setMessage("Tunggu Sebentar...");
        progressDialog.show();

        //creating a new user
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //checking if success
                        if(task.isSuccessful()) {
                            //display some message here
                            Toast.makeText(Register.this, "Successfully registered "+spPilih.getSelectedItem(), Toast.LENGTH_LONG).show();
                            if (spPilih.getSelectedItem().toString().equals("Donatur")) {
                                Intent register = new Intent(Register.this, lengkapidata_donatur.class);
                                startActivity(register);
                            }
                            else if(spPilih.getSelectedItem().toString().equals("Kurir")){
                                Intent register = new Intent(Register.this, lengkapidata_kurir.class);
                                startActivity(register);
                            }
                            else if(spPilih.getSelectedItem().toString().equals("Penerima")){
                                Intent register = new Intent(Register.this, lengkapidata_penerima.class);
                                startActivity(register);
                            }
                            else{
                                Toast.makeText(Register.this,"Pilih Level",Toast.LENGTH_LONG).show();
                            }
                        }
                        else{
                            //display some message here
                            Toast.makeText(Register.this,"Registration Error",Toast.LENGTH_LONG).show();
                        }
                        progressDialog.dismiss();
                    }
                });
    }
}