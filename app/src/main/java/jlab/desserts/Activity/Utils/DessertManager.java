package jlab.desserts.Activity.Utils;

/*
 * Created by Javier on 22/03/2020.
 */

import java.util.Locale;
import java.util.Random;
import android.util.Base64;
import java.util.ArrayList;
import android.util.LruCache;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.content.ContentValues;
import android.graphics.BitmapFactory;
import android.database.sqlite.SQLiteDatabase;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class DessertManager extends SQLiteAssetHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "desserts.db";
    private static final String DESSERT_TABLE_NAME = "Dessert";
    private static final String IMAGES_TABLE_NAME = "Images";
    private static final String ID_COLUMN = "id";
    private static final String DESSERT_ID_COLUMN = "idDessert";
    private static final String NAME_COLUMN = "Name";
    private static final String INGREDIENTS_COLUMN = "Ingredients";
    private static final String DESCRIPTION_COLUMN = "Description";
    private static final String PREP_DESCRIPTION_COLUMN = "PrepDescription";
    private static final String DIFFICULTY_COLUMN = "Difficulty";
    private static final String IMAGE_COLUMN = "Image";
    private static final String DESSERT_COLUMN_FOREING_KEY = "Dessert";
    private static final String LANGUAGE_COLUMN = "Language";
    private static final String FAVORITE_COLUMN = "Favorite";
    private static LruCache<Integer, Bitmap> bitmapCache = new LruCache<>(30);

    public DessertManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public Dessert getDessert(int id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(DESSERT_TABLE_NAME, null, ID_COLUMN + "= ?", new String[] {String.valueOf(id)}, null, null, null, null);
        Dessert result = null;
        if (cursor.moveToFirst()) {
            result = new Dessert(id, cursor.getString(cursor.getColumnIndex(NAME_COLUMN)),
                    cursor.getString(cursor.getColumnIndex(DESCRIPTION_COLUMN)),
                    cursor.getString(cursor.getColumnIndex(INGREDIENTS_COLUMN)),
                    cursor.getString(cursor.getColumnIndex(PREP_DESCRIPTION_COLUMN)),
                    cursor.getInt(cursor.getColumnIndex(DIFFICULTY_COLUMN)),
                    cursor.getInt(cursor.getColumnIndex(DESSERT_ID_COLUMN)),
                    cursor.getInt(cursor.getColumnIndex(FAVORITE_COLUMN)) != 0);
        }
        cursor.close();
        return result;
    }

    public ArrayList<String> getBase64Images (int dessertId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(IMAGES_TABLE_NAME, null, DESSERT_COLUMN_FOREING_KEY + "= " + dessertId, null, null, null, null, null);
        ArrayList<String> result = new ArrayList<>();
        while (cursor.moveToNext())
            result.add(cursor.getString(cursor.getColumnIndex(IMAGE_COLUMN)));
        cursor.close();
        return result;
    }

    public ArrayList<Bitmap> getBitmapImages (int dessertId) {
        ArrayList<Bitmap> result = new ArrayList<>();
        for (String base64: getBase64Images(dessertId))
            result.add(getBitmapFromBase64(base64));
        return result;
    }

    public Bitmap getImage (int dessertId) {
        Bitmap result = bitmapCache.get(dessertId);
        if(result == null) {
            ArrayList<String> images = getBase64Images(dessertId);
            result = getBitmapFromBase64(images.get(new Random().nextInt(images.size())));
            bitmapCache.put(dessertId, result);
        }
        return result;
    }

    private Bitmap getBitmapFromBase64 (String base64) {
        byte[] decodeString = Base64.decode(base64, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodeString, 0, decodeString.length);
    }

    private String getLanguage () {
        //TODO: Actualizar al añadir nuevos idiomas
        String language = Locale.getDefault().getLanguage();
        switch (language) {
            case "es":
            case "en":
                return language;
            default:
                return "en";
        }
    }

    public int setFavoriteDessert (int dessertId, boolean isFavorite) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(FAVORITE_COLUMN, isFavorite);
        return db.update(DESSERT_TABLE_NAME, contentValues, DESSERT_ID_COLUMN + " LIKE ?",
                new String[] {String.valueOf(dessertId)} );
    }

    public ArrayList<Dessert> getFavorites (String query) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(DESSERT_TABLE_NAME, null,
                        String.format("%s = ? and %s = 1", LANGUAGE_COLUMN, FAVORITE_COLUMN),
                        new String[] { getLanguage() },
                null, null, null, null);
        ArrayList<Dessert> result = new ArrayList<>();
        while (cursor.moveToNext()) {
            Dessert dessert = new Dessert(cursor.getInt(cursor.getColumnIndex(ID_COLUMN)),
                    cursor.getString(cursor.getColumnIndex(NAME_COLUMN)),
                    cursor.getString(cursor.getColumnIndex(DESCRIPTION_COLUMN)),
                    cursor.getString(cursor.getColumnIndex(INGREDIENTS_COLUMN)),
                    null,
                    cursor.getInt(cursor.getColumnIndex(DIFFICULTY_COLUMN)),
                    cursor.getInt(cursor.getColumnIndex(DESSERT_ID_COLUMN)),
                    cursor.getInt(cursor.getColumnIndex(FAVORITE_COLUMN)) != 0);
            if(query.equals("") || dessert.getName().toLowerCase().contains(query))
                result.add(dessert);
        }
        cursor.close();
        return result;
    }

    public ArrayList<Dessert> getAllDetails (String query, int difficulty) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(DESSERT_TABLE_NAME, null,
                difficulty != -1
                    ? String.format("%s = ? and %s = ?", LANGUAGE_COLUMN, DIFFICULTY_COLUMN)
                    : String.format("%s = ?", LANGUAGE_COLUMN),
                difficulty != -1
                    ? new String[] { getLanguage(), String.valueOf(difficulty)}
                    : new String[] { getLanguage()},
                null, null, null, null);
        ArrayList<Dessert> result = new ArrayList<>();
        while (cursor.moveToNext()) {
            Dessert dessert = new Dessert(cursor.getInt(cursor.getColumnIndex(ID_COLUMN)),
                    cursor.getString(cursor.getColumnIndex(NAME_COLUMN)),
                    cursor.getString(cursor.getColumnIndex(DESCRIPTION_COLUMN)),
                    cursor.getString(cursor.getColumnIndex(INGREDIENTS_COLUMN)),
                    null,
                    cursor.getInt(cursor.getColumnIndex(DIFFICULTY_COLUMN)),
                    cursor.getInt(cursor.getColumnIndex(DESSERT_ID_COLUMN)),
                    cursor.getInt(cursor.getColumnIndex(FAVORITE_COLUMN)) != 0);
            if(query.equals("") || dessert.getName().toLowerCase().contains(query))
                result.add(dessert);
        }
        cursor.close();
        return result;
    }
}
