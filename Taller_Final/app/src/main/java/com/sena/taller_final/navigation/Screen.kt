package com.sena.taller_final.navigation

sealed class Screen(val route: String) {
    object EmpleadoList : Screen("empleado_list")
    object AddEmpleado : Screen("add_empleado")
    object EmpleadoDetail : Screen("empleado_detail/{localId}") {
        fun createRoute(localId: Int) = "empleado_detail/$localId"
    }
    object EmpleadoEdit : Screen("empleado_edit/{empleadoLocalId}") {
        fun createRoute(empleadoLocalId: Int) = "empleado_edit/$empleadoLocalId"
    }
}