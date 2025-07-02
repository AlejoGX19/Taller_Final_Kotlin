package com.sena.taller_final.data.local.dao

import androidx.room.*
import com.sena.taller_final.data.local.model.CargoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LocalCargoDao {
    @Query("SELECT * FROM cargos")
    fun getAll(): Flow<List<CargoEntity>>

    @Query("SELECT * FROM cargos WHERE localId = :localId")
    fun getById(localId: Int): Flow<CargoEntity?>

    @Query("SELECT * FROM cargos WHERE remoteId = :remoteId")
    suspend fun getByRemoteId(remoteId: Int): CargoEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(cargo: CargoEntity): Long

    @Update
    suspend fun update(cargo: CargoEntity)

    @Delete
    suspend fun delete(cargo: CargoEntity)

    @Query("SELECT * FROM cargos WHERE isSynced = 0 AND remoteId IS NULL")
    suspend fun getUnsyncedCargos(): List<CargoEntity>

    @Query("SELECT * FROM cargos WHERE isSynced = 0 AND remoteId IS NOT NULL")
    suspend fun getUpdatedCargos(): List<CargoEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(cargos: List<CargoEntity>)
}