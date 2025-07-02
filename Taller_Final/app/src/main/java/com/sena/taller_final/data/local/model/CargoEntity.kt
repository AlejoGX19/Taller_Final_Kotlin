package com.sena.taller_final.data.local.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "cargos",
    foreignKeys = [ForeignKey(
        entity = EmpleadoEntity::class,
        parentColumns = ["localId"],
        childColumns = ["empleadoLocalId"],
        onDelete = ForeignKey.CASCADE
    )
    ],
    indices = [Index(value = ["empleadoLocalId"])]
)
data class CargoEntity(
    @PrimaryKey(autoGenerate = true)
    val localId: Int = 0,
    var remoteId: Int?,
    val profesion: String,
    val sueldo: Double,
    val empleadoLocalId: Int,
    var isSynced: Boolean = false
)