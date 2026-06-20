package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import compose.icons.TablerIcons
import compose.icons.tablericons.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.ui.theme.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL
import org.json.JSONObject

data class FileNode(
    val name: String,
    val path: String = "",
    val isFolder: Boolean = false,
    val children: MutableList<FileNode> = mutableListOf(),
    var isExpanded: Boolean = false
)

@Composable
fun EditorScreen(navController: NavController) {
    val context = LocalContext.current
    
    // File tree state
    var selectedFileNode by remember { mutableStateOf<FileNode?>(null) }
    var treeExpanded by remember { mutableStateOf(true) }
    
    var rootNodes by remember { mutableStateOf<List<FileNode>>(emptyList()) }
    val expandedPaths = remember { mutableStateListOf<String>() }
    var fileContent by remember { mutableStateOf("Selecciona un archivo para ver su contenido") }
    var isLoading by remember { mutableStateOf(true) }
    
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            try {
                val result = URL("https://api.github.com/repos/Syllkom/HorekuOs/git/trees/aethero?recursive=1").readText()
                val jsonResult = JSONObject(result)
                val treeArray = jsonResult.getJSONArray("tree")
                
                val parsedNodes = mutableListOf<FileNode>()
                
                for (i in 0 until treeArray.length()) {
                    val obj = treeArray.getJSONObject(i)
                    val path = obj.getString("path")
                    val type = obj.getString("type")
                    val isFolder = type == "tree"
                    
                    val parts = path.split("/")
                    var currentList = parsedNodes
                    var currentPath = ""
                    
                    for (j in parts.indices) {
                        val partName = parts[j]
                        val isLast = j == parts.size - 1
                        currentPath = if (currentPath.isEmpty()) partName else "$currentPath/$partName"
                        
                        var node = currentList.find { it.name == partName }
                        if (node == null) {
                            node = FileNode(
                                name = partName,
                                path = currentPath,
                                isFolder = if (isLast) isFolder else true,
                                isExpanded = false
                            )
                            currentList.add(node)
                        }
                        currentList = node.children
                    }
                }
                rootNodes = parsedNodes
                isLoading = false
            } catch (e: Exception) {
                isLoading = false
                fileContent = "Error cargando archivos: ${e.message}"
            }
        }
    }
    
    LaunchedEffect(selectedFileNode) {
        if (selectedFileNode != null && !selectedFileNode!!.isFolder) {
            fileContent = "Cargando..."
            withContext(Dispatchers.IO) {
                try {
                    val rawUrl = "https://raw.githubusercontent.com/Syllkom/HorekuOs/refs/heads/aethero/${selectedFileNode!!.path}"
                    val content = URL(rawUrl).readText()
                    fileContent = content
                } catch (e: Exception) {
                    fileContent = "No se pudo cargar el archivo: ${e.message}"
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgrounDark)
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Archivos", color = Accent, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                Row {
                    Icon(TablerIcons.Search, tint = TextSecondary, contentDescription = null)
                    Spacer(modifier = Modifier.width(16.dp))
                    Icon(
                        TablerIcons.Download,
                        tint = Accent,
                        contentDescription = "Descargar ZIP",
                        modifier = Modifier.clickable {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Syllkom/HorekuOs/archive/refs/heads/aethero.zip"))
                            context.startActivity(intent)
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { treeExpanded = !treeExpanded }) {
                Icon(if (treeExpanded) TablerIcons.Folder else TablerIcons.Folder, tint = Accent, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(selectedFileNode?.name ?: "Explorador", color = Accent, fontSize = 16.sp)
                Icon(if (treeExpanded) TablerIcons.ChevronUp else TablerIcons.ChevronDown, tint = TextSecondary, contentDescription = null)
            }
        }
        
        Row(modifier = Modifier.weight(1f).fillMaxWidth()) {
            if (treeExpanded) {
                Surface(
                    color = SurfaceDark,
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DividerColor),
                    modifier = Modifier.weight(0.4f).fillMaxHeight().padding(end = 8.dp)
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp)
                    ) {
                    if (isLoading) {
                        item {
                            Text("Cargando repositorio...", color = TextSecondary, fontSize = 12.sp, modifier = Modifier.padding(8.dp))
                        }
                    } else {
                        fun renderNode(node: FileNode, level: Int) {
                            val isExpanded = expandedPaths.contains(node.path)
                            item {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { 
                                            if (node.isFolder) {
                                                if (isExpanded) expandedPaths.remove(node.path) else expandedPaths.add(node.path)
                                            } else {
                                                selectedFileNode = node
                                            }
                                        }
                                        .padding(start = (level * 12).dp, top = 4.dp, bottom = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (node.isFolder) {
                                        Icon(if (isExpanded) TablerIcons.ChevronDown else TablerIcons.ChevronRight, tint = TextSecondary, contentDescription = null, modifier = Modifier.size(12.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                    }
                                    Icon(
                                        imageVector = if (node.isFolder) TablerIcons.Folder else TablerIcons.FileCode,
                                        contentDescription = null,
                                        tint = if (node.isFolder) Accent else TextSecondary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = node.name,
                                        color = if (node == selectedFileNode) Accent else TextSecondary,
                                        fontSize = 12.sp,
                                        maxLines = 1
                                    )
                                }
                            }
                            if (node.isFolder && isExpanded) {
                                node.children.forEach { renderNode(it, level + 1) }
                            }
                        }
                        rootNodes.forEach { renderNode(it, 0) }
                    }
                }
                }
            }

            // Editor Area
            Box(modifier = Modifier.weight(if (treeExpanded) 0.6f else 1f)) {
                val codeLines = fileContent.split("\n")

                LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
                    items(codeLines.size) { index ->
                        Row {
                            Text(
                                text = "${index + 1}",
                                color = TextSecondary.copy(alpha = 0.5f),
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp,
                                modifier = Modifier.width(32.dp)
                            )
                            Text(
                                text = codeLines[index],
                                color = TextSecondary,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                FloatingActionButton(
                    onClick = { },
                    containerColor = Color.White,
                    contentColor = Color.Black,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                ) {
                    Icon(TablerIcons.PlayerPlay, contentDescription = "Run")
                }
            }
        }
    }
}


