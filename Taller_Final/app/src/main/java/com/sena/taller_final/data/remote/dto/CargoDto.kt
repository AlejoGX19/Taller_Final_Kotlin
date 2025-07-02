package com.sena.taller_final.data.remote.dto

import com.squareup.moshi.Json

data class CargoDto(
    @field:Json(name = "id_cargo") val id_cargo: Int?,
    @field:Json(name = "sueldo") val sueldo: Double,
    @field:Json(name = "profesion") val profesion: String,
    @field:Json(name = "id_empleado") val id_empleado: Int
)