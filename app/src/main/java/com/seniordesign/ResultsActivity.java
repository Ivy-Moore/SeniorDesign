package com.seniordesign;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ResultsActivity extends AppCompatActivity {

//    private class Item {
//        String title;
//        String link;
//
//        public Item(String title, String link) {
//            this.title = title;
//            this.link = link;
//        }
//
//        @Override
//        public String toString() {
//            return String.format("Title - %s, Link - %s", title, link);
//        }
//    }

    private TextView httpString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        setTitle("RESULTS");

        httpString = findViewById(R.id.http_string);

        Bundle bundle = getIntent().getExtras();
        String httpStr = bundle.getString("http_string");
        String[] httpStrSplit = httpStr.split("\n");
//        List<Item> items = new ArrayList<>();
//
//        for (String item : httpStrSplit) {
//            String title = item.split(",")[0].split(":")[1];
//            String link = item.split(",")[1].split(":")[1];
//            items.add(new Item(title, link));
//        }
//
//        for (Item item : items) {
//            System.out.println(item);
//        }

        httpString.setText(httpStr);

    }


    public void gotoLocation(View view) {
        Intent intent = new Intent(this, LocationActivity.class);
        startActivity(intent);
    }
}
