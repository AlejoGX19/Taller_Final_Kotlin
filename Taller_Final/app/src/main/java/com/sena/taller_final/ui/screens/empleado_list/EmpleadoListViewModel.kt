package com.sena.taller_final.ui.screens.empleado_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sena.taller_final.data.local.model.EmpleadoEntity
import com.sena.taller_final.data.repository.EmpleadoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import android.util.Log

@HiltViewModel
class EmpleadoListViewModel @Inject constructor(
    private val repository: EmpleadoRepository
) : ViewModel() {
    val empleados: StateFlow<List<EmpleadoEntity>> = repository.getEmpleados()
        .onEach {
            Log.d("EmpleadoListVM", "Lista de empleados actualizada. Cantidad: ${it.size}. Nombres: ${it.map { emp -> emp.nombre }}")
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        syncData()
    }

    fun syncData() {
        viewModelScope.launch {
            repository.syncAll()
        }
    }
}