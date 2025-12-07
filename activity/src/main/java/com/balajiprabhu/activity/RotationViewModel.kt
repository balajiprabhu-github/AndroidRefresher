package com.balajiprabhu.activity

import androidx.compose.runtime.mutableIntStateOf
import androidx.lifecycle.ViewModel

/**
 * APPROACH 3: The Architecture Way (ViewModel)
 * 
 * ViewModels are designed to store and manage UI-related data in a lifecycle conscious way.
 * They allow data to survive configuration changes such as screen rotations.
 */
class RotationViewModel : ViewModel() {
    
    // We use mutableStateOf inside ViewModel so Compose can observe it.
    // In a real app, you might use StateFlow.
    var count = mutableIntStateOf(0)
        private set

    fun increment() {
        count.intValue++
    }
}
