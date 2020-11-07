package com.example.inventoryapp.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

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
    public void bindView(View view, final Context context, final Cursor cursor) {
        TextView nameTextView = (TextView) view.findViewById(R.id.list_item_name);
        TextView quantityTextView = (TextView) view.findViewById(R.id.list_item_quantity);
        TextView priceTextView = (TextView) view.findViewById(R.id.list_item_price);

        // Find the columns of product attributes that we're interested in
        int idColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_ID);
        int nameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_NAME);
        int quantityColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_QUANTITY);
        int priceColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRICE);

        // Read the product attributes from the Cursor for the current product
        final int id = cursor.getInt(idColumnIndex);
        String productName = cursor.getString(nameColumnIndex);
        final int productQuantity = cursor.getInt(quantityColumnIndex);
        int productPrice = cursor.getInt(priceColumnIndex);

        // Update the TextViews with the attributes for the current pet
        nameTextView.setText("Name: " + productName);
        quantityTextView.setText("Quantity: " + productQuantity);
        priceTextView.setText("Price: " + productPrice + "$");
        Button sale = view.findViewById(R.id.sale);
        sale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (productQuantity>1){
                    ContentValues values = new ContentValues();
                    values.put(ProductEntry.COLUMN_QUANTITY,productQuantity-1);
                    String selection = ProductEntry.COLUMN_ID + "=?";
                    String [] selectionArgs = new String[] {String.valueOf(id)};
                    context.getContentResolver().update(ProductEntry.CONTENT_URI,values,selection,selectionArgs);
                }else{
                    Toast.makeText(context,"Quantity can't be less than one",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    }
