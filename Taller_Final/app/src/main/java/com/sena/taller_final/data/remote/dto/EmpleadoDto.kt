package com.sena.taller_final.data.remote.dto

import com.squareup.moshi.Json

data class EmpleadoDto(
    @field:Json(name = "id_empleado") val id_empleado: Int?,
    @field:Json(name = "nombre") val nombre: String,
    @field:Json(name = "edad") val edad: Int,
    @field:Json(name = "telefono") val telefono: String?
)