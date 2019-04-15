package com.seniordesign;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemListActivity extends AppCompatActivity {

    private class Item {
        String title;
        String link;

        public Item(String title, String link) {
            this.title = title;
            this.link = link;
        }

        @Override
        public String toString() {
            return String.format("Title - %s, Link - %s", title, link);
        }
    }

    private String getLink(String item) {
        String[] linkSplit = item.split(delimiter)[1].split(":");
        if (linkSplit.length < 3) {
            return null;
        }
        return linkSplit[1] + ":" + linkSplit[2];
    }


    final static String delimiter = ",";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);
        Bundle bundle = getIntent().getExtras();
        String httpStr = bundle.getString("http_string");
        String[] httpStrSplit = httpStr.split("\n");
        List<String> items = new ArrayList<>();

        for (String item : httpStrSplit) {
            String title = item.split(delimiter)[0].split(":")[1];
            System.out.println(getLink(item));
            String link = getLink(item);
            if (link == null) {
                continue;
            }
            items.add(new Item(title, link).toString());
        }
//
//        for (String item : items) {
//            System.out.println(item);
//        }
//
        ListAdapter myAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
        ListView myListView = (ListView)findViewById(R.id.myListView);
        myListView.setAdapter(myAdapter);

        myListView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String item = String.valueOf(parent.getItemAtPosition(position));
                        StringBuilder sb = new StringBuilder();
                        String[] linkSplit = item.split(delimiter)[1].split("-");
                        for (int i = 1; i < linkSplit.length; i++) {
                            sb.append(linkSplit[i]);
                        }
                        String link = sb.toString().trim();
//                        Toast.makeText(ItemListActivity.this, link, Toast.LENGTH_LONG).show();

                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                        startActivity(browserIntent);
                    }
                }
        );
    }
}
