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

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.data.GithubApi

class MainViewModel(private val repository: AetheroRepository) : ViewModel() {
    private val _githubActivity = MutableStateFlow<List<Int>>(listOf(0,0,0,0,0,0,0))
    val githubActivity = _githubActivity.asStateFlow()

    val plugins: StateFlow<List<PluginEntity>> = repository.allPlugins
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pluginCount: StateFlow<Int> = repository.pluginCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val commandCount: StateFlow<Int> = repository.commandCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val recentActivity: StateFlow<List<ActivityEntity>> = repository.recentActivity
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _lastSyncTime = MutableStateFlow(formatCurrentTime())
    val lastSyncTime = _lastSyncTime.asStateFlow()

    private fun formatCurrentTime(): String {
        return java.text.SimpleDateFormat("MMM dd, hh:mm a", java.util.Locale.getDefault()).format(java.util.Date())
    }

    init {
        viewModelScope.launch {
            // Usually we'd check if empty first, but the DAO is REPLACE on conflict
            repository.populateDummyDataIfNeeded()
        }
        viewModelScope.launch {
            while(true) {
                repository.syncPluginsFromGithub()
                _githubActivity.value = GithubApi.fetchRawCommitActivity()
                _lastSyncTime.value = formatCurrentTime()
                kotlinx.coroutines.delay(60_000) // refresh every 60s
            }
        }
    }
    
    fun forceSync() {
        viewModelScope.launch {
            repository.syncPluginsFromGithub()
            _githubActivity.value = GithubApi.fetchRawCommitActivity()
            _lastSyncTime.value = formatCurrentTime()
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
