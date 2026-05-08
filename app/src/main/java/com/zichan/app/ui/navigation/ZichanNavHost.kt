package com.zichan.app.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.zichan.app.ui.asset.AssetDetailScreen
import com.zichan.app.ui.asset.AssetEditScreen
import com.zichan.app.ui.asset.AssetListScreen
import com.zichan.app.ui.asset.LendManageScreen
import com.zichan.app.ui.home.HomeScreen
import com.zichan.app.ui.profile.LogScreen
import com.zichan.app.ui.profile.PersonEditScreen
import com.zichan.app.ui.profile.PersonListScreen
import com.zichan.app.ui.profile.ProfileScreen
import com.zichan.app.ui.stats.StatsScreen
import com.zichan.app.ui.theme.Amber500
import com.zichan.app.ui.theme.TextSecondary

@Composable
fun ZichanNavHost() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val showBottomBar = Screen.bottomTabs.any { screen ->
        currentDestination?.hierarchy?.any { it.route == screen.route } == true
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    Screen.bottomTabs.forEach { screen ->
                        val selected = currentDestination?.hierarchy?.any {
                            it.route == screen.route
                        } == true

                        NavigationBarItem(
                            icon = {
                                Icon(
                                    imageVector = if (selected) screen.selectedIcon!! else screen.unselectedIcon!!,
                                    contentDescription = screen.label
                                )
                            },
                            label = { Text(screen.label) },
                            selected = selected,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Amber500,
                                selectedTextColor = Amber500,
                                unselectedIconColor = TextSecondary,
                                unselectedTextColor = TextSecondary,
                                indicatorColor = Amber500.copy(alpha = 0.12f),
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    onAssetClick = { assetId ->
                        navController.navigate(Screen.AssetDetail.createRoute(assetId))
                    },
                    onAddAsset = {
                        navController.navigate(Screen.AssetEdit.createRoute(0))
                    }
                )
            }

            composable(Screen.Assets.route) {
                AssetListScreen(
                    onAssetClick = { assetId ->
                        navController.navigate(Screen.AssetDetail.createRoute(assetId))
                    },
                    onAddAsset = {
                        navController.navigate(Screen.AssetEdit.createRoute(0))
                    }
                )
            }

            composable(
                Screen.AssetDetail.route,
                arguments = listOf(navArgument("assetId") { type = NavType.LongType })
            ) { backStackEntry ->
                val assetId = backStackEntry.arguments?.getLong("assetId") ?: 0L
                AssetDetailScreen(
                    assetId = assetId,
                    onEdit = { navController.navigate(Screen.AssetEdit.createRoute(it)) },
                    onLend = { navController.navigate(Screen.LendManage.createRoute(it)) },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(
                Screen.AssetEdit.route,
                arguments = listOf(navArgument("assetId") { type = NavType.LongType })
            ) { backStackEntry ->
                val assetId = backStackEntry.arguments?.getLong("assetId") ?: 0L
                AssetEditScreen(
                    assetId = assetId,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(
                Screen.LendManage.route,
                arguments = listOf(navArgument("assetId") { type = NavType.LongType })
            ) { backStackEntry ->
                val assetId = backStackEntry.arguments?.getLong("assetId") ?: 0L
                LendManageScreen(
                    assetId = assetId,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Stats.route) {
                StatsScreen()
            }

            composable(Screen.Profile.route) {
                ProfileScreen(
                    onPersonList = { navController.navigate(Screen.PersonList.route) },
                    onLogView = { navController.navigate(Screen.LogView.route) }
                )
            }

            composable(Screen.PersonList.route) {
                PersonListScreen(
                    onPersonClick = { personId ->
                        navController.navigate(Screen.PersonEdit.createRoute(personId))
                    },
                    onAddPerson = {
                        navController.navigate(Screen.PersonEdit.createRoute(0))
                    },
                    onBack = { navController.popBackStack() },
                    onAssetClick = { assetId ->
                        navController.navigate(Screen.AssetDetail.createRoute(assetId))
                    }
                )
            }

            composable(
                Screen.PersonEdit.route,
                arguments = listOf(navArgument("personId") { type = NavType.LongType })
            ) { backStackEntry ->
                val personId = backStackEntry.arguments?.getLong("personId") ?: 0L
                PersonEditScreen(
                    personId = personId,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.LogView.route) {
                LogScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}
