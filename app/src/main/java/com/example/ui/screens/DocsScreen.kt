package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import compose.icons.TablerIcons
import compose.icons.tablericons.*
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocsScreen(navController: NavController) {
    var searchQuery by remember { mutableStateOf("") }
    val tabs = listOf("Guías", "API", "Eventos", "Plugins")
    var selectedTab by remember { mutableStateOf("Guías") }
    
    val sidebarItems = listOf("Introducción", "Instalación", "Configuración", "Comandos", "Eventos", "Plugins", "API Reference", "Ejemplos", "FAQ")
    var selectedSidebarItem by remember { mutableStateOf("Introducción") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgrounDark)
            .padding(top = 24.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text("Documentación", color = Accent, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Buscar en la documentación...", color = TextSecondary) },
                leadingIcon = { Icon(TablerIcons.Search, tint = TextSecondary, contentDescription = null) },
                modifier = Modifier.fillMaxWidth().height(50.dp),
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
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                tabs.forEach { tab ->
                    val isSelected = selectedTab == tab
                    Box(
                        modifier = Modifier
                            .clickable { selectedTab = tab }
                            .padding(vertical = 8.dp)
                    ) {
                        Text(
                            text = tab,
                            color = if (isSelected) Accent else TextSecondary,
                            fontSize = 14.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = DividerColor)
        }
        
        Row(modifier = Modifier.fillMaxSize()) {
            // Sidebar
            LazyColumn(
                modifier = Modifier
                    .weight(0.4f)
                    .fillMaxHeight()
                    .padding(start = 8.dp, top = 16.dp)
            ) {
                items(sidebarItems) { item ->
                    val isSelected = selectedSidebarItem == item
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedSidebarItem = item }
                            .background(if (isSelected) SurfaceVariantDark else Color.Transparent, RoundedCornerShape(8.dp))
                            .padding(12.dp)
                    ) {
                        Icon(TablerIcons.InfoCircle, contentDescription = null, tint = if (isSelected) Accent else TextSecondary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = item,
                            color = if (isSelected) Accent else TextSecondary,
                            fontSize = 14.sp
                        )
                    }
                }
            }
            
            // Content
            Column(
                modifier = Modifier
                    .weight(0.6f)
                    .fillMaxHeight()
                    .padding(16.dp)
            ) {
                Text(text = selectedSidebarItem, color = Accent, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Aethero es un framework para crear bots de WhatsApp rápidos, escalables y altamente personalizables.",
                    color = TextSecondary,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
                Spacer(modifier = Modifier.height(24.dp))
                
                Surface(color = SurfaceDark, shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "npm install aethero",
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                        color = Accent,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                Surface(color = SurfaceVariantDark.copy(alpha = 0.5f), shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.padding(12.dp)) {
                        Icon(TablerIcons.InfoCircle, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Aethero requiere Node.js v18.0.0 o superior.",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                Text(text = "Características", color = Accent, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))
                
                val features = listOf("Ligero y rápido", "Sistema de plugins", "Fácil de extender", "Completamente modular")
                features.forEach { feature ->
                    Row(modifier = Modifier.padding(vertical = 4.dp)) {
                        Text(text = "✓", color = Accent, fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = feature, color = TextSecondary, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}
