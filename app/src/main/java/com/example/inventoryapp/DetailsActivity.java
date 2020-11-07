package com.example.inventoryapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.inventoryapp.data.ProductContract.ProductEntry;


public class DetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int EXISTING_Product_LOADER = 0;
    Uri currentUri;

    TextView name_text_view;
    TextView price_text_view;
    TextView quantity_text_view;
    TextView email_text_view;
    ImageView product_image_view;
    String email;
    int quantity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Intent intent = getIntent();
        currentUri = intent.getData();

        name_text_view = findViewById(R.id.details_name);
        price_text_view = findViewById(R.id.details_price);
        quantity_text_view = findViewById(R.id.details_quantity);
        email_text_view = findViewById(R.id.details_email);
        product_image_view = findViewById(R.id.details_image);

        Button order = findViewById(R.id.order);
        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendEmail();
            }
        });

        final Button decrease_quantity = findViewById(R.id.decrease);
        decrease_quantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                decrease_quantity();
            }
        });

        final Button increase_quantity = findViewById(R.id.increase);
        increase_quantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                increase_quantity();
            }
        });

        Button delete = findViewById(R.id.delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeleteConfirmationDialog();
            }
        });

        getLoaderManager().initLoader(EXISTING_Product_LOADER,null,this);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                ProductEntry._ID,
                ProductEntry.COLUMN_NAME,
                ProductEntry.COLUMN_QUANTITY,
                ProductEntry.COLUMN_PRICE,
                ProductEntry.COLUMN_EMAIL,
                ProductEntry.COLUMN_IMAGE
        };

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,currentUri,projection,null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            int name_column_index = cursor.getColumnIndex(ProductEntry.COLUMN_NAME);
            int quantity_column_index = cursor.getColumnIndex(ProductEntry.COLUMN_QUANTITY);
            int price_column_index = cursor.getColumnIndex(ProductEntry.COLUMN_PRICE);
            int email_column_index = cursor.getColumnIndex(ProductEntry.COLUMN_EMAIL);
            int image_column_index = cursor.getColumnIndex(ProductEntry.COLUMN_IMAGE);

            String name = cursor.getString(name_column_index);
            int price = cursor.getInt(price_column_index);
            email = cursor.getString(email_column_index);
            quantity = cursor.getInt(quantity_column_index);
            byte[] image = cursor.getBlob(image_column_index);
            Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0 ,image.length);

            name_text_view.setText("Name: " + name);
            email_text_view.setText("Supplier Email: " + email);
            quantity_text_view.setText( "Quantity: "+ quantity);
            price_text_view.setText("Price: " + price + "$");
            product_image_view.setImageBitmap(bitmap);

    }}

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }


    public void sendEmail() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL,new String[]{email});
        intent.setType("message/rfc822");
        startActivity(Intent.createChooser(intent,getString(R.string.email_chooser_title)));
    }

    public void increase_quantity(){
        ContentValues values = new ContentValues();
        values.put(ProductEntry.COLUMN_QUANTITY,quantity+1);
        getContentResolver().update(currentUri,values,null,null);
    }

    public void decrease_quantity(){
        if (quantity>1){
        ContentValues values = new ContentValues();
        values.put(ProductEntry.COLUMN_QUANTITY,quantity-1);
        getContentResolver().update(currentUri,values,null,null);
    }else{
            Toast.makeText(this,getString(R.string.error_quantity),Toast.LENGTH_SHORT).show();
        }
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the product.
                deleteProduct();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteProduct() {
        // Only perform the delete if this is an existing product.
        if (currentUri != null) {
            // Call the ContentResolver to delete the product at the given content URI.
            // Pass in null for the selection and selection args because the currentUri
            // content URI already identifies the product that we want.
            int rowsDeleted = getContentResolver().delete(currentUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
            finish();
        }

    }
}