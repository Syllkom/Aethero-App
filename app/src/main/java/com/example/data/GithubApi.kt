package com.example.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

object GithubApi {
    suspend fun fetchPlugins(): List<PluginEntity> = withContext(Dispatchers.IO) {
        val plugins = mutableListOf<PluginEntity>()
        try {
            val ts = System.currentTimeMillis()
            val url = URL("https://api.github.com/repos/Syllkom/HorekuOs/git/trees/aethero?recursive=1&t=$ts")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.setRequestProperty("User-Agent", "Aethero-App")
            connection.setRequestProperty("Cache-Control", "no-cache")
            connection.connectTimeout = 5000
            connection.readTimeout = 5000

            if (connection.responseCode == 200) {
                val response = connection.inputStream.bufferedReader().readText()
                val jsonObject = org.json.JSONObject(response)
                val jsonArray = jsonObject.getJSONArray("tree")

                val targetPaths = mutableListOf<String>()
                
                for (i in 0 until jsonArray.length()) {
                    val item = jsonArray.getJSONObject(i)
                    val path = item.getString("path")
                    if (item.getString("type") == "blob" && (path.startsWith("plugins/") || path.startsWith("events/")) && path.endsWith(".js")) {
                        targetPaths.add(path)
                    }
                }
                
                // Process in chunks to avoid overwhelming raw.githubusercontent.com rate limitations and timing out
                val chunks = targetPaths.chunked(10)
                for (chunk in chunks) {
                    val deferreds = chunk.map { path ->
                        async {
                            try {
                                val isEvent = path.startsWith("events/")
                                val fileName = path.substringAfterLast("/")
                                val isInit = fileName.startsWith("@")
                                
                                var name = fileName.removeSuffix(".plugin.js").removeSuffix(".js")
                                if (isEvent && name.contains(".")) {
                                    name = name.substringAfter(".")
                                }

                                val rawUrlStr = "https://raw.githubusercontent.com/Syllkom/HorekuOs/aethero/$path"
                                val rawUrl = URL(rawUrlStr)
                                val rawConn = rawUrl.openConnection() as HttpURLConnection
                                rawConn.requestMethod = "GET"
                                rawConn.setRequestProperty("User-Agent", "Aethero-App")
                                rawConn.connectTimeout = 5000
                                rawConn.readTimeout = 5000
                                
                                var code = ""
                                var isCmd = !isEvent && !isInit
                                var desc = if (isInit) "" else if (isEvent) "Evento $name" else "Plugin $name"
                                var cat = if (isInit) "Init" else if (isEvent) "Event" else "Utility"
                                var usages = if (isInit || isEvent) "" else "!$name"
                                
                                if (rawConn.responseCode == 200) {
                                    code = rawConn.inputStream.bufferedReader().readText()
                                    if (code.contains("category:")) {
                                        val match = Regex("category:\\s*['\"]([^'\"]+)['\"]").find(code)
                                        if (match != null) cat = match.groupValues[1].replaceFirstChar { it.uppercase() }
                                    }
                                    if (code.contains("description:")) {
                                        val match = Regex("description:\\s*['\"]([^'\"]+)['\"]").find(code)
                                        if (match != null) desc = match.groupValues[1]
                                    }
                                    if (code.contains("usage:")) {
                                        val match = Regex("usage:\\s*\\[(.*?)\\]", RegexOption.DOT_MATCHES_ALL).find(code)
                                        if (match != null) {
                                            val usageStr = match.groupValues[1]
                                            usages = Regex("['\"]([^'\"]+)['\"]").findAll(usageStr).map { it.groupValues[1] }.joinToString("|")
                                        }
                                    }
                                    
                                    PluginEntity(
                                        id = name,
                                        name = name,
                                        isCommand = isCmd,
                                        usePrefix = code.contains("usePrefix: true"),
                                        cases = name,
                                        description = desc,
                                        category = cat,
                                        usages = usages,
                                        code = code,
                                        isActive = true,
                                        version = "1.0.0",
                                        url = rawUrlStr
                                    )
                                } else { null }
                            } catch(e: Exception) {
                                null
                            }
                        }
                    }
                    plugins.addAll(deferreds.mapNotNull { it.await() })
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return@withContext plugins
    }

    suspend fun fetchRawCommitActivity(): List<Int> = withContext(Dispatchers.IO) {
        try {
            val ts = System.currentTimeMillis()
            val url = URL("https://api.github.com/repos/Syllkom/HorekuOs/commits?sha=aethero&t=$ts")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.setRequestProperty("User-Agent", "Aethero-App")
            connection.setRequestProperty("Cache-Control", "no-cache")
            
            if (connection.responseCode == 200) {
                val response = connection.inputStream.bufferedReader().readText()
                val jsonArray = JSONArray(response)
                
                val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).apply {
                    timeZone = TimeZone.getTimeZone("UTC")
                }
                
                val commitsPerDay = IntArray(7) { 0 }
                val todayStart = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.timeInMillis
                
                val millisPerDay = 1000 * 60 * 60 * 24L

                for (i in 0 until jsonArray.length()) {
                    val dateStr = jsonArray.getJSONObject(i).getJSONObject("commit").getJSONObject("author").getString("date")
                    val date = dateFormat.parse(dateStr)
                    if (date != null) {
                        val diff = todayStart - date.time
                        val diffDays = if (diff < 0) 0 else (diff / millisPerDay).toInt() + 1
                        val dayIndex = if (date.time >= todayStart) 0 else diffDays
                        if (dayIndex in 0..6) {
                            commitsPerDay[dayIndex]++
                        }
                    }
                }
                
                val activity = mutableListOf<Int>()
                for (i in 6 downTo 0) {
                    activity.add(commitsPerDay[i])
                }
                return@withContext activity
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return@withContext listOf(0, 0, 0, 0, 0, 0, 0)
    }
}
