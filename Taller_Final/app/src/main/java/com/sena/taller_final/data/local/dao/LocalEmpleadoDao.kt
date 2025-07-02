package com.sena.taller_final.data.local.dao

import androidx.room.*
import com.sena.taller_final.data.local.model.EmpleadoConCargo
import com.sena.taller_final.data.local.model.EmpleadoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LocalEmpleadoDao {
    @Query("SELECT * FROM empleados")
    fun getAll(): Flow<List<EmpleadoEntity>>

    @Query("SELECT * FROM empleados WHERE localId = :localId")
    fun getById(localId: Int): Flow<EmpleadoEntity?>

    @Query("SELECT * FROM empleados WHERE remoteId = :remoteId")
    suspend fun getByRemoteId(remoteId: Int): EmpleadoEntity?

    @Transaction
    @Query("SELECT * FROM empleados WHERE localId = :localId")
    fun getEmpleadoConCargo(localId: Int): Flow<EmpleadoConCargo?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(empleado: EmpleadoEntity): Long

    @Update
    suspend fun update(empleado: EmpleadoEntity)

    @Delete
    suspend fun delete(empleado: EmpleadoEntity)

    @Query("DELETE FROM empleados WHERE localId = :localId")
    suspend fun deleteFromDb(localId: Int)

    @Query("SELECT * FROM empleados WHERE isSynced = 0 AND remoteId IS NULL AND needsDeletion = 0")
    suspend fun getUnsyncedEmpleados(): List<EmpleadoEntity>

    @Query("SELECT * FROM empleados WHERE needsDeletion = 1")
    suspend fun getDeletedEmpleados(): List<EmpleadoEntity>

    @Query("SELECT * FROM empleados WHERE isSynced = 0 AND remoteId IS NOT NULL AND needsDeletion = 0")
    suspend fun getUpdatedEmpleados(): List<EmpleadoEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(empleados: List<EmpleadoEntity>)
}