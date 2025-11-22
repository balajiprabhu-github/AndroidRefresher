package com.balajiprabhu.intent

import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_SEND
import android.content.Intent.ACTION_VIEW
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.balajiprabhu.intent.explicit.SecondActivity
import com.balajiprabhu.intent.ui.theme.AndroidRefreshTheme
import androidx.core.net.toUri

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val launcher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            if (result.resultCode == RESULT_OK) {
                val returnMessage =
                    result.data?.getStringExtra("returnMessage") ?: "No return message"
                Toast.makeText(this, returnMessage, Toast.LENGTH_LONG).show()
            }
        }
        setContent {
            AndroidRefreshTheme {
                StartIntentScreen(launcher)
            }
        }
    }
}

@Composable
fun StartIntentScreen(launcher: ActivityResultLauncher<Intent>) {
    val context: Context = LocalContext.current
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = {
            val intent = Intent(context, SecondActivity::class.java)
            intent.putExtra("message", "Hello from MainActivity")
            launcher.launch(intent)
        }) {
            Text(text = "Go to next screen")
        }

        // Implicit Intent to open a web page
        Button(onClick = {
            val intent = Intent(ACTION_VIEW)
            intent.data = "https://www.google.com".toUri()
            if(intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(intent)
            } else {
                Toast.makeText(context, "No application can handle this request.", Toast.LENGTH_LONG).show()
            }
        }) {
            Text(text = "Open Google")
        }

        // Implicit Intent to share text
        Button( onClick = {
            val shareIntent = Intent(ACTION_SEND).apply {
                action = ACTION_SEND
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, "Hello from MainActivity")
            }
            val chooser = Intent.createChooser(shareIntent, "Share via")
            context.startActivity(chooser)
        }
        ) {
            Text(text = "Share Text")
        }

    }
}