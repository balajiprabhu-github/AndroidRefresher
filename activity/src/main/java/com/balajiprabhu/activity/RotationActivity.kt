package com.balajiprabhu.activity

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.balajiprabhu.activity.ui.theme.AndroidRefreshTheme

/**
 * THE ULTIMATE STATE EXPERIMENT
 * comparing 3 ways to handle rotation.
 */
class RotationActivity : ComponentActivity() {

    private val TAG = "RotationExperiment"
    
    // APPROACH 3: ViewModel
    // The 'viewModels()' delegate handles creating/retrieving the ViewModel.
    private val viewModel: RotationViewModel by viewModels()

    // APPROACH 2: The Classic Way (Activity Fields)
    // We must manually save/restore this in onSaveInstanceState.
    private var classicCountState = mutableIntStateOf(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Classic Way: Restore state if Bundle exists (recreation)
        if (savedInstanceState != null) {
            val savedValue = savedInstanceState.getInt("KEY_CLASSIC_COUNT", 0)
            classicCountState.intValue = savedValue
            Log.d(TAG, "RESTORED classic count: $savedValue")
        } else {
            Log.d(TAG, "FRESH START (Bundle is null)")
        }

        setContent {
            AndroidRefreshTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    RotationComparisonScreen(
                        classicCount = classicCountState.intValue,
                        onClassicIncrement = { classicCountState.intValue++ },
                        viewModel = viewModel
                    )
                }
            }
        }
    }

    // APPROACH 2 Implementation: Saving the state
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("KEY_CLASSIC_COUNT", classicCountState.intValue)
        Log.d(TAG, "SAVED classic count: ${classicCountState.intValue}")
    }

    @Composable
    fun RotationComparisonScreen(
        classicCount: Int,
        onClassicIncrement: () -> Unit,
        viewModel: RotationViewModel
    ) {
        // APPROACH 1: The Compose Way
        // rememberSaveable automatically saves to the Bundle under the hood!
        var composeCount by rememberSaveable { mutableIntStateOf(0) }
        
        // APPROACH 0: The BUG (Standard remember)
        // This validates that rotation normally destroys state.
        var buggyCount by remember { mutableIntStateOf(0) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text("Rotate your device! 🔄", style = MaterialTheme.typography.headlineMedium)

            // 0. THE BUG
            ExperimentCard(
                title = "1. The Bug (remember)",
                count = buggyCount,
                color = MaterialTheme.colorScheme.errorContainer,
                description = "Resets on rotation. Data is stored in memory tied to Composition, which dies.",
                onIncrement = { buggyCount++ }
            )

            // 1. Compose Way
            ExperimentCard(
                title = "2. Compose Way (rememberSaveable)",
                count = composeCount,
                color = MaterialTheme.colorScheme.primaryContainer,
                description = "Survives! Uses 'SavedStateHandle' (Bundle) under the hood.",
                onIncrement = { composeCount++ }
            )

            // 2. Classic Way
            ExperimentCard(
                title = "3. Classic Way (onSaveInstanceState)",
                count = classicCount,
                color = MaterialTheme.colorScheme.secondaryContainer,
                description = "Survives! Saved manually in Activity.onSaveInstanceState()",
                onIncrement = onClassicIncrement
            )

            // 3. ViewModel Way
            ExperimentCard(
                title = "4. ViewModel Way",
                count = viewModel.count.intValue,
                color = MaterialTheme.colorScheme.tertiaryContainer,
                description = "Survives! ViewModel lives outside Activity lifecycle.",
                onIncrement = { viewModel.increment() }
            )
        }
    }

    @Composable
    fun ExperimentCard(
        title: String,
        count: Int,
        color: Color,
        description: String,
        onIncrement: () -> Unit
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = color),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Count: $count", style = MaterialTheme.typography.displayMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = onIncrement) { Text("Increment") }
                Spacer(modifier = Modifier.height(8.dp))
                Text(description, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
