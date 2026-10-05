# Despliegue seguro del backend

Lista de acciones para publicar el backend en un servidor manteniendo la
autenticación JWT y los permisos definidos por Spring Security. Los valores
concretos de dominio, proveedor, credenciales y topología deben definirse para
cada entorno; no se deben guardar secretos en este documento ni en el
repositorio.

## 1. Preparar el entorno de producción

- [ ] Elegir un servidor o plataforma con Docker Engine o un orquestador de
  contenedores compatible. La imagen de la aplicación debe usar la versión de
  Java declarada en `pom.xml` (actualmente Java 26); el servidor no necesita
  tener Java instalado.
- [ ] Elegir un registro de contenedores privado o con acceso restringido para
  publicar las imágenes de producción.
- [ ] Crear una instancia MySQL 8 o superior, preferiblemente en una red
  privada y accesible solo desde el backend.
- [ ] Crear una base de datos de producción y un usuario exclusivo para la
  aplicación. Conceder únicamente los privilegios que requieran la aplicación
  y Flyway sobre esa base; no usar una cuenta administrativa global de MySQL.
- [ ] Configurar almacenamiento persistente y respaldos automáticos de la base
  de datos. Definir y probar un procedimiento de restauración.
- [ ] Mantener MySQL como servicio aparte del contenedor del backend, usando
  una base de datos administrada o una instancia con almacenamiento persistente.
  No depender del almacenamiento efímero del contenedor para conservar datos.
- [ ] Elegir un dominio para la API y configurar sus registros DNS hacia el
  punto de entrada público del servicio.

## 2. Configurar secretos y propiedades

- [ ] Configurar en el gestor de secretos o en las variables seguras del
  proveedor las variables requeridas por `application.yaml`:

  | Variable | Uso |
  |---|---|
  | `DB_URL` | URL JDBC de MySQL de producción; no debe apuntar a `localhost` salvo que MySQL esté en el mismo servidor. |
  | `DB_USERNAME` | Usuario de base de datos dedicado a la aplicación. |
  | `DB_PASSWORD` | Contraseña de ese usuario, guardada solo en el gestor seguro. |
  | `JWT_SECRET` | Clave de firma JWT en Base64, que al decodificarse tenga al menos 32 bytes. |
  | `JWT_EXPIRATION` | Duración de los JWT en milisegundos, definida de acuerdo con la política de sesión. |
  | `CLOUDINARY_URL` | URL de conexión de Cloudinary, guardada como secreto. |

- [ ] Generar una clave JWT fuerte y aleatoria para producción; no reutilizar la
  clave de desarrollo ni incluirla en Git, imágenes, argumentos de build,
  comandos registrados o documentación.
- [ ] Restringir el acceso a los secretos a las personas y procesos que deban
  desplegar o ejecutar la aplicación. Documentar el procedimiento para
  rotarlos; cambiar la clave JWT invalida los tokens firmados con la clave
  anterior.
- [ ] No copiar `.env` a la imagen ni tratar `.env.example` como archivo de
  producción. Inyectar las variables directamente al contenedor desde la
  configuración segura de la plataforma.
- [ ] No activar el perfil `dev`: agrega datos de demostración mediante una
  migración Flyway.

## 3. Revisar la aplicación antes de publicarla

- [ ] Desactivar `spring.jpa.show-sql` y el formateo de SQL en la configuración
  de producción para evitar ruido y exposición innecesaria en los logs.
- [ ] Confirmar que Flyway aplica las migraciones de `db/migration` al iniciar
  y que la cuenta de base de datos tiene los permisos necesarios. Revisar y
  respaldar la base antes de desplegar cambios de esquema.
- [ ] Revisar las reglas actuales de `SecurityConfig` frente a
  [3-endpoints.md](./3-endpoints.md). Mantener públicas solo las operaciones
  previstas, como el login, el registro y las lecturas públicas del catálogo;
  las demás deben conservar sus requisitos de JWT, propiedad del recurso y
  rol.
- [ ] Verificar el flujo de creación del primer administrador. El registro
  público crea una cuenta con rol `cliente`, no un administrador. No ejecutar
  `src/main/resources/db/creacion_admin.sql` tal como está en el repositorio:
  contiene una credencial fija y operaciones destructivas sobre un usuario.
  Definir un procedimiento controlado que guarde la contraseña del
  administrador como hash BCrypt y no exponga credenciales en SQL, terminal o
  logs.
- [ ] Si el frontend se sirve desde otro origen y llama a la API desde un
  navegador, configurar CORS para permitir únicamente los orígenes y métodos
  necesarios. No permitir cualquier origen por conveniencia. Si no se usa un
  frontend en otro origen, no es necesario habilitar CORS de forma global.
- [ ] Revisar qué endpoints de administración y monitorización se exponen.
  No publicar información de entorno, detalles de errores, métricas ni otros
  endpoints de gestión sin control de acceso explícito.

## 4. Construir y desplegar con Docker

- [ ] Ejecutar las pruebas automatizadas antes de crear la imagen:

  ```bash
  ./mvnw clean verify
  ```

- [ ] Crear y mantener un `Dockerfile` para compilar y ejecutar la aplicación.
  Preferir una construcción multi-stage para no incluir herramientas de
  compilación en la imagen final, usar una imagen base oficial y mantenerla
  actualizada.
- [ ] Ejecutar el proceso de la aplicación con un usuario no privilegiado
  dentro del contenedor y mantener la imagen lo más reducida posible.
- [ ] Añadir un `.dockerignore` para excluir `.env`, archivos de Git, artefactos
  locales, archivos temporales y otros elementos innecesarios. Revisar el
  contexto de build antes de construir.
- [ ] No copiar secretos a la imagen ni definirlos en instrucciones `ENV` o
  `ARG` del `Dockerfile`. Inyectarlos solo en tiempo de ejecución desde el
  gestor de secretos o el mecanismo seguro del proveedor.
- [ ] Construir la imagen a partir del código revisado, etiquetarla con una
  versión inmutable o el identificador del commit y publicarla en el registro
  configurado. Evitar usar `latest` como única referencia de producción.
- [ ] Desplegar esa versión de imagen con reinicio automático ante fallos y
  política de arranque tras reinicios del host. Configurar el puerto interno
  `8080` (o el valor establecido por el entorno) y las variables necesarias.
- [ ] Conectar el contenedor a MySQL mediante una red privada o una conexión
  cifrada admitida por el proveedor. No usar `localhost` en `DB_URL` si MySQL
  está fuera del contenedor del backend.
- [ ] Comprobar durante el arranque que la aplicación conecta a MySQL, valida
  la configuración JWT, conecta con Cloudinary y termina correctamente las
  migraciones Flyway.
- [ ] Mantener el contenedor del backend en una red no pública detrás del proxy
  inverso o balanceador. En el firewall, publicar solo los puertos requeridos
  para HTTPS; no publicar el puerto de MySQL.
- [ ] Conservar la imagen de la versión anterior para poder volver a
  desplegarla. Antes de revertir, comprobar la compatibilidad de esa versión
  con las migraciones de base de datos ya aplicadas.

## 5. Publicar tráfico de forma segura

- [ ] Colocar un proxy inverso o balanceador delante del contenedor y
  configurar el dominio para que reenvíe solicitudes al puerto interno de la
  aplicación. No publicar ese puerto directamente a Internet.
- [ ] Habilitar HTTPS con un certificado válido y redirigir HTTP a HTTPS.
  Mantener TLS actualizado y permitir únicamente versiones y cifrados
  compatibles con la política del proveedor.
- [ ] Configurar correctamente los encabezados de proxy y los límites de
  solicitud. El backend limita las cargas multipart a 5 MB; el proxy no debe
  permitir un tamaño incompatible con ese límite.
- [ ] Usar en el frontend la URL HTTPS pública de la API. Para las operaciones
  protegidas, enviar el JWT en el encabezado `Authorization` con el esquema
  `Bearer`; nunca incluirlo en la URL ni en logs del cliente.
- [ ] No desactivar CSRF ni ampliar permisos como solución a errores de
  conectividad: el backend usa autenticación sin estado por JWT y las
  autorizaciones se aplican en Spring Security.

## 6. Verificar después del despliegue

- [ ] Comprobar que el dominio resuelve, que el certificado HTTPS es válido y
  que la API responde a través del proxy.
- [ ] Probar las rutas públicas previstas sin JWT y confirmar que una ruta
  protegida sin token recibe `401`.
- [ ] Probar una ruta protegida con un JWT válido y confirmar que un usuario
  sin el rol requerido recibe `403`. Verificar también que un usuario no puede
  acceder a recursos propios de otro usuario.
- [ ] Probar login, registro, las operaciones principales del catálogo,
  pedidos y carga de imágenes según los flujos que vaya a usar el cliente.
- [ ] Revisar logs de arranque y errores sin registrar JWT, contraseñas,
  secretos ni datos personales. Configurar alertas y monitorización sin
  exponer información sensible públicamente.
- [ ] Para cada actualización, repetir pruebas, construir y publicar una nueva
  imagen versionada, actualizar el servicio para usarla y verificar el arranque
  y las operaciones principales. Mantener disponible la imagen anterior para
  una reversión, teniendo en cuenta que revertir el contenedor no revierte las
  migraciones de Flyway.
- [ ] Confirmar que los respaldos se ejecutan y probar periódicamente la
  restauración en un entorno separado.
