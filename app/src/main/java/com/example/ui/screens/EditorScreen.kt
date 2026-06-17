package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import compose.icons.TablerIcons
import compose.icons.tablericons.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.ui.theme.*

@Composable
fun EditorScreen(navController: NavController) {
    var selectedTab by remember { mutableStateOf("Código") }
    val tabs = listOf("Código", "Metadata")
    
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
                Text("Editor", color = Accent, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                Row {
                    Icon(TablerIcons.Search, tint = TextSecondary, contentDescription = null)
                    Spacer(modifier = Modifier.width(16.dp))
                    Icon(TablerIcons.DotsVertical, tint = TextSecondary, contentDescription = null)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(TablerIcons.FileCode, tint = TextSecondary, contentDescription = null, modifier = Modifier.size(16.dp)) // Document Icon mock
                Spacer(modifier = Modifier.width(8.dp))
                Text("eval.js", color = Accent, fontSize = 16.sp)
                Icon(TablerIcons.ChevronDown, tint = TextSecondary, contentDescription = null)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                tabs.forEach { tab ->
                    val isSelected = selectedTab == tab
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedTab = tab },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = tab,
                                color = if (isSelected) Accent else TextSecondary,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                            if (isSelected) {
                                Box(modifier = Modifier.fillMaxWidth().height(2.dp).background(Accent))
                            }
                        }
                    }
                }
            }
        }
        
        // Editor Area
        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            val codeLines = """
const { exec } = require('child_process');

module.exports = {
  command: true,
  usePrefix: false,
  case: ['>', '=>', '$'],
  description: 'Ejecuta código asíncrono\n(JavaScript) y comandos de consola\n(Shell).',
  category: 'owner',
  usage: ['> <script>', '=> <return script>', '$ <shell>'],
};

run: async (client, message, args) => {
  const text = args.join(' ');
  if (!text) return message.reply('Ingresa un código o comando para ejecutar.');
  
  try {
    if (message.body.startsWith('$')) {
      exec(text, (err, stdout, stderr) => {
        if (err) return message.reply(stderr);
        message.reply(stdout || 'Comando ejecutado.');
      });
    }
  } catch (error) {
    message.reply(`Error: ${"$"}error`);
  }
};
            """.trimIndent().split("\n")

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

            // Floating Play Button
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
        
        // Terminal Bottom Sheet simulation
        Surface(
            color = SurfaceDark,
            modifier = Modifier.fillMaxWidth().height(150.dp),
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text("Terminal", color = Accent, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text("Salida", color = TextSecondary, fontSize = 14.sp)
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text("+", color = Accent, fontSize = 18.sp)
                        Icon(TablerIcons.Trash, contentDescription = "Clear", tint = TextSecondary, modifier = Modifier.size(18.dp))
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text("✓ Plugin cargado correctamente (eval.js)", color = StatusGreen, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                Text("Tiempo de carga: 120ms", color = TextSecondary, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Aethero > _", color = Accent, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
            }
        }
    }
}
