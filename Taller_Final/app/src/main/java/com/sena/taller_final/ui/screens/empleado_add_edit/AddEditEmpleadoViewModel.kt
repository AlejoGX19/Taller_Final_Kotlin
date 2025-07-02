package com.sena.taller_final.ui.screens.empleado_add_edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sena.taller_final.data.local.model.CargoEntity
import com.sena.taller_final.data.local.model.EmpleadoEntity
import com.sena.taller_final.data.repository.EmpleadoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditEmpleadoViewModel @Inject constructor(
    private val repository: EmpleadoRepository
) : ViewModel() {
    fun save(
        nombre: String, edad: String, telefono: String,
        profesion: String, sueldo: String
    ) {
        viewModelScope.launch {
            val empleado = EmpleadoEntity(
                remoteId = null, nombre = nombre, edad = edad.toIntOrNull() ?: 0,
                telefono = telefono, isSynced = false
            )
            val cargo = CargoEntity(
                remoteId = null, profesion = profesion, sueldo = sueldo.toDoubleOrNull() ?: 0.0,
                empleadoLocalId = 0, isSynced = false
            )
            repository.createEmpleadoYCargo(empleado, cargo)
        }
    }
}