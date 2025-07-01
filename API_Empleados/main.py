from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from typing import List, Optional
import mysql.connector

app = FastAPI()

# Configuración de la conexión a la base de datos
db_config = {
    "host": "localhost",
    "user": "root",
    "password": "",
    "database": "taller_gestion_empleados"
}

# Modelos Pydantic
class Empleado(BaseModel):
    id_empleado: Optional[int] = None
    nombre: str
    edad: int
    telefono: Optional[str]

class Cargo(BaseModel):
    id_cargo: Optional[int] = None
    sueldo: float
    profesion: str
    id_empleado: int

# CRUD para Empleados

@app.get("/empleados", response_model=List[Empleado])
def get_empleados():
    conn = mysql.connector.connect(**db_config)
    cursor = conn.cursor(dictionary=True)
    cursor.execute("SELECT * FROM empleados")
    empleados = cursor.fetchall()
    cursor.close()
    conn.close()
    return empleados

@app.post("/empleados", response_model=Empleado)
def create_empleado(empleado: Empleado):
    conn = mysql.connector.connect(**db_config)
    cursor = conn.cursor()
    cursor.execute(
        "INSERT INTO empleados (nombre, edad, telefono) VALUES (%s, %s, %s)",
        (empleado.nombre, empleado.edad, empleado.telefono)
    )
    conn.commit()
    empleado.id_empleado = cursor.lastrowid
    cursor.close()
    conn.close()
    return empleado

@app.get("/empleados/{id_empleado}", response_model=Empleado)
def get_empleado(id_empleado: int):
    conn = mysql.connector.connect(**db_config)
    cursor = conn.cursor(dictionary=True)
    cursor.execute("SELECT * FROM empleados WHERE id_empleado = %s", (id_empleado,))
    empleado = cursor.fetchone()
    cursor.close()
    conn.close()
    if empleado:
        return empleado
    raise HTTPException(status_code=404, detail="Empleado no encontrado")

@app.put("/empleados/{id_empleado}", response_model=Empleado)
def update_empleado(id_empleado: int, empleado: Empleado):
    conn = mysql.connector.connect(**db_config)
    cursor = conn.cursor()
    cursor.execute(
        "UPDATE empleados SET nombre=%s, edad=%s, telefono=%s WHERE id_empleado=%s",
        (empleado.nombre, empleado.edad, empleado.telefono, id_empleado)
    )
    conn.commit()
    cursor.close()
    conn.close()
    empleado.id_empleado = id_empleado
    return empleado

@app.delete("/empleados/{id_empleado}")
def delete_empleado(id_empleado: int):
    conn = mysql.connector.connect(**db_config)
    cursor = conn.cursor()
    cursor.execute("DELETE FROM empleados WHERE id_empleado=%s", (id_empleado,))
    conn.commit()
    cursor.close()
    conn.close()
    return {"message": "Empleado eliminado"}

# CRUD para Cargo

@app.get("/cargos", response_model=List[Cargo])
def get_cargos():
    conn = mysql.connector.connect(**db_config)
    cursor = conn.cursor(dictionary=True)
    cursor.execute("SELECT * FROM cargo")
    cargos = cursor.fetchall()
    cursor.close()
    conn.close()
    return cargos

@app.post("/cargos", response_model=Cargo)
def create_cargo(cargo: Cargo):
    conn = mysql.connector.connect(**db_config)
    cursor = conn.cursor()
    cursor.execute(
        "INSERT INTO cargo (sueldo, profesion, id_empleado) VALUES (%s, %s, %s)",
        (cargo.sueldo, cargo.profesion, cargo.id_empleado)
    )
    conn.commit()
    cargo.id_cargo = cursor.lastrowid
    cursor.close()
    conn.close()
    return cargo

@app.get("/cargos/{id_cargo}", response_model=Cargo)
def get_cargo(id_cargo: int):
    conn = mysql.connector.connect(**db_config)
    cursor = conn.cursor(dictionary=True)
    cursor.execute("SELECT * FROM cargo WHERE id_cargo = %s", (id_cargo,))
    cargo = cursor.fetchone()
    cursor.close()
    conn.close()
    if cargo:
        return cargo
    raise HTTPException(status_code=404, detail="Cargo no encontrado")

@app.put("/cargos/{id_cargo}", response_model=Cargo)
def update_cargo(id_cargo: int, cargo: Cargo):
    conn = mysql.connector.connect(**db_config)
    cursor = conn.cursor()
    cursor.execute(
        "UPDATE cargo SET sueldo=%s, profesion=%s, id_empleado=%s WHERE id_cargo=%s",
        (cargo.sueldo, cargo.profesion, cargo.id_empleado, id_cargo)
    )
    conn.commit()
    cursor.close()
    conn.close()
    cargo.id_cargo = id_cargo
    return cargo

@app.delete("/cargos/{id_cargo}")
def delete_cargo(id_cargo: int):
    conn = mysql.connector.connect(**db_config)
    cursor = conn.cursor()
    cursor.execute("DELETE FROM cargo WHERE id_cargo=%s", (id_cargo,))
    conn.commit()
    cursor.close()
    conn.close()
    return {"message": "Cargo eliminado"}