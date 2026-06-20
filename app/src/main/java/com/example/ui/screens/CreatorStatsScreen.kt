package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.ui.theme.*
import compose.icons.TablerIcons
import compose.icons.tablericons.ArrowLeft
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatorStatsScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgrounDark)
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 24.dp, bottom = 24.dp)
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(TablerIcons.ArrowLeft, contentDescription = "Back", tint = TextPrimary)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "GitHub Stats",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }

        Surface(
            color = SurfaceDark,
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, SurfaceVariantDark.copy(alpha = 0.5f)),
            shadowElevation = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "215 contributions in the last year",
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(16.dp))
                GitHubContributionsGraph()
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Other stats
        Text(text = "Contribution activity", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
        Spacer(modifier = Modifier.height(16.dp))
        
        Surface(
            color = SurfaceDark,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                ActivityItem("Created 18 commits in 3 repositories", "Syllkom/MyArchive")
                Spacer(modifier = Modifier.height(16.dp))
                ActivityItem("Created 1 repository", "Syllkom/Aethero-App")
                Spacer(modifier = Modifier.height(16.dp))
                ActivityItem("Opened their first pull request on GitHub", "Syllkom/Horeku...")
            }
        }
    }
}

@Composable
fun ActivityItem(title: String, repo: String) {
    Column {
        Text(text = title, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = repo, color = Accent, fontSize = 12.sp)
    }
}

@Composable
fun GitHubContributionsGraph() {
    val scrollState = rememberScrollState()
    val cellCountX = 52
    val cellCountY = 7
    val cellSize = 12.dp
    val cellSpacing = 3.dp

    // Make up some values for the graph
    // 0 = no contribution
    // 1-4 = increasing contribution level
    val randomData = List(cellCountX) {
        List(cellCountY) {
            if (Random.nextFloat() > 0.7f) Random.nextInt(1, 5) else 0
        }
    }
    
    val baseColor = SurfaceVariantDark
    val level1 = Color(0xFF0E4429)
    val level2 = Color(0xFF006D32)
    val level3 = Color(0xFF26A641)
    val level4 = Color(0xFF39D353)

    Row(modifier = Modifier.horizontalScroll(scrollState)) {
        Canvas(
            modifier = Modifier
                .width((cellSize * cellCountX) + (cellSpacing * (cellCountX - 1)))
                .height((cellSize * cellCountY) + (cellSpacing * (cellCountY - 1)))
        ) {
            val cellPx = cellSize.toPx()
            val spacePx = cellSpacing.toPx()
            
            for (x in 0 until cellCountX) {
                for (y in 0 until cellCountY) {
                    val contributionLevel = randomData[x][y]
                    val color = when (contributionLevel) {
                        0 -> baseColor
                        1 -> level1
                        2 -> level2
                        3 -> level3
                        4 -> level4
                        else -> baseColor
                    }
                    
                    drawRoundRect(
                        color = color,
                        topLeft = Offset(
                            x = x * (cellPx + spacePx),
                            y = y * (cellPx + spacePx)
                        ),
                        size = Size(cellPx, cellPx),
                        cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
                    )
                }
            }
        }
    }
}
