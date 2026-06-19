package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    @Query("SELECT * FROM plugins ORDER BY name ASC")
    fun getAllPlugins(): Flow<List<PluginEntity>>

    @Query("SELECT * FROM plugins WHERE id = :id")
    suspend fun getPluginById(id: String): PluginEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlugins(plugins: List<PluginEntity>)

    @Query("DELETE FROM plugins WHERE id NOT IN (:ids)")
    suspend fun deletePluginsNotIn(ids: List<String>)

    @Update
    suspend fun updatePlugin(plugin: PluginEntity)

    @Query("SELECT * FROM activity ORDER BY date DESC LIMIT 50")
    fun getRecentActivity(): Flow<List<ActivityEntity>>

    @Insert
    suspend fun insertActivity(activity: ActivityEntity)
    
    @Query("SELECT COUNT(*) FROM plugins")
    fun getPluginCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM plugins")
    suspend fun getPluginCountSync(): Int
    
    @Query("SELECT COUNT(*) FROM plugins WHERE isCommand = 1")
    fun getCommandCount(): Flow<Int>
}
