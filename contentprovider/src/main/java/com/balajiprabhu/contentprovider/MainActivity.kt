package com.balajiprabhu.contentprovider

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.balajiprabhu.contentprovider.ui.theme.AndroidRefreshTheme

data class DictionaryWord(val id: Long, val word: String, val definition: String)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidRefreshTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DictionaryScreen()
                }
            }
        }
    }

    // THE CLIENT LOGIC: How to query a Provider
    private fun fetchWords(): List<DictionaryWord> {
        val wordsList = mutableListOf<DictionaryWord>()

        // 1. Get the ContentResolver (The Client)
        val resolver = contentResolver

        // 2. Query the Provider using the URI from our Contract
        val cursor = resolver.query(
            DictionaryContract.CONTENT_URI, // The URL (content://...)
            null, // Projection: null means "all columns"
            null, // Selection: null means "all rows"
            null, // SelectionArgs
            null  // SortOrder
        )

        // 3. Iterate through the Cursor (The Result Set)
        cursor?.use {
            val idIndex = it.getColumnIndexOrThrow(DictionaryContract.Words._ID)
            val wordIndex = it.getColumnIndexOrThrow(DictionaryContract.Words.COLUMN_WORD)
            val defIndex = it.getColumnIndexOrThrow(DictionaryContract.Words.COLUMN_DEFINITION)

            while (it.moveToNext()) {
                val id = it.getLong(idIndex)
                val word = it.getString(wordIndex)
                val def = it.getString(defIndex)
                wordsList.add(DictionaryWord(id, word, def))
            }
        }

        Log.d("ClientApp", "Fetched ${wordsList.size} words from Provider")
        return wordsList
    }

    @Composable
    fun DictionaryScreen() {
        var wordList by remember { mutableStateOf(emptyList<DictionaryWord>()) }

        Column(modifier = Modifier.padding(16.dp)) {
            Button(
                onClick = {
                    // In a real app, run this on a background thread (IO Dispatcher)!
                    // ContentProvider queries can be slow (Database IO).
                    wordList = fetchWords()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Load Words from Provider")
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn {
                items(wordList) { item ->
                    WordItem(item)
                }
            }
        }
    }

    @Composable
    fun WordItem(word: DictionaryWord) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = word.word, style = MaterialTheme.typography.titleMedium)
                Text(text = word.definition, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}