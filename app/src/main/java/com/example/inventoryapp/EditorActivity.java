package com.example.inventoryapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.example.inventoryapp.data.ProductContract.ProductEntry;

public class EditorActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 100;
    EditText name_edit_text;
    EditText quantity_edit_text;
    EditText price_edit_text;
    EditText email_edit_text;
    ImageView selected_image;
    Boolean isImageSelected = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        name_edit_text = (EditText) findViewById(R.id.name_text_view);

        quantity_edit_text = (EditText) findViewById(R.id.quantity_text_view);

        price_edit_text = (EditText) findViewById(R.id.price_text_view);

        email_edit_text = (EditText) findViewById(R.id.email_text_view);

        selected_image = (ImageView) findViewById(R.id.select_image);
        selected_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                readImage();
            }
        });

        Button add = (Button) findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertData();
            }
        });
    }

    public void insertData(){
        String name = name_edit_text.getText().toString();
        if(name.isEmpty()){
            Toast.makeText(this,"Name of product is required",Toast.LENGTH_SHORT).show();
            return;
        }

        String string_quantity = quantity_edit_text.getText().toString();
        if (string_quantity.isEmpty()){
            Toast.makeText(this,"Please enter product quantity",Toast.LENGTH_SHORT).show();
            return;
        }

        int quantity = Integer.valueOf(string_quantity);
        if (quantity<1){
            Toast.makeText(this,"Quantity can't be less than 1",Toast.LENGTH_SHORT).show();
            return;
        }

        String string_price = price_edit_text.getText().toString();
        if (string_price.isEmpty()){
            Toast.makeText(this,"Please enter product price",Toast.LENGTH_SHORT).show();
            return;
        }

        int price = Integer.valueOf(string_price);
        if (price<1){
            Toast.makeText(this,"Price can't be less than 1",Toast.LENGTH_SHORT).show();
            return;
        }

        String email = email_edit_text.getText().toString();
        if(email.isEmpty()){
            Toast.makeText(this,"Supplier email is required",Toast.LENGTH_SHORT).show();
            return;
        }

        if(!(android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())){
            Toast.makeText(this,"Please write valid email address",Toast.LENGTH_SHORT).show();
            return;
        }

        if(!isImageSelected){
            Toast.makeText(this,"Please select the image",Toast.LENGTH_SHORT).show();
            return;
        }
        Bitmap image = ((BitmapDrawable)selected_image.getDrawable()).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG,10,byteArrayOutputStream);
        byte imageInByte[] = byteArrayOutputStream.toByteArray();

        ContentValues values = new ContentValues();
        values.put(ProductEntry.COLUMN_NAME,name);
        values.put(ProductEntry.COLUMN_EMAIL,email);
        values.put(ProductEntry.COLUMN_PRICE,price);
        values.put(ProductEntry.COLUMN_QUANTITY,quantity);
        values.put(ProductEntry.COLUMN_IMAGE,imageInByte);
        Uri newUri = getContentResolver().insert(ProductEntry.CONTENT_URI,values);
        if (newUri == null) {
            // If the new content URI is null, then there was an error with insertion.
            Toast.makeText(this, getString(R.string.editor_insert_product_failed),
                    Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the insertion was successful and we can display a toast.
            Toast.makeText(this, getString(R.string.editor_insert_product_successful),
                    Toast.LENGTH_SHORT).show();
        }
        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(intent);
    }

    public void readImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"),REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK){
            try{
                Uri selectedImage = data.getData();
                InputStream imageStream = getContentResolver().openInputStream(selectedImage);
                selected_image.setImageBitmap(BitmapFactory.decodeStream(imageStream));
                isImageSelected = true;

            }catch (IOException exeption){
                exeption.printStackTrace();
            }
        }
}
}
