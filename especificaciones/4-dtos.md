# Ejemplos de solicitudes y respuestas para el frontend

Esta guía muestra cómo consumir la API implementada actualmente. Las rutas y
permisos se detallan en [3-endpoints.md](./3-endpoints.md). Cuando este documento
o `2-modeladoDtos.md` difiera de los controladores y DTOs actuales, los ejemplos
de aquí describen el contrato que acepta o devuelve la aplicación hoy.

## Convenciones de las solicitudes

- URL local: `http://localhost:8080`.
- Los ejemplos JSON usan `Content-Type: application/json`.
- Las respuestas JSON incluyen `charset=UTF-8` en `Content-Type`.
- Sustituir los valores entre `<...>` por los valores reales. No enviar las
  marcas `<` y `>` literalmente.
- Los endpoints protegidos requieren:

  ```http
  Authorization: Bearer <JWT>
  ```

- El JWT se obtiene en `POST /api/usuarios/login`. No se debe enviar la
  contraseña salvo en login, registro o cuando el formulario modifica la
  contraseña.
- En los ejemplos de respuesta se omiten propiedades nulas para facilitar la
  lectura. Los campos presentes reflejan los DTOs actuales; la aplicación puede
  incluir propiedades adicionales con valor `null`.
- Los precios y totales están expresados en pesos chilenos como números enteros.

## Flujo público: catálogo, geografía, registro y login

### Leer catálogo y datos auxiliares

Estas consultas no requieren token. Entre las disponibles están:

```http
GET /api/productos
GET /api/productos/24
GET /api/productos/marca/Staedtler
GET /api/productos/precio?min=1000&max=8000
GET /api/productos/nombre?nombre=cuaderno
GET /api/productos/24/imagenes
GET /api/marcas
GET /api/regiones/comunas
GET /api/comunas
GET /api/roles
```

Ejemplo de respuesta de `GET /api/productos/24`:

```json
{
  "idProducto": 24,
  "sku": "CUAD-001",
  "nombre": "Cuaderno universitario",
  "descripcion": "Cuaderno de 100 hojas",
  "precio": 2990,
  "stock": 35,
  "marca": "Staedtler",
  "marcaDetalle": {
    "idMarca": 2,
    "nombre": "Staedtler"
  },
  "idMarca": 2,
  "imagenes": [
    {
      "idImagenProducto": 81,
      "url": "https://res.cloudinary.com/<cuenta>/image/upload/<imagen>.jpg",
      "orden": 0,
      "principal": true
    }
  ]
}
```

Las imágenes son públicas; para conocer una URL se usa el listado
`GET /api/productos/{idProducto}/imagenes`. El formato para subir imágenes está en la
sección de vendedor.

La respuesta de `GET /api/regiones` es una lista de objetos con
`idRegion` y `nombre`. `GET /api/comunas` devuelve `idComuna`, `nombre` e
`idRegion`; este último contiene el ID de la región asociada. Las respuestas de
`GET /api/regiones/comunas` usan también `nombre` para el nombre de la región.
`GET /api/roles` devuelve objetos como:

```json
[
  { "idRolUsuario": 1, "nombre": "admin" },
  { "idRolUsuario": 2, "nombre": "vendedor" },
  { "idRolUsuario": 3, "nombre": "cliente" }
]
```

Consultar roles es público, pero no permite escoger el rol al registrarse.

### Registrar cliente

`POST /api/usuarios` es público y el servidor asigna el rol `cliente`.
El cuerpo actual de `UsuarioDTORequest` requiere los dos grupos de datos
identificatorios que aparecen aquí. `nombre`, `apellido` y `email` son
obligatorios por validación, aunque el servicio persiste `nombres`,
`aPaterno`, `aMaterno`, `rut`, `dv` y `correo`.

```http
POST /api/usuarios
Content-Type: application/json
```

```json
{
  "nombre": "<nombre>",
  "apellido": "<apellido>",
  "email": "<correo válido>",
  "password": "<contraseña de 8 a 72 caracteres>",
  "telefono": "+56912345678",
  "nombres": "<nombres>",
  "aPaterno": "<apellido paterno>",
  "aMaterno": "<apellido materno>",
  "rut": 12345678,
  "dv": "K",
  "fechaNacimiento": "2000-01-01",
  "direccion": "<dirección>",
  "correo": "<correo válido>",
  "idRegion": 7,
  "idComuna": 104
}
```

`idDireccion` también es aceptado por el DTO, pero es opcional. No enviar
`idRolUsuario`: el registro público no permite elegir rol.

Respuesta `201 Created`, ejemplo abreviado:

```json
{
  "id": 15,
  "nombres": "<nombres>",
  "aPaterno": "<apellido paterno>",
  "aMaterno": "<apellido materno>",
  "rut": 12345678,
  "dv": "K",
  "correo": "<correo válido>",
  "fechaNacimiento": "2000-01-01",
  "rol": "cliente",
  "rolDetalle": {
    "idRolUsuario": 3,
    "nombre": "cliente"
  },
  "comuna": {
    "idComuna": 104,
    "nombre": "<comuna>"
  }
}
```

### Iniciar sesión

```http
POST /api/usuarios/login
Content-Type: application/json
```

```json
{
  "correo": "<correo usado al registrarse>",
  "password": "<contraseña>"
}
```

Respuesta `200 OK`:

```json
{
  "loggin": true,
  "token": "<JWT>",
  "usuario": {
    "id": 15,
    "nombres": "<nombres>",
    "aPaterno": "<apellido paterno>",
    "correo": "<correo>",
    "rol": "cliente",
    "rolDetalle": {
      "idRolUsuario": 3,
      "nombre": "cliente"
    }
  }
}
```

El nombre `loggin` está así en el DTO actual. El controlador actual no asigna
`tipoToken` ni `expiraEn`; si el serializador los incluye, estarán en `null`.
Con credenciales incorrectas, la API responde `401 Unauthorized` y `loggin` es
`false`. Almacenar el token de forma segura y enviarlo como `Bearer` en las
siguientes solicitudes protegidas.

## Cliente: perfil, direcciones, carrito y pedidos

Todas las solicitudes de esta sección requieren un token de cliente. Los
endpoints toman la identidad del token; no usar IDs para acceder a datos de
otra persona.

### Perfil

```http
GET /api/usuarios/perfil
Authorization: Bearer <JWT>
```

Actualizar perfil usa `PUT /api/usuarios/perfil` y un cuerpo `UsuarioDTORequest`. En el DTO actual,
`password` también lleva validación de obligatoriedad y el servicio codifica y
guarda cualquier valor no vacío recibido. Para cambiar el perfil por esta ruta
hay que enviar una contraseña nueva:

```json
{
  "nombre": "<nombre>",
  "apellido": "<apellido>",
  "email": "<correo>",
  "password": "<nueva contraseña de 8 a 72 caracteres>",
  "nombres": "<nombres>",
  "aPaterno": "<apellido paterno>",
  "aMaterno": "<apellido materno>",
  "rut": 12345678,
  "dv": "K",
  "fechaNacimiento": "2000-01-01",
  "correo": "<correo>",
  "idRegion": 7,
  "idComuna": 104
}
```

El servicio no cambia el rol al actualizar el perfil; sí reemplaza la contraseña
por la enviada en el cuerpo.

### Direcciones propias

Consultar las direcciones del usuario autenticado:

```http
GET /api/direcciones
Authorization: Bearer <JWT>
```

También existen `GET /api/direcciones/usuario/{idUsuario}`,
`GET /api/direcciones/usuario/{idUsuario}/activas` y
`GET /api/direcciones/{id}`. El ID de usuario debe corresponder al principal
autenticado; si no, se deniega el acceso.

Crear una dirección con `POST /api/direcciones`:

```json
{
  "nombreReceptor": "<nombre de quien recibe>",
  "telefonoReceptor": "+56912345678",
  "calle": "<calle>",
  "numero": "123",
  "complemento": "<opcional>",
  "predeterminada": true,
  "idUsuario": 15,
  "idComuna": 104
}
```

El DTO exige `idUsuario`, pero el controlador asocia la dirección al usuario
del token. El servicio no confía en el ID recibido para establecer el dueño;
usar el ID propio en la solicitud. La respuesta devuelve un objeto
`DireccionDTO`, por ejemplo:

```json
{
  "idDireccion": 42,
  "nombreReceptor": "<nombre de quien recibe>",
  "telefonoReceptor": "+56912345678",
  "calle": "<calle>",
  "numero": "123",
  "complemento": "<opcional>",
  "predeterminada": true,
  "activo": true,
  "idUsuario": 15,
  "idComuna": 104
}
```

Editar usa `PUT /api/direcciones/{id}` con el mismo cuerpo. Eliminar usa
`DELETE /api/direcciones/{id}`. Ambos operan solo sobre una dirección propia.

### Carrito propio

Obtener el carrito:

```http
GET /api/carrito
Authorization: Bearer <JWT>
```

Agregar un producto con `POST /api/carrito/items`:

```json
{
  "idProducto": 24,
  "cantidad": 2
}
```

Cambiar cantidad con `PUT /api/carrito/items/{idProducto}`:

```json
{
  "cantidad": 3
}
```

Quitar un producto: `DELETE /api/carrito/items/{idProducto}`. Vaciar el carrito:
`DELETE /api/carrito`.

Las operaciones que devuelven el carrito responden, por ejemplo:

```json
{
  "idCarrito": 9,
  "items": [
    {
      "idItemCarrito": 31,
      "producto": {
        "idProducto": 24,
        "nombre": "Cuaderno universitario",
        "precio": 2990,
        "stock": 35
      },
      "cantidad": 2,
      "precioUnitario": 2990,
      "subtotal": 5980
    }
  ],
  "cantidadTotal": 2,
  "total": 5980
}
```

Los precios y subtotales de carrito son calculados por el servidor. No enviar
un precio desde el frontend.

### Crear pedido e historial propio

El pedido se crea a partir del carrito; el cliente solo envía una dirección:

```http
POST /api/pedidos
Authorization: Bearer <JWT>
Content-Type: application/json
```

```json
{
  "idDireccion": 42
}
```

Respuesta `201 Created`, con forma `PedidoDTOResponse`:

```json
{
  "idPedido": 101,
  "numeroPedido": "<número generado>",
  "estado": "PENDIENTE",
  "total": 5980,
  "creadoEn": "<fecha y hora ISO-8601>",
  "envio": {
    "nombreReceptor": "<nombre de quien recibe>",
    "telefonoReceptor": "+56912345678",
    "calle": "<calle>",
    "numero": "123",
    "complemento": "<opcional>",
    "comunaNombre": "<comuna>",
    "regionNombre": "<región>"
  },
  "detalles": [
    {
      "idDetallePedido": 201,
      "idProducto": 24,
      "nombreProducto": "Cuaderno universitario",
      "skuProducto": "CUAD-001",
      "precioUnitario": 2990,
      "cantidad": 2,
      "subtotal": 5980
    }
  ],
  "historialEstados": [
    {
      "estadoAnterior": null,
      "estadoNuevo": "PENDIENTE",
      "cambiadoEn": "<fecha y hora ISO-8601>"
    }
  ]
}
```

El historial del cliente se consulta con `GET /api/pedidos` y devuelve una lista
de resúmenes (`idPedido`, `numeroPedido`, `estado`, `total`, `cantidadItems`,
`creadoEn`). El detalle propio se obtiene con `GET /api/pedidos/{idPedido}`.

## Vendedor: catálogo e imágenes

El vendedor puede leer el catálogo público y, con JWT, crear, editar, eliminar
productos y mantener sus imágenes. No puede modificar categorías, marcas,
geografía, roles, usuarios ni pedidos de administración.

### Crear o editar producto

El DTO actual requiere `sku`, `nombre`, `precio`, `stock`, `idMarca` e
`idCategorias`. La creación usa `POST /api/productos`; la edición usa
`POST /api/productos/{idProducto}` (no `PUT`). Aunque algunos nombres de
parámetros del controlador dicen `sku`, las rutas de producto reciben un número
y el servicio busca el registro por su ID primario. Para las rutas de stock e
imágenes se debe usar también ese ID numérico.

```http
POST /api/productos
Authorization: Bearer <JWT de vendedor>
Content-Type: application/json
```

```json
{
  "sku": "CUAD-002",
  "nombre": "Cuaderno cuadriculado",
  "descripcion": "Cuaderno de 100 hojas",
  "precio": 2490,
  "stock": 20,
  "idMarca": 2,
  "idCategorias": [4]
}
```

Respuesta `201 Created`: producto con campos como `idProducto`, `sku`, `nombre`,
`descripcion`, `precio`, `stock`, `marca`, `marcaDetalle`, `idMarca` e
`imagenes`.

Cambiar el stock:

```http
PUT /api/productos/24/stock/setear?stock=18
Authorization: Bearer <JWT de vendedor>
```

También existen las rutas `/stock/aumentar?unidades=2` y
`/stock/disminuir?unidades=2`.

### Subir imagen

`POST /api/productos/{idProducto}/imagenes` recibe `multipart/form-data` con el
campo `file`:

```bash
curl -X POST http://localhost:8080/api/productos/24/imagenes \
  -H "Authorization: Bearer <JWT de vendedor>" \
  -F "file=@<ruta-local-de-la-imagen>"
```

La respuesta incluye `idImagenProducto`, `url`, `textoAlternativo`, `orden` y
`principal`. En el controlador actual no hay parámetros para `principal`,
`orden` o texto alternativo al subir; esos valores no deben asumirse
configurables desde esta operación.

Eliminar una imagen usa
`DELETE /api/productos/{idProducto}/imagenes/{idImagenProducto}` y eliminar
todas las imágenes de un producto usa
`DELETE /api/productos/{idProducto}/imagenes`.

Solo admin puede elegir la imagen principal de un producto mediante
`PUT /api/productos/{sku}/imagenes/{idImagenProducto}/principal`. No requiere
cuerpo: el ID de la imagen seleccionada va en la ruta. El servidor marca esa
imagen como `principal: true` y desmarca las demás imágenes del producto en
una sola operación. Devuelve `200 OK` con el `ImagenProductoResponse` actualizado:

```http
PUT /api/productos/24/imagenes/11/principal
Authorization: Bearer <JWT de admin>
```

```json
{
  "idImagenProducto": 11,
  "url": "https://<url-de-cloudinary>",
  "textoAlternativo": null,
  "orden": 1,
  "principal": true
}
```

Si el producto no existe responde `404 PRODUCTO_NO_ENCONTRADO`; si la imagen no
existe o no pertenece a ese producto, responde `404 IMAGEN_NO_ENCONTRADA`. El
listado y detalle de productos reflejan después la selección en `principal` y
`imagenPrincipal`.

## Admin: usuarios, datos maestros y pedidos

Todas las rutas de esta sección requieren JWT con rol `admin`. El listado de
roles puede consultarse públicamente, pero solo admin puede mutarlos o asignar
un rol al crear una cuenta.

### Crear usuario con rol

Usar `POST /api/usuarios/admin` con los mismos campos de registro y agregar
`idRolUsuario`. El ID se obtiene de `GET /api/roles`.

```json
{
  "nombre": "<nombre>",
  "apellido": "<apellido>",
  "email": "<correo válido>",
  "password": "<contraseña de 8 a 72 caracteres>",
  "nombres": "<nombres>",
  "aPaterno": "<apellido paterno>",
  "aMaterno": "<apellido materno>",
  "rut": 12345678,
  "dv": "K",
  "fechaNacimiento": "2000-01-01",
  "correo": "<correo válido>",
  "idRegion": 7,
  "idComuna": 104,
  "idRolUsuario": 2
}
```

En el ejemplo, `idRolUsuario` debe ser el ID que corresponde a `vendedor` en la
respuesta actual de `GET /api/roles`; no asumir que los IDs serán iguales en
todos los ambientes. Admin también puede listar usuarios con `GET /api/usuarios`,
consultar `GET /api/usuarios/{id}`, filtrar por
`GET /api/usuarios/rol/{idRolUsuario}`, editar con `PUT /api/usuarios/{id}` y
eliminar con `DELETE /api/usuarios/{id}`.

### Mantener datos maestros y categorías

Ejemplo para crear una marca:

```http
POST /api/marcas
Authorization: Bearer <JWT de admin>
Content-Type: application/json
```

```json
{
  "nombre": "<nombre de marca>"
}
```

Respuesta `201 Created`, por ejemplo:

```json
{
  "idMarca": 5,
  "nombre": "<nombre de marca>"
}
```

Otros cuerpos:

```json
// CategoriaDTORequest: POST /api/categorias y PUT /api/categorias/{id}
{
  "nombre": "<categoría>",
  "idCategoriaPadre": null
}
```

`CategoriaDTORequest.nombre` es obligatorio, no puede estar en blanco y admite
hasta 100 caracteres. `idCategoriaPadre` es opcional: se envía `null` para una
categoría raíz o el ID de una categoría existente para crear una subcategoría.
La imagen no forma parte de este JSON; se carga mediante el endpoint multipart
descrito abajo.

Las respuestas de categoría usan `CategoriaDTO`. Ejemplo de `201 Created` al
crear una categoría y de los objetos devueltos por los endpoints GET:

```json
{
  "idCategoria": 12,
  "nombre": "<categoría>",
  "slug": "<categoria>",
  "idCategoriaPadre": null,
  "imagenUrl": null
}
```

`idCategoria`, `slug` e `imagenUrl` son generados o gestionados por el backend;
el cliente envía solo los campos de `CategoriaDTORequest`. `imagenUrl` será
`null` si aún no se ha cargado una imagen.

```json
// POST /api/regiones
{
  "nombre": "<región>"
}
```

```json
// POST /api/comunas
{
  "nombre": "<comuna>",
  "idRegion": 7
}
```

```json
// POST /api/roles
{
  "nombre": "<rol>"
}
```

`PUT` usa la misma estructura con el ID en la ruta. `DELETE` se envía sin cuerpo.
Las consultas de categorías (`GET /api/categorias`, `/raiz`,
`/padre/{idPadre}` y `/{id}`) son públicas y no requieren token. Sus escrituras
son exclusivas de admin.

Para subir o reemplazar la imagen de portada, admin envía `multipart/form-data`
con el campo `file` a `POST /api/categorias/{id}/imagen`. Se aceptan JPEG, PNG y
WebP hasta 5 MB. La respuesta de categoría incluye `imagenUrl`; se elimina la
imagen con `DELETE /api/categorias/{id}/imagen`.

### Consultar y actualizar pedidos

Admin puede listar todos los pedidos con `GET /api/pedidos/admin`, obtener uno
con `GET /api/pedidos/admin/{idPedido}` y cambiar su estado:

```http
PUT /api/pedidos/admin/101/estado
Authorization: Bearer <JWT de admin>
Content-Type: application/json
```

```json
{
  "estado": "CONFIRMADO"
}
```

Los estados válidos son `PENDIENTE`, `CONFIRMADO`, `ENVIADO`, `ENTREGADO` y
`CANCELADO`. El servicio también valida que la transición solicitada sea
permitida.

## Errores y permisos

La respuesta de error usa `ApiErrorResponse`. Ejemplo:

```json
{
  "timestamp": "<fecha y hora ISO-8601>",
  "status": 403,
  "error": "Forbidden",
  "code": "ACCESO_DENEGADO",
  "message": "No tiene permiso para realizar esta operación",
  "path": "/api/categorias",
  "mensaje": "No tiene permiso para realizar esta operación",
  "ruta": "/api/categorias",
  "errores": []
}
```

- `401 Unauthorized`: falta el JWT o no es válido/ha expirado.
- `403 Forbidden`: el usuario está autenticado pero no tiene permiso o intenta
  acceder a un recurso propio de otro usuario.
- `400 Bad Request`: el cuerpo o los parámetros no pasan validación.
- `404 Not Found`: recurso inexistente cuando la ruta responde con ese estado.
- `GET /api/productos/{id}` devuelve `404` con `code` `PRODUCTO_NO_ENCONTRADO`
  cuando no existe el producto.
- `409 Conflict`: conflicto de negocio, como stock insuficiente o recursos con
  dependencias.

Para errores de validación, el arreglo `errores` contiene objetos con
`campo` y `mensaje`. El frontend debe basar el manejo en `status` y `code`, no
en comparar textos de `message`.

Las validaciones de campos devuelven sus detalles en `errores`, como una lista
de objetos `{ "campo": "...", "mensaje": "..." }`. `message` conserva el texto
concatenado para clientes que aún no consumen ese arreglo.

## Diferencias actuales que el frontend debe tener en cuenta

- `2-modeladoDtos.md` describe DTOs objetivo que no coinciden completamente con
  el código actual. Para evitar enviar cuerpos rechazados, seguir los ejemplos
  de este documento y verificar el DTO correspondiente al endpoint.
- El registro y la edición de perfil reutilizan `UsuarioDTORequest`, que mezcla
  campos identificatorios de dos contratos y marca algunos campos obligatorios
  aunque no todos sean persistidos por el servicio.
- El controlador de login actual no llena `tipoToken` ni `expiraEn` aunque esos
  campos existan en el DTO de respuesta.
- Las respuestas de usuario incluyen propiedades antiguas (`id`, `nombres`,
  `correo`, `rolDetalle`, entre otras); no asumir que solo existen `idUsuario`,
  `nombre`, `email` y `rol`.
- La creación de producto exige SKU y categorías; la edición de producto usa
  `POST /api/productos/{idProducto}`. Las rutas que nombran el parámetro `{sku}`
  reciben en realidad el ID numérico del producto. Esto difiere de contratos
  REST más convencionales y del ejemplo simplificado de la documentación general.
- No existe un endpoint público de categorías en la configuración actual.
- La subida de imagen acepta el archivo `file`; los campos adicionales de
  respuesta no son configurables en la solicitud actual.
