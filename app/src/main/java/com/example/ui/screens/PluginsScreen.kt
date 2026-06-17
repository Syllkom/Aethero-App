package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import compose.icons.TablerIcons
import compose.icons.tablericons.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.ui.MainViewModel
import com.example.data.PluginEntity
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PluginsScreen(viewModel: MainViewModel, navController: NavController) {
    val plugins by viewModel.plugins.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Todos") }
    var selectedPluginId by remember { mutableStateOf<String?>(null) }
    
    val categories = listOf("Todos", "Owner", "Utility", "Fun", "General")

    if (selectedPluginId != null) {
        val plugin = plugins.find { it.id == selectedPluginId }
        if (plugin != null) {
            PluginDetailScreen(
                plugin = plugin,
                onBack = { selectedPluginId = null },
                onToggle = { isActive -> viewModel.togglePluginActive(plugin, isActive) }
            )
        } else {
            selectedPluginId = null
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgrounDark)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Plugins", color = Accent, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(SurfaceVariantDark, RoundedCornerShape(50)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(TablerIcons.Plus, contentDescription = "Add", tint = Accent)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Buscar plugins...", color = TextSecondary) },
                    leadingIcon = { Icon(TablerIcons.Search, tint = TextSecondary, contentDescription = null) },
                    modifier = Modifier.weight(1f).height(50.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = SurfaceDark,
                        unfocusedContainerColor = SurfaceDark,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = Accent,
                        unfocusedTextColor = Accent
                    ),
                    shape = RoundedCornerShape(25.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .background(SurfaceDark, RoundedCornerShape(25.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(TablerIcons.Adjustments, contentDescription = "Filter", tint = TextSecondary)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(categories) { category ->
                    val isSelected = selectedCategory == category
                    Surface(
                        color = if (isSelected) Accent else SurfaceDark,
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.clickable { selectedCategory = category }
                    ) {
                        Text(
                            text = category,
                            color = if (isSelected) Color.Black else TextSecondary,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            
            val filteredPlugins = plugins.filter { 
                (selectedCategory == "Todos" || it.category.equals(selectedCategory, ignoreCase = true)) &&
                it.name.contains(searchQuery, ignoreCase = true)
            }
            
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(filteredPlugins) { plugin ->
                    PluginListItem(
                        plugin = plugin,
                        onClick = { selectedPluginId = plugin.id },
                        onToggle = { isActive -> viewModel.togglePluginActive(plugin, isActive) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
fun PluginListItem(plugin: PluginEntity, onClick: () -> Unit, onToggle: (Boolean) -> Unit) {
    Surface(
        color = SurfaceDark,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth().clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(SurfaceVariantDark),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "JS", color = Accent, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(text = plugin.name, color = Accent, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = plugin.description, color = TextSecondary, fontSize = 12.sp, maxLines = 2)
                    }
                }
                Switch(
                    checked = plugin.isActive,
                    onCheckedChange = { onToggle(it) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.Black,
                        checkedTrackColor = Accent,
                        uncheckedThumbColor = TextSecondary,
                        uncheckedTrackColor = SurfaceVariantDark
                    )
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(color = SurfaceVariantDark, shape = RoundedCornerShape(8.dp)) {
                        Text(text = plugin.category, color = TextSecondary, fontSize = 10.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                    }
                    Surface(color = SurfaceVariantDark, shape = RoundedCornerShape(8.dp)) {
                        Text(text = plugin.version, color = TextSecondary, fontSize = 10.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = if(plugin.usesCount > 1000) "${plugin.usesCount/1000}k usos" else "${plugin.usesCount} usos", color = TextSecondary, fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(TablerIcons.DotsVertical, contentDescription = "More", tint = TextSecondary, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
fun PluginDetailScreen(plugin: PluginEntity, onBack: () -> Unit, onToggle: (Boolean) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgrounDark)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onBack) {
                Icon(TablerIcons.ArrowLeft, contentDescription = "Back", tint = Accent)
            }
            Switch(
                checked = plugin.isActive,
                onCheckedChange = { onToggle(it) },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.Black,
                    checkedTrackColor = Accent,
                    uncheckedThumbColor = TextSecondary,
                    uncheckedTrackColor = SurfaceVariantDark
                )
            )
        }
        
        LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(SurfaceDark),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "JS", color = Accent, fontWeight = FontWeight.Bold, fontSize = 28.sp)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = plugin.name, color = Accent, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(text = plugin.version, color = TextSecondary, fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(color = StatusGreen.copy(alpha = 0.2f), shape = RoundedCornerShape(16.dp)) {
                                Text(text = "• Activo", color = StatusGreen, fontSize = 10.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = plugin.description, color = TextSecondary, fontSize = 14.sp)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Surface(color = SurfaceVariantDark, shape = RoundedCornerShape(8.dp)) {
                    Text(text = plugin.category, color = TextSecondary, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp))
                }
                Spacer(modifier = Modifier.height(32.dp))
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    StatBox("Usos", if(plugin.usesCount >= 1000) "${plugin.usesCount / 1000}K" else "${plugin.usesCount}")
                    StatBox("Actualizado", "Hoy")
                    StatBox("Autor", plugin.author)
                    StatBox("Licencia", plugin.license)
                }
                
                Spacer(modifier = Modifier.height(32.dp))
            }
            
            item {
                Text(text = "Información", color = Accent, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                InfoRow("command", plugin.isCommand.toString())
                InfoRow("usePrefix", plugin.usePrefix.toString())
                InfoRow("case", "[${plugin.cases}]")
                InfoRow("category", plugin.category)
                InfoRow("description", plugin.description)
                
                Spacer(modifier = Modifier.height(24.dp))
                Text(text = "usage", color = TextSecondary, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(8.dp))
                val usages = plugin.usages.split("|")
                usages.forEach { usage ->
                    Text(text = usage, color = Accent, fontSize = 14.sp, modifier = Modifier.padding(vertical = 4.dp))
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun StatBox(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, color = Accent, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = label, color = TextSecondary, fontSize = 12.sp)
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = TextSecondary, fontSize = 14.sp)
        Text(text = value, color = Accent, fontSize = 14.sp)
    }
}
