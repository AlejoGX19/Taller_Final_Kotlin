package com.sena.taller_final.ui.screens.empleado_edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sena.taller_final.data.local.model.CargoEntity
import com.sena.taller_final.data.local.model.EmpleadoEntity
import com.sena.taller_final.data.repository.EmpleadoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EmpleadoEditUiState(
    val localId: Int = 0,
    val remoteId: Int? = null,
    val nombre: String = "",
    val edad: String = "",
    val telefono: String = "",
    val isSynced: Boolean = false,
    val needsDeletion: Boolean = false,
    val cargoLocalId: Int = 0,
    val cargoRemoteId: Int? = null,
    val profesion: String = "",
    val sueldo: String = "",
    val isCargoSynced: Boolean = false
)

@HiltViewModel
class EmpleadoEditViewModel @Inject constructor(
    private val repository: EmpleadoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EmpleadoEditUiState())
    val uiState: StateFlow<EmpleadoEditUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<EmpleadoEditEvent>()
    val events: SharedFlow<EmpleadoEditEvent> = _events.asSharedFlow()

    fun loadEmpleadoForEdit(localId: Int) {
        viewModelScope.launch {
            repository.getEmpleadoConCargo(localId).collectLatest { empleadoConCargo ->
                empleadoConCargo?.let {
                    val empleado = it.empleado
                    val cargo = it.cargo

                    _uiState.value = EmpleadoEditUiState(
                        localId = empleado.localId,
                        remoteId = empleado.remoteId,
                        nombre = empleado.nombre,
                        edad = empleado.edad.toString(),
                        telefono = empleado.telefono.toString(),
                        isSynced = empleado.isSynced,
                        needsDeletion = empleado.needsDeletion,
                        cargoLocalId = cargo?.localId ?: 0,
                        cargoRemoteId = cargo?.remoteId,
                        profesion = cargo?.profesion ?: "",
                        sueldo = cargo?.sueldo?.toString() ?: "",
                        isCargoSynced = cargo?.isSynced ?: false
                    )
                }
            }
        }
    }

    fun onNombreChange(newValue: String) { _uiState.update { it.copy(nombre = newValue) } }
    fun onEdadChange(newValue: String) { _uiState.update { it.copy(edad = newValue) } }
    fun onTelefonoChange(newValue: String) { _uiState.update { it.copy(telefono = newValue) } }
    fun onProfesionChange(newValue: String) { _uiState.update { it.copy(profesion = newValue) } }
    fun onSueldoChange(newValue: String) { _uiState.update { it.copy(sueldo = newValue) } }

    fun updateEmpleado() {
        viewModelScope.launch {
            try {
                val currentUiState = _uiState.value

                if (currentUiState.nombre.isBlank() || currentUiState.edad.isBlank() ||
                    currentUiState.telefono.isBlank() || currentUiState.profesion.isBlank() ||
                    currentUiState.sueldo.isBlank()) {
                    return@launch
                }

                val empleadoToUpdate = EmpleadoEntity(
                    localId = currentUiState.localId,
                    remoteId = currentUiState.remoteId,
                    nombre = currentUiState.nombre,
                    edad = currentUiState.edad.toIntOrNull() ?: 0,
                    telefono = currentUiState.telefono,
                    isSynced = false,
                    needsDeletion = currentUiState.needsDeletion
                )

                val cargoToUpdate = CargoEntity(
                    localId = currentUiState.cargoLocalId,
                    remoteId = currentUiState.cargoRemoteId,
                    profesion = currentUiState.profesion,
                    sueldo = currentUiState.sueldo.toDoubleOrNull() ?: 0.0,
                    empleadoLocalId = currentUiState.localId,
                    isSynced = false
                )

                repository.updateEmpleadoYCargo(empleadoToUpdate, cargoToUpdate)
                _events.emit(EmpleadoEditEvent.NavigateBack)
            } catch (e: Exception) {

            }
        }
    }
}

sealed class EmpleadoEditEvent {
    object NavigateBack : EmpleadoEditEvent()
}