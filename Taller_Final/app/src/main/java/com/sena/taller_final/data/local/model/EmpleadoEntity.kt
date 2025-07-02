package com.sena.taller_final.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "empleados")
data class EmpleadoEntity(
    @PrimaryKey(autoGenerate = true)
    val localId: Int = 0,
    var remoteId: Int?,
    val nombre: String,
    val edad: Int,
    val telefono: String?,
    // sincronizacion
    var isSynced: Boolean = false,
    var needsDeletion: Boolean = false
)