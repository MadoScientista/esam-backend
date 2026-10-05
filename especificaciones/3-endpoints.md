# Endpoints y acceso por rol

Este documento resume los endpoints implementados y el acceso que aplica la
configuración actual de Spring Security. Los métodos HTTP son parte de la
definición del endpoint: tener acceso de lectura no implica permiso de escritura.

## Convenciones

- **Público:** no requiere JWT.
- **Autenticado:** requiere un JWT válido; aplica a cliente, vendedor y admin.
- **Propio:** el recurso debe pertenecer al usuario autenticado. En direcciones
  y pedidos, cambiar el ID de la ruta no permite consultar recursos ajenos.
- `—`: el rol no tiene acceso a ese endpoint.
- Las rutas `admin` requieren el rol `admin`. El rol `vendedor` no recibe
  permisos administrativos por defecto.

## Endpoints públicos

| Método | Endpoint | Descripción |
|---|---|---|
| `POST` | `/api/usuarios` | Registrar una cuenta; se crea con rol `cliente`. |
| `POST` | `/api/usuarios/login` | Iniciar sesión y obtener JWT. |
| `GET` | `/api/productos` | Listar productos. |
| `GET` | `/api/productos/{sku}` | Obtener producto por SKU. |
| `GET` | `/api/productos/marca/{idONombreMarca}` | Filtrar productos por marca. |
| `GET` | `/api/productos/precio?min={min}&max={max}` | Filtrar productos por precio. |
| `GET` | `/api/productos/stock?min={min}&max={max}` | Filtrar productos por stock. |
| `GET` | `/api/productos/nombre?nombre={nombre}` | Filtrar productos por nombre. |
| `GET` | `/api/productos/{sku}/imagenes` | Listar imágenes de un producto. |
| `GET` | `/api/categorias` | Listar categorías. |
| `GET` | `/api/categorias/raiz` | Listar categorías raíz. |
| `GET` | `/api/categorias/padre/{idPadre}` | Listar subcategorías de una categoría. |
| `GET` | `/api/categorias/{id}` | Obtener categoría por ID. |
| `GET` | `/api/marcas` | Listar marcas. |
| `GET` | `/api/marcas/{id}` | Obtener marca por ID. |
| `GET` | `/api/regiones` | Listar regiones. |
| `GET` | `/api/regiones/{id}` | Obtener región por ID. |
| `GET` | `/api/regiones/comunas` | Listar regiones con sus comunas. |
| `GET` | `/api/regiones/{id}/comunas` | Obtener comunas de una región. |
| `GET` | `/api/comunas` | Listar comunas. |
| `GET` | `/api/comunas/{id}` | Obtener comuna por ID. |
| `GET` | `/api/roles` | Listar roles. |
| `GET` | `/api/roles/{id}` | Obtener rol por ID. |

## Endpoint para admin y vendedor

| Método | Endpoint | Descripción |
|---|---|---|
| `GET` | `/api/dashboard/resumen` | Obtener métricas agregadas del dashboard. |

## Matriz de acceso por rol del sistema

| Grupo de endpoints | Público | Cliente | Vendedor | Admin |
|---|:---:|:---:|:---:|:---:|
| Login y registro (`POST /api/usuarios/login`, `POST /api/usuarios`) | Sí | Sí | Sí | Sí |
| Lecturas públicas de productos, imágenes, categorías, marcas, regiones, comunas y roles | Sí | Sí | Sí | Sí |
| Crear, editar y eliminar productos e imágenes; cambiar stock | — | — | Sí | Sí |
| Crear, editar y eliminar categorías | — | — | — | Sí |
| Crear, editar y eliminar marcas, regiones, comunas y roles | — | — | — | Sí |
| Perfil propio (`GET` y `PUT /api/usuarios/perfil`) | — | Sí | Sí | Sí |
| Direcciones propias (`/api/direcciones...`) | — | Sí | Sí | Sí |
| Carrito propio (`/api/carrito...`) | — | Sí | Sí | Sí |
| Crear pedidos e historial/detalle propio (`/api/pedidos`) | — | Sí | Sí | Sí |
| Resumen del dashboard (`/api/dashboard/resumen`) | — | — | Sí | Sí |
| Administración de usuarios (`/api/usuarios` salvo registro, login y perfil) | — | — | — | Sí |
| Administración de pedidos (`/api/pedidos/admin...`) | — | — | — | Sí |

El encabezado para los endpoints protegidos es:

```http
Authorization: Bearer <JWT>
```

## Endpoints autenticados: perfil, direcciones, carrito y pedidos

Estas operaciones aceptan a los roles de sistema autenticados. Los datos de
direcciones, carrito, perfil e historial de pedidos se limitan al usuario
asociado al token.

| Método | Endpoint | Acceso |
|---|---|---|
| `GET` | `/api/usuarios/perfil` | Autenticado; devuelve el perfil propio. |
| `PUT` | `/api/usuarios/perfil` | Autenticado; actualiza el perfil propio, no el rol. |
| `GET` | `/api/direcciones` | Autenticado; lista las direcciones propias. |
| `GET` | `/api/direcciones/usuario/{idUsuario}` | Autenticado; el ID debe ser el del usuario autenticado. |
| `GET` | `/api/direcciones/usuario/{idUsuario}/activas` | Autenticado; solo direcciones activas propias. |
| `GET` | `/api/direcciones/{id}` | Autenticado; solo devuelve una dirección propia. |
| `POST` | `/api/direcciones` | Autenticado; crea una dirección para el usuario autenticado. |
| `PUT` | `/api/direcciones/{id}` | Autenticado; edita una dirección propia. |
| `DELETE` | `/api/direcciones/{id}` | Autenticado; elimina una dirección propia. |
| `GET` | `/api/carrito` | Autenticado; obtiene o crea el carrito propio. |
| `POST` | `/api/carrito/items` | Autenticado; agrega un artículo al carrito propio. |
| `PUT` | `/api/carrito/items/{idProducto}` | Autenticado; cambia la cantidad de un artículo propio. |
| `DELETE` | `/api/carrito/items/{idProducto}` | Autenticado; quita un artículo del carrito propio. |
| `DELETE` | `/api/carrito` | Autenticado; vacía el carrito propio. |
| `POST` | `/api/pedidos` | Autenticado; crea un pedido para el usuario autenticado. |
| `GET` | `/api/pedidos` | Autenticado; consulta el historial propio. |
| `GET` | `/api/pedidos/{idPedido}` | Autenticado; consulta un pedido propio. |

## Endpoints de catálogo: vendedor y admin

El rol vendedor puede gestionar productos e imágenes, igual que admin. Las
lecturas de productos e imágenes siguen siendo públicas.

| Método | Endpoint | Acceso |
|---|---|---|
| `POST` | `/api/productos` | Vendedor o admin; crea producto. |
| `POST` | `/api/productos/{sku}` | Vendedor o admin; edita producto. |
| `PUT` | `/api/productos/{sku}/stock/setear?stock={stock}` | Vendedor o admin; fija stock. |
| `PUT` | `/api/productos/{sku}/stock/disminuir?unidades={unidades}` | Vendedor o admin; disminuye stock. |
| `PUT` | `/api/productos/{sku}/stock/aumentar?unidades={unidades}` | Vendedor o admin; aumenta stock. |
| `DELETE` | `/api/productos/{sku}` | Vendedor o admin; elimina producto. |
| `DELETE` | `/api/productos/{sku}/cascada` | Vendedor o admin; elimina producto e imágenes. |
| `POST` | `/api/productos/{sku}/imagenes` | Vendedor o admin; sube imagen. |
| `PUT` | `/api/productos/{sku}/imagenes/{idImagenProducto}/principal` | **Solo admin**; selecciona la imagen principal y desmarca las demás del producto. |
| `DELETE` | `/api/productos/{sku}/imagenes/{idImagenProducto}` | Vendedor o admin; elimina imagen. |
| `DELETE` | `/api/productos/{sku}/imagenes` | Vendedor o admin; elimina todas las imágenes del producto. |

## Endpoints exclusivos de admin

| Método | Endpoint | Descripción |
|---|---|---|
| `GET` | `/api/usuarios` | Listar usuarios. |
| `GET` | `/api/usuarios/{id}` | Obtener usuario por ID. |
| `GET` | `/api/usuarios/rol/{idRolUsuario}` | Filtrar usuarios por rol. |
| `POST` | `/api/usuarios/admin` | Crear usuario asignándole un rol. |
| `PUT` | `/api/usuarios/{id}` | Editar usuario, incluido el rol. |
| `DELETE` | `/api/usuarios/{id}` | Eliminar usuario. |
| `PUT` | `/api/productos/{sku}/imagenes/{idImagenProducto}/principal` | Seleccionar la imagen principal del producto; reemplaza la selección anterior. |
| `POST` | `/api/categorias` | Crear categoría. |
| `PUT` | `/api/categorias/{id}` | Editar categoría. |
| `DELETE` | `/api/categorias/{id}` | Eliminar categoría y su imagen de Cloudinary, si tiene. |
| `POST` | `/api/categorias/{id}/imagen` | Subir o reemplazar la imagen de portada (multipart, campo `file`). |
| `DELETE` | `/api/categorias/{id}/imagen` | Eliminar la imagen de portada de Cloudinary y de la categoría. |
| `POST` | `/api/marcas` | Crear marca. |
| `PUT` | `/api/marcas/{id}` | Editar marca. |
| `DELETE` | `/api/marcas/{id}` | Eliminar marca. |
| `DELETE` | `/api/marcas/{id}/cascada` | Eliminar marca junto con todos sus productos. |
| `POST` | `/api/regiones` | Crear región. |
| `PUT` | `/api/regiones/{id}` | Editar región. |
| `DELETE` | `/api/regiones/{id}` | Eliminar región. |
| `POST` | `/api/comunas` | Crear comuna. |
| `PUT` | `/api/comunas/{id}` | Editar comuna. |
| `DELETE` | `/api/comunas/{id}` | Eliminar comuna. |
| `POST` | `/api/roles` | Crear rol. |
| `PUT` | `/api/roles/{id}` | Editar rol. |
| `DELETE` | `/api/roles/{id}` | Eliminar rol. |
| `GET` | `/api/pedidos/admin` | Listar pedidos para administración. |
| `GET` | `/api/pedidos/admin/{idPedido}` | Obtener pedido para administración. |
| `PUT` | `/api/pedidos/admin/{idPedido}/estado` | Cambiar estado del pedido. |

## Respuestas de acceso

- Sin JWT, con token inválido o expirado en una ruta protegida: `401`.
- Con JWT válido, pero sin el rol requerido: `403`.
- Una dirección o pedido ajeno no queda accesible por conocer su ID.
