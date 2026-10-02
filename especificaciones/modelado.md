# Modelado de datos: Backend Ecommerce (Nivel N1)

**Stack:** Spring Boot · JPA/Hibernate · MySQL

---

## 1. Convenciones de modelado

| Tema | Decisión |
|---|---|
| Clave primaria | `Long`, `GenerationType.IDENTITY` |
| Auditoría | `@MappedSuperclass EntidadBase` con `id`, `creadoEn`, `actualizadoEn` |
| Dinero | `Long` en pesos chilenos (CLP no tiene decimales) |
| Fetch | Todas las relaciones `LAZY` |
| Colecciones `@ManyToMany` | `Set<>` |
| Enums | `@Enumerated(EnumType.STRING)` |
| Nombres | Entidades y propiedades en español, tablas en `snake_case` |

---

## 2. Diagrama de relaciones

```
Region 1─N Comuna 1─N Direccion N─1 Usuario N─1 RolUsuario
                                      │
                          ┌───────────┴───────────┐
                       1─1 Carrito             1─N Pedido 1─N DetallePedido
                          │ 1─N                                  │ N─1
                     ItemCarrito N─1 ──────► Producto ◄──────────┘
                                              │   │   │
                                  N─1 Marca ──┘   │   └── 1─N ImagenProducto
                                                  │
                                         N─M Categoria ──(padre)──► Categoria
                                  (tabla intermedia: producto_categoria)
```

### Resumen de cardinalidades

| Origen | Cardinalidad | Destino | Notas |
|---|---|---|---|
| Region | 1 : N | Comuna | |
| Comuna | 1 : N | Direccion | |
| RolUsuario | 1 : N | Usuario | |
| Usuario | 1 : N | Direccion | |
| Usuario | 1 : 1 | Carrito | |
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
| idRegion | Long | PK |
| nombre | String | Único, no nulo |

**Relaciones:** `Region 1 ─── N Comuna`

### Comuna

| Propiedad | Tipo | Restricciones |
|---|---|---|
| idComuna | Long | PK |
| nombre | String | No nulo |
| region | Region | `@ManyToOne`, no nulo |

**Restricción única:** (`nombre`, `region_id`)
**Relaciones:** `N Comuna ─── 1 Region` · `1 Comuna ─── N Direccion`

---

## 4. Catálogo

### Marca

| Propiedad | Tipo | Restricciones |
|---|---|---|
| idMarca | Long | PK |
| nombre | String | Único, no nulo |
| activo | Boolean | Por defecto `true` |

**Relaciones:** `1 Marca ─── N Producto`

### Categoria

| Propiedad | Tipo | Restricciones |
|---|---|---|
| idCategoria | Long | PK |
| nombre | String | No nulo |
| slug | String | Único, no nulo |
| activo | Boolean | Por defecto `true` |
| padre | Categoria | `@ManyToOne` opcional (autorreferencia) |
| productos | Set\<Producto\> | `@ManyToMany(mappedBy = "categorias")`, lado inverso |

**Relaciones:**
- `N Categoria ─── 1 Categoria (padre)`
- `N Categoria ─── M Producto` (lado inverso)

### Producto

| Propiedad | Tipo | Restricciones |
|---|---|---|
| idProducto | Long | PK |
| sku | String | Único, no nulo |
| nombre | String | No nulo |
| descripcion | String (`TEXT`) | Opcional |
| precio | Long | No nulo |
| stock | Integer | No nulo |
| activo | Boolean | Por defecto `true` |
| version | Long | `@Version` |
| marca | Marca | `@ManyToOne`, no nulo |
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
    joinColumns = @JoinColumn(name = "producto_id"),
    inverseJoinColumns = @JoinColumn(name = "categoria_id")
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

PK compuesta (`producto_id`, `categoria_id`).

### ImagenProducto

| Propiedad | Tipo | Restricciones |
|---|---|---|
| idPropiedad | Long | PK |
| url | String | No nulo |
| textoAlternativo | String | Opcional |
| orden | Integer | Posición en la galería |
| principal | Boolean | Por defecto `false` |
| producto | Producto | `@ManyToOne`, no nulo |

**Relaciones:** `N ImagenProducto ─── 1 Producto`

---

## 5. Identidad

### RolUsuario

| Propiedad | Tipo | Restricciones |
|---|---|---|
| idRolUsuario | Long | PK |
| nombre | String | Único, no nulo |

**Relaciones:** `1 RolUsuario ─── N Usuario`

### Usuario

| Propiedad | Tipo | Restricciones |
|---|---|---|
| idUsuario | Long | PK |
| nombre | String | No nulo |
| apellido | String | No nulo |
| email | String | Único, no nulo |
| passwordHash | String | No nulo |
| telefono | String | Opcional |
| activo | Boolean | Por defecto `true` |
| rol | RolUsuario | `@ManyToOne`, no nulo |

**Relaciones:**
- `N Usuario ─── 1 RolUsuario`
- `1 Usuario ─── N Direccion`
- `1 Usuario ─── 1 Carrito`
- `1 Usuario ─── N Pedido`

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
| usuario | Usuario | `@ManyToOne`, no nulo |
| comuna | Comuna | `@ManyToOne`, no nulo |

**Relaciones:** `N Direccion ─── 1 Usuario` · `N Direccion ─── 1 Comuna`

---

## 6. Compra

### Carrito

| Propiedad | Tipo | Restricciones |
|---|---|---|
| idCompra | Long | PK |
| usuario | Usuario | `@OneToOne`, único, no nulo |
| items | List\<ItemCarrito\> | `@OneToMany(mappedBy = "carrito")`, cascade ALL + orphanRemoval |

**Relaciones:** `1 Carrito ─── 1 Usuario` · `1 Carrito ─── N ItemCarrito`

### ItemCarrito

| Propiedad | Tipo | Restricciones |
|---|---|---|
| idItemCarrito | Long | PK |
| cantidad | Integer | No nulo |
| carrito | Carrito | `@ManyToOne`, no nulo |
| producto | Producto | `@ManyToOne`, no nulo |

**Restricción única:** (`carrito_id`, `producto_id`)
**Nota de modelado:** no tiene columna de precio; el carrito no es un registro de venta.

### Pedido

| Propiedad | Tipo | Restricciones |
|---|---|---|
| idPedido | Long | PK |
| numeroPedido | String | Único, no nulo |
| estado | EstadoPedido | `@Enumerated(STRING)`, no nulo |
| total | Long | No nulo |
| nombreReceptor | String | No nulo (copia de datos de envío) |
| telefonoReceptor | String | No nulo (copia) |
| calle | String | No nulo (copia) |
| numero | String | No nulo (copia) |
| complemento | String | Opcional (copia) |
| comunaNombre | String | No nulo (copia como texto) |
| regionNombre | String | No nulo (copia como texto) |
| usuario | Usuario | `@ManyToOne`, no nulo |
| detalles | List\<DetallePedido\> | `@OneToMany(mappedBy = "pedido")`, cascade ALL |

**Relaciones:** `N Pedido ─── 1 Usuario` · `1 Pedido ─── N DetallePedido`
**Nota de modelado:** no tiene FK a `Direccion`; los datos de envío viven como columnas propias del pedido.

### DetallePedido

| Propiedad | Tipo | Restricciones |
|---|---|---|
| idDetallePedido | Long | PK |
| nombreProducto | String | No nulo (copia) |
| skuProducto | String | No nulo (copia) |
| precioUnitario | Long | No nulo (copia) |
| cantidad | Integer | No nulo |
| subtotal | Long | No nulo |
| pedido | Pedido | `@ManyToOne`, no nulo |
| producto | Producto | `@ManyToOne`, no nulo |

**Relaciones:** `N DetallePedido ─── 1 Pedido` · `N DetallePedido ─── 1 Producto`

---

## 7. Enum

### EstadoPedido

Valores: `PENDIENTE`, `CONFIRMADO`, `ENVIADO`, `ENTREGADO`, `CANCELADO`