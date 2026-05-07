package com.zichan.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val label: String = "",
    val selectedIcon: ImageVector? = null,
    val unselectedIcon: ImageVector? = null
) {
    data object Home : Screen("home", "首页", Icons.Filled.Home, Icons.Outlined.Home)
    data object Assets : Screen("assets", "资产", Icons.Filled.Inventory2, Icons.Outlined.Inventory2)
    data object Stats : Screen("stats", "统计", Icons.Filled.DateRange, Icons.Outlined.DateRange)
    data object Profile : Screen("profile", "我的", Icons.Filled.Person, Icons.Outlined.Person)

    data object AssetDetail : Screen("asset_detail/{assetId}") {
        fun createRoute(assetId: Long) = "asset_detail/$assetId"
    }

    data object AssetEdit : Screen("asset_edit/{assetId}") {
        fun createRoute(assetId: Long = 0) = "asset_edit/$assetId"
    }

    data object PersonList : Screen("person_list")
    data object PersonEdit : Screen("person_edit/{personId}") {
        fun createRoute(personId: Long = 0) = "person_edit/$personId"
    }

    data object LendManage : Screen("lend_manage/{assetId}") {
        fun createRoute(assetId: Long) = "lend_manage/$assetId"
    }

    data object LogView : Screen("log_view")

    companion object {
        val bottomTabs = listOf(Home, Assets, Stats, Profile)
    }
}
