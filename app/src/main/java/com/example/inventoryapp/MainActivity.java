package com.example.inventoryapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.example.inventoryapp.data.ProductContract.ProductEntry;
import com.example.inventoryapp.data.ProductContract;
import com.example.inventoryapp.data.ProductDbHelper;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ProductDbHelper productDbHelper = new ProductDbHelper(this);
        SQLiteDatabase database = productDbHelper.getReadableDatabase();
        String [] projetion = {ProductEntry.COLUMN_ID,ProductEntry.COLUMN_NAME,ProductEntry.COLUMN_QUANTITY};
        Cursor cursor = database.query(ProductEntry.TABLE_NAME,projetion,null,null,null,null,null);
        TextView display = (TextView) findViewById(R.id.display_text_view);
        while (cursor.moveToNext()){
            int name = cursor.getColumnIndex(ProductEntry.COLUMN_NAME);
            int quantity = cursor.getColumnIndex(ProductEntry.COLUMN_QUANTITY);
            String disp = "\n\n" + cursor.getString(name) + "\n" + cursor.getString(quantity);
            display.append(disp);
        }
        cursor.close();

        Button add = (Button) findViewById(R.id.add_new_product);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addDummy();
            }
        });
    }

    public void addDummy (){
        ContentValues values = new ContentValues();
        values.put(ProductEntry.COLUMN_NAME,"TETE");
        values.put(ProductEntry.COLUMN_EMAIL,"ali@gmail.com");
        values.put(ProductEntry.COLUMN_PRICE,2);
        values.put(ProductEntry.COLUMN_QUANTITY,10);
        getContentResolver().insert(ProductEntry.CONTENT_URI,values);

    }
}
