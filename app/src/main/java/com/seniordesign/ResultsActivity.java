package com.seniordesign;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class ResultsActivity extends AppCompatActivity {

    private TextView httpString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        setTitle("RESULTS");

        httpString = findViewById(R.id.http_string);

        Bundle bundle = getIntent().getExtras();
        String httpStr = bundle.getString("http_string");
        httpString.setText(httpStr);

    }


    public void gotoLocation(View view) {
        Intent intent = new Intent(this, LocationActivity.class);
        startActivity(intent);
    }
}
