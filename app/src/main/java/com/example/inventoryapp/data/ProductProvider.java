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
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
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
        // Notify all listeners that the data has changed for the pet content URI
        getContext().getContentResolver().notifyChange(uri, null);
        // return the new URI with the ID appended to the end of it
        return ContentUris.withAppendedId(uri,id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;

        switch (match){
            case PRODUCTS:
                rowsDeleted = database.delete(ProductEntry.TABLE_NAME,selection,selectionArgs);
                break;

            case PRODUCT_ID:
                selection = ProductEntry.COLUMN_ID + "=?";
                selectionArgs = new String [] {String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(ProductEntry.TABLE_NAME,selection,selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        if (rowsDeleted!=0){
            getContext().getContentResolver().notifyChange(uri,null);
        }

        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match){
            case PRODUCTS:
                return updateProduct(uri,contentValues,selection,selectionArgs);

            case PRODUCT_ID:
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateProduct(uri, contentValues, selection, selectionArgs);

            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateProduct(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        if (contentValues.containsKey(ProductEntry.COLUMN_NAME)) {
            String name = contentValues.getAsString(ProductEntry.COLUMN_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Product requires a name");
            }
        }

        if (contentValues.containsKey(ProductEntry.COLUMN_EMAIL)) {
            Integer email = contentValues.getAsInteger(ProductEntry.COLUMN_EMAIL);
            if (email == null) {
                throw new IllegalArgumentException("Product requires an email");
            }
        }

        if (contentValues.containsKey(ProductEntry.COLUMN_QUANTITY)) {
            Integer quantity = contentValues.getAsInteger(ProductEntry.COLUMN_QUANTITY);
            if (quantity != null && quantity < 1) {
                throw new IllegalArgumentException("Product requires valid quantity");
            }
        }

        if (contentValues.containsKey(ProductEntry.COLUMN_QUANTITY)) {
            Integer price = contentValues.getAsInteger(ProductEntry.COLUMN_PRICE);
            if (price != null && price < 1) {
                throw new IllegalArgumentException("Product requires valid price");
            }
        }

        if (contentValues.containsKey(ProductEntry.COLUMN_IMAGE)) {
            byte[] image = contentValues.getAsByteArray(ProductEntry.COLUMN_IMAGE);
            if (image != null) {
                throw new IllegalArgumentException("Product requires valid image");
            }
        }

        if (contentValues.size() == 0) {
            return 0;
        }
        int rowsUpdated = database.update(ProductEntry.TABLE_NAME,contentValues,selection,selectionArgs);
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        // Return the number of rows updated
        return rowsUpdated;
    }
}
