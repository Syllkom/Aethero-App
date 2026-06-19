package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.*
import compose.icons.TablerIcons
import compose.icons.tablericons.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.ui.res.painterResource
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectTapGestures
import com.example.R
import com.example.ui.MainViewModel
import com.example.data.PluginEntity
import com.example.ui.theme.*

@Composable
fun DashboardScreen(viewModel: MainViewModel, navController: NavController) {
    val plugins by viewModel.plugins.collectAsState()
    val pluginCount by viewModel.pluginCount.collectAsState()
    val commandCount by viewModel.commandCount.collectAsState()
    val githubActivity by viewModel.githubActivity.collectAsState()
    val lastSyncTime by viewModel.lastSyncTime.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgrounDark)
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item { Spacer(modifier = Modifier.height(24.dp)) }
        item { HeaderArea() }
        item { Spacer(modifier = Modifier.height(24.dp)) }
        item { RepoStatusCard(onSync = { viewModel.forceSync() }, lastSyncTime = lastSyncTime) }
        item { Spacer(modifier = Modifier.height(16.dp)) }
        item { StatsRow(pluginCount, commandCount) }
        item { Spacer(modifier = Modifier.height(24.dp)) }
        item { ActivityChart(githubActivity = githubActivity) }
        item { Spacer(modifier = Modifier.height(24.dp)) }
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Plugins recientes",
                    fontSize = 18.sp,
                    color = Accent,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = {
                    navController.navigate("plugins") {
                        popUpTo("dashboard") {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }) {
                    Text("Ver todos", color = TextSecondary)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
        items(plugins.take(3)) { plugin ->
            PluginRecentItem(plugin)
        }
        item { Spacer(modifier = Modifier.height(32.dp)) }
    }
}

@Composable
fun HeaderArea() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(id = R.drawable.ic_aethero),
                contentDescription = "Logo",
                tint = Accent,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = "Aethero", color = Accent, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Text(text = "WhatsApp Bot Framework", color = TextSecondary, fontSize = 12.sp)
            }
        }
        IconButton(onClick = { }) {
            Icon(TablerIcons.Bell, contentDescription = "Notificaciones", tint = TextSecondary)
        }
    }
}

@Composable
fun RepoStatusCard(onSync: () -> Unit, lastSyncTime: String) {
    Surface(
        color = SurfaceDark,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, SurfaceVariantDark.copy(alpha = 0.5f)),
        shadowElevation = 8.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(SurfaceVariantDark),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(TablerIcons.BrandGithub, contentDescription = "Repo", tint = Accent)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(text = "Repositorio conectado", color = TextSecondary, fontSize = 12.sp)
                        Text(text = "Aethero-Framework/Aethero-Bot", color = Accent, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).clip(RoundedCornerShape(50)).background(StatusGreen))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Conectado", color = TextSecondary, fontSize = 12.sp)
                        }
                    }
                }
                Icon(TablerIcons.ChevronRight, contentDescription = "Go", tint = TextSecondary)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "Última sincronización", color = TextSecondary, fontSize = 12.sp)
                    Text(text = lastSyncTime, color = Accent, fontSize = 14.sp)
                }
                IconButton(
                    onClick = onSync,
                    modifier = Modifier.background(SurfaceVariantDark, RoundedCornerShape(50))
                ) {
                    Icon(TablerIcons.Refresh, contentDescription = "Sync", tint = Accent)
                }
            }
        }
    }
}

@Composable
fun StatsRow(pluginCount: Int, commandCount: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        StatCard(modifier = Modifier.weight(1f), title = "Plugins", count = if (pluginCount > 0) pluginCount else 128, trend = "+12 este mes")
        StatCard(modifier = Modifier.weight(1f), title = "Comandos", count = if (commandCount > 0) commandCount else 842, trend = "+84 este mes")
    }
}

@Composable
fun StatCard(modifier: Modifier = Modifier, title: String, count: Int, trend: String) {
    Surface(
        color = SurfaceDark,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, SurfaceVariantDark.copy(alpha = 0.5f)),
        shadowElevation = 8.dp,
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, color = TextSecondary, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = count.toString(), color = Accent, fontSize = 32.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = trend, color = StatusGreen, fontSize = 12.sp)
        }
    }
}

@Composable
fun ActivityChart(githubActivity: List<Int>) {
    var selectedIndex by remember { mutableStateOf<Int?>(null) }
    
    Surface(
        color = SurfaceDark,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, SurfaceVariantDark.copy(alpha = 0.5f)),
        shadowElevation = 8.dp,
        modifier = Modifier.fillMaxWidth().height(180.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Actividad (7 días)", color = Accent, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Surface(
                    color = SurfaceVariantDark,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Commits (repo)",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Box(modifier = Modifier.fillMaxSize()) {
                val dataPoints = remember(githubActivity) {
                    val maxVal = githubActivity.maxOrNull()?.coerceAtLeast(1) ?: 1
                    githubActivity.map { (it.toFloat() / maxVal).coerceIn(0.1f, 1.0f) }
                }
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onTap = { offset ->
                                    val width = size.width.toFloat()
                                    // 7 items = 6 intervals
                                    val stepX = width / 6
                                    // find closest index
                                    val index = Math.round(offset.x / stepX)
                                    selectedIndex = index.coerceIn(0, 6)
                                }
                            )
                        }
                ) {
                    val path = Path()
                    val width = size.width
                    val height = size.height
                    
                    val stepX = width / (dataPoints.size - 1)
                    
                    path.moveTo(0f, height * (1 - dataPoints[0]))
                    for (i in 1 until dataPoints.size) {
                        val x = i * stepX
                        val y = height * (1 - dataPoints[i])
                        val x0 = (i - 1) * stepX
                        val y0 = height * (1 - dataPoints[i - 1])
                        val cx = (x0 + x) / 2
                        path.cubicTo(cx, y0, cx, y, x, y)
                    }

                    // Fill area below contour
                    val fillPath = Path().apply {
                        addPath(path)
                        lineTo(width, height)
                        lineTo(0f, height)
                        close()
                    }

                    drawPath(
                        path = fillPath,
                        brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                            colors = listOf(Accent.copy(alpha = 0.3f), Color.Transparent)
                        )
                    )

                    drawPath(
                        path = path,
                        color = Accent,
                        style = Stroke(
                            width = 3.dp.toPx(),
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                    )
                    
                    // Draw indicator for selected point
                    selectedIndex?.let { index ->
                        val pointX = index * stepX
                        val pointY = height * (1 - dataPoints[index])
                        drawCircle(
                            color = Accent,
                            radius = 6.dp.toPx(),
                            center = androidx.compose.ui.geometry.Offset(pointX, pointY)
                        )
                        drawCircle(
                            color = BackgrounDark,
                            radius = 3.dp.toPx(),
                            center = androidx.compose.ui.geometry.Offset(pointX, pointY)
                        )
                    }
                }
                
                // Show tooltip overlay when point is selected
                selectedIndex?.let { index ->
                    val actualValue = githubActivity.getOrNull(index) ?: 0
                    val daysAgo = 6 - index
                    val dayLabel = if (daysAgo == 0) "Hoy" else "Hace $daysAgo d"
                    
                    Surface(
                        color = TextSecondary,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.align(Alignment.TopCenter)
                    ) {
                        Text(
                            text = "$dayLabel: $actualValue commits",
                            color = BackgrounDark,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PluginRecentItem(plugin: PluginEntity) {
    Surface(
        color = SurfaceDark,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, SurfaceVariantDark.copy(alpha = 0.5f)),
        shadowElevation = 8.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceVariantDark),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "JS", color = Accent, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Text(text = plugin.name, color = Accent, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Surface(
                        color = SurfaceVariantDark,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = plugin.category,
                            color = TextSecondary,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = plugin.description, color = TextSecondary, fontSize = 12.sp, maxLines = 1)
            }
        }
    }
}
