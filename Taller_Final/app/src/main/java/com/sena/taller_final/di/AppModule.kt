package com.sena.taller_final.di

import android.content.Context
import androidx.room.Room
import com.sena.taller_final.data.local.AppDatabase
import com.sena.taller_final.data.local.dao.LocalCargoDao
import com.sena.taller_final.data.local.dao.LocalEmpleadoDao
import com.sena.taller_final.data.remote.api.EmpleadoApiService
import com.sena.taller_final.data.repository.EmpleadoRepository
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "gestion_empleados_db")
            .fallbackToDestructiveMigrationOnDowngrade().build()

    @Provides
    fun provideEmpleadoDao(db: AppDatabase): LocalEmpleadoDao = db.empleadoDao()

    @Provides
    fun provideCargoDao(db: AppDatabase): LocalCargoDao = db.cargoDao()

    @Provides
    @Singleton
    fun provideMoshi(): Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    @Provides
    @Singleton
    fun provideRetrofit(moshi: Moshi): Retrofit = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:8000/")
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): EmpleadoApiService =
        retrofit.create(EmpleadoApiService::class.java)

    @Provides
    @Singleton
    fun provideRepository(api: EmpleadoApiService, eDao: LocalEmpleadoDao, cDao: LocalCargoDao) =
        EmpleadoRepository(api, eDao, cDao)
}