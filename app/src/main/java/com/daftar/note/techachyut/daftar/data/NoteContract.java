package com.daftar.note.techachyut.daftar.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * API Contract for the Daftar app.
 */
public final class NoteContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private NoteContract() {}

    /**
     * The "Content authority" is a name for the entire content provider, similar to the
     * relationship between a domain name and its website.  A convenient string to use for the
     * content authority is the package name for the app, which is guaranteed to be unique on the
     * device.
     */
    public static final String CONTENT_AUTHORITY = "com.daftar.note.techachyut.daftar";

    /**
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * Possible path (appended to base content URI for possible URI's)
     * For instance, content://com.daftar.note.techachyut.daftar.data/notes/ is a valid path for
     * looking at note data. content:com.daftar.note.techachyut.daftar.data/staff/ will fail,
     * as the ContentProvider hasn't been given any information on what to do with "staff".
     */
    public static final String PATH_NOTES = "notes";

    /**
     * Inner class that defines constant values for the notes database table.
     * Each entry in the table represents a single note.
     */
    public static final class NoteEntry implements BaseColumns {

        /** The content URI to access the note data in the provider */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_NOTES);


        /** Name of database table for notes */
        public final static String TABLE_NAME = "notes";

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of notes.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_NOTES;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single notes.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_NOTES;

        /**
         * Unique ID number for the note (only for use in the database table).
         *
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;

        /**
         * Title of the note.
         *
         * Type: TEXT
         */
        public final static String COLUMN_NOTE_TITLE ="title";

        /**
         * Details of the note.
         *
         * Type: TEXT
         */
        public final static String COLUMN_NOTE_DETAILS = "details";

        /**
         * Priority of the note.
         *
         * The only possible values are {@link #PRIORITY_LOW}, {@link #PRIORITY_HIGH},
         * or {@link #PRIORITY_MIDIUM}.
         *
         * Type: INTEGER
         */
        public final static String COLUMN_NOTE_PRIORITY = "priority";

        /**
         * Possible values for the gender of the pet.
         */
        public static final int PRIORITY_LOW = 0;
        public static final int PRIORITY_HIGH = 1;
        public static final int PRIORITY_MIDIUM = 2;

        /**
         * Returns whether or not the given gender is {@link #PRIORITY_LOW}, {@link #PRIORITY_HIGH},
         * or {@link #PRIORITY_MIDIUM}.
         */

        public static boolean isValidPriority(int priority) {

            if (priority == PRIORITY_LOW || priority == PRIORITY_HIGH || priority == PRIORITY_MIDIUM) {
                return true;
            }
            return false;
        }
    }
}

