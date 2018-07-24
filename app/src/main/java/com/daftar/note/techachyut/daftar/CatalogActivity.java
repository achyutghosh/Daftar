package com.daftar.note.techachyut.daftar;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.daftar.note.techachyut.daftar.data.NoteContract;
import com.daftar.note.techachyut.daftar.data.NotesDbHelper;

import static com.daftar.note.techachyut.daftar.data.NoteContract.*;

public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    /** Identifier for the pet data loader */
    private static final int NOTE_LOADER = 0;

    /** Adapter for the ListView */
   NoteCursorAdapter mCursorAdapter;

   NotesDbHelper mDbHelper;

   SQLiteDatabase db;
  private int id, priority;
  String title1, details;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);



        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        // Find the Swipe ListView which will be populated with the note data
        //ListView noteListView = (ListView) findViewById(R.id.list);
        SwipeMenuListView noteListView = findViewById(R.id.list);
        noteListView.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT);


        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        noteListView.setEmptyView(emptyView);



        // Setup an Adapter to create a swipe list item for each row of note data in the Cursor.
        // There is no note data yet (until the loader finishes) so pass in null for the Cursor.
        mCursorAdapter = new NoteCursorAdapter(this, null);
        noteListView.setAdapter(mCursorAdapter);

        SwipeMenuCreator swipeMenuCreator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem deleteItem = new SwipeMenuItem(getApplicationContext());
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF0, 0x51, 0x4B)));
                deleteItem.setWidth(200);
                deleteItem.setIcon(R.drawable.ic_action_discard);
                menu.addMenuItem(deleteItem);
            }
        };

        noteListView.setMenuCreator(swipeMenuCreator);

        noteListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        // TODO: 23-07-2018 Delete method, not working, Deletes All Notes
                        // Only perform the delete if this is an existing note.
                        if (NoteEntry.CONTENT_URI != null) {
                            // Call the ContentResolver to delete the pet at the given content URI.
                            // Pass in null for the selection and selection args because the mCurrentPetUri
                            // content URI already identifies the pet that we want.
                            int rowsDeleted = getContentResolver().delete(NoteEntry.CONTENT_URI, null, null);

                            // Show a toast message depending on whether or not the delete was successful.
                            if (rowsDeleted == 0) {
                                // If no rows were deleted, then there was an error with the delete.
                                //Toast.makeText(this, getString(R.string.editor_delete_note_failed),
                                        //Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(CatalogActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                                // Otherwise, the delete was successful and we can display a toast.
                                //Toast.makeText(this, getString(R.string.editor_delete_note_successful),
                                       // Toast.LENGTH_SHORT).show();
                            }
                        }
                        //deleteByID();

                }
                return true;
            }
        });

        // Setup the item click listener
        noteListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Create new intent to go to {@link EditorActivity}
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);

                // Form the content URI that represents the specific pet that was clicked on,
                // by appending the "id" (passed as input to this method) onto the
                // {@link PetEntry#CONTENT_URI}.
                // For example, the URI would be "content://com.example.android.notes/notes/2"
                // if the pet with ID 2 was clicked on.
                Uri currentNoteUri = ContentUris.withAppendedId(NoteEntry.CONTENT_URI, id);

                // Set the URI on the data field of the intent
                intent.setData(currentNoteUri);

                // Launch the {@link EditorActivity} to display the data for the current note.
                startActivity(intent);
            }
        });

        // Kick off the loader
        getLoaderManager().initLoader(NOTE_LOADER, null, this);
    }



    private void insertNotes() {
        // Create a ContentValues object where column names are the keys,
        // and Particular note attributes are the values
        ContentValues values = new ContentValues();
        values.put(NoteEntry.COLUMN_NOTE_TITLE, "Test");
        values.put(NoteEntry.COLUMN_NOTE_DETAILS, "SQLite");
        values.put(NoteEntry.COLUMN_NOTE_PRIORITY, 0);

        // Insert a new row for a note into the provider using the ContentResolver.
        // Use the {@link NoteEntry#CONTENT_URI} to indicate that we want to insert
        // into the notes database table.
        // Receive the new content URI that will allow us to access Particular data in the future.
        Uri newUri = getContentResolver().insert(NoteEntry.CONTENT_URI, values);
    }

    /*
    * Helper method to delete all notes in the database.
    */
    private void deleteAllNotes() {
        int rowsDeleted = getContentResolver().delete(NoteEntry.CONTENT_URI, null, null);
        Log.v("CatalogActivity", rowsDeleted + " rows deleted from note database");
    }

    /*
     * Helper method to delete single notes in the database.
     */
    /*public void deleteByID() {
        int deletedRows = mDbHelper.deleteById(R.id.list);

        if (deletedRows > 0) {
            Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Not deleted", Toast.LENGTH_SHORT).show();
        }
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertNotes();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                deleteAllNotes();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
    // Define a projection that specifies the columns from the table we care about.
        String[] projection = {
                NoteEntry._ID,
                NoteEntry.COLUMN_NOTE_TITLE,
                NoteEntry.COLUMN_NOTE_DETAILS };

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                NoteEntry.CONTENT_URI,   // Provider content URI to query
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Update {@link NoteCursorAdapter} with this new cursor containing updated pet data
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Callback called when the data needs to be deleted
        mCursorAdapter.swapCursor(null);
    }
}
