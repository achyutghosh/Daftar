package com.daftar.note.techachyut.daftar.data;


import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

/**

 * {@link ContentProvider} for Notes app.

 */
public class NoteProvider extends ContentProvider{

    /** Tag for the log messages */
    public static final String LOG_TAG = NoteProvider.class.getSimpleName();

    /** Database helper object */
    private NotesDbHelper mDbHelper;

    /** URI matcher code for the content URI for the notes table */
    private static final int NOTES = 100;

    /** URI matcher code for the content URI for a single note in the notes table */
    private static final int NOTE_ID = 101;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.

    static {

        // The calls to addURI() go here, for all of the content URI patterns that the provider

        // should recognize. All paths added to the UriMatcher have a corresponding code to return

        // when a match is found.



        // The content URI of the form "content://com.example.android.pets/pets" will map to the

        // integer code {@link #PETS}. This URI is used to provide access to MULTIPLE rows

        // of the pets table.
        sUriMatcher.addURI(NoteContract.CONTENT_AUTHORITY, NoteContract.PATH_NOTES, NOTES);

        // The content URI of the form "content://com.example.android.pets/pets/#" will map to the

        // integer code {@link #PET_ID}. This URI is used to provide access to ONE single row

        // of the pets table.

        //

        // In this case, the "#" wildcard is used where "#" can be substituted for an integer.

        // For example, "content://com.example.android.pets/pets/3" matches, but

        // "content://com.example.android.notes/notes" (without a number at the end) doesn't match.
        sUriMatcher.addURI(NoteContract.CONTENT_AUTHORITY, NoteContract.PATH_NOTES + "/#", NOTE_ID);

    }



    /**
     * Initialize the provider and the database helper object.
     */
    @Override
    public boolean onCreate() {

        mDbHelper = new NotesDbHelper(getContext());

        // Make sure the variable is a global variable, so it can be referenced from other

        // ContentProvider methods.

        return true;

    }



    /**

     * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.

     */

    @Override

    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,

                        String sortOrder) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case NOTES:
                // For the PETS code, query the pets table directly with the given
                // projection, selection, selection arguments, and sort order. The cursor
                // could contain multiple rows of the pets table.
                cursor = database.query(NoteContract.NoteEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case NOTE_ID:
                selection = NoteContract.NoteEntry._ID + "=?";
                selectionArgs = new String [] { String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(NoteContract.NoteEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
                default: throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }



    /**

     * Insert new data into the provider with the given ContentValues.

     */

    @Override

    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case NOTES:
                return insertNote(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insert is not supported for " + uri);
        }
    }

    /**
     * Insert a note into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertNote(Uri uri, ContentValues values) {

        // Check that the title is not null
        String name = values.getAsString(NoteContract.NoteEntry.COLUMN_NOTE_TITLE);
        if (name == null) {
            throw new IllegalArgumentException("Note requires a title");
        }

        // Check that the priority is valid
        Integer priority = values.getAsInteger(NoteContract.NoteEntry.COLUMN_NOTE_PRIORITY);
        if (priority == null || !NoteContract.NoteEntry.isValidPriority(priority)) {
            throw new IllegalArgumentException("Note requires valid priority");
        }


        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Insert the new pet with the given values
        long id = database.insert(NoteContract.NoteEntry.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        // Notify all listeners that the data has changed for the pet content URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Once we know the ID of the new row in the table,
        // return the new URI with the ID appended to the end of it
        return  ContentUris.withAppendedId(uri, id);
    }

    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     */

    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {


        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case NOTES:
                // For the PETS code, query the pets table directly with the given
                // projection, selection, selection arguments, and sort order. The cursor
                // could contain multiple rows of the pets table.
                return updateNote(uri, contentValues, selection, selectionArgs);

            case NOTE_ID:
                // For the NOTE_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = NoteContract.NoteEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri))};
                return updateNote(uri, contentValues, selection, selectionArgs);

            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    /**
     * Update notes in the database with the given content values. Apply the changes to the rows
     * specified in the selection and selection arguments (which could be 0 or 1 or more notes).
     * Return the number of rows that were successfully updated.
     */
    private int updateNote(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // If the {@link NoteEntry#COLUMN_NOTE_TITLE} key is present,
        // check that the name value is not null.
        if (values.containsKey(NoteContract.NoteEntry.COLUMN_NOTE_TITLE)) {
            String name = values.getAsString(NoteContract.NoteEntry.COLUMN_NOTE_TITLE);
            if (name == null) {
                throw new IllegalArgumentException("Note requires a title");
            }
        }

        // If the {@link NoteEntry#COLUMN_NOTE_PRIORITY} key is present,
        // check that the gender value is valid.
        if (values.containsKey(NoteContract.NoteEntry.COLUMN_NOTE_PRIORITY)) {
            Integer priority = values.getAsInteger(NoteContract.NoteEntry.COLUMN_NOTE_PRIORITY);
            if (priority == null || !NoteContract.NoteEntry.isValidPriority(priority)) {
                throw new IllegalArgumentException("Note requires valid priority");
            }
        }


        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        // Otherwise, get writeable database to update the data
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = database.update(NoteContract.NoteEntry.TABLE_NAME, values, selection, selectionArgs);

        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
            }
       // Return the number of rows updated
         return rowsUpdated;


    }

        /**
         * Delete the data at the given selection and selection arguments.
         */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Track the number of rows that were deleted
         int rowsDeleted;


        final int match = sUriMatcher.match(uri);
        switch (match) {
            case NOTES:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(NoteContract.NoteEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case NOTE_ID:
                // Delete a single row given by the ID in the URI
                selection = NoteContract.NoteEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = database.delete(NoteContract.NoteEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        // If 1 or more rows were deleted, then notify all listeners that the data at the
        // given URI has changed
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
            }

       // Return the number of rows deleted
        return rowsDeleted;
    }


    /**
     * Returns the MIME type of data for the content URI.
     */
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case NOTES:
                return NoteContract.NoteEntry.CONTENT_LIST_TYPE;
            case NOTE_ID:
                return NoteContract.NoteEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}
