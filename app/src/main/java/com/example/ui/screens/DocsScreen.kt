package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import compose.icons.TablerIcons
import compose.icons.tablericons.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.ui.theme.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL

data class DocSection(
    val title: String,
    val subsections: MutableList<DocSubsection> = mutableListOf()
)

data class DocSubsection(
    val title: String,
    var content: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocsScreen(navController: NavController) {
    var searchQuery by remember { mutableStateOf("") }
    
    var sections by remember { mutableStateOf(listOf<DocSection>()) }
    var selectedSectionIndex by remember { mutableStateOf(0) }
    var selectedSubsectionIndex by remember { mutableStateOf(0) }
    
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            try {
                val url = "https://raw.githubusercontent.com/Syllkom/HorekuOs-Landing-Page/refs/heads/main/src/pages/docs/DOCS.md"
                val content = URL(url).readText()
                
                val parsedSections = mutableListOf<DocSection>()
                var currentSection: DocSection? = null
                var currentSubsection: DocSubsection? = null
                val currentContent = StringBuilder()
                
                for (line in content.lines()) {
                    if (line.startsWith("# ") && !line.startsWith("## ")) {
                        if (currentSubsection != null) {
                            currentSubsection.content = currentContent.toString()
                            currentContent.clear()
                        } else if (currentSection != null && currentContent.isNotBlank()) {
                            // Add a default General section if there was text before the first ##
                            currentSection.subsections.add(0, DocSubsection("General", currentContent.toString()))
                            currentContent.clear()
                        }
                        
                        currentSection = DocSection(line.removePrefix("# ").trim())
                        parsedSections.add(currentSection)
                        currentSubsection = null
                    } else if (line.startsWith("## ")) {
                        if (currentSubsection != null) {
                            currentSubsection.content = currentContent.toString()
                            currentContent.clear()
                        } else if (currentSection == null) {
                            currentSection = DocSection("Documentación")
                            parsedSections.add(currentSection)
                        } else if (currentContent.isNotBlank()) {
                            currentSection.subsections.add(DocSubsection("General", currentContent.toString()))
                            currentContent.clear()
                        }
                        
                        currentSubsection = DocSubsection(line.removePrefix("## ").trim())
                        currentSection!!.subsections.add(currentSubsection)
                    } else {
                        currentContent.append(line).append("\n")
                    }
                }
                
                if (currentSubsection != null) {
                    currentSubsection.content = currentContent.toString()
                } else if (currentSection != null && currentContent.isNotBlank()) {
                    currentSection.subsections.add(DocSubsection("General", currentContent.toString()))
                }
                
                sections = parsedSections
                isLoading = false
            } catch (e: Exception) {
                isLoading = false
                errorMessage = "No se pudo cargar DOCS.md dinámico: ${e.message}"
            }
        }
    }

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
            
            // Dynamic Tabs (Pills)
            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (sections.isNotEmpty()) {
                    sections.forEachIndexed { index, section ->
                        val isSelected = selectedSectionIndex == index
                        Surface(
                            color = if (isSelected) Accent else SurfaceVariantDark,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.clickable {
                                selectedSectionIndex = index
                                selectedSubsectionIndex = 0
                            }
                        ) {
                            Text(
                                text = section.title,
                                color = if (isSelected) Color.Black else TextSecondary,
                                fontSize = 14.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = SurfaceVariantDark)
        }
        
        Row(modifier = Modifier.fillMaxSize()) {
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Cargando la documentación...", color = TextSecondary)
                }
            } else if (errorMessage != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(errorMessage ?: "", color = Color.Red)
                }
            } else if (sections.isNotEmpty()) {
                val currentSection = sections[selectedSectionIndex]
                val subsections = currentSection.subsections
                
                // Sidebar
                LazyColumn(
                    modifier = Modifier
                        .weight(0.4f)
                        .fillMaxHeight()
                        .padding(start = 8.dp, top = 16.dp)
                ) {
                    items(subsections.size) { index ->
                        val item = subsections[index]
                        val isSelected = selectedSubsectionIndex == index
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedSubsectionIndex = index }
                                .padding(vertical = 4.dp, horizontal = 4.dp)
                        ) {
                            Surface(
                                color = if (isSelected) Accent else SurfaceDark,
                                shape = RoundedCornerShape(20.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(12.dp)) {
                                    Icon(TablerIcons.InfoCircle, contentDescription = null, tint = if (isSelected) Color.Black else TextSecondary, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = item.title,
                                        color = if (isSelected) Color.Black else TextSecondary,
                                        fontSize = 12.sp,
                                        maxLines = 2
                                    )
                                }
                            }
                        }
                    }
                }
                
                // Content
                val selectedSubsection = if (subsections.isNotEmpty()) subsections[selectedSubsectionIndex] else null
                
                LazyColumn(
                    modifier = Modifier
                        .weight(0.6f)
                        .fillMaxHeight()
                        .padding(16.dp)
                ) {
                    if (selectedSubsection != null) {
                        item {
                            Text(text = selectedSubsection.title, color = Accent, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                        
                        val paragraphs = selectedSubsection.content.split("\n\n")
                        items(paragraphs) { paragraph ->
                            if (paragraph.isNotBlank()) {
                                if (paragraph.startsWith("```")) {
                                    Surface(color = SurfaceDark, shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth()) {
                                        SelectionContainer {
                                            Text(
                                                text = paragraph.removePrefix("```").removeSuffix("```").trim(),
                                                fontFamily = FontFamily.Monospace,
                                                color = Accent,
                                                fontSize = 12.sp,
                                                modifier = Modifier.padding(16.dp)
                                            )
                                        }
                                    }
                                } else {
                                    Text(
                                        text = paragraph.trim(),
                                        color = TextSecondary,
                                        fontSize = 14.sp,
                                        lineHeight = 20.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                        }
                    } else {
                        item {
                            Text("Esta sección no tiene contenido.", color = TextSecondary)
                        }
                    }
                    
                    item {
                        Spacer(modifier = Modifier.height(48.dp)) // padding at bottom
                    }
                }
            }
        }
    }
}
