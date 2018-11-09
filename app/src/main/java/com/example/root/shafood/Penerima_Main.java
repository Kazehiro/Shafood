package com.example.root.shafood;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Penerima_Main extends AppCompatActivity {

    private Button BtnQrcode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_penerima__main);

    BtnQrcode = (Button) findViewById(R.id.BtnQrcode);


    BtnQrcode.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
        Intent mIntent = new Intent(Penerima_Main.this, Penerima_Verifikasi.class);
        startActivity(mIntent);
        }
    });
    }
}
