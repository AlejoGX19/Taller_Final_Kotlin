package com.sena.taller_final.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sena.taller_final.data.local.model.CargoEntity
import com.sena.taller_final.data.local.model.EmpleadoEntity
import com.sena.taller_final.data.local.dao.LocalCargoDao
import com.sena.taller_final.data.local.dao.LocalEmpleadoDao

@Database(entities = [EmpleadoEntity::class, CargoEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun empleadoDao(): LocalEmpleadoDao
    abstract fun cargoDao(): LocalCargoDao
}