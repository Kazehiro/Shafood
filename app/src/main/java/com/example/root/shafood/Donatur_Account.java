package com.example.root.shafood;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

public class Donatur_Account extends AppCompatActivity {

    private ImageButton ImgBtnBack;
    private long backPressedTime;
    private Toast backToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donatur__account);

        ImgBtnBack = findViewById(R.id.imageButton);

        ImgBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(Donatur_Account.this, Donatur_Main.class);
                startActivity(mIntent);
            }
        });
    }
}
