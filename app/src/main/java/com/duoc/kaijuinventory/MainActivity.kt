package com.duoc.kaijuinventory

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.duoc.kaijuinventory.viewmodel.InventoryViewModel
import com.duoc.kaijuinventory.screens.MainScreen

class MainActivity : ComponentActivity() {

    private val inventoryViewModel: InventoryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainScreen(viewModel = inventoryViewModel)

        }
    }
}
