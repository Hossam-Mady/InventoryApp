package com.example.inventoryapp.data;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;
import com.example.inventoryapp.data.ProductContract.ProductEntry;
import com.example.inventoryapp.R;

public class ProductCursor extends CursorAdapter {

    public ProductCursor(Context context, Cursor c) {
        super(context, c,0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item,parent,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView nameTextView = (TextView) view.findViewById(R.id.list_item_name);
        TextView quantityTextView = (TextView) view.findViewById(R.id.list_item_quantity);
        TextView priceTextView = (TextView) view.findViewById(R.id.list_item_price);

        // Find the columns of product attributes that we're interested in
        int nameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_NAME);
        int quantityColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_QUANTITY);
        int priceColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRICE);

        // Read the product attributes from the Cursor for the current product
        String productName = cursor.getString(nameColumnIndex);
        int productQuantity = cursor.getInt(quantityColumnIndex);
        int productPrice = cursor.getInt(priceColumnIndex);

        // Update the TextViews with the attributes for the current pet
        nameTextView.setText("Name: " + productName);
        quantityTextView.setText("Quantity: " + productQuantity);
        priceTextView.setText("Price: " + productPrice + "$");
    }

    }
