package com.example.mybills;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DashboardActivity extends AppCompatActivity {

    Button btnEstimate, btnViewBills, btnAccount, btnAbout, btnLogout;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        TextView tvWelcome = findViewById(R.id.tvWelcome);
        btnEstimate = findViewById(R.id.btnEstimate);
        btnViewBills = findViewById(R.id.btnViewBills);
        btnAccount = findViewById(R.id.btnAccount);
        btnAbout = findViewById(R.id.btnAbout);
        btnLogout = findViewById(R.id.btnLogout);
        mAuth = FirebaseAuth.getInstance();

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            tvWelcome.setText("WELCOME " + user.getEmail());

            // Show toast only if passed from MainActivity
            String welcomeMsg = getIntent().getStringExtra("WELCOME_MSG");
            if (welcomeMsg != null) {
                Toast.makeText(this, welcomeMsg, Toast.LENGTH_LONG).show();
            }
        }

        btnEstimate.setOnClickListener(v -> {
            Intent i = new Intent(DashboardActivity.this, EstimateActivity.class);
            startActivity(i);
        });

        btnViewBills.setOnClickListener(v -> {
            Intent i = new Intent(DashboardActivity.this, BillListActivity.class);
            startActivity(i);
        });

        btnAccount.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, AccountActivity.class);
            startActivity(intent);
        });

        btnAbout.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, AboutActivity.class);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            Intent i = new Intent(DashboardActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        });
    }
}
