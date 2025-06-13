package com.example.mybills;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class DetailActivity extends AppCompatActivity {

    private static final int REQUEST_UPDATE_BILL = 1;

    TextView tvDetails;
    DatabaseHelper dbHelper;
    Button btnUpdate;
    Button btnDelete;
    int billId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        tvDetails = findViewById(R.id.tvDetails);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnDelete = findViewById(R.id.btnDelete);

        dbHelper = new DatabaseHelper(this);
        billId = getIntent().getIntExtra("bill_id", -1);

        if (billId == -1) {
            Toast.makeText(this, "Invalid bill ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadBillDetails();  // Load details initially

        btnUpdate.setOnClickListener(v -> {
            Intent intent = new Intent(this, UpdateBillActivity.class);
            intent.putExtra("bill_id", billId);
            startActivityForResult(intent, REQUEST_UPDATE_BILL);
        });

        btnDelete.setOnClickListener(v -> {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            int rows = db.delete("bills", "id=?", new String[]{String.valueOf(billId)});
            if (rows > 0) {
                Toast.makeText(this, "Bill deleted successfully", Toast.LENGTH_SHORT).show();
                Intent resultIntent = new Intent();
                resultIntent.putExtra("deleted", true);
                setResult(RESULT_OK, resultIntent);
                finish();
            } else {
                Toast.makeText(this, "Failed to delete bill", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadBillDetails() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM bills WHERE id=?", new String[]{String.valueOf(billId)});
        if (cursor.moveToFirst()) {
            // Use column names instead of fixed indexes for safety
            String month = cursor.getString(cursor.getColumnIndexOrThrow("month"));
            double units = cursor.getDouble(cursor.getColumnIndexOrThrow("units"));
            double rebate = cursor.getDouble(cursor.getColumnIndexOrThrow("rebate"));
            double total = cursor.getDouble(cursor.getColumnIndexOrThrow("total"));
            double finalCost = cursor.getDouble(cursor.getColumnIndexOrThrow("final"));

            String rebatePercent = String.format("%.0f%%", rebate);
            String result = "Month: " + month +
                    "\nUnits: " + units +
                    "\nRebate: " + rebatePercent +
                    "\nTotal: RM " + String.format("%.2f", total) +
                    "\nFinal Cost: RM " + String.format("%.2f", finalCost);

            tvDetails.setText(result);
        }
        cursor.close();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_UPDATE_BILL && resultCode == RESULT_OK) {
            Toast.makeText(this, "Bill updated successfully", Toast.LENGTH_SHORT).show();
            loadBillDetails();  // Refresh the UI with updated info
        }
    }
}
