package com.sena.taller_final.data.repository

import android.util.Log
import com.sena.taller_final.data.remote.dto.CargoDto
import com.sena.taller_final.data.remote.dto.EmpleadoDto
import com.sena.taller_final.data.local.model.CargoEntity
import com.sena.taller_final.data.local.model.EmpleadoConCargo
import com.sena.taller_final.data.local.model.EmpleadoEntity
import com.sena.taller_final.data.remote.api.EmpleadoApiService
import com.sena.taller_final.data.local.dao.LocalCargoDao as RoomCargoDao
import com.sena.taller_final.data.local.dao.LocalEmpleadoDao as RoomEmpleadoDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.CancellationException

class EmpleadoRepository @Inject constructor(
    private val api: EmpleadoApiService,
    private val empleadoDao: RoomEmpleadoDao,
    private val cargoDao: RoomCargoDao
) {
    fun getEmpleados(): Flow<List<EmpleadoEntity>> = empleadoDao.getAll()
    fun getEmpleadoConCargo(id: Int): Flow<EmpleadoConCargo?> = empleadoDao.getEmpleadoConCargo(id)

    suspend fun createEmpleadoYCargo(empleado: EmpleadoEntity, cargo: CargoEntity) {

        val localId = empleadoDao.insert(empleado)
        cargoDao.insert(cargo.copy(empleadoLocalId = localId.toInt()))
    }

    suspend fun markForDeletion(empleado: EmpleadoEntity) {
        withContext(Dispatchers.IO) {
            empleadoDao.update(empleado.copy(needsDeletion = true))
        }
    }

    suspend fun updateEmpleadoYCargo(empleado: EmpleadoEntity, cargo: CargoEntity) {
        withContext(Dispatchers.IO) {
            empleadoDao.update(empleado)
            cargoDao.update(cargo)
        }
    }

    suspend fun syncAll() {
        Log.d("Sync", "Iniciando sincronización completa...")
        try {
            withContext(Dispatchers.IO) {
                syncCreations()
                syncUpdates()
                syncDeletions()
                refreshFromRemote()
            }
            Log.d("Sync", "Sincronización completa finalizada.")
        } catch (e: CancellationException) {
            Log.w("Sync", "Sincronización cancelada (JobCancellationException): ${e.message}")
        } catch (e: Exception) {
            Log.e("Sync", "Error durante la sincronización: ${e.message}", e)
        }
    }

    private suspend fun refreshFromRemote() {
        Log.d("Sync", "Refrescando datos desde el servidor...")
        try {
            val remoteEmpleados = api.getEmpleados()
            val remoteCargos = api.getCargos()

            val empleadoEntities = remoteEmpleados.map { dto ->
                EmpleadoEntity(
                    remoteId = dto.id_empleado,
                    nombre = dto.nombre,
                    edad = dto.edad,
                    telefono = dto.telefono,
                    isSynced = true,
                    localId = empleadoDao.getByRemoteId(dto.id_empleado!!)?.localId ?: 0
                )
            }
            empleadoDao.upsertAll(empleadoEntities)

            val cargoEntities = remoteCargos.mapNotNull { dto ->
                empleadoDao.getByRemoteId(dto.id_empleado)?.let { parent ->
                    CargoEntity(
                        remoteId = dto.id_cargo,
                        profesion = dto.profesion,
                        sueldo = dto.sueldo,
                        empleadoLocalId = parent.localId,
                        isSynced = true
                    )
                }
            }
            cargoDao.upsertAll(cargoEntities)
            Log.d("Sync", "Datos refrescados correctamente.")
        } catch(e: Exception) {
            Log.e("Sync", "Fallo al refrescar datos: ${e.message}")
        }
    }

    private suspend fun syncCreations() {
        Log.d("Sync", "Sincronizando creaciones de empleados...")
        val unsyncedEmpleados = empleadoDao.getUnsyncedEmpleados()
        unsyncedEmpleados.forEach { local ->
            try {
                val dto = EmpleadoDto(null, local.nombre, local.edad, local.telefono)
                val remote = api.createEmpleado(dto)
                local.remoteId = remote.id_empleado
                local.isSynced = true
                empleadoDao.update(local)
                Log.d("Sync", "Empleado ${local.nombre} sincronizado.")
            } catch (e: Exception) {
                Log.e("Sync", "Error sincronizando empleado ${local.nombre}: ${e.message}")
            }
        }

        Log.d("Sync", "Sincronizando creaciones de cargos...")
        val unsyncedCargos = cargoDao.getUnsyncedCargos()
        unsyncedCargos.forEach { localCargo ->
            try {
                val empleadoConCargoFlow = empleadoDao.getEmpleadoConCargo(localCargo.empleadoLocalId)
                val empleadoConCargo = empleadoConCargoFlow.first()

                if (empleadoConCargo?.empleado != null) {
                    val parentEmpleado = empleadoConCargo.empleado
                    if (parentEmpleado.isSynced && parentEmpleado.remoteId != null) {
                        val dto = CargoDto(null, localCargo.sueldo, localCargo.profesion, parentEmpleado.remoteId!!)
                        val remote = api.createCargo(dto)
                        localCargo.remoteId = remote.id_cargo
                        localCargo.isSynced = true
                        cargoDao.update(localCargo)
                        Log.d("Sync", "Cargo para ${parentEmpleado.nombre} sincronizado.")
                    } else {
                        Log.d("Sync", "Cargo para ${parentEmpleado.nombre} en espera, padre no sincronizado o sin remoteId.")
                    }
                } else {
                    Log.w("Sync", "Cargo local ${localCargo.profesion} con empleadoLocalId ${localCargo.empleadoLocalId} no tiene un empleado padre válido. No se sincronizará.")
                }
            } catch (e: Exception) {
                Log.e("Sync", "Error sincronizando cargo con ID local ${localCargo.localId}: ${e.message}")
            }
        }
    }

    private suspend fun syncUpdates() {
        Log.d("Sync", "Sincronizando actualizaciones...")

        val updatedEmpleados = empleadoDao.getUpdatedEmpleados()
        updatedEmpleados.forEach { local ->
            if (local.remoteId != null && !local.needsDeletion) {
                try {
                    val dto = EmpleadoDto(local.remoteId, local.nombre, local.edad, local.telefono)
                    api.updateEmpleado(local.remoteId!!, dto)
                    local.isSynced = true
                    empleadoDao.update(local)
                    Log.d("Sync", "Empleado ${local.nombre} (ID remoto: ${local.remoteId}) actualizado.")
                } catch (e: Exception) {
                    Log.e("Sync", "Error actualizando empleado ${local.nombre} (ID remoto: ${local.remoteId}): ${e.message}")
                }
            }
        }

        val updatedCargos = cargoDao.getUpdatedCargos()
        updatedCargos.forEach { localCargo ->
            if (localCargo.remoteId != null) {
                try {
                    val empleadoConCargoFlow = empleadoDao.getEmpleadoConCargo(localCargo.empleadoLocalId)
                    val empleadoConCargo = empleadoConCargoFlow.first()

                    if (empleadoConCargo?.empleado != null) {
                        val parentEmpleado = empleadoConCargo.empleado
                        if (parentEmpleado.isSynced && parentEmpleado.remoteId != null) {
                            val dto = CargoDto(localCargo.remoteId, localCargo.sueldo, localCargo.profesion, parentEmpleado.remoteId!!)
                            api.updateCargo(localCargo.remoteId!!, dto)
                            localCargo.isSynced = true
                            cargoDao.update(localCargo)
                            Log.d("Sync", "Cargo de ${parentEmpleado.nombre} (ID remoto: ${localCargo.remoteId}) actualizado.")
                        } else {
                            Log.d("Sync", "Cargo de ${parentEmpleado.nombre} en espera de actualización, padre no sincronizado.")
                        }
                    } else {
                        Log.w("Sync", "Cargo local ${localCargo.profesion} con empleadoLocalId ${localCargo.empleadoLocalId} no tiene un empleado padre válido para actualizar. Podría ser un huérfano.")
                    }
                } catch (e: Exception) {
                    Log.e("Sync", "Error actualizando cargo con ID local ${localCargo.localId} (ID remoto: ${localCargo.remoteId}): ${e.message}")
                }
            }
        }
    }

    private suspend fun syncDeletions() {
        Log.d("Sync", "Sincronizando eliminaciones...")
        val deletedEmpleados = empleadoDao.getDeletedEmpleados()
        deletedEmpleados.forEach { local ->
            try {
                if (local.remoteId != null) {
                    api.deleteEmpleado(local.remoteId!!)
                }
                empleadoDao.deleteFromDb(local.localId)
                Log.d("Sync", "Empleado ${local.nombre} eliminado permanentemente.")
            } catch (e: Exception) {
                Log.e("Sync", "Error eliminando empleado ${local.nombre}: ${e.message}")
            }
        }
    }
}