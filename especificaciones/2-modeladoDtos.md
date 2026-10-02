# Modelado de DTOs: Backend Ecommerce (Nivel N1)

**Stack:** Spring Boot · Bean Validation
**Convención de ids:** cada id lleva el nombre de la entidad (`idUsuario`, `idProducto`, `idImagenProducto`). Las referencias entre entidades siguen la misma regla (`idMarca`, `idComuna`).

---

## 1. Qué documenta cada DTO

Cada DTO responde cuatro preguntas:

| Pregunta | Ejemplo con `Usuario` |
|---|---|
| ¿Qué se **expone**? | `idUsuario`, `nombre`, `correo`. Nunca `password`. |
| ¿Qué se **recibe**? | Al registrarse: `nombres`, `aPaterno`, `correo`, `password`. |
| ¿Qué se **acepta**? | `correo` con formato válido, `password` con largo mínimo. |
| ¿**Por qué**? | `rol` no viene en el request: si viniera, cualquiera podría registrarse como administrador (mass assignment). |

En la mayoría de los DTOs, lo que **no** entra por el request es tan relevante como lo que sí entra.

---

## 2. Estructura del documento

| Sección | Contenido |
|---|---|
| **3.1** | Principios y convenciones |
| **3.2** | Transversales: error, paginación |
| **3.3** | Identidad: registro, login, perfil, direcciones, geografía |
| **3.4** | Catálogo (lectura pública) |
| **3.5** | Compra: carrito y pedidos |
| **3.6** | Administración |

**Fuera de alcance en N1:** gestión de usuarios por un administrador (sin DTOs por ahora).

---

## 3.1 Principios y convenciones

### P1. Las entidades nunca salen del servicio
El controlador solo ve DTOs.
**Por qué:** evita exponer columnas por accidente y las `LazyInitializationException` al serializar relaciones.

### P2. Un Request por entidad; se divide solo si diverge
Por defecto, un único `XxxDTORequest` sirve para crear y reemplazar (por ejemplo `DireccionDTORequest`). Se crea otro cuando los campos o la obligatoriedad difieren (por ejemplo `UsuarioRegistroDTORequest` frente a `UsuarioPerfilDTORequest`).
**Por qué:** evita duplicar clases idénticas, sin forzar un DTO que mezcle casos de uso distintos.

### P3. Request y Response separados
Nunca la misma clase para entrada y salida.
**Por qué:** lo que se acepta y lo que se expone casi nunca coinciden.

### P4. Lo que el cliente no decide, no está en el request
Ids propios, `total`, `estado`, `precioUnitario`, `rol`, `slug`, entre otros.
**Por qué:** es la defensa de fondo contra mass assignment. Un campo que no existe en la clase no se puede enviar, y no depende de acordarse de ignorarlo.

### P5. Dos niveles de respuesta para recursos grandes
`ProductoResumenDTOResponse` para listados y `ProductoDetalleDTOResponse` para la ficha.
**Por qué:** los listados no cargan descripción ni galería completa.

### P6. Relaciones como ids al recibir, objetos pequeños al devolver
Al crear se recibe `idMarca`; al leer se devuelve un `MarcaDTOResponse` con `idMarca` y `nombre`. Nunca entidades anidadas completas.
**Por qué:** evita respuestas gigantes, ciclos de serialización y exposición de datos de entidades relacionadas.

### P7. Validación estructural en el DTO
Con Bean Validation: obligatoriedad, largo, formato y rango. Los largos máximos deben coincidir con los de las columnas.
**Por qué:** rechaza entradas mal formadas antes de llegar al servicio. Las reglas de negocio no van en el DTO y se tratan en el punto 2.

### P8. Clases normales
Los DTOs son clases con campos privados, constructor sin argumentos, getters y setters (Lombok opcional).
**Por qué:** es la forma estándar de que Jackson deserialice los requests y de que Bean Validation anote los campos. Al no ser inmutables, P4 se vuelve más importante: la seguridad viene de los campos que no existen.

### P9. Convenciones de datos

| Dato | Tipo y formato |
|---|---|
| Dinero | `Long` en CLP |
| Fechas | `Instant`, ISO-8601 UTC |
| Paginación | DTO propio, sin exponer `Page` de Spring |
| Estados (enum) | Se exponen como `String` con el nombre del enum |

**Por qué:** `Page` de Spring acopla el contrato público a la librería y expone campos que el frontend no necesita.

### Convención de nombres

`<Concepto>DTORequest` y `<Concepto>DTOResponse`. El concepto puede llevar un calificador cuando hay más de una variante.

| Ejemplo | Uso |
|---|---|
| `ProductoDTORequest` | Entrada |
| `ProductoDTOResponse`, `ProductoDetalleDTOResponse` | Salida |
| `ProductoResumenDTOResponse` | Salida reducida para listados |
| `UsuarioRegistroDTORequest` | Entrada de un caso de uso específico |

---

## 3.2 Transversales

### ErrorDTOResponse
Formato único para todos los errores de la API.

| Campo | Tipo | Descripción |
|---|---|---|
| timestamp | Instant | Momento del error |
| status | int | Código HTTP |
| error | String | Código legible por máquina (por ejemplo `VALIDACION_FALLIDA`) |
| mensaje | String | Texto para mostrar al usuario |
| ruta | String | Endpoint solicitado |
| errores | List\<CampoErrorDTOResponse\> | Solo en errores de validación; si no, vacío |

**Por qué:** el frontend maneja un único formato. El `mensaje` nunca incluye trazas, nombres de clases ni detalles de la base de datos.

### CampoErrorDTOResponse

| Campo | Tipo | Descripción |
|---|---|---|
| campo | String | Nombre del campo inválido |
| mensaje | String | Motivo |

**Por qué:** permite al frontend marcar cada campo del formulario.

### PaginacionDTORequest
Parámetros de consulta (query params) para endpoints de listado.

| Campo | Tipo | Validación | Por defecto |
|---|---|---|---|
| pagina | Integer | `@Min(0)` | 0 |
| tamano | Integer | `@Min(1)` `@Max(50)` | 20 |

**Por qué:** el tope máximo impide que un cliente pida una página de 100.000 registros.

### PaginaDTOResponse\<T\>

| Campo | Tipo |
|---|---|
| contenido | List\<T\> |
| pagina | int |
| tamano | int |
| totalElementos | long |
| totalPaginas | int |

---

## 3.3 Identidad

### UsuarioRegistroDTORequest

| Campo | Tipo | Validación |
|---|---|---|
| nombre | String | `@NotBlank` `@Size(max = 100)` |
| apellido | String | `@NotBlank` `@Size(max = 100)` |
| email | String | `@NotBlank` `@Email` `@Size(max = 254)` |
| password | String | `@NotBlank` `@Size(min = 8, max = 72)` |
| telefono | String | Opcional. `@Pattern("^\\+?[0-9]{8,15}$")` |

**Por qué:** no incluye `rol`, `activo` ni `idUsuario`. El rol lo asigna el servidor (siempre cliente al registrarse). El máximo de 72 en `password` es el límite de bcrypt.

### LoginDTORequest

| Campo | Tipo | Validación |
|---|---|---|
| email | String | `@NotBlank` |
| password | String | `@NotBlank` |

**Por qué:** en el login solo se valida que vengan, no su formato. Validar largo o patrón de la contraseña acá revelaría las reglas de contraseñas a un atacante.

### AuthDTOResponse *(provisional)*

| Campo | Tipo |
|---|---|
| token | String |
| tipoToken | String |
| expiraEn | Instant |

**Por qué provisional:** su forma depende del mecanismo de autenticación (JWT, sesión), que se define en el punto 3 (seguridad).

### UsuarioDTOResponse

| Campo | Tipo |
|---|---|
| idUsuario | Long |
| nombre | String |
| apellido | String |
| email | String |
| telefono | String |
| rol | String |
| creadoEn | Instant |

**Por qué:** `rol` se devuelve como texto, no como entidad. Nunca incluye `passwordHash` ni `activo`.

### UsuarioPerfilDTORequest

| Campo | Tipo | Validación |
|---|---|---|
| nombre | String | `@NotBlank` `@Size(max = 100)` |
| apellido | String | `@NotBlank` `@Size(max = 100)` |
| telefono | String | Opcional. `@Pattern("^\\+?[0-9]{8,15}$")` |

**Por qué:** no incluye `email` ni `password`. El email es la identidad de acceso y la contraseña tiene su propio DTO, porque ambos requieren un tratamiento más estricto que un cambio de perfil.

### PasswordCambioDTORequest

| Campo | Tipo | Validación |
|---|---|---|
| passwordActual | String | `@NotBlank` |
| passwordNueva | String | `@NotBlank` `@Size(min = 8, max = 72)` |

**Por qué:** exigir la contraseña actual evita que una sesión robada cambie la contraseña sin conocerla.

### DireccionDTORequest

| Campo | Tipo | Validación |
|---|---|---|
| nombreReceptor | String | `@NotBlank` `@Size(max = 150)` |
| telefonoReceptor | String | `@NotBlank` `@Pattern("^\\+?[0-9]{8,15}$")` |
| calle | String | `@NotBlank` `@Size(max = 150)` |
| numero | String | `@NotBlank` `@Size(max = 20)` |
| complemento | String | Opcional. `@Size(max = 200)` |
| predeterminada | Boolean | Opcional |
| idComuna | Long | `@NotNull` |

**Por qué:** no incluye `idUsuario`. El dueño de la dirección es siempre el usuario autenticado, nunca un dato del cliente.

### DireccionDTOResponse

| Campo | Tipo |
|---|---|
| idDireccion | Long |
| nombreReceptor | String |
| telefonoReceptor | String |
| calle | String |
| numero | String |
| complemento | String |
| predeterminada | Boolean |
| comuna | ComunaDTOResponse |

**Por qué:** no incluye `idUsuario` (es el propio usuario quien consulta) ni `activo`.

### RegionDTOResponse

| Campo | Tipo |
|---|---|
| idRegion | Long |
| nombre | String |

### ComunaDTOResponse

| Campo | Tipo |
|---|---|
| idComuna | Long |
| nombre | String |
| idRegion | Long |

**Por qué (geografía):** son datos de solo lectura, sin Request. Alimentan los selectores del formulario de dirección.

---

## 3.4 Catálogo (lectura pública)

### MarcaDTOResponse

| Campo | Tipo |
|---|---|
| idMarca | Long |
| nombre | String |

**Por qué:** es lo bastante pequeña como para servir también de objeto anidado en productos (P6).

### CategoriaDTOResponse

| Campo | Tipo |
|---|---|
| idCategoria | Long |
| nombre | String |
| slug | String |
| idCategoriaPadre | Long (nullable) |

**Por qué:** la jerarquía se devuelve plana, con el id del padre. El frontend arma el árbol y la respuesta evita anidamiento recursivo.

### ImagenProductoDTOResponse

| Campo | Tipo |
|---|---|
| idImagenProducto | Long |
| url | String |
| textoAlternativo | String |
| orden | Integer |
| principal | Boolean |

### ProductoFiltroDTORequest
Extiende `PaginacionDTORequest`. Parámetros de consulta del listado.

| Campo | Tipo | Validación |
|---|---|---|
| texto | String | Opcional. `@Size(max = 100)` |
| idCategoria | Long | Opcional |
| idMarca | Long | Opcional |
| precioMin | Long | Opcional. `@PositiveOrZero` |
| precioMax | Long | Opcional. `@PositiveOrZero` |
| orden | OrdenProducto (enum) | Opcional |

`OrdenProducto`: `PRECIO_ASC`, `PRECIO_DESC`, `NOMBRE_ASC`, `RECIENTES`.

**Por qué:** el orden es un enum cerrado, no un nombre de campo libre. Aceptar el nombre de una columna arbitraria permite ordenar por datos que no deben ser consultables.

### ProductoResumenDTOResponse
Para listados.

| Campo | Tipo |
|---|---|
| idProducto | Long |
| nombre | String |
| precio | Long |
| imagenPrincipal | ImagenProductoDTOResponse (nullable) |
| marca | MarcaDTOResponse |
| stock | Integer |

**Por qué:** `imagenPrincipal` es el objeto completo y no solo la URL, para que el listado también tenga el texto alternativo (accesibilidad); es `null` si el producto no tiene imágenes. Se expone la cantidad exacta de stock para que el frontend decida cómo mostrarla (disponible, "quedan N unidades", agotado).

### ProductoDetalleDTOResponse
Para la ficha del producto.

| Campo | Tipo |
|---|---|
| idProducto | Long |
| sku | String |
| nombre | String |
| descripcion | String |
| precio | Long |
| stock | Integer |
| marca | MarcaDTOResponse |
| categorias | List\<CategoriaDTOResponse\> |
| imagenes | List\<ImagenProductoDTOResponse\> |

**Por qué:** no incluye `activo` ni `version`, que son información interna (`activo` solo se ve en la respuesta de administración). La imagen principal es la de `imagenes` con `principal = true`; no se repite como campo aparte para no duplicar datos.

---

## 3.5 Compra

### ItemCarritoDTORequest
Agregar un producto al carrito.

| Campo | Tipo | Validación |
|---|---|---|
| idProducto | Long | `@NotNull` |
| cantidad | Integer | `@NotNull` `@Min(1)` `@Max(99)` |

**Por qué:** no incluye precio. El cliente dice qué y cuánto; el precio lo determina siempre el servidor. El tope de 99 es estructural, no una regla de stock.

### ItemCarritoCantidadDTORequest
Cambiar la cantidad de un ítem ya agregado.

| Campo | Tipo | Validación |
|---|---|---|
| cantidad | Integer | `@NotNull` `@Min(1)` `@Max(99)` |

**Por qué:** el producto se identifica en la URL; el body solo lleva lo que cambia.

### ItemCarritoDTOResponse

| Campo | Tipo |
|---|---|
| idItemCarrito | Long |
| producto | ProductoResumenDTOResponse |
| cantidad | Integer |
| precioUnitario | Long |
| subtotal | Long |

**Por qué:** `precioUnitario` y `subtotal` son valores calculados al consultar, solo informativos. No son un compromiso de precio hasta confirmar el pedido.

### CarritoDTOResponse

| Campo | Tipo |
|---|---|
| idCarrito | Long |
| items | List\<ItemCarritoDTOResponse\> |
| cantidadTotal | Integer |
| total | Long |

### PedidoDTORequest
Crear un pedido desde el carrito.

| Campo | Tipo | Validación |
|---|---|---|
| idDireccion | Long | `@NotNull` |

**Por qué:** es deliberadamente mínimo. Los ítems salen del carrito del servidor, la dirección se referencia por id y el servidor copia sus datos, y `total` y `estado` no los decide el cliente.

### DatosEnvioDTOResponse

| Campo | Tipo |
|---|---|
| nombreReceptor | String |
| telefonoReceptor | String |
| calle | String |
| numero | String |
| complemento | String |
| comunaNombre | String |
| regionNombre | String |

**Por qué:** refleja los datos de envío guardados en el pedido, no la dirección actual del usuario.

### DetallePedidoDTOResponse

| Campo | Tipo |
|---|---|
| idDetallePedido | Long |
| idProducto | Long |
| nombreProducto | String |
| skuProducto | String |
| precioUnitario | Long |
| cantidad | Integer |
| subtotal | Long |

### PedidoResumenDTOResponse
Para listados del historial.

| Campo | Tipo |
|---|---|
| idPedido | Long |
| numeroPedido | String |
| estado | String |
| total | Long |
| cantidadItems | Integer |
| creadoEn | Instant |

### PedidoDTOResponse

| Campo | Tipo |
|---|---|
| idPedido | Long |
| numeroPedido | String |
| estado | String |
| total | Long |
| creadoEn | Instant |
| envio | DatosEnvioDTOResponse |
| detalles | List\<DetallePedidoDTOResponse\> |
| historialEstados | List\<CambioEstadoPedidoDTOResponse\> |

`CambioEstadoPedidoDTOResponse` contiene `estadoAnterior` (nullable para el estado inicial), `estadoNuevo` y `cambiadoEn`.
El cliente crea pedidos desde su carrito y solo puede consultar su propio historial y detalle. La administración consulta todos los pedidos y actualiza únicamente su estado, sujeto a las transiciones definidas en el modelo.

---

## 3.6 Administración

### ProductoDTORequest
Crear y reemplazar un producto.

| Campo | Tipo | Validación |
|---|---|---|
| sku | String | `@NotBlank` `@Size(max = 50)` |
| nombre | String | `@NotBlank` `@Size(max = 150)` |
| descripcion | String | Opcional. `@Size(max = 5000)` |
| precio | Long | `@NotNull` `@Positive` |
| stock | Integer | `@NotNull` `@PositiveOrZero` |
| idMarca | Long | `@NotNull` |
| idCategorias | Set\<Long\> | `@NotEmpty` |

**Por qué:** no incluye `version`, `activo` ni imágenes. La baja es una operación aparte y las imágenes se gestionan con endpoints propios, mediante `ImagenProductoDTORequest`.

### ProductoAdminDTOResponse

| Campo | Tipo |
|---|---|
| idProducto | Long |
| sku | String |
| nombre | String |
| descripcion | String |
| precio | Long |
| stock | Integer |
| activo | Boolean |
| marca | MarcaDTOResponse |
| categorias | List\<CategoriaDTOResponse\> |
| imagenes | List\<ImagenProductoDTOResponse\> |
| creadoEn | Instant |
| actualizadoEn | Instant |

**Por qué:** a diferencia de la respuesta pública, expone `activo` y las fechas de auditoría.

### ImagenProductoDTORequest

| Campo | Tipo | Validación |
|---|---|---|
| url | String | `@NotBlank` `@URL` `@Size(max = 500)` |
| textoAlternativo | String | Opcional. `@Size(max = 200)` |
| orden | Integer | Opcional. `@PositiveOrZero` |
| principal | Boolean | Opcional |

**Por qué:** solo recibe la URL. La subida de archivos queda fuera de N1.

### MarcaDTORequest

| Campo | Tipo | Validación |
|---|---|---|
| nombre | String | `@NotBlank` `@Size(max = 100)` |

### CategoriaDTORequest

| Campo | Tipo | Validación |
|---|---|---|
| nombre | String | `@NotBlank` `@Size(max = 100)` |
| idCategoriaPadre | Long | Opcional |

**Por qué:** no incluye `slug`. Lo genera el servidor a partir del nombre, para garantizar formato y unicidad.

### PedidoEstadoDTORequest

| Campo | Tipo | Validación |
|---|---|---|
| estado | EstadoPedido | `@NotNull` |

**Por qué:** es el único dato que un administrador cambia en un pedido. Qué transiciones son válidas lo decide el servicio, no el DTO.

### PedidoAdminDTOResponse
Extiende `PedidoDTOResponse`.

| Campo adicional | Tipo |
|---|---|
| idUsuario | Long |
| emailUsuario | String |

**Por qué:** el administrador necesita saber de quién es el pedido. El cliente no necesita verlo, porque ya es el suyo.