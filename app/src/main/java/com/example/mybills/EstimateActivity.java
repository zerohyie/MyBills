package com.example.mybills;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EstimateActivity extends AppCompatActivity {

    Spinner spinnerMonth;
    EditText etUnits, etRebate;
    TextView tvTotal, tvFinal;
    Button btnCalc;
    DatabaseHelper dbHelper;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estimate);

        spinnerMonth = findViewById(R.id.spinnerMonth);
        etUnits = findViewById(R.id.etUnits);
        etRebate = findViewById(R.id.etRebate);
        tvTotal = findViewById(R.id.tvTotal);
        tvFinal = findViewById(R.id.tvFinal);
        btnCalc = findViewById(R.id.btnCalc);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.months_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMonth.setAdapter(adapter);

        dbHelper = new DatabaseHelper(this);
        user = FirebaseAuth.getInstance().getCurrentUser();

        btnCalc.setOnClickListener(v -> calculateBill());
    }

    private void calculateBill() {
        String month = spinnerMonth.getSelectedItem().toString();
        String unitText = etUnits.getText().toString().trim();
        String rebateText = etRebate.getText().toString().trim();

        if (month.equals("<Please select month>")) {
            Toast.makeText(this, "Please select a month", Toast.LENGTH_SHORT).show();
            return;
        }

        if (unitText.isEmpty()) {
            Toast.makeText(this, "Please enter the units", Toast.LENGTH_SHORT).show();
            return;
        }

        if (rebateText.isEmpty()) {
            Toast.makeText(this, "Please enter the rebate", Toast.LENGTH_SHORT).show();
            return;
        }

        double units;
        double rebatePercent;

        try {
            units = Double.parseDouble(unitText);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid units input", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            rebatePercent = Double.parseDouble(rebateText);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid rebate input", Toast.LENGTH_SHORT).show();
            return;
        }

        if (rebatePercent < 0 || rebatePercent > 5) {
            Toast.makeText(this, "Please enter rebate between 0 and 5", Toast.LENGTH_SHORT).show();
            return;
        }

        rebatePercent = rebatePercent / 100;

        double total = 0;
        double remaining = units;

        if (remaining > 0) {
            double block = Math.min(remaining, 200);
            total += block * 0.218;
            remaining -= block;
        }
        if (remaining > 0) {
            double block = Math.min(remaining, 100);
            total += block * 0.334;
            remaining -= block;
        }
        if (remaining > 0) {
            double block = Math.min(remaining, 300);
            total += block * 0.516;
            remaining -= block;
        }
        if (remaining > 0) {
            total += remaining * 0.546;
        }

        double finalCost = total - (total * rebatePercent);

        tvTotal.setText(String.format("Total Charges: RM %.2f", total));
        tvFinal.setText(String.format("Final Cost: RM %.2f", finalCost));

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", user.getEmail());
        values.put("month", month);
        values.put("units", units);
        values.put("rebate", rebatePercent * 100);
        values.put("total", total);
        values.put("final", finalCost);
        db.insert("bills", null, values);

        Toast.makeText(this, "Saved to database", Toast.LENGTH_SHORT).show();
    }

}
