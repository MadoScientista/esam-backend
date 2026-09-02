# ESAM Backend

API RESTful para la gestion de productos, usuarios y datos geograficos de una tienda en linea.

## Stack Tecnologico

- **Java 26** con **Spring Boot 4.1.1**
- **Spring Data JPA** (Hibernate)
- **MySQL** como base de datos
- **Flyway** para migraciones de BD
- **Lombok** para reduccion de boilerplate
- **Spring Boot Actuator** para monitoreo

## Inicio Rápido

### Prerequisitos

- Java 26
- MySQL 8+
- Maven

### Configuracion

La aplicacion se ejecuta en el puerto **8087** y se conecta a MySQL en `localhost:3306/esam_db`.

```bash
# Compilar el proyecto
./mvnw clean compile

# Ejecutar
./mvnw spring-boot:run
```

La base de datos `esam_db` se crea automaticamente si no existe. Las migraciones de Flyway se ejecutan al iniciar.

## Modelos de Datos

| Modelo          | Descripcion                              | Campo Clave |
|-----------------|------------------------------------------|-------------|
| `Usuario`       | Usuarios del sistema                     | `idUsuario` |
| `RolUsuario`    | Roles de usuario                         | `idRolUsuario` |
| `Region`        | Regiones geograficas                     | `idRegion`  |
| `Comuna`        | Comunas dentro de una region             | `idComuna`  |
| `Marca`         | Marcas de productos                      | `idMarca`   |
| `Producto`      | Productos de la tienda                   | `sku`       |

### Relaciones

- `RolUsuario` 1 — N `Usuario`
- `Region` 1 — N `Comuna`
- `Region` 1 — N `Usuario`
- `Comuna` 1 — N `Usuario`
- `Marca` 1 — N `Producto`

> Cada `Usuario` pertenece a un `RolUsuario`, a una `Comuna` y a una `Region`.

---

## Endpoints

### Usuarios - `/api/usuarios`

> Los endpoints de consulta y mutacion de usuarios devuelven un `UsuarioDTOResponse`
> donde `rol` es el **nombre** del rol (no el `idRolUsuario`).

| Metodo   | Endpoint                      | Descripcion                     | Body / Params                       |
|----------|-------------------------------|---------------------------------|-------------------------------------|
| `GET`    | `/api/usuarios`               | Obtener todos los usuarios      | —                                   |
| `GET`    | `/api/usuarios/{id}`          | Obtener un usuario por ID       | —                                   |
| `GET`    | `/api/usuarios/rol/{idRolUsuario}` | Obtener usuarios por rol   | —                                   |
| `POST`   | `/api/usuarios/login`         | Confirmar login                 | `UsuarioDTOLogin` JSON              |
| `POST`   | `/api/usuarios`               | Crear un usuario                | `Usuario` JSON                      |
| `PUT`    | `/api/usuarios/{id}`          | Editar un usuario               | `Usuario` JSON                      |
| `DELETE` | `/api/usuarios/{id}`          | Eliminar un usuario             | —                                   |

**Body `Usuario` (entrada):**
```json
{
  "pNombre": "string",
  "sNombre": "string",
  "aPaterno": "string",
  "aMaterno": "string",
  "nombreUsuario": "string",
  "password": "string",
  "rolUsuario": { "idRolUsuario": 1 },
  "comuna": { "idComuna": 1 },
  "region": { "idRegion": 1 }
}
```

**Body `UsuarioDTOLogin`:**
```json
{
  "nombreUsuario": "string",
  "password": "string"
}
```

**Respuesta `UsuarioDTOLoginResponse` (login):**
```json
{
  "loggin": true
}
```

**Respuesta `UsuarioDTOResponse`:**
```json
{
  "id": 1,
  "pNombre": "string",
  "sNombre": "string",
  "aPaterno": "string",
  "aMaterno": "string",
  "rol": "Nombre del rol",
  "nombreUsuario": "string"
}
```

---

### Roles - `/api/roles`

| Metodo   | Endpoint              | Descripcion              | Body / Params      |
|----------|-----------------------|--------------------------|--------------------|
| `GET`    | `/api/roles`          | Obtener todos los roles  | —                  |
| `GET`    | `/api/roles/{id}`     | Obtener un rol por ID    | —                  |
| `POST`   | `/api/roles`          | Crear un rol             | `RolUsuario` JSON  |
| `PUT`    | `/api/roles/{id}`     | Editar un rol            | `RolUsuario` JSON  |
| `DELETE` | `/api/roles/{id}`     | Eliminar un rol          | —                  |

**Body `RolUsuario`:**
```json
{
  "nombre": "string"
}
```

---

### Regiones - `/api/regiones`

| Metodo   | Endpoint               | Descripcion               | Body / Params    |
|----------|------------------------|---------------------------|------------------|
| `GET`    | `/api/regiones`        | Obtener todas las regiones| —                |
| `GET`    | `/api/regiones/{id}`   | Obtener una region por ID | —                |
| `POST`   | `/api/regiones`        | Crear una region          | `Region` JSON    |
| `PUT`    | `/api/regiones/{id}`   | Editar una region         | `Region` JSON    |
| `DELETE` | `/api/regiones/{id}`   | Eliminar una region       | —                |

**Body `Region`:**
```json
{
  "nombre": "string"
}
```

---

### Comunas - `/api/comunas`

| Metodo   | Endpoint               | Descripcion               | Body / Params    |
|----------|------------------------|---------------------------|------------------|
| `GET`    | `/api/comunas`         | Obtener todas las comunas | —                |
| `GET`    | `/api/comunas/{id}`    | Obtener una comuna por ID | —                |
| `POST`   | `/api/comunas`         | Crear una comuna          | `Comuna` JSON    |
| `PUT`    | `/api/comunas/{id}`    | Editar una comuna         | `Comuna` JSON    |
| `DELETE` | `/api/comunas/{id}`    | Eliminar una comuna       | —                |

**Body `Comuna`:**
```json
{
  "nombre": "string",
  "region": { "idRegion": 1 }
}
```

---

### Marcas - `/api/marcas`

| Metodo   | Endpoint               | Descripcion               | Body / Params    |
|----------|------------------------|---------------------------|------------------|
| `GET`    | `/api/marcas`          | Obtener todas las marcas  | —                |
| `GET`    | `/api/marcas/{id}`     | Obtener una marca por ID  | —                |
| `POST`   | `/api/marcas`          | Crear una marca           | `Marca` JSON     |
| `PUT`    | `/api/marcas/{id}`     | Editar una marca          | `Marca` JSON     |
| `DELETE` | `/api/marcas/{id}`     | Eliminar una marca        | —                |

**Body `Marca`:**
```json
{
  "nombre": "string"
}
```

---

### Productos - `/api/productos`

> Los endpoints de consulta y mutacion de productos devuelven un `ProductoDTOResponse`
> donde `marca` es el **nombre** de la marca (no el `idMarca`).

| Metodo   | Endpoint                                        | Descripcion                            | Body / Params                              |
|----------|-------------------------------------------------|----------------------------------------|--------------------------------------------|
| `GET`    | `/api/productos/{sku}`                          | Obtener un producto por SKU            | —                                          |
| `GET`    | `/api/productos/marca/{idMarca}`                | Obtener productos por marca            | —                                          |
| `GET`    | `/api/productos/precio?min=X&max=Y`             | Obtener productos por rango precio     | Query params: `min`, `max` (Long)          |
| `GET`    | `/api/productos/stock?min=X&max=Y`              | Obtener productos por rango stock      | Query params: `min`, `max` (Long)          |
| `GET`    | `/api/productos/nombre/{nombre}`                | Obtener productos por nombre           | —                                          |
| `POST`   | `/api/productos`                                | Crear un producto                      | `Producto` JSON                            |
| `PUT`    | `/api/productos/{sku}`                          | Editar un producto                     | `Producto` JSON                            |
| `PUT`    | `/api/productos/{sku}/stock/setear?stock=X`     | Setear stock a un valor especifico     | Query param: `stock` (Long, >= 0)          |
| `PUT`    | `/api/productos/{sku}/stock/disminuir?unidades=X`| Disminuir stock en N unidades         | Query param: `unidades` (Long, >= 0)       |
| `PUT`    | `/api/productos/{sku}/stock/aumentar?unidades=X`| Aumentar stock en N unidades          | Query param: `unidades` (Long, >= 0)       |
| `DELETE` | `/api/productos/{sku}`                          | Eliminar un producto                   | —                                          |

> **Regla de stock:** el stock solo admite numeros enteros positivos mas el cero (>= 0).
> Si `disminuir` dejara el stock en negativo, se retorna un error.

**Body `Producto` (entrada):**
```json
{
  "nombre": "string",
  "descripcion": "string",
  "precio": 10000,
  "stock": 50,
  "img": "string",
  "marca": { "idMarca": 1 }
}
```

**Respuesta `ProductoDTOResponse`:**
```json
{
  "sku": 1,
  "nombre": "string",
  "descripcion": "string",
  "marca": "Nombre de la marca",
  "precio": 10000,
  "stock": 50
}
```

---

## Estructura del Proyecto

```
src/main/java/com/esam/esam_backend/
├── controller/
│   ├── UsuarioController.java
│   ├── RolUsuarioController.java
│   ├── RegionController.java
│   ├── ComunaController.java
│   ├── MarcaController.java
│   └── ProductoController.java
├── dto/
│   ├── producto/
│   │   └── ProductoDTOResponse.java
│   └── usuario/
│       ├── UsuarioDTOResponse.java
│       ├── UsuarioDTOLogin.java
│       ├── UsuarioDTOLoginResponse.java
│       └── UsuarioDTORequest.java
├── mapper/
│   ├── ProductoMapper.java
│   ├── UsuarioMapper.java
│   └── UsuarioDTORequestMapper.java
├── model/
│   ├── Usuario.java
│   ├── RolUsuario.java
│   ├── Region.java
│   ├── Comuna.java
│   ├── Marca.java
│   ├── Producto.java
│   └── Carrito.java
├── repository/
│   ├── UsuarioRepository.java
│   ├── RolUsuarioRepository.java
│   ├── RegionRepository.java
│   ├── ComunaRepository.java
│   ├── MarcaRepository.java
│   └── ProductoRepository.java
├── service/
│   ├── UsuarioService.java
│   ├── RolUsuarioService.java
│   ├── RegionService.java
│   ├── ComunaService.java
│   ├── MarcaService.java
│   └── ProductoService.java
└── EsamBackendApplication.java
```

## Migraciones Flyway

| Archivo   | Descripcion                     |
|-----------|---------------------------------|
| `V1`      | Creacion de tablas iniciales    |
| `V2`      | Carga de regiones               |
| `V3`      | Carga de comunas                |
| `V4`      | Poblado de roles de usuario     |
| `V5`      | Poblado de marcas               |
| `V6`      | Poblado de productos            |
