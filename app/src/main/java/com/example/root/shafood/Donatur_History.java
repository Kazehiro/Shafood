package com.example.root.shafood;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

public class Donatur_History extends AppCompatActivity {

    private ImageButton ImgBtnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donatur__history);

        ImgBtnBack = findViewById(R.id.imageButton);

        ImgBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(Donatur_History.this, Donatur_Main.class);
                startActivity(mIntent);
            }
        });
    }
}
