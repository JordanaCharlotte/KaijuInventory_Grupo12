package com.duoc.kaijuinventory_grupo12

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.duoc.kaijuinventory_grupo12.screens.MainScreen
import com.duoc.kaijuinventory_grupo12.viewmodel.InventoryViewModel

class MainActivity : ComponentActivity() {

    private val inventoryViewModel: InventoryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainScreen(viewModel = inventoryViewModel)

        }
    }
}