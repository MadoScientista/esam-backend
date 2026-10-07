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
  "stockReservado": 3,
  "marca": "Staedtler",
  "marcaDetalle": {
    "idMarca": 2,
    "nombre": "Staedtler"
  },
  "idMarca": 2,
  "categorias": [
    {
      "idCategoria": 4,
      "nombre": "Cuadernos",
      "slug": "cuadernos",
      "idCategoriaPadre": null,
      "imagenUrl": null
    }
  ],
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

El campo `stock` es la cantidad total del producto; `stockReservado` es la parte
comprometida por pedidos creados y aún no entregados ni cancelados. El stock
*disponible* para nuevos
pedidos es la resta `stock - stockReservado` y lo calcula el frontend a partir
de ambos campos.

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

Las operaciones que devuelven el carrito responden, por ejemplo. Cada
`producto` tiene la forma de un resumen de producto, con `stock` (total),
`stockReservado`, `imagenPrincipal` y `marca` además de los campos mostrados:

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
        "stock": 35,
        "stockReservado": 3
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

El carrito **no** valida stock, ni al agregar ni al cambiar cantidades: el
stock se exige recién al crear el pedido. El disponible para comprar es la
resta `stock - stockReservado`.

### Crear pedido e historial propio

El pedido se crea siempre a partir del carrito persistido en el backend: el
cuerpo de la solicitud no lleva ítems ni totales, solo la modalidad de entrega.

Campos del cuerpo (`PedidoDTORequest`):

| Campo | Obligatorio | Detalle |
|---|---|---|
| `tipoEntrega` | Sí | `DESPACHO` o `RETIRA_TIENDA`. Si falta o el valor no es válido, responde `400` (code `ERROR_VALIDACION`). |
| `idDireccion` | Solo en `DESPACHO` | ID de una dirección **propia y activa**. Con `RETIRA_TIENDA` no se envía; si se envía igual, el servidor la ignora. |

Errores posibles al crear:

| Status | `code` | Cuándo |
|---|---|---|
| `400` | `ERROR_VALIDACION` | Falta `tipoEntrega` o el valor no es un `TipoEntrega` válido. |
| `400` | `PEDIDO_INVALIDO` | `DESPACHO` sin `idDireccion`; el usuario no tiene carrito; el carrito está vacío; o un producto del carrito ya no está activo. |
| `404` | `DIRECCION_NO_ENCONTRADA` | `idDireccion` inexistente, inactiva o que no pertenece al usuario del token. |
| `409` | `CONFLICTO_STOCK` | No alcanza el stock disponible de uno de los productos. |

```http
POST /api/pedidos
Authorization: Bearer <JWT>
Content-Type: application/json
```

Despacho a domicilio:

```json
{
  "tipoEntrega": "DESPACHO",
  "idDireccion": 42
}
```

Retiro en tienda:

```json
{
  "tipoEntrega": "RETIRA_TIENDA"
}
```

Respuesta `201 Created`, con forma `PedidoDTOResponse`. El campo `tipoEntrega`
repite la modalidad elegida; `envio` solo se completa para `DESPACHO` y en
cualquier otro caso es `null`. Para el despacho es:

```json
{
  "idPedido": 101,
  "numeroPedido": "<número generado>",
  "estado": "PENDIENTE",
  "tipoEntrega": "DESPACHO",
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

Para el retiro en tienda la respuesta es la misma forma con `envio: null` y
sin datos de envío:

```json
{
  "idPedido": 102,
  "numeroPedido": "<número generado>",
  "estado": "PENDIENTE",
  "tipoEntrega": "RETIRA_TIENDA",
  "total": 5980,
  "creadoEn": "<fecha y hora ISO-8601>",
  "detalles": [
    {
      "idDetallePedido": 202,
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

Al crear el pedido el servidor valida que haya stock disponible
(`stock - stockReservado`) para todos los productos del carrito. Si falta
stock responde `409` (code `CONFLICTO_STOCK`), el pedido **no** se crea y el
carrito queda intacto: la operación es una transacción y se revierte por
completo. El mensaje identifica el producto, por ejemplo
`"Stock insuficiente para el producto 24"`; el frontend debe basarse en
`status` y `code`, no en el texto.

Cuando el pedido sí se crea, la cantidad de cada producto queda **reservada**
(aumenta su `stockReservado`) y el carrito del usuario queda vacío. El
`PedidoDTOResponse` devuelto queda en estado `PENDIENTE`.

Ciclo de stock por estado del pedido:

| Transición | Efecto sobre el stock |
|---|---|
| Creación → `PENDIENTE` | `stockReservado += cantidad`; el total físico no cambia. |
| `PENDIENTE` → `CONFIRMADO` | Sin cambio: la reserva se mantiene mientras el pago está aceptado. |
| `CONFIRMADO` → `ENVIADO` | Sin cambio. |
| `ENVIADO` → `ENTREGADO` | `stock -= cantidad` y `stockReservado -= cantidad`: se descuenta lo vendido y se libera la reserva. El disponible (`stock - stockReservado`) queda igual que antes de entregar. |
| `PENDIENTE` → `CANCELADO` | `stockReservado -= cantidad`: se libera la reserva sin tocar el total. |

Consulta del historial y del detalle propio:

```http
GET /api/pedidos
GET /api/pedidos/{idPedido}
Authorization: Bearer <JWT>
```

`GET /api/pedidos` devuelve una lista de resúmenes (`PedidoResumenDTOResponse`)
ordenada por `creadoEn` descendente, sin paginación:

```json
[
  {
    "idPedido": 101,
    "numeroPedido": "<número generado>",
    "estado": "PENDIENTE",
    "total": 5980,
    "cantidadItems": 2,
    "creadoEn": "<fecha y hora ISO-8601>"
  }
]
```

`GET /api/pedidos/{idPedido}` devuelve el `PedidoDTOResponse` completo (el
mismo formato del `201 Created` de arriba), también sin paginación. Si el
pedido no existe o pertenece a otro usuario responde `404` (code
`PEDIDO_NO_ENCONTRADO`); no se distingue entre ambos casos.

## Vendedor: catálogo e imágenes

El vendedor puede leer el catálogo público y, con JWT, crear, editar, eliminar
productos y mantener sus imágenes. No puede modificar categorías, marcas,
geografía, roles ni usuarios; los pedidos de administración (`/api/pedidos/admin...`)
sí puede consultarlos y actualizar su estado, igual que admin.

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
`descripcion`, `precio`, `stock`, `marca`, `marcaDetalle`, `idMarca`,
`categorias` e `imagenes`.

Cambiar el stock:

```http
PUT /api/productos/24/stock/setear?stock=18
Authorization: Bearer <JWT de vendedor>
```

También existen las rutas `/stock/aumentar?unidades=2` y
`/stock/disminuir?unidades=2`. Las tres rutas devuelven el
`ProductoDTOResponse` actualizado.

El `stock` que se envía al crear o editar un producto, y el que fija
`stock/setear`, es siempre el **total físico**, no el disponible. El vendedor
**no** envía `stockReservado`: el servidor lo inicializa en `0` y lo administra
internamente cuando se crean y se entregan o cancelan pedidos.

Comportamiento de cada operación:

| Operación | Valida | Errores |
|---|---|---|
| Crear / editar producto | `stock >= 0` | `400 PRODUCTO_INVALIDO` si `stock` es negativo o nulo. |
| Editar producto | `stock >= stockReservado` | `409 CONFLICTO_STOCK` si el total quedaría por debajo del reservado. |
| `stock/setear?stock=N` | `N >= 0` y `N >= stockReservado` | `400 PRODUCTO_INVALIDO` si `N < 0`; `409 CONFLICTO_STOCK` si `N` deja el total por debajo del reservado. |
| `stock/disminuir?unidades=U` | `U > 0`, `U <= stock` y `stock - U >= stockReservado` | `400 PRODUCTO_INVALIDO` si `U <= 0`; `409 CONFLICTO_STOCK` si `U` supera el total (`"No hay stock suficiente. Stock actual: ..."`) o si dejaría el total por debajo del reservado. |
| `stock/aumentar?unidades=U` | `U > 0` y no desbordar el total | `400 PRODUCTO_INVALIDO` si `U <= 0`; `409 CONFLICTO_STOCK` si el aumento excede el máximo permitido. No considera el reservado porque solo incrementa. |

En todas, un producto inexistente responde `404 PRODUCTO_NO_ENCONTRADO`.

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

Todas las rutas de esta sección requieren JWT con rol `admin`, con una única
excepción: las rutas de administración de pedidos (`GET` y `PUT`
`/api/pedidos/admin...`) también las puede usar el rol `vendedor`, como se
detalla más abajo. El listado de roles puede consultarse públicamente, pero
solo admin puede mutarlos o asignar un rol al crear una cuenta.

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

Las rutas `/api/pedidos/admin...` están disponibles para **admin y vendedor**.
El rol `cliente` no puede usarlas: recibe `403` (code `ACCESO_DENEGADO`), y sin
token recibe `401`.

| Método | Ruta | Qué hace | Respuesta |
|---|---|---|---|
| `GET` | `/api/pedidos/admin` | Lista todos los pedidos, de más reciente a más antiguo, sin paginación. | `200`, lista de `PedidoAdminDTOResponse`. |
| `GET` | `/api/pedidos/admin/{idPedido}` | Detalle de un pedido, sea de quien sea. | `200`, `PedidoAdminDTOResponse`. `404 PEDIDO_NO_ENCONTRADO` si no existe. |
| `PUT` | `/api/pedidos/admin/{idPedido}/estado` | Cambia el estado. | `200`, el pedido actualizado. |

`PedidoAdminDTOResponse` es el `PedidoDTOResponse` de la sección del cliente
(con `tipoEntrega`, `envio`, `detalles` e `historialEstados`) más dos campos
del comprador: `idUsuario` y `emailUsuario`. Ejemplo de lista:

```json
[
  {
    "idPedido": 101,
    "numeroPedido": "<número generado>",
    "estado": "PENDIENTE",
    "tipoEntrega": "DESPACHO",
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
    ],
    "idUsuario": 15,
    "emailUsuario": "<correo del comprador>"
  }
]
```

Para un pedido de retiro en tienda, `envio` es `null`. El campo
`emailUsuario` es correo personal: no debe registrarse en logs.

`historialEstados` registra cada cambio con `estadoAnterior`, `estadoNuevo` y
`cambiadoEn`, pero **no** incluye qué usuario hizo el cambio.

Cambio de estado:

```http
PUT /api/pedidos/admin/101/estado
Authorization: Bearer <JWT de admin o vendedor>
Content-Type: application/json
```

```json
{
  "estado": "CONFIRMADO"
}
```

Respuesta `200 OK` con el `PedidoAdminDTOResponse` actualizado, incluyendo el
`historialEstados` con la transición recién agregada.

Transiciones permitidas:

| Estado actual | Transiciones permitidas |
|---|---|
| `PENDIENTE` | `CONFIRMADO`, `CANCELADO` |
| `CONFIRMADO` | `ENVIADO` |
| `ENVIADO` | `ENTREGADO` |
| `ENTREGADO` | Ninguna (estado final) |
| `CANCELADO` | Ninguna (estado final) |

Errores de `PUT .../estado`:

| Status | `code` | Cuándo |
|---|---|---|
| `400` | `ERROR_VALIDACION` | Falta `estado` o el valor no es un `EstadoPedido` válido. |
| `400` | `PEDIDO_INVALIDO` | La transición solicitada no está permitida (tabla anterior). |
| `403` | `ACCESO_DENEGADO` | El token no es de admin ni vendedor. |
| `404` | `PEDIDO_NO_ENCONTRADO` | No existe un pedido con ese ID. |

Flujo de pago (simulado): el pago **no** se procesa en el backend y no existe
endpoint de pago ni endpoint para que un cliente confirme su propio pedido. El
pedido nace `PENDIENTE`; el frontend del cliente puede simular la pasarela de
pago en su interfaz, pero eso no cambia nada en el servidor. Quien registra el
pago aceptado es un **admin o un vendedor** desde el panel, mediante
`PUT /api/pedidos/admin/{idPedido}/estado` con `{"estado": "CONFIRMADO"}`.
Solo admin o vendedor pueden hacerlo; el cliente no tiene acceso a esa ruta.

Efecto sobre el stock de cada transición (ver detalle en la sección de
creación de pedido): la reserva nace en `PENDIENTE`, se mantiene en
`CONFIRMADO` y `ENVIADO`, se descuenta del total y se libera en `ENTREGADO`,
y se libera sin tocar el total al pasar a `CANCELADO` (solo posible desde
`PENDIENTE`).

### Resumen del dashboard

Admin y vendedor pueden consultar `GET /api/dashboard/resumen` con un JWT:

```http
GET /api/dashboard/resumen
Authorization: Bearer <JWT de admin o vendedor>
```

Respuesta `200 OK`:

```json
{
  "pedidosEntregados": 18,
  "pedidosPendientes": 4,
  "productosTotales": 45,
  "productosConStock": 38,
  "clientes": 210,
  "vendedores": 8,
  "administradores": 2
}
```

Los conteos incluyen todos los pedidos con estado `ENTREGADO` o `PENDIENTE`,
respectivamente; todos los productos; y los usuarios agrupados por rol
(`cliente`, `vendedor` y `admin`).

`productosConStock` cuenta los productos con **stock físico** mayor que cero
(`stock > 0`), no el disponible. Un producto con unidades totalmente reservadas
sigue contando como "con stock" aunque no se pueda vender nada de él hasta que
se entregue o cancele un pedido.

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
- Las respuestas de producto y de carrito exponen `stock` como total y
  también `stockReservado`; el disponible se calcula como la resta de ambos.
  El total solo baja al entregar un pedido; la reserva nace al crearlo.
- La creación de pedido exige `tipoEntrega` (`DESPACHO` o `RETIRA_TIENDA`);
  `idDireccion` solo se envía para despacho y se ignora en el retiro.
- No existe endpoint de pago ni endpoint para que el cliente confirme su
  pedido: `CONFIRMADO` lo aplica admin o vendedor desde el panel.
- Ningún listado de pedidos tiene paginación: todos devuelven el conjunto
  completo. `historialEstados` no indica qué usuario hizo el cambio.
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
