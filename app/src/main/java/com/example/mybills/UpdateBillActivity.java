package com.example.mybills;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class UpdateBillActivity extends AppCompatActivity {

    private EditText etUnits, etRebate;
    private Spinner spinnerMonth;
    private Button btnSave;
    private TextView tvTotal, tvFinal;
    private DatabaseHelper dbHelper;
    private int billId;
    private double total = 0;
    private double finalCost = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_bill);

        etUnits = findViewById(R.id.etUnitsUpdate);
        etRebate = findViewById(R.id.etRebateUpdate);
        spinnerMonth = findViewById(R.id.spinnerMonthUpdate);
        btnSave = findViewById(R.id.btnSave);
        tvTotal = findViewById(R.id.tvTotal);
        tvFinal = findViewById(R.id.tvFinal);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.months_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMonth.setAdapter(adapter);

        dbHelper = new DatabaseHelper(this);
        billId = getIntent().getIntExtra("bill_id", -1);

        loadBillData();

        btnSave.setOnClickListener(v -> {
            if (validateAndCalculate()) {
                updateBill();
            }
        });
    }

    private void loadBillData() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM bills WHERE id=?", new String[]{String.valueOf(billId)});
        if (cursor.moveToFirst()) {
            String month = cursor.getString(cursor.getColumnIndexOrThrow("month"));
            spinnerMonth.setSelection(((ArrayAdapter) spinnerMonth.getAdapter()).getPosition(month));
            etUnits.setText(cursor.getString(cursor.getColumnIndexOrThrow("units")));
            etRebate.setText(cursor.getString(cursor.getColumnIndexOrThrow("rebate")));
        }
        cursor.close();
    }

    private boolean validateAndCalculate() {
        String month = spinnerMonth.getSelectedItem().toString();
        String unitText = etUnits.getText().toString().trim();
        String rebateText = etRebate.getText().toString().trim();

        if (month.equals("<Please select month>") || month.equals("Month")) {
            Toast.makeText(this, "Please select a month", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (unitText.isEmpty()) {
            Toast.makeText(this, "Please enter the units", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (rebateText.isEmpty()) {
            Toast.makeText(this, "Please enter the rebate", Toast.LENGTH_SHORT).show();
            return false;
        }

        double units;
        double rebatePercent;

        try {
            units = Double.parseDouble(unitText);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid units input", Toast.LENGTH_SHORT).show();
            return false;
        }

        try {
            rebatePercent = Double.parseDouble(rebateText);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid rebate input", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (rebatePercent < 0 || rebatePercent > 5) {
            Toast.makeText(this, "Please enter rebate between 0 and 5", Toast.LENGTH_SHORT).show();
            return false;
        }

        rebatePercent /= 100.0;

        total = 0;
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

        finalCost = total - (total * rebatePercent);

        tvTotal.setText(String.format("Total Charges: RM %.2f", total));
        tvFinal.setText(String.format("Final Cost: RM %.2f", finalCost));

        return true;
    }

    private void updateBill() {
        String month = spinnerMonth.getSelectedItem().toString();
        double units = Double.parseDouble(etUnits.getText().toString().trim());
        double rebate = Double.parseDouble(etRebate.getText().toString().trim());

        ContentValues values = new ContentValues();
        values.put("month", month);
        values.put("units", units);
        values.put("rebate", rebate);
        values.put("total", total);
        values.put("final", finalCost);

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rows = db.update("bills", values, "id=?", new String[]{String.valueOf(billId)});

        if (rows > 0) {
            Toast.makeText(this, "Bill updated successfully", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);    // <-- THIS LINE is added
            finish();
        } else {
            Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show();
        }
    }
}
