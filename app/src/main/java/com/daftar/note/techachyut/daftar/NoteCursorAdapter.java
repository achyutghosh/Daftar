package com.daftar.note.techachyut.daftar;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.daftar.note.techachyut.daftar.data.NoteContract;

/**
 * {@link NoteCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of pet data as its data source. This adapter knows
 * how to create list items for each row of pet data in the {@link Cursor}.
 */
public class NoteCursorAdapter extends CursorAdapter {

    /**
     * Constructs a new {@link NoteCursorAdapter}.
     *  @param context The context
     * @param c       The cursor from which to get the data.
     */
    public NoteCursorAdapter(Context context, Uri c) {
        super(context, (Cursor) c, 0 /* flags */);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    /**
     * This method binds the note data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current note can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find individual views that we want to modify in the list item layout
        TextView titleTextView = (TextView) view.findViewById(R.id.titleNote);
        TextView detailsTextView = (TextView) view.findViewById(R.id.detailsNote);

        // Find the columns of note attributes that we're interested in
        int titleColumnIndex = cursor.getColumnIndex(NoteContract.NoteEntry.COLUMN_NOTE_TITLE);
        int detailColumnIndex = cursor.getColumnIndex(NoteContract.NoteEntry.COLUMN_NOTE_DETAILS);

        // Read the pet attributes from the Cursor for the current pet
        String noteTitle = cursor.getString(titleColumnIndex);
        String noteDetails = cursor.getString(detailColumnIndex);

        // If the note details is empty string or null, then use some default text
        // that says "No details added yet!", so the TextView isn't blank.
        if (TextUtils.isEmpty(noteDetails)) {
            noteDetails = context.getString(R.string.no_details);
        }

        // Update the TextViews with the attributes for the current pet
        titleTextView.setText(noteTitle);
        detailsTextView.setText(noteDetails);
    }


}