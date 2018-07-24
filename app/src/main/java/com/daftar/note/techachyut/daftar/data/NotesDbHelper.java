package com.daftar.note.techachyut.daftar.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Database helper for Pets app. Manages database creation and version management.
 */
public class NotesDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = NotesDbHelper.class.getSimpleName();

    /** Name of the database file */
    private static final String DATABASE_NAME = "allNotes.db";

    /**
     * Database version. If you change the database schema, you must increment the database version.
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * Constructs a new instance of {@link NotesDbHelper}.
     *
     * @param context of the app
     */
    public NotesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This is called when the database is created for the first time.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create the pets table
        String SQL_CREATE_NOTES_TABLE = "CREATE TABLE " + NoteContract.NoteEntry.TABLE_NAME + " ("
                + NoteContract.NoteEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + NoteContract.NoteEntry.COLUMN_NOTE_TITLE + " TEXT NOT NULL, "
                + NoteContract.NoteEntry.COLUMN_NOTE_DETAILS + " TEXT, "
                + NoteContract.NoteEntry.COLUMN_NOTE_PRIORITY + " INTEGER NOT NULL); ";

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_NOTES_TABLE);
    }

    /**
     * This is called when the database needs to be upgraded.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /**
     * Delete from database
     * @param id
     */
    public int deleteById(int id){
        SQLiteDatabase database = this.getWritableDatabase();
        return database.delete(NoteContract.NoteEntry.TABLE_NAME, "NoteContract.NoteEntry._ID = ?", new String[] {String.valueOf(id)});
    }


}
