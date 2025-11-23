package com.balajiprabhu.intent.intent

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.balajiprabhu.intent.ui.theme.AndroidRefreshTheme

class SecondActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        var message: String

        if(intent?.action == Intent.ACTION_SEND && intent.type == "text/plain") {
            message = intent.getStringExtra(Intent.EXTRA_TEXT) ?: "No message received"
        } else {
            message = intent.getStringExtra("message") ?: "No message received"
        }

        Toast.makeText(this, message, Toast.LENGTH_LONG).show()


        setContent {
            AndroidRefreshTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text(text = "Second Activity") },
                            navigationIcon = {
                                IconButton({
                                    val returnIntent = Intent()
                                    returnIntent.putExtra("returnMessage", "Hello from SecondActivity")
                                    setResult(RESULT_OK, returnIntent)
                                    finish()
                                }) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Back"
                                    )
                                }
                            }
                        )
                    }
                ) { paddingValues ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = message)
                    }
                }
            }
        }
    }
}