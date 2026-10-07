# Modelado de datos: Backend Ecommerce (Nivel N1)

**Stack:** Spring Boot · JPA/Hibernate · MySQL

---

## 1. Convenciones de modelado

| Tema | Decisión |
|---|---|
| Clave primaria | `Long`, `GenerationType.IDENTITY`. Nombre del campo: `id` + nombre de la entidad (ej.: `idRegion`, `idUsuario`, `idCategoria`, `idMarca`, `idProducto`, `idComuna`, `idRolUsuario`, `idCarrito`, `idItemCarrito`, `idPedido`, `idDetallePedido`, `idImagenProducto`) |
| Auditoría | No hay auditoría general; Pedido registra fecha de creación y cada cambio de estado |
| Concurrencia | `Producto` usa `@Version` (`Long`, no nulo, por defecto `0`) para bloqueo optimista. Ninguna otra entidad lo usa |
| Dinero | `Long` en pesos chilenos (CLP no tiene decimales) |
| Stock Producto | `Integer` (precio unitario es `Long`, pero stock es `Integer`). El stock es **total**; la parte comprometida por pedidos creados y aún no entregados ni cancelados se lleva en `stockReservado` aparte |
| Fetch | Todas las relaciones `LAZY` |
| Colecciones `@ManyToMany` | `Set<>` |
| Enums | `@Enumerated(EnumType.STRING)` |
| Nombres | Entidades y propiedades en español, tablas en `snake_case` |
| Columnas FK | Se declaran con `@JoinColumn` explícito: `<entidad>_id` (`producto_id`, `usuario_id`, `comuna_id`, `region_id`, `pedido_id`) salvo `producto_categoria` y `producto.id_marca`, que usan `id_<entidad>` |
| ID obligatorio | Todos los entities deben tener `id` + nombre de entidad (Convención 1) |
| Propiedades Usuario | Deben coincidir con lo definido en la entidad `Usuario` (Convención 2) |
| Unique Comuna | El campo `nombre` en Comuna debe ser único (Convención 3) |
| Unique RolUsuario | El campo `nombre` en RolUsuario debe ser único (Convención 4) |

La generación de IDs serán con la notación @Id y Generation type Identity

---

## 2. Diagrama de relaciones

```
Region 1─N Comuna 1─N Direccion N─1 Usuario N─1 RolUsuario
          │             ▲           │
          └──── 1─N ────┘           ├── 1─1 Carrito 1─N ItemCarrito N─1 Producto
                                        └── 1─N Pedido 1─N DetallePedido
                                                │   │   │
                                                │   │   └──────────┘ (N─1)
                                                │   │
                                                │   └─ N─M Categoria ──(padre)──► Categoria
                                                │      (tabla intermedia: producto_categoria)
                                                │
                                                └── N─1 Producto
                                                         │
                                              N─1 Marca ─┴── 1─N ImagenProducto
```

### Resumen de cardinalidades

| Origen | Cardinalidad | Destino | Notas |
|---|---|---|---|
| Region | 1 : N | Comuna | |
| Comuna | 1 : N | Direccion | |
| Comuna | 1 : N | Usuario | Comuna de residencia, opcional |
| RolUsuario | 1 : N | Usuario | |
| Usuario | 1 : N | Direccion | |
| Usuario | 1 : 1 | Carrito | `usuario_id` único en `carrito` |
| Usuario | 1 : N | Pedido | |
| Marca | 1 : N | Producto | |
| Producto | N : M | Categoria | Tabla intermedia `producto_categoria` |
| Categoria | N : 1 | Categoria (padre) | Autorreferencia opcional |
| Producto | 1 : N | ImagenProducto | |
| Carrito | 1 : N | ItemCarrito | cascade ALL + orphanRemoval |
| Producto | 1 : N | ItemCarrito | |
| Pedido | 1 : N | DetallePedido | cascade ALL |
| Producto | 1 : N | DetallePedido | Referencia de trazabilidad |

---

## 3. Geografía

### Region

| Propiedad | Tipo | Restricciones |
|---|---|---|
| idRegion | Long | PK (id + nombre de la entidad) |
| nombre | String | Único, no nulo |
| comunas | List\<Comuna\> | `@OneToMany(mappedBy = "region")`, lado inverso |

**Relaciones:** `Region 1 ─── N Comuna`

### Comuna

| Propiedad | Tipo | Restricciones |
|---|---|---|
| idComuna | Long | PK (id + nombre de la entidad) |
| nombre | String | No nulo, **único** |
| region | Region | `@ManyToOne`, no nulo (`region_id`) |
| direcciones | List\<Direccion\> | `@OneToMany(mappedBy = "comuna")`, lado inverso |
| usuarios | List\<Usuario\> | `@OneToMany(mappedBy = "comuna")`, lado inverso |

**Restricción única:** `nombre`
**Relaciones:** `N Comuna ─── 1 Region` · `1 Comuna ─── N Direccion` · `1 Comuna ─── N Usuario`

---

## 4. Catálogo

### Marca

| Propiedad | Tipo | Restricciones |
|---|---|---|
| idMarca | Long | PK (id + nombre de la entidad) |
| nombre | String | Único, no nulo |
| activo | Boolean | Por defecto `true` |
| productos | List\<Producto\> | `@OneToMany(mappedBy = "marca")`, lado inverso |

**Relaciones:** `1 Marca ─── N Producto`

### Categoria

| Propiedad | Tipo | Restricciones |
|---|---|---|
| idCategoria | Long | PK (id + nombre de la entidad) |
| nombre | String | No nulo |
| slug | String | Único, no nulo |
| activo | Boolean | Por defecto `true` |
| imagenUrl | String | Opcional; URL segura de la imagen de portada en Cloudinary, longitud máxima 500 |
| imagenIdPublico | String | Opcional, único; identificador de Cloudinary para reemplazar o eliminar la imagen |
| padre | Categoria | `@ManyToOne` opcional (autorreferencia, `padre_id`) |
| productos | Set\<Producto\> | `@ManyToMany(mappedBy = "categorias")`, lado inverso |

**Relaciones:**
- `N Categoria ─── 1 Categoria (padre)`
- `N Categoria ─── M Producto` (lado inverso)

### Producto

| Propiedad | Tipo | Restricciones |
|---|---|---|
| idProducto | Long | PK (id + nombre de la entidad) |
| sku | String | Único, no nulo |
| nombre | String | No nulo |
| descripcion | String (`TEXT`) | Opcional |
| precio | Long | No nulo |
| stock | Integer | No nulo (stock **total** del producto) |
| stockReservado | Integer | No nulo, por defecto `0`. Parte del stock comprometida por pedidos creados y aún no entregados ni cancelados |
| activo | Boolean | Por defecto `true` |
| version | Long | `@Version`, no nulo, por defecto `0` (bloqueo optimista) |
| marca | Marca | `@ManyToOne`, no nulo (`id_marca`) |
| categorias | Set\<Categoria\> | `@ManyToMany`, lado propietario |
| imagenes | List\<ImagenProducto\> | `@OneToMany(mappedBy = "producto")`, cascade ALL + orphanRemoval |

**Relaciones:**
- `N Producto ─── 1 Marca`
- `N Producto ─── M Categoria`
- `1 Producto ─── N ImagenProducto`
- `1 Producto ─── N ItemCarrito`
- `1 Producto ─── N DetallePedido`

**Mapeo de la relación N:M:**

```java
// En Producto (lado propietario)
@ManyToMany(fetch = FetchType.LAZY)
@JoinTable(
    name = "producto_categoria",
    joinColumns = @JoinColumn(name = "id_producto"),
    inverseJoinColumns = @JoinColumn(name = "id_categoria")
)
private Set<Categoria> categorias = new HashSet<>();

// En Categoria (lado inverso)
@ManyToMany(mappedBy = "categorias", fetch = FetchType.LAZY)
private Set<Producto> productos = new HashSet<>();
```

**Tabla intermedia `producto_categoria`:**

| Columna | Tipo | Restricciones |
|---|---|---|
| idProducto | BIGINT | FK → producto, parte de la PK |
| idCategoria | BIGINT | FK → categoria, parte de la PK |

PK compuesta (`id_producto`, `id_categoria`).

### ImagenProducto

| Propiedad | Tipo | Restricciones |
|---|---|---|
| idImagenProducto | Long | PK |
| idPublico | String | **Único**, no nulo. Identificador del recurso en Cloudinary, necesario para eliminar la imagen |
| url | String | No nulo, longitud 500 |
| textoAlternativo | String | Opcional, longitud 255 |
| orden | Integer | No nulo. Posición en la galería |
| principal | Boolean | No nulo, por defecto `false` |
| producto | Producto | `@ManyToOne`, no nulo (`producto_id`) |

**Relaciones:** `N ImagenProducto ─── 1 Producto`

---

## 5. Identidad

### RolUsuario

| Propiedad | Tipo | Restricciones |
|---|---|---|
| idRolUsuario | Long | PK |
| nombre | String | Único, no nulo |
| usuarios | List\<Usuario\> | `@OneToMany(mappedBy = "rolUsuario")`, lado inverso |

**Relaciones:** `1 RolUsuario ─── N Usuario`

### Usuario

| Propiedad | Tipo | Restricciones |
|---|---|---|
| idUsuario | Long | PK |
| nombres | String | Opcional, longitud 25 |
| aPaterno | String | Opcional, longitud 25 |
| aMaterno | String | Opcional, longitud 25 |
| rut | Long | Long, opcional, longitud 8 |
| dv | String | Opcional, longitud 1 |
| fechaNacimiento | LocalDate | Opcional |
| correo | String | Único, no nulo (`@Email`, `@NotBlank`) |
| password | String | No nulo |
| telefono | String | Opcional |
| activo | Boolean | Por defecto `true` |
| rolUsuario | RolUsuario | `@ManyToOne`, no nulo (`id_rol_usuario`) |
| comuna | Comuna | `@ManyToOne` opcional (`id_comuna`) |

**Colecciones (lado inverso):**
- `direcciones` · `@OneToMany(mappedBy = "usuario")`
- `carrito` · `@OneToOne(mappedBy = "usuario")`
- `pedidos` · `@OneToMany(mappedBy = "usuario")`

**Relaciones:**
- `N Usuario ─── 1 RolUsuario`
- `1 Usuario ─── N Direccion`
- `1 Usuario ─── 1 Carrito`
- `1 Usuario ─── N Pedido`
- `N Usuario ─── 1 Comuna` (opcional)

### Direccion

| Propiedad | Tipo | Restricciones |
|---|---|---|
| idDireccion | Long | PK |
| nombreReceptor | String | No nulo |
| telefonoReceptor | String | No nulo |
| calle | String | No nulo |
| numero | String | No nulo |
| complemento | String | Opcional |
| predeterminada | Boolean | Por defecto `false` |
| activo | Boolean | Por defecto `true` |
| usuario | Usuario | `@ManyToOne`, no nulo (`usuario_id`) |
| comuna | Comuna | `@ManyToOne`, no nulo (`comuna_id`) |

**Relaciones:** `N Direccion ─── 1 Usuario` · `N Direccion ─── 1 Comuna`

---

## 6. Compra

### Carrito

| Propiedad | Tipo | Restricciones |
|---|---|---|
| idCarrito | Long | PK |
| usuario | Usuario | `@OneToOne`, único, no nulo (`usuario_id`) |
| items | List\<ItemCarrito\> | `@OneToMany(mappedBy = "carrito")`, cascade ALL + orphanRemoval |

**Relaciones:** `1 Carrito ─── 1 Usuario` · `1 Carrito ─── N ItemCarrito`

### ItemCarrito

| Propiedad | Tipo | Restricciones |
|---|---|---|
| idItemCarrito | Long | PK |
| cantidad | Integer | No nulo |
| carrito | Carrito | `@ManyToOne`, no nulo (`id_carrito`) |
| producto | Producto | `@ManyToOne`, no nulo (`id_producto`) |

**Restricción única:** `uk_item_carrito_carrito_producto` (`id_carrito`, `id_producto`), declarada en `@Table(uniqueConstraints = ...)`
**Nota de modelado:** no tiene columna de precio; el carrito no es un registro de venta.

### Pedido

| Propiedad | Tipo | Restricciones |
|---|---|---|
| idPedido | Long | PK |
| numeroPedido | String | Único, no nulo |
| estado | EstadoPedido | `@Enumerated(STRING)`, no nulo, longitud 20 |
| tipoEntrega | TipoEntrega | `@Enumerated(STRING)`, no nulo, longitud 20 (`RETIRA_TIENDA` / `DESPACHO`) |
| total | Long | No nulo |
| creadoEn | Instant | No nulo, asignado al crear el pedido |
| nombreReceptor | String | Solo para `DESPACHO` (copia de datos de envío) |
| telefonoReceptor | String | Solo para `DESPACHO` (copia) |
| calle | String | Solo para `DESPACHO` (copia) |
| numero | String | Solo para `DESPACHO` (copia) |
| complemento | String | Opcional (copia) |
| comunaNombre | String | Solo para `DESPACHO` (copia como texto) |
| regionNombre | String | Solo para `DESPACHO` (copia como texto) |
| usuario | Usuario | `@ManyToOne`, no nulo (`usuario_id`) |
| detalles | List\<DetallePedido\> | `@OneToMany(mappedBy = "pedido")`, cascade ALL |
| historialEstados | List\<CambioEstadoPedido\> | `@OneToMany(mappedBy = "pedido")`, cascade ALL |

**Relaciones:** `N Pedido ─── 1 Usuario` · `1 Pedido ─── N DetallePedido` · `1 Pedido ─── N CambioEstadoPedido`
**Nota de modelado:** no tiene FK a `Direccion`; los datos de envío viven como columnas propias del pedido y solo se copian cuando `tipoEntrega = DESPACHO`. Con `RETIRA_TIENDA` no se guardan datos de dirección. Al crear el pedido se valida stock suficiente y la cantidad queda **reservada** (`stockReservado` del producto); el pago se confirma posteriormente (flujo simulado, sin pasarela) poniendo el pedido en `CONFIRMADO`, operación que realiza admin o vendedor. Al entregar el pedido se descuenta el total físico y se libera la reserva; al cancelarlo se libera la reserva sin tocar el total.
Los pedidos se crean desde el carrito; sus productos, cantidades y precios se copian a los detalles. No se editan ni eliminan.

### CambioEstadoPedido

| Propiedad | Tipo | Restricciones |
|---|---|---|
| idCambioEstadoPedido | Long | PK |
| estadoAnterior | EstadoPedido | Opcional para el estado inicial |
| estadoNuevo | EstadoPedido | No nulo |
| cambiadoEn | Instant | No nulo |
| pedido | Pedido | `@ManyToOne`, no nulo (`pedido_id`) |
| usuario | Usuario | `@ManyToOne`, no nulo (`usuario_id`); actor del cambio |

Las transiciones permitidas son `PENDIENTE → CONFIRMADO → ENVIADO → ENTREGADO` y `PENDIENTE → CANCELADO`. Pasar de `PENDIENTE` a `CONFIRMADO` equivale a aceptar el pago (flujo simulado: el cliente solo lo simula en su interfaz y lo registra admin o vendedor desde el panel). Al cancelar un pedido pendiente se libera la reserva de stock; al pasar a `ENTREGADO` se descuenta el total físico y se libera la reserva.

### DetallePedido

| Propiedad | Tipo | Restricciones |
|---|---|---|
| idDetallePedido | Long | PK |
| nombreProducto | String | No nulo (copia) |
| skuProducto | String | No nulo (copia) |
| precioUnitario | Long | No nulo (copia) |
| cantidad | Integer | No nulo |
| subtotal | Long | No nulo |
| pedido | Pedido | `@ManyToOne`, no nulo (`pedido_id`) |
| producto | Producto | `@ManyToOne`, no nulo (`producto_id`) |

**Relaciones:** `N DetallePedido ─── 1 Pedido` · `N DetallePedido ─── 1 Producto`

---

## 7. Enum

### EstadoPedido

Valores: `PENDIENTE`, `CONFIRMADO`, `ENVIADO`, `ENTREGADO`, `CANCELADO`

Persistido en `pedido.estado` con `@Enumerated(EnumType.STRING)`.

### TipoEntrega

Valores: `RETIRA_TIENDA`, `DESPACHO`

Persistido en `pedido.tipo_entrega` con `@Enumerated(EnumType.STRING)`. Indica si el
pedido se retira en tienda o se despacha a domicilio; solo el despacho requiere
datos de envío.

### RolSistema

| Constante | nombre |
|---|---|
| `ADMIN` | `Administrador` |
| `VENDEDOR` | `Vendedor` |
| `CLIENTE` | `Cliente` |

No se persiste en base de datos: `RolUsuario.nombre` es un `String` libre y este enum
solo centraliza los nombres con los que se buscan o siembran los roles del sistema.