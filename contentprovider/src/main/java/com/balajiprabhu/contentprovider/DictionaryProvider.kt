package com.balajiprabhu.contentprovider

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import android.util.Log

/**
 * A Simple Content Provider that serves a hardcoded list of words.
 * In a real app, this would query a Room Database or SQLite.
 */
class DictionaryProvider : ContentProvider() {

    // Simulating a Database
    private val mockData = listOf(
        Pair("Intent", "A messaging object used to request an action from another app component."),
        Pair("Service", "An application component that can perform long-running operations in the background."),
        Pair("BroadcastReceiver", "A component that enables the system to deliver events to the app."),
        Pair("ContentProvider", "A component that manages access to a central repository of data."),
        Pair("Activity", "An entry point for interacting with the user.")
    )

    override fun onCreate(): Boolean {
        Log.d("DictionaryProvider", "Provider Created/Started!")
        return true
    }

    // THIS IS THE KEY METHOD INTERVIEWERS ASK ABOUT
    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor {
        Log.d("DictionaryProvider", "Query received for: $uri")

        // 1. Define the columns for our "table"
        val columns = arrayOf(
            DictionaryContract.Words._ID,
            DictionaryContract.Words.COLUMN_WORD,
            DictionaryContract.Words.COLUMN_DEFINITION
        )

        // 2. Create a MatrixCursor (A fake cursor that holds data in memory)
        val cursor = MatrixCursor(columns)

        // 3. Fill the cursor with our mock data
        mockData.forEachIndexed { index, (word, def) ->
            cursor.addRow(arrayOf(index, word, def))
        }

        return cursor
    }

    // We can leave these unimplemented for this read-only demo
    override fun getType(uri: Uri): String? = null
    override fun insert(uri: Uri, values: ContentValues?): Uri? = null
    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int = 0
    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int = 0
}
