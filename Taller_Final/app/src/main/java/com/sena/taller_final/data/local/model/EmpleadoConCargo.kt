package com.sena.taller_final.data.local.model

import androidx.room.Embedded
import androidx.room.Relation

data class EmpleadoConCargo(
    @Embedded val empleado: EmpleadoEntity,
    @Relation(
        parentColumn = "localId",
        entityColumn = "empleadoLocalId"
    )
    val cargo: CargoEntity?
)