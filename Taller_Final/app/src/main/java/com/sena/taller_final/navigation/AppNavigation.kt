package com.sena.taller_final.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.sena.taller_final.ui.screens.empleado_add_edit.AddEditEmpleadoScreen
import com.sena.taller_final.ui.screens.empleado_detail.EmpleadoDetailScreen
import com.sena.taller_final.ui.screens.empleado_edit.EmpleadoEditScreen
import com.sena.taller_final.ui.screens.empleado_list.EmpleadoListScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.EmpleadoList.route) {
        composable(Screen.EmpleadoList.route) {
            EmpleadoListScreen(navController = navController)
        }
        composable(Screen.AddEmpleado.route) {
            AddEditEmpleadoScreen(navController = navController)
        }
        composable(
            route = Screen.EmpleadoDetail.route,
            arguments = listOf(navArgument("localId") { type = NavType.IntType })
        ) {
            val localId = it.arguments?.getInt("localId") ?: 0
            EmpleadoDetailScreen(localId = localId, navController = navController)
        }
        composable(
            route = Screen.EmpleadoEdit.route,
            arguments = listOf(navArgument("empleadoLocalId") { defaultValue = -1 })
        ) { backStackEntry ->
            val empleadoLocalId = backStackEntry.arguments?.getInt("empleadoLocalId") ?: -1
            EmpleadoEditScreen(navController = navController, empleadoLocalId = empleadoLocalId)
        }
    }
}