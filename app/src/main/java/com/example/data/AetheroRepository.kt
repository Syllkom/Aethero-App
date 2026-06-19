package com.example.data

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.Flow

class AetheroRepository(private val dao: AppDao) {
    val allPlugins: Flow<List<PluginEntity>> = dao.getAllPlugins()
    val pluginCount: Flow<Int> = dao.getPluginCount()
    val commandCount: Flow<Int> = dao.getCommandCount()
    val recentActivity: Flow<List<ActivityEntity>> = dao.getRecentActivity()

    suspend fun getPlugin(id: String): PluginEntity? = dao.getPluginById(id)
    
    suspend fun updatePlugin(plugin: PluginEntity) {
        dao.updatePlugin(plugin)
    }

    suspend fun syncPluginsFromGithub() {
        val githubPlugins = GithubApi.fetchPlugins()
        if (githubPlugins.isNotEmpty()) {
            val existing = dao.getAllPlugins().first()
            val existingMap = existing.associateBy { it.id }
            
            val toInsert = githubPlugins.map { newPlugin ->
                val curr = existingMap[newPlugin.id]
                if (curr != null) {
                    newPlugin.copy(isActive = curr.isActive)
                } else {
                    newPlugin
                }
            }
            
            val validIds = toInsert.map { it.id }
            dao.deletePluginsNotIn(validIds)
            dao.insertPlugins(toInsert)
            
            dao.insertActivity(ActivityEntity(actionType = "Sincronización", description = "Plugins sincronizados: ${toInsert.size} encontrados.", date = System.currentTimeMillis()))
        } else {
            // Log an error activity if it strictly fails to fetch anything
            dao.insertActivity(ActivityEntity(actionType = "Error", description = "Fallo al sincronizar plugins.", date = System.currentTimeMillis()))
        }
    }

    suspend fun populateDummyDataIfNeeded() {
        if (dao.getPluginCountSync() == 0) {
            dao.insertActivity(ActivityEntity(actionType = "Inicializando", description = "Sincronización inicial en proceso...", date = System.currentTimeMillis()))
            syncPluginsFromGithub()
        }
    }
}
