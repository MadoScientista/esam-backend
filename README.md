# ESAM - Tienda Escolar en Línea (Backend)

Backend de una tienda en línea de artículos escolares y de oficina. Se encarga de
gestionar **usuarios**, **roles**, **regiones y comunas**, **marcas**, **productos**,
su **stock** y las **imágenes** de cada producto.

## ¿Qué hace la aplicación?

- Permite registrarse e iniciar sesión como **administrador**, **vendedor** o **cliente**.
  Las contraseñas se guardan cifradas con BCrypt.
- El login devuelve un **JWT** (HS256) que el cliente debe enviar en el header
  `Authorization: Bearer <token>` en cada petición posterior.
- El acceso depende del rol: hay un catálogo público de lectura, escritura de
  catálogo para vendedores, y usuarios y datos maestros solo para administradores.
  Ver [Autorización](#autorización).
- Administra el catálogo de productos: crear, editar, eliminar, buscar por nombre,
  marca, precio o stock, y controlar el stock (aumentar, disminuir o fijar un valor).
- Gestiona las imágenes de cada producto, almacenadas en **Cloudinary**: subir varias
  por producto, elegir una como principal y eliminarlas. Solo un producto puede tener
  una imagen principal a la vez, y la base de datos lo garantiza con un índice único.
- Entrega el listado de regiones y comunas de Chile, útil para completar el
  domicilio de los usuarios.
- Al iniciar por primera vez, crea la base de datos, las tablas y los **datos de
  ejemplo** (16 regiones, 346 comunas, 3 roles y 4 marcas) de forma automática.
  No se crean usuarios ni productos de ejemplo en la configuración normal.
  El perfil opcional `dev` carga tres productos de demostración para probar el
  catálogo.

> El registro es público, pero toda cuenta creada por esa vía nace con el rol
> `cliente`. Crear cuentas de `admin` o `vendedor` requiere un token de
> administrador (`POST /api/usuarios/admin`).

## Tecnologías

- **Java 26** con **Spring Boot 4.1.1**
- **Spring Data JPA** (Hibernate) y **MySQL** como base de datos
- **Flyway** para crear y actualizar la base de datos automáticamente
- **Cloudinary** para el almacenamiento de las imágenes de los productos
- **BCrypt** (`spring-security-crypto`) para el cifrado de contraseñas
- **Spring Security** con autenticación **stateless** por JWT
  (`jjwt` 0.13.0)
- **Lombok**, **Spring Boot Actuator** y **Bean Validation**

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

Este script crea la base `esam_db` y el usuario `admin_esam_db`, que la
aplicación usa para conectarse. La clave la eliges tú: no hay ninguna clave por
defecto en el repositorio y `DB_PASSWORD` debe coincidir con la que asignes.

### 3. Configurar las variables de entorno

La aplicación lee sus credenciales desde variables de entorno. Crea un archivo
`.env` en la raíz del proyecto (`.env.example` sirve como plantilla) con:

| Variable         | Descripción                                                              |
|------------------|--------------------------------------------------------------------------|
| `DB_URL`         | URL de conexión a MySQL. Tiene un valor por defecto en `application.yaml` |
| `DB_USERNAME`    | Usuario de MySQL                                                         |
| `DB_PASSWORD`    | Contraseña de MySQL                                                      |
| `CLOUDINARY_URL` | Credenciales de Cloudinary, con el formato `cloudinary://<key>:<secret>@<cloud_name>` |
| `JWT_SECRET`     | Clave para firmar los JWT, en Base64. Debe decodificar a 32 bytes o más, porque HS256 la exige. Genera una con `openssl rand -base64 32` |
| `JWT_EXPIRATION` | Duración del token en milisegundos                                      |

El archivo `.env` está en el `.gitignore` y la aplicación lo carga sola al
arrancar, sin necesidad de exportarlo en la terminal.

> `CLOUDINARY_URL` es obligatoria: sin ella la aplicación no levanta. Se puede
> obtener desde el panel de Cloudinary, en *Settings → API Keys*.

### 4. Iniciar la aplicación

```bash
./mvnw clean compile
./mvnw spring-boot:run
```

La aplicación queda disponible en **http://localhost:8080**. La base de datos se
crea y se llena sola en el primer inicio (no hace falta hacer nada más).

Para cargar los productos de demostración en un entorno local, inicia la
aplicación con el perfil `dev`:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

Este perfil agrega los productos de muestra mediante una migración repetible.
Úsalo solo con una base de datos de desarrollo; no se activa por defecto.

### 5. Crear el primer usuario

No hay usuarios de ejemplo. El registro público (`POST /api/usuarios`) crea
siempre un `cliente`, así que **no sirve para obtener un administrador**.

Para partir con el primer `admin`, insértalo directamente en la base de datos con
un `INSERT` sobre la tabla `usuario`. La contraseña debe guardarse como un hash
BCrypt de 60 caracteres, no en texto plano: un valor en claro no permitirá iniciar
sesión, porque el login compara contra el hash.

A partir de ese usuario ya se puede usar `POST /api/usuarios/admin` para crear el
resto de las cuentas. Las regiones, las comunas y los tres roles ya vienen cargados.

## Autorización

La API es stateless: cada petición debe enviar el token en el header
`Authorization: Bearer <token>`, salvo las rutas públicas. Los permisos se
resuelven por rol (`admin`, `vendedor`, `cliente`).

| Operación                                     | Público | admin | vendedor | cliente |
|-----------------------------------------------|:-------:|:-----:|:--------:|:-------:|
| `GET` productos, marcas, regiones, comunas, roles | sí   | sí    | sí       | sí      |
| `POST`/`PUT`/`DELETE` productos, stock, imágenes | no    | sí    | sí       | no      |
| `POST`/`PUT`/`DELETE` marcas                  | no       | sí    | no       | no      |
| `POST`/`PUT`/`DELETE` regiones, comunas, roles | no     | sí    | no       | no      |
| `POST /api/usuarios` (registro)               | sí       | sí    | no       | sí      |
| `POST /api/usuarios/login`                    | sí       | sí    | sí       | sí      |
| `POST /api/usuarios/admin`                    | no       | sí    | no       | no      |
| `GET /api/usuarios`, `GET /api/usuarios/{id}`, `GET /api/usuarios/rol/{id}` | no | sí | no | no |
| `DELETE /api/usuarios/{id}`                   | no       | sí    | no       | no      |
| `GET`/`PUT` `/api/usuarios/perfil`            | no       | sí    | sí       | sí      |

Los permisos se aplican por método y ruta, no por path completo: el registro y el
login son las únicas operaciones públicas de escritura.

Un token expirado, con la firma alterada o de un usuario que ya no existe se
rechaza con `401` `NO_AUTORIZADO`, no con `500`.

## Uso de la API

### Usuarios - `/api/usuarios`

| Método | Endpoint                       | Acceso | Descripción                            |
|--------|--------------------------------|--------|----------------------------------------|
| GET    | `/api/usuarios`                | admin  | Listar todos los usuarios              |
| GET    | `/api/usuarios/{id}`           | admin  | Obtener un usuario por su ID           |
| GET    | `/api/usuarios/rol/{idRolUsuario}` | admin | Listar usuarios por rol             |
| GET    | `/api/usuarios/perfil`         | cualquiera autenticado | Obtener el perfil propio        |
| POST   | `/api/usuarios/login`          | público | Iniciar sesión (correo y password)   |
| POST   | `/api/usuarios`                | público | Crear un usuario, siempre `cliente`    |
| POST   | `/api/usuarios/admin`          | admin  | Crear un usuario eligiendo su rol     |
| PUT    | `/api/usuarios/{id}`           | admin  | Editar un usuario, incluido su rol     |
| PUT    | `/api/usuarios/perfil`         | cualquiera autenticado | Editar el perfil propio, sin cambiar el rol |
| DELETE | `/api/usuarios/{id}`           | admin  | Eliminar un usuario                    |

**Datos para crear un usuario** (`password` es obligatoria). El registro público
no acepta `idRolUsuario`: el rol queda fijo en `cliente`. Los endpoints de
administración usan el mismo cuerpo más ese campo:

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
  "correo": "string",
  "password": "string",
  "idRegion": 7,
  "idComuna": 104
}
```

Al editar, `password` es opcional: si viene vacía o nula, se conserva la anterior.
`PUT /api/usuarios/perfil` nunca cambia el rol, aunque el cuerpo lo incluya.

**Respuesta del login:**

```json
{
  "loggin": true,
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "usuario": {
    "id": 1,
    "nombres": "Ana",
    "region": { "...": "..." },
    "comuna": { "...": "..." },
    "rol": { "...": "..." }
  }
}
```

> El campo `loggin` está mal escrito en la API (le falta la `i`), pero así lo
> devuelve el backend. Si las credenciales no coinciden, `loggin` vale `false` y
> `usuario` viene en `null`. El `password` nunca se devuelve en ninguna respuesta.

### Roles - `/api/roles`

Los roles `admin`, `vendedor` y `cliente` son del sistema: no se pueden renombrar
ni eliminar, porque el registro público y las reglas de autorización los buscan
por nombre. Otros roles que se creen quedan libres.

| Método | Endpoint          | Acceso | Descripción              |
|--------|-------------------|--------------------------|
| GET    | `/api/roles`      | Listar roles             |
| GET    | `/api/roles/{id}` | Obtener un rol por ID    |
| POST   | `/api/roles`      | Crear un rol             |
| PUT    | `/api/roles/{id}` | Editar un rol            |
| DELETE | `/api/roles/{id}` | Eliminar un rol          |

Un rol con usuarios asociados no se puede eliminar: devuelve `409`
`ROL_USUARIO_CON_USUARIOS`.

### Regiones - `/api/regiones`

| Método | Endpoint                          | Descripción                        |
|--------|-----------------------------------|------------------------------------|
| GET    | `/api/regiones`                   | Listar regiones                    |
| GET    | `/api/regiones/{id}`              | Obtener una región por ID          |
| GET    | `/api/regiones/comunas`           | Listar regiones con sus comunas     |
| GET    | `/api/regiones/{id}/comunas`      | Obtener una región con sus comunas |
| POST   | `/api/regiones`                   | Crear una región                   |
| PUT    | `/api/regiones/{id}`              | Editar una región                  |
| DELETE | `/api/regiones/{id}`              | Eliminar una región                |

Una región con comunas o usuarios asociados no se puede eliminar: devuelve `409`
`REGION_CON_DEPENDENCIAS`.

### Comunas - `/api/comunas`

| Método | Endpoint             | Descripción                 |
|--------|----------------------|-----------------------------|
| GET    | `/api/comunas`       | Listar comunas              |
| GET    | `/api/comunas/{id}`  | Obtener una comuna por ID    |
| POST   | `/api/comunas`       | Crear una comuna             |
| PUT    | `/api/comunas/{id}`  | Editar una comuna            |
| DELETE | `/api/comunas/{id}`  | Eliminar una comuna          |

Una comuna con usuarios asociados no se puede eliminar: devuelve `409`
`COMUNA_CON_USUARIOS`.

### Marcas - `/api/marcas`

| Método | Endpoint                    | Descripción                              |
|--------|-----------------------------|------------------------------------------|
| GET    | `/api/marcas`               | Listar marcas                            |
| GET    | `/api/marcas/{id}`          | Obtener una marca por ID                 |
| POST   | `/api/marcas`               | Crear una marca                          |
| PUT    | `/api/marcas/{id}`          | Editar una marca                         |
| DELETE | `/api/marcas/{id}`          | Eliminar una marca                       |
| DELETE | `/api/marcas/{id}/cascada`  | Eliminar la marca y todos sus productos  |

Una marca con productos asociados no se puede eliminar: devuelve `409`
`MARCA_CON_PRODUCTOS`. La variante `/cascada` sí los elimina.

### Productos - `/api/productos`

| Método | Endpoint                                       | Descripción                              |
|--------|------------------------------------------------|------------------------------------------|
| GET    | `/api/productos`                               | Listar todos los productos               |
| GET    | `/api/productos/{sku}`                         | Obtener un producto por su SKU           |
| GET    | `/api/productos/marca/{idONombreMarca}`        | Listar productos de una marca por ID o nombre |
| GET    | `/api/productos/precio?min=X&max=Y`            | Listar productos por rango de precio     |
| GET    | `/api/productos/stock?min=X&max=Y`             | Listar productos por rango de stock      |
| GET    | `/api/productos/nombre?nombre=X`               | Listar productos cuyo nombre contiene X  |
| POST   | `/api/productos`                               | Crear un producto                        |
| POST   | `/api/productos/{sku}`                         | Editar un producto                       |
| PUT    | `/api/productos/{sku}/stock/setear?stock=X`    | Fijar el stock en un valor específico    |
| PUT    | `/api/productos/{sku}/stock/disminuir?unidades=X` | Disminuir el stock                   |
| PUT    | `/api/productos/{sku}/stock/aumentar?unidades=X` | Aumentar el stock                     |
| DELETE | `/api/productos/{sku}`                         | Eliminar un producto                     |
| DELETE | `/api/productos/{sku}/cascada`                 | Eliminar el producto y todas sus imágenes |

**Datos para crear o editar un producto:**

```json
{
  "nombre": "string",
  "descripcion": "string",
  "idMarca": 1,
  "precio": 3990,
  "stock": 25
}
```

**Respuesta de un producto:**

```json
{
  "sku": 1,
  "nombre": "string",
  "descripcion": "string",
  "marca": "Staedtler",
  "precio": 3990,
  "stock": 25,
  "imagenes": [
    { "idImagenProducto": 1, "url": "https://.../imagen.jpg", "principal": true }
  ]
}
```

> El stock solo acepta números enteros positivos o cero (>= 0). Si al disminuir
> el stock quedaría en negativo, la aplicación devuelve un error.
>
> Un producto con imágenes no se puede eliminar: devuelve `409`
> `PRODUCTO_CON_IMAGENES`. La variante `/cascada` también borra las imágenes
> correspondientes en Cloudinary.

### Imágenes de producto - `/api/productos/{sku}/imagenes`

| Método | Endpoint                                                     | Descripción                          |
|--------|--------------------------------------------------------------|--------------------------------------|
| POST   | `/api/productos/{sku}/imagenes`                              | Subir una imagen a un producto       |
| GET    | `/api/productos/{sku}/imagenes`                              | Listar imágenes, la principal primero |
| PUT    | `/api/productos/{sku}/imagenes/{idImagenProducto}/principal` | Marcar una imagen como principal     |
| DELETE | `/api/productos/{sku}/imagenes/{idImagenProducto}`           | Eliminar una imagen                  |

`POST` es `multipart/form-data` y acepta dos campos:

| Campo        | Tipo    | Obligatorio | Descripción                                  |
|--------------|---------|-------------|----------------------------------------------|
| `file`       | archivo | sí          | La imagen a subir                            |
| `principal`  | boolean | no          | Si viene en `true`, queda como imagen principal |

```bash
curl -X POST http://localhost:8080/api/productos/1/imagenes \
  -F "file=@cuaderno.jpg" \
  -F "principal=true"
```

Reglas de la subida:

- Formatos aceptados: `jpg`, `jpeg`, `png` y `webp`. Cualquier otro devuelve
  `400` `IMAGEN_INVALIDA`.
- Tamaño máximo: **5 MB**. Si se excede, devuelve `400` `ARCHIVO_MUY_GRANDE`.
- Cada producto puede tener varias imágenes, pero **solo una principal**. Al marcar
  una como principal, la anterior se desmarca automáticamente.
- Las imágenes se guardan en Cloudinary dentro de la carpeta `productos/{sku}`.
- El campo `idImagenProducto` de la respuesta es el que se usa para las rutas de
  principal y de borrado. El identificador interno de Cloudinary no se expone.

### Errores

Las respuestas de error usan siempre el mismo formato:

```json
{
  "timestamp": "2026-01-01T12:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "code": "IMAGEN_INVALIDA",
  "message": "Formato no permitido, se aceptan jpg, png y webp",
  "path": "/api/productos/1/imagenes"
}
```

`code` identifica el tipo de error de forma estable. Los más frecuentes:

| Código                                 | Estado | Cuándo ocurre                                |
|----------------------------------------|--------|----------------------------------------------|
| `ERROR_VALIDACION`                    | 400    | Faltan campos o no cumplen las validaciones  |
| `PARAMETRO_INVALIDO`                   | 400    | Un parámetro de la URL o del query no es válido |
| `PRODUCTO_INVALIDO`                    | 400    | El stock, el precio o las unidades no sirven  |
| `IMAGEN_INVALIDA`                      | 400    | Formato o tamaño de imagen no permitido       |
| `ARCHIVO_MUY_GRANDE`                   | 400    | El archivo supera los 5 MB                   |
| `CONFLICTO_STOCK`                      | 409    | No hay stock suficiente para la operación    |
| `PRODUCTO_CON_IMAGENES`                | 409    | El producto tiene imágenes y se intenta borrar |
| `MARCA_CON_PRODUCTOS`                  | 409    | La marca tiene productos                     |
| `COMUNA_CON_USUARIOS`                  | 409    | La comuna tiene usuarios                      |
| `REGION_CON_DEPENDENCIAS`               | 409    | La región tiene comunas o usuarios           |
| `ROL_USUARIO_CON_USUARIOS`             | 409    | El rol tiene usuarios                        |
| `ROL_USUARIO_DUPLICADO`               | 409    | Ya existe un rol con ese nombre               |
| `ROL_USUARIO_SISTEMA`                 | 409    | Se intenta renombrar o borrar un rol del sistema |
| `NO_AUTORIZADO`                       | 401    | Token ausente, vencido, con firma inválida o de un usuario que ya no existe |
| `ACCESO_DENEGADO`                      | 403    | El rol del usuario no alcanza para la operación |
| `CONFLICTO_DATOS`                      | 409    | La operación viola una restricción de integridad (correo o RUT duplicado) |
| `*_NO_ENCONTRADO` / `*_NO_ENCONTRADA`  | 404    | El recurso solicitado no existe              |
| `ERROR_INTERNO`                        | 500    | Error inesperado                             |

## Estructura del proyecto

```
src/main/java/com/esam/esam_backend/
├── config/           Configuración de la aplicación
├── controller/       Rutas de la API (7 controladores)
├── dto/              Objetos de entrada y salida de la API
├── exception/        Excepciones tipadas y manejador global
├── mapper/           Conversión entre entidades y DTOs
├── model/            Entidades de la base de datos
├── repository/       Acceso a los datos
├── security/         Autenticación por JWT, filtro y reglas de acceso
└── service/          Lógica de negocio

src/main/resources/
├── application.yaml  Configuración (puerto, base de datos, Flyway, Cloudinary, JWT)
├── db/creacion_admin.sql  Script para crear la BD y el usuario
└── db/migration/     Migraciones de Flyway (V1 a V7)

src/test/java/        Pruebas automatizadas
```
