# Reglas de implementación

Convenciones del proyecto ESAM. Aplican a todo vertical slice nuevo y a las
correcciones sobre los existentes.

---

## 1. Capas y responsabilidades

Un feature es un *vertical slice*: `Controller → Service → Repository`, más un
`Mapper` para traducir entidad ↔ DTO.

| Capa | Habla con | Regla |
|---|---|---|
| Controller | Service, Mapper | **Nunca** toca el repositorio |
| Service | Repository, Mapper | Devuelve **entidades**, no DTOs de salida |
| Mapper | — | Sin acceso a base de datos |
| Repository | — | Solo `JpaRepository` y queries derivadas |

El service conoce el DTO de **entrada** (`guardar(ComunaDTORequest)`) pero
nunca el de salida. El mapeo entidad → DTO ocurre en el controller.

`Service` es una **clase concreta** con `@Service`. No hay interfaces ni
`*Impl` en este proyecto.

---

## 2. Enrutado

- `@RestController` + `@RequestMapping("/api/{plural}")`
- Plural en español y minúscula: `/api/comunas`, `/api/marcas`,
  `/api/regiones`, `/api/productos`, `/api/roles`, `/api/usuarios`
- CRUD de cinco verbos, nada más:

  | Verbo | Ruta |
  |---|---|
  | GET | `/{id}` |
  | GET | `` (colección) |
  | POST | `` |
  | PUT | `/{id}` |
  | DELETE | `/{id}` |

- Rutas auxiliares se cuelgan del recurso: `/{id}/comunas`, `/{id}/cascada`
- Nombres de método en **español**: `obtenerPorId`, `obtenerTodos`,
  `guardar`, `editar`, `borrar`

---

## 3. Códigos de estado

Todos los endpoints devuelven `ResponseEntity`.

| Caso | Código | Expresión |
|---|---|---|
| Recurso no encontrado en lectura | 404 | `notFound().build()` |
| Recurso no encontrado en escritura | 404 | excepción tipada |
| Colección vacía | 200 | `ok(lista vacía)` |
| Creado | 201 | `status(HttpStatus.CREATED).body(dto)` |
| Actualizado | 200 | `ok(dto)` |
| Referencia inválida en el cuerpo | 400 | excepción tipada |
| Violación de validación | 400 | `@Valid` → `ERROR_VALIDACION` |
| Entidad con dependientes | 409 | excepción tipada |
| Borrado correcto | 204 | `status(HttpStatus.NO_CONTENT).build()` |

Una colección vacía es `200` con `[]`, **nunca** `204`. `204` significa "sin
cuerpo", no "sin elementos".

Cuando un método no devuelve cuerpo, el tipo genérico es `ResponseEntity<Void>`.
Declarar `ResponseEntity<MarcaDTO>` y luego `build()` sin cuerpo es incorrecto.

---

## 4. "No encontrado"

Es la regla con más matiz del proyecto. Hay dos vías y se usan según el verbo:

**Lecturas (`GET`) — vía `null`:**

```java
public Comuna obtenerPorId(Long id) {
    return cRepo.findById(id).orElse(null);
}
```

El controller hace el null-check y responde 404 sin cuerpo.

**Escrituras (`PUT`, `DELETE`) — vía excepción tipada:**

```java
public Comuna editar(Long id, ComunaDTORequest request) {
    Comuna comuna = obtenerComuna(id);   // lanza si no existe
    ...
}
```

Cuando varios métodos necesitan la misma validación, se extrae un helper
privado en el service:

```java
private Comuna obtenerComuna(Long id) {
    Comuna comuna = obtenerPorId(id);
    if (comuna == null) {
        throw new ComunaNoEncontradaException("No existe una comuna con id " + id);
    }
    return comuna;
}
```

`editar`, `borrar` y `borrarEnCascada` lo reutilizan. Nunca se escribe tres
veces la misma validación.

**Referencia inválida ≠ recurso inexistente.** Si un `POST /api/usuarios` trae
`idRegion: 999`, el problema está en el cuerpo de la petición, no en la URL:
se responde **400**, no 404. Solo se usa 404 cuando el recurso *de la ruta* no
existe.

---

## 5. Validación

- Las reglas de validación viven en el DTO de **request**, con
  `jakarta.validation` (nunca `javax`)
- El controller activa con `@RequestBody @Valid XDTORequest`
- DTO de response: puede replicar las anotaciones, pero son **inerte** porque
  las respuestas nunca se validan
- Nombres de campo en camelCase, sin anotaciones Jackson. El proyecto no usa
  ninguna

Campos requeridos en un DTO de request:

```java
@NotBlank @Size(max = 25)  private String nombre;
@NotNull @Min(1)            private Long idRegion;
```

`@Min` **no** rechaza `null` (por spec de Bean Validation). Si el campo es
obligatorio hace falta `@NotNull` **además** de `@Min`, o el `null` se cuela
hasta el repositorio y revienta con 500.

**Excepción: `password` en Usuario.** No lleva `@NotBlank` porque el PUT tiene
la regla "si viene vacío, conservar el anterior". Anotarlo obligaría a mandar
la contraseña en cada actualización. Se valida en el service solo al crear.

La **existencia** de una FK no se valida con anotaciones: se resuelve en el
service con `findById(...).orElseThrow(...)` lanzando la excepción de 400
correspondiente. Patrón de referencia: `ProductoMapper.resolverMarca`.

---

## 6. Borrado y cascada

**Política: nunca se permite borrado en cascada de usuarios ni de comunas.**

Un `DELETE` sobre una entidad con dependientes responde **409**, nunca borra en
silencio:

```java
public void borrar(Long id) {
    Comuna comuna = obtenerComuna(id);

    long usuariosAsociados = uRepo.countByComunaIdComuna(id);

    if (usuariosAsociados > 0) {
        throw new ComunaConUsuariosException(
                "La comuna " + id + " tiene " + usuariosAsociados + " usuario(s) asociado(s)");
    }

    cRepo.delete(comuna);
}
```

Si además se quiere exponer un borrado en cascada, va en una **ruta aparte y
explícita** (`DELETE /{id}/cascada`), nunca como comportamiento implícito del
endpoint normal.

El conteo se hace con una query `countBy...` del repositorio, **no** con
`entidad.getColeccion()`. Ver sección 9.

Esta política se aplica también en el modelo: `Region.comunas` ya no declara
`cascade = CascadeType.ALL` ni `orphanRemoval`. Un guard en el service protege
una sola vía de entrada; si el mapping cascada, `rRepo.delete(region)` sigue
borrando las comunas. La garantía va en los dos lugares.

---

## 7. Mappers

- `@Component` a mano. **No** se usa MapStruct en este proyecto
- Responsabilidad única: traducir. Sin acceso a repositorios
- Nombres: `toDTO`, `toDTOList`, `toEntity`. Cuando hay varias formas de
  salida desde la misma entidad se cualifican (`toConComunasDTO`), porque Java
  no admite dos `toDTO(X)` con distinto tipo de retorno
- **Siempre inyectados** con `@Autowired`. Nunca `new XMapper()` en un
  controller ni en un service: duplica el bean y anula cualquier prueba
- El DTO de request → entidad va en el mapper (`toEntity`), no armado con
  `new` dentro del service. Fue exactamente ahí donde en Comuna se perdió el
  `nombre` al crear

---

## 8. Excepciones y códigos de error

Excepciones propias, todas `extends RuntimeException` con constructor
`(String message)`, en el package `exception`.

| Sufijo | Significado | HTTP |
|---|---|---|
| `NoEncontradaException` | el recurso de la ruta no existe | 404 |
| `ConProductosException` / `ConDependenciasException` / `ConUsuariosException` | tiene dependientes | 409 |
| `InvalidoException` | referencia inválida en el cuerpo | 400 |

Cada una se registra en `GlobalExceptionHandler` (`@RestControllerAdvice`),
que responde siempre el mismo envelope:

```java
public record ApiErrorResponse(
        Instant timestamp, int status, String error,
        String code, String message, String path) {}
```

`code` es un string en `SCREAMING_SNAKE_CASE`:
`COMUNA_NO_ENCONTRADA`, `MARCA_CON_PRODUCTOS`, `REGION_CON_DEPENDENCIAS`.

Registros del handler, en orden:

| Excepción | HTTP | `code` |
|---|---|---|
| `ProductoNoEncontradoException` | 404 | `PRODUCTO_NO_ENCONTRADO` |
| `ProductoInvalidoException` | 400 | `PRODUCTO_INVALIDO` |
| `ConflictoStockException` | 409 | `CONFLICTO_STOCK` |
| `ComunaNoEncontradaException` | 404 | `COMUNA_NO_ENCONTRADA` |
| `MarcaNoEncontradaException` | 404 | `MARCA_NO_ENCONTRADA` |
| `MarcaConProductosException` | 409 | `MARCA_CON_PRODUCTOS` |
| `RegionNoEncontradaException` | 404 | `REGION_NO_ENCONTRADA` |
| `RegionConDependenciasException` | 409 | `REGION_CON_DEPENDENCIAS` |
| `MethodArgumentNotValidException` | 400 | `ERROR_VALIDACION` |
| `MethodArgumentTypeMismatchException` | 400 | `PARAMETRO_INVALIDO` |
| `ErrorResponseException` | derivado | `ERROR_HTTP` |
| `Exception` | 500 | `ERROR_INTERNO` |

**Nunca** se lanza `RuntimeException` pelado: cae en el catch-all y produce un
500 sin información útil. Un 500 en este API significa casi siempre que falta
una excepción tipada.

---

## 9. Consideraciones técnicas

**`open-in-view` está activo sin declararlo.** `application.yaml` no lo
configura, así que Spring Boot lo habilita por defecto. Eso es lo que sostiene
`ProductoMapper.toDTO` al leer `producto.getMarca().getNombre()` (LAZY) fuera
de una transacción. No depender de él: para contar o validar dependientes se
usa una query `countBy...`, no `entidad.getColeccion()`. Si algún día se
desactiva, los mappers que tocan relaciones lazy hay que revisarlos.

**El DDL manda sobre la entidad.** Flyway está activo y `ddl-auto` está
comentado en `application.yaml`. Las anotaciones `@Column(nullable = false,
length = 25)` de las entidades no crean ni restringen nada: el esquema real
sale de `db/migration/*.sql`, donde los `VARCHAR` son 255. La validación de
largo y de obligatoriedad va en los DTO, no en la entidad.

**No hay tests de persistencia.** El proyecto no incluye H2 ni Testcontainers.
`GlobalExceptionHandlerTests` es el único test y es unitario. Cualquier cosa
que toque base de datos — guard de 409, conteo de dependientes, cascada,
resolución de FK — **no se puede verificar automáticamente** y hay que probarla
contra MySQL a mano.

**Relaciones sin cascade, salvo excepción.** El borrado en cascada está
prohibido (sección 6), así que los `@OneToMany` van sin `cascade` ni
`orphanRemoval`. Sin cascade, borrar un padre con hijos viola la FK y produce
un 500 opaco en vez del 409 previsto.

---

## 10. Pendientes

Ordenados por impacto en un cliente real.

| # | Pendiente | Impacto |
|---|---|---|
| 1 | `ComunaService.borrar` sin guard: borrar una comuna con usuarios viola `FK_usuario_comuna` y da 500 opaco en vez de 409 | Alto |
| 2 | Usuario se puede guardar con `idRegion` o `idComuna` nulos: persiste la fila con la FK en `NULL` y después revienta con NPE en el mapper. Ninguna de las seis capas lo impide hoy | Alto, corrompe datos |
| 3 | `UsuarioService.editar` nunca asigna `region` ni `comuna`, solo `rolUsuario`. Un PUT cambia el rol e ignora en silencio el resto | Alto |
| 4 | Contraseñas en texto plano, comparadas con `.equals()` en `confirmarLogin` | Alto, seguridad |
| 5 | `UsuarioService.confirmarLogin:69` hace NPE si el password almacenado es `null` | Medio |
| 6 | Sin H2 ni Testcontainers: ningún guard, conteo ni cascada se puede probar automáticamente | Medio, bloquea verificación |
| 7 | Migrar el slice `RolUsuario` a estas convenciones: es el último sin tocar (entidades crudas, sin DTO, sin `ResponseEntity`, sin validación) | Medio |
| 8 | `ProductoController.borrar` declara `ResponseEntity<Producto>` pero devuelve `204` sin cuerpo, con una variable local muerta | Bajo |
| 9 | 404 con cuerpo en unos endpoints y sin cuerpo en otros, dentro del mismo API. Decision consciente, no un descuido | Bajo |
| 10 | `open-in-view` implícito: los mappers de Producto y Region dependen de él sin declararlo | Bajo, frágil |
| 11 | El DDL dice `VARCHAR(255)` y las entidades `length = 25/50` | Bajo, Flyway manda |
| 12 | Migración `NOT NULL` para `usuario.id_region` / `id_comuna`. Requiere limpiar huérfanos en las bases ya migradas antes de aplicarla | Bajo |
| 13 | Commitear el renombre de paquete `dto.rolusuario` → `dto.rolUsuario`, que está en el working tree sin commitear | Bajo, higiene |

### Inconsistencias aceptadas a propósito

- `GET /{id}` inexistente devuelve 404 **sin cuerpo**; `PUT /{id}` inexistente
  devuelve 404 **con** envelope. Consecuencia de mantener el flujo por `null`
  en las lecturas (sección 4). Cerrarlo requiere unificar ambas vías.
- `RegionController` y `RolUsuarioController` exponen endpoints con rutas
  literales (`/comunas`, `/rol/{id}`) conviviendo con `/{id}`. Spring resuelve
  bien, pero conviene documentar cada ruta auxiliar.
