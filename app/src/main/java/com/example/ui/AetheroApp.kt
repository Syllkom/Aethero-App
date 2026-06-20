package com.example.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import compose.icons.TablerIcons
import compose.icons.tablericons.*
import com.example.ui.theme.Accent
import com.example.ui.theme.BackgrounDark
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextSecondary
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.PluginsScreen
import com.example.ui.screens.DocsScreen
import com.example.ui.screens.EditorScreen

@Composable
fun AetheroApp(viewModel: MainViewModel) {
    val navController = rememberNavController()

    Scaffold(
        containerColor = BackgrounDark,
        bottomBar = {
            Box(modifier = Modifier.padding(16.dp)) {
                AetheroBottomBar(navController = navController)
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            NavHost(
                navController = navController,
                startDestination = "dashboard",
                modifier = Modifier.fillMaxSize()
            ) {
                composable("dashboard") { DashboardScreen(viewModel, navController) }
                composable("plugins") { PluginsScreen(viewModel, navController) }
                composable("docs") { DocsScreen(navController) }
                composable("editor") { EditorScreen(navController) }
                composable("precios") { com.example.ui.screens.PricingScreen(navController) }
                composable("creator_stats") { com.example.ui.screens.CreatorStatsScreen(navController) }
            }
        }
    }
}

@Composable
fun AetheroBottomBar(navController: NavController) {
    val items = listOf(
        Pair("dashboard", Pair(TablerIcons.Home, "Inicio")),
        Pair("plugins", Pair(TablerIcons.Puzzle, "Plugins")),
        Pair("precios", Pair(TablerIcons.CurrencyDollar, "Precios")),
        Pair("docs", Pair(TablerIcons.FileText, "Docs")),
        Pair("editor", Pair(TablerIcons.Code, "Editor"))
    )
    
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        modifier = Modifier.clip(RoundedCornerShape(32.dp)),
        containerColor = Color(0xFF242424),
        contentColor = TextSecondary
    ) {
        items.forEach { (route, info) ->
            val (icon, label) = info
            val selected = currentRoute == route
            NavigationBarItem(
                icon = { Icon(icon, contentDescription = label) },
                label = { Text(label) },
                selected = selected,
                onClick = {
                    navController.navigate(route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.Black,
                    unselectedIconColor = TextSecondary,
                    selectedTextColor = Accent,
                    unselectedTextColor = TextSecondary,
                    indicatorColor = Accent
                )
            )
        }
    }
}
