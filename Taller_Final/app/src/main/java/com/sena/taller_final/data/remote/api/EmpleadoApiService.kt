package com.sena.taller_final.data.remote.api

import com.sena.taller_final.data.remote.dto.CargoDto
import com.sena.taller_final.data.remote.dto.EmpleadoDto
import retrofit2.http.*

interface EmpleadoApiService {
    @GET("empleados")
    suspend fun getEmpleados(): List<EmpleadoDto>

    @GET("cargos")
    suspend fun getCargos(): List<CargoDto>

    @POST("empleados")
    suspend fun createEmpleado(@Body empleado: EmpleadoDto): EmpleadoDto

    @POST("cargos")
    suspend fun createCargo(@Body cargo: CargoDto): CargoDto

    @DELETE("empleados/{id}")
    suspend fun deleteEmpleado(@Path("id") id: Int)

    @PUT("empleados/{id}")
    suspend fun updateEmpleado(@Path("id") id: Int, @Body empleado: EmpleadoDto): EmpleadoDto

    @PUT("cargos/{id}")
    suspend fun updateCargo(@Path("id") id: Int, @Body cargo: CargoDto): CargoDto
}