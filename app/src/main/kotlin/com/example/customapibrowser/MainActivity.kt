package com.example.customapibrowser

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.customapibrowser.browser.BrowserViewModel
import com.example.customapibrowser.data.ApiConfigRepository
import com.example.customapibrowser.ui.screens.ApiEditScreen
import com.example.customapibrowser.ui.screens.ApiListScreen
import com.example.customapibrowser.ui.screens.BrowserScreen
import com.example.customapibrowser.ui.theme.CustomAPIBrowserTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CustomAPIBrowserTheme {
                val repository = ApiConfigRepository(applicationContext)
                val viewModel: BrowserViewModel = viewModel { BrowserViewModel(repository) }
                AppNavigation(viewModel)
            }
        }
    }
}

sealed class Screen(val route: String, val titleRes: Int, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    data object Browser : Screen("browser", R.string.browser_tab, Icons.Filled.Public)
    data object Apis : Screen("apis", R.string.apis_tab, Icons.Filled.List)
    data object ApiEdit : Screen("api_edit", R.string.edit, Icons.Filled.List)
}

@Composable
fun AppNavigation(viewModel: BrowserViewModel) {
    val navController = rememberNavController()
    val items = listOf(Screen.Browser, Screen.Apis)

    Scaffold(
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination
            if (currentDestination?.route in items.map { it.route }) {
                NavigationBar {
                    items.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = stringResource(screen.titleRes)) },
                            label = { Text(stringResource(screen.titleRes)) },
                            selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Browser.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Browser.route) {
                BrowserScreen(viewModel = viewModel)
            }
            composable(Screen.Apis.route) {
                ApiListScreen(
                    viewModel = viewModel,
                    onEdit = { api ->
                        navController.currentBackStackEntry
                            ?.savedStateHandle
                            ?.set("apiId", api.id)
                        navController.navigate(Screen.ApiEdit.route)
                    },
                    onAdd = {
                        navController.currentBackStackEntry
                            ?.savedStateHandle
                            ?.set<String?>("apiId", null)
                        navController.navigate(Screen.ApiEdit.route)
                    }
                )
            }
            composable(Screen.ApiEdit.route) {
                val apiId = navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.get<String>("apiId")
                ApiEditScreen(
                    viewModel = viewModel,
                    apiId = apiId,
                    onSave = { config ->
                        viewModel.saveApi(config)
                        navController.popBackStack()
                    },
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
