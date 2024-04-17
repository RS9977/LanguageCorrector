package com.example.languagechecker;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "SentenceDB";
    private static final String TABLE_SENTENCES = "sentences";
    private static final String KEY_ID = "id";
    private static final String KEY_SENTENCE = "sentence";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_SENTENCES_TABLE = "CREATE TABLE " + TABLE_SENTENCES + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_SENTENCE + " TEXT)";
        db.execSQL(CREATE_SENTENCES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SENTENCES);
        onCreate(db);
    }

    // Add methods to perform CRUD operations (insert, get all sentences, delete) here
    // Add a new sentence
    public long addSentence(String sentence) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_SENTENCE, sentence);
        long id = db.insert(TABLE_SENTENCES, null, values);
        db.close();
        return id;
    }

    // Get all sentences
    public List<String> getAllSentences() {
        List<String> sentences = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_SENTENCES;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // Loop through all rows and add to list
        if (cursor.moveToFirst()) {
            do {
                sentences.add(cursor.getString(1)); // 1 is the index of sentence column
            } while (cursor.moveToNext());
        }

        // Close the cursor and database
        cursor.close();
        db.close();
        return sentences;
    }

    // Delete a sentence
    public void deleteSentence(String sentence) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SENTENCES, KEY_SENTENCE + " = ?", new String[]{sentence});
        db.close();
    }
}
