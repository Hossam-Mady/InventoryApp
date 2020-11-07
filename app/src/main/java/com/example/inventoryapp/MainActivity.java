package com.example.inventoryapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import com.example.inventoryapp.data.ProductContract.ProductEntry;
import com.example.inventoryapp.data.ProductCursor;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int URL_LOADER = 0;
    ProductCursor productCursor;
    Cursor cursor = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView product_list = (ListView) findViewById(R.id.product_list);
        View empty = findViewById(R.id.empty_view);
        product_list.setEmptyView(empty);
        productCursor = new ProductCursor(this,cursor);
        product_list.setAdapter(productCursor);
        product_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent i = new Intent(MainActivity.this,DetailsActivity.class);
                Uri currentProductUri = ContentUris.withAppendedId(ProductEntry.CONTENT_URI, id);

                // Set the URI on the data field of the intent
                i.setData(currentProductUri);

                // Launch the {@link EditorActivity} to display the data for the current product.
                startActivity(i);
            }
        });


        getLoaderManager().initLoader(URL_LOADER,null,this);
        Button add = findViewById(R.id.add_new_product);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),EditorActivity.class);
                startActivity(intent);
            }
        });
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle args) {
        switch(i){
            case URL_LOADER:
                String [] projection = {ProductEntry.COLUMN_ID,ProductEntry.COLUMN_NAME,ProductEntry.COLUMN_QUANTITY,ProductEntry.COLUMN_PRICE};
                return new CursorLoader(getApplicationContext(),ProductEntry.CONTENT_URI,projection,null,null,null);
            default:
                return null;
        }

    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        productCursor.swapCursor(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        productCursor.swapCursor(null);
    }
}
