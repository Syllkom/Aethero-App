package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import com.example.data.AetheroRepository
import com.example.data.AppDatabase
import com.example.ui.AetheroApp
import com.example.ui.MainViewModel
import com.example.ui.MainViewModelFactory
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    
    val database = AppDatabase.getDatabase(this)
    val repository = AetheroRepository(database.appDao())
    val factory = MainViewModelFactory(repository)
    val viewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]
    
    setContent {
      MyApplicationTheme {
        AetheroApp(viewModel = viewModel)
      }
    }
  }
}
