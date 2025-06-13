package com.example.mybills;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.*;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class BillListActivity extends AppCompatActivity {

    ListView listView;
    ArrayList<String> listItems = new ArrayList<>();
    ArrayList<Integer> ids = new ArrayList<>();
    DatabaseHelper dbHelper;
    FirebaseUser user;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_list);

        listView = findViewById(R.id.listView);
        dbHelper = new DatabaseHelper(this);
        user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            Toast.makeText(this, "Please login first.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listItems);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Intent i = new Intent(BillListActivity.this, DetailActivity.class);
            i.putExtra("bill_id", ids.get(position));
            startActivity(i);
        });
    }

    // Reload list every time user returns to this screen
    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    private void loadData() {
        listItems.clear();
        ids.clear();

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        try (Cursor cursor = db.rawQuery("SELECT id, month FROM bills WHERE user_id=?", new String[]{user.getEmail()})) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(0);
                String month = cursor.getString(1);
                listItems.add(month);
                ids.add(id);
            }
        }

        adapter.notifyDataSetChanged();
    }
}
