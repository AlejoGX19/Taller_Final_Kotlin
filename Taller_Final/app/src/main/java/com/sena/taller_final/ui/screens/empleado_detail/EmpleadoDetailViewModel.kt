package com.sena.taller_final.ui.screens.empleado_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sena.taller_final.data.local.model.EmpleadoConCargo
import com.sena.taller_final.data.repository.EmpleadoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EmpleadoDetailViewModel @Inject constructor(
    private val repository: EmpleadoRepository
) : ViewModel() {
    private val _empleadoConCargo = MutableStateFlow<EmpleadoConCargo?>(null)
    val empleadoConCargo: StateFlow<EmpleadoConCargo?> = _empleadoConCargo.asStateFlow()

    private val _events = MutableSharedFlow<EmpleadoDetailEvent>()
    val events: SharedFlow<EmpleadoDetailEvent> = _events.asSharedFlow()

    fun loadEmpleado(localId: Int) {
        viewModelScope.launch {
            repository.getEmpleadoConCargo(localId).collect {
                _empleadoConCargo.value = it
                if (it == null) {
                    _events.emit(EmpleadoDetailEvent.NavigateBack)
                }
            }
        }
    }

    fun deleteEmpleado() {
        viewModelScope.launch {
            _empleadoConCargo.value?.empleado?.let {
                repository.markForDeletion(it)
                _empleadoConCargo.value = null
                _events.emit(EmpleadoDetailEvent.NavigateBack)
            }
        }
    }
}

sealed class EmpleadoDetailEvent {
    object NavigateBack : EmpleadoDetailEvent()
}