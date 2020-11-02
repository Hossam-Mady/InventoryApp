package com.example.inventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import com.example.inventoryapp.data.ProductContract.ProductEntry;

public class ProductProvider extends ContentProvider {
    private ProductDbHelper mDbHelper;

    private static final int PRODUCTS = 100;

    private static final int PRODUCT_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        sUriMatcher.addURI(ProductContract.CONTENT_AUTHORITY,ProductContract.PATH_PRODUCTS,PRODUCTS);
        sUriMatcher.addURI(ProductContract.CONTENT_AUTHORITY,ProductContract.PATH_PRODUCTS +"/#",PRODUCT_ID);
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new ProductDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projections, String selection, String[] selectionArgs, String order) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                cursor = database.query(ProductEntry.TABLE_NAME,projections,selection,selectionArgs,null,null,order);
                break;

            case PRODUCT_ID:
                selection = ProductEntry.COLUMN_ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                cursor = database.query(ProductEntry.TABLE_NAME,projections,selection,selectionArgs,null,null,order);
                break;

            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                  return inserProduct (uri,contentValues);

            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
    }

    private Uri inserProduct (Uri uri, ContentValues contentValues){

        //check that name is not empty
        String name = contentValues.getAsString(ProductEntry.COLUMN_NAME);
        if (name == null){
            throw new IllegalArgumentException("name can't be empty");
        }

        //check that quantity is greater than or equal to 0
        int quantity = contentValues.getAsInteger(ProductEntry.COLUMN_QUANTITY);
        if (quantity < 0){
            throw new IllegalArgumentException("quantity can't be less than zero");
        }

        //check that price is greater than or equal to 0
        int price = contentValues.getAsInteger(ProductEntry.COLUMN_PRICE);
        if (price < 0){
            throw new IllegalArgumentException("price can't be less than zero");
        }

        //check that email is not empty
        String email = contentValues.getAsString(ProductEntry.COLUMN_EMAIL);
        if (email == null){
            throw new IllegalArgumentException("email can't be empty");
        }

        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Insert the new pet with the given values
        long id = database.insert(ProductEntry.TABLE_NAME, null, contentValues);

        if (id == -1){
            return null;
        }
        // return the new URI with the ID appended to the end of it
        return ContentUris.withAppendedId(uri,id);
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }
}
