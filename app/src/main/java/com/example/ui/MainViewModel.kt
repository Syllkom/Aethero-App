package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.AetheroRepository
import com.example.data.ActivityEntity
import com.example.data.PluginEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(private val repository: AetheroRepository) : ViewModel() {
    val plugins: StateFlow<List<PluginEntity>> = repository.allPlugins
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pluginCount: StateFlow<Int> = repository.pluginCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val commandCount: StateFlow<Int> = repository.commandCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val recentActivity: StateFlow<List<ActivityEntity>> = repository.recentActivity
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            // Usually we'd check if empty first, but the DAO is REPLACE on conflict
            repository.populateDummyDataIfNeeded()
        }
    }
    
    fun togglePluginActive(plugin: PluginEntity, isActive: Boolean) {
        viewModelScope.launch {
            repository.updatePlugin(plugin.copy(isActive = isActive))
        }
    }
    
    suspend fun getPlugin(id: String): PluginEntity? {
        return repository.getPlugin(id)
    }
}

class MainViewModelFactory(private val repository: AetheroRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
