package com.example.mybills;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        TextView tv = findViewById(R.id.tvAbout);
        TextView link = findViewById(R.id.tvGithub);

        tv.setText("Name: Ain Nazirah Binti Mohammad Ibrahim\nStudent ID: 2024988457\nGroup: CDCS2405B\nCourse: ICT602 Mobile Technology\n© 2025");
        link.setText("GitHub: https://ufuture.uitm.edu.my/home/");
        link.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://ufuture.uitm.edu.my/home/"));
            startActivity(browserIntent);
        });
    }
}