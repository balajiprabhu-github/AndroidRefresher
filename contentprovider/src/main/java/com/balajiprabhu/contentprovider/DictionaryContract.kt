package com.balajiprabhu.contentprovider

import android.net.Uri
import android.provider.BaseColumns

/**
 * The Contract Class
 * It defines the "Schema" that both the Provider (Server) and Resolver (Client) agree on.
 */
object DictionaryContract {

    // 1. The Authority: Unique ID for our provider (usually package name + ".provider")
    const val AUTHORITY = "com.balajiprabhu.contentprovider.dictionary"

    // 2. The Base URI: The root URL for this provider
    val CONTENT_URI: Uri = Uri.parse("content://$AUTHORITY/words")

    // 3. Column Names
    object Words : BaseColumns {
        const val _ID = BaseColumns._ID
        const val COLUMN_WORD = "word"      // The word itself (e.g., "Android")
        const val COLUMN_DEFINITION = "definition" // The meaning (e.g., "A specific OS")
    }
}
