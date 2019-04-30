package com.seniordesign;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CustomList extends ArrayAdapter<ItemListActivity.Item> {

    private final Activity context;
    private final List<ItemListActivity.Item> items;

    public CustomList(Activity context, List<ItemListActivity.Item> items) {
        super(context, R.layout.list_single, items);
        this.context = context;
        this.items = items;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.list_single, null, true);
        TextView titleText = (TextView) rowView.findViewById(R.id.title);
        TextView priceText = (TextView)rowView.findViewById(R.id.price);
        TextView linkText =  (TextView)rowView.findViewById(R.id.link);
        titleText.setText(items.get(position).title);

        StringBuilder dollars = new StringBuilder("$");
        Random random = new Random();
        int numDollars = random.nextInt(3);

        for (int i = 0; i < numDollars; i++) {
            dollars.append("$");
        }

        priceText.setText(dollars.toString());
        linkText.setText(items.get(position).link);
        return rowView;
    }
}
