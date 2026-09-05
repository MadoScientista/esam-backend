# ESAM - Tienda Escolar en Línea (Backend)

Backend de una tienda en línea de artículos escolares y de oficina. Se encarga de
gestionar **usuarios**, **roles**, **regiones y comunas**, **marcas**, **productos**
y su **stock**.

## ¿Qué hace la aplicación?

- Permite registrar e iniciar sesión como **administrador**, **vendedor** o **cliente**.
- Administra el catálogo de productos: crear, editar, eliminar, buscar por nombre,
  marca, precio o stock, y controlar el stock (aumentar, disminuir o fijar un valor).
- Entrega el listado de regiones y comunas de Chile, útil para completar el
  domicilio de los usuarios.
- Al iniciar por primera vez, crea la base de datos, las tablas y **datos de ejemplo**
  (16 regiones, comunas, roles, marcas y productos) de forma automática.

## Tecnologías

- **Java 26** con **Spring Boot 4.1.1**
- **Spring Data JPA** (Hibernate) y **MySQL** como base de datos
- **Flyway** para crear y actualizar la base de datos automáticamente
- **Lombok** y **Spring Boot Actuator**

## Puesta en marcha

### 1. Requisitos

- Java 26
- MySQL 8 o superior
- Maven (incluido en el proyecto, igual se puede usar `mvnw`)

### 2. Crear la base de datos y el usuario (una sola vez)

Ejecuta en MySQL el script incluido en el proyecto:

```
src/main/resources/db/creacion_admin.sql
```

Este script crea la base `esam_db` y el usuario `admin_esam_db` (clave `1234`)
que la aplicación usa para conectarse.

### 3. Iniciar la aplicación

```bash
./mvnw clean compile
./mvnw spring-boot:run
```

La aplicación queda disponible en **http://localhost:8080**. La base de datos se
crea y se llena sola en el primer inicio (no hace falta hacer nada más).

### 4. Cuentas de acceso de ejemplo

| Rol       | Usuario                    | Contraseña    |
|-----------|----------------------------|---------------|
| Admin     | `admin@duoc.cl`            | `admin123`    |
| Vendedor  | `vendedores@gmail.cl`      | `vendedor123` |
| Cliente   | `cliente@duoc.com`         | `cliente123`  |

## Uso de la API

### Usuarios - `/api/usuarios`

| Método | Endpoint                 | Descripción                        |
|--------|--------------------------|------------------------------------|
| GET    | `/api/usuarios`          | Listar todos los usuarios          |
| GET    | `/api/usuarios/{id}`     | Obtener un usuario por su ID       |
| GET    | `/api/usuarios/rol/{idRolUsuario}` | Listar usuarios por rol    |
| POST   | `/api/usuarios/login`    | Iniciar sesión (nombreUsuario y password) |
| POST   | `/api/usuarios`          | Crear un usuario                   |
| POST   | `/api/usuarios/{id}`     | Editar un usuario                  |
| DELETE | `/api/usuarios/{id}`     | Eliminar un usuario                |

**Datos para crear o editar un usuario:**

```json
{
  "nombres": "string",
  "aPaterno": "string",
  "aMaterno": "string",
  "rut": 12345678,
  "dv": "K",
  "fechaNacimiento": "2000-01-01",
  "direccion": "string",
  "telefono": 56912345678,
  "nombreUsuario": "string",
  "correo": "string",
  "password": "string",
  "idRolUsuario": 1,
  "idRegion": 7,
  "idComuna": 104
}
```

**Respuesta del login:**

```json
{
  "loggin": true,
  "usuario": { "id": 1, "nombres": "Carlos", "...": "..." }
}
```

### Roles - `/api/roles`

| Método | Endpoint           | Descripción             |
|--------|--------------------|-------------------------|
| GET    | `/api/roles`       | Listar roles            |
| GET    | `/api/roles/{id}`  | Obtener un rol por ID   |
| POST   | `/api/roles`       | Crear un rol            |
| PUT    | `/api/roles/{id}`  | Editar un rol           |
| DELETE | `/api/roles/{id}`  | Eliminar un rol         |

### Regiones y comunas

| Método | Endpoint                   | Descripción                              |
|--------|----------------------------|------------------------------------------|
| GET    | `/api/regiones`            | Listar regiones                          |
| GET    | `/api/regiones/{id}`       | Obtener una región por ID                |
| GET    | `/api/regiones/comunas`    | Listar regiones con sus comunas          |
| GET    | `/api/regiones/{id}/comunas` | Obtener una región con sus comunas     |
| GET    | `/api/comunas`             | Listar comunas                           |
| GET    | `/api/comunas/{id}`        | Obtener una comuna por ID                |

> Los endpoints de crear, editar y eliminar también existen para regiones y
> comunas (`POST`, `PUT` y `DELETE` sobre las mismas rutas).

### Marcas - `/api/marcas`

| Método | Endpoint          | Descripción            |
|--------|-------------------|------------------------|
| GET    | `/api/marcas`     | Listar marcas          |
| GET    | `/api/marcas/{id}`| Obtener una marca por ID |

### Productos - `/api/productos`

| Método | Endpoint                                       | Descripción                              |
|--------|------------------------------------------------|------------------------------------------|
| GET    | `/api/productos`                               | Listar todos los productos               |
| GET    | `/api/productos/{sku}`                         | Obtener un producto por su SKU           |
| GET    | `/api/productos/marca/{idMarca}`               | Listar productos de una marca            |
| GET    | `/api/productos/precio?min=X&max=Y`            | Listar productos por rango de precio     |
| GET    | `/api/productos/stock?min=X&max=Y`             | Listar productos por rango de stock      |
| GET    | `/api/productos/nombre/{nombre}`               | Listar productos por nombre              |
| POST   | `/api/productos`                               | Crear un producto                        |
| POST   | `/api/productos/{sku}`                         | Editar un producto                       |
| PUT    | `/api/productos/{sku}/stock/setear?stock=X`    | Fijar el stock en un valor específico    |
| PUT    | `/api/productos/{sku}/stock/disminuir?unidades=X` | Disminuir el stock                   |
| PUT    | `/api/productos/{sku}/stock/aumentar?unidades=X` | Aumentar el stock                     |
| DELETE | `/api/productos/{sku}`                         | Eliminar un producto                     |

**Datos para crear o editar un producto:**

```json
{
  "nombre": "string",
  "descripcion": "string",
  "idMarca": 1,
  "precio": 3990,
  "stock": 25,
  "img": "string"
}
```

> El stock solo acepta números enteros positivos o cero (>= 0). Si al disminuir
> el stock quedaría en negativo, la aplicación devuelve un error.

## Estructura del proyecto

```
src/main/java/com/esam/esam_backend/
├── controller/       Rutas de la API (6 controladores)
├── dto/              Objetos de entrada y salida de la API
├── mapper/           Conversión entre entidades y DTOs
├── model/            Entidades de la base de datos
├── repository/       Acceso a los datos
└── service/          Lógica de negocio

src/main/resources/
├── application.yaml  Configuración (puerto, base de datos, Flyway)
├── db/creacion_admin.sql  Script para crear la BD y el usuario
└── db/migration/     Migraciones de Flyway (V1 a V6)
```