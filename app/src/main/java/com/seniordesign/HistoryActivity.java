package com.seniordesign;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class HistoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        setTitle("HISTORY");
    }

    public void gotoLocation(View view) {
        Intent intent = new Intent(this, ResultsActivity.class);
        startActivity(intent);
    }
}
