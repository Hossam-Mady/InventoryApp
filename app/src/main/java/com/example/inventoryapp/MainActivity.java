package com.example.inventoryapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.example.inventoryapp.data.ProductContract.ProductEntry;
import com.example.inventoryapp.data.ProductContract;
import com.example.inventoryapp.data.ProductCursor;
import com.example.inventoryapp.data.ProductDbHelper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
    ProductCursor productCursor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String [] projetion = {ProductEntry.COLUMN_ID,ProductEntry.COLUMN_NAME,ProductEntry.COLUMN_QUANTITY,ProductEntry.COLUMN_PRICE};
        Cursor cursor = getContentResolver().query(ProductEntry.CONTENT_URI,projetion,null,null,null);
        ListView product_list = (ListView) findViewById(R.id.product_list);
        View empty = findViewById(R.id.empty_view);
        product_list.setEmptyView(empty);
        productCursor = new ProductCursor(this,cursor);
        product_list.setAdapter(productCursor);

        Button add = (Button) findViewById(R.id.add_new_product);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),EditorActivity.class);
                startActivity(intent);
            }
        });
    }
}
