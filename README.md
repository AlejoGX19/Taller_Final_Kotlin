# Taller_Final_Kotlin

## Descripción

Este proyecto es el resultado final de lo aprendido en Kotlin, desarrollado principalmente con el objetivo de ofrecer una API funcional junto a un proyecto realizado en Kotlin, permitiendo la interacción entre ambas partes como un sistema completo, funcionado tando sin conexión a la API como con conxión y con sincronizacion al momento de conectarse a la API, todo esto dando como resultado un aplicación para la gestion de empleados.

## Características principales

- **API desarrollada en Python con FastApi** que expone endpoints para la gestión de datos.
- **Proyecto cliente en Kotlin** que consume la API y muestra la información de manera interactiva.
- **Gestión de base de datos** para almacenar y recuperar información.
- **Sincronización de datos.**

## Requisitos

### Generales

- **Git** para clonar el repositorio.
- **Entornos de de desarrollo** (VSCode, Android Studio)
- **Tener disponible el puerto: 8000**

### Para la API (Python)

- Python 3.8 o superior
- SQL (peronalmente la base de datos la tengo en WampServer)

### Para el proyecto Kotlin

- JDK 8 o superior
- IDE compatible con Kotlin (Android Studio recomendado)
---

## Puesta en marcha de la API

### 1. Clonar el repositorio

```bash
git clone https://github.com/AlejoGX19/Taller_Final_Kotlin.git
cd API_Empleados
```

### 2. Crear y activar entorno virtual (opcional pero recomendado)
- Si tienes algun problema con el entorno elimina la carpeta .venv y crea de nuevo el entorno

```bash
Crear entorno: python -m venv venv

Activar entonor: .venv\Scripts\activate

```

### 3. Instalar dependencias

```bash
pip install -r requirements.txt
```

### 4. Crear la base de datos

El proyecto utiliza SQL, Para crear la base de datos, ten en cuenta el siguiente script SQL.

```sql
Base de datos: `taller_gestion_empleados`

DROP TABLE IF EXISTS `empleados`;
CREATE TABLE IF NOT EXISTS `empleados` (
  `id_empleado` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) NOT NULL,
  `edad` int NOT NULL,
  `telefono` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id_empleado`)
);

DROP TABLE IF EXISTS `cargo`;
CREATE TABLE IF NOT EXISTS `cargo` (
  `id_cargo` int NOT NULL AUTO_INCREMENT,
  `sueldo` decimal(10,2) NOT NULL,
  `profesion` varchar(100) NOT NULL,
  `id_empleado` int NOT NULL,
  PRIMARY KEY (`id_cargo`),
  KEY `id_empleado` (`id_empleado`)
)

```

### 5. Ejecutar la API

El archivo principal de la API se llama `main.py`.

```bash
uvicorn main:app --reload
```

La API debería quedar corriendo en `http://localhost:8000`.

---

## Puesta en marcha del proyecto Kotlin

1. Abre el proyecto Kotlin en tu IDE favorito (Android Studio recomendado).
2. Asegúrate de tener configurado el JDK y Gradle correctamente.
3. Modifica la URL base de la API en el código Kotlin si es necesario para que apunte a tu instancia local.
4. Construye y ejecuta el proyecto Kotlin desde tu IDE.

---

### 6. Librerias utilizadas en el proyecto de Kotlin

```bash
lifecycle-viewmodel-compose
corountines-core
corountines-android
room-compiler
room-ktx
room-rutime 
androidx-navigation-compose
lifecycle-viewmodel-ktx 
retrofit 
retrofit-converter-moshi 
moshi
moshi-ksp 
hilt 
hilt-compiler 
hilt-navigation-compose
```

---

## Observaciones

- Asegúrate de revisar y actualizar las rutas y nombres de archivo si cambian por alguna razón en el proyecto.
- Si tienes dudas, abre un issue en el repositorio.

---
### Autor: Alejandro Sepúlveda Ramírez
---
