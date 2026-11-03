# Autenticación con Google

> Estado: implementado. Este documento describe el flujo y sus requisitos.

## Comportamiento implementado

El inicio de sesión con Google complementa el registro y login con correo y
contraseña. Google verifica la identidad; el backend resuelve el usuario, sus
permisos y el JWT que consume la API.

Decisiones para orientar la implementación:

- El frontend obtiene un **ID token de Google** y lo envía al backend. El
  backend no recibe ni confía en un perfil de usuario enviado por el cliente.
- Se vincula una cuenta existente solo cuando el ID token es válido y su correo
  está verificado por Google.
- Si no existe una cuenta con ese correo, se crea una cuenta local con rol
  `cliente` y perfil incompleto. El usuario debe completar los datos requeridos
  antes de poder comprar.
- El JWT propio de la API sigue siendo el mecanismo de acceso a los endpoints
  protegidos. El ID token de Google no se usa como JWT de la API.

## Flujo propuesto

1. El frontend inicia Google Identity Services y obtiene un ID token.
2. Envía el token a `POST /api/usuarios/oauth/google`.
3. El backend valida firma, emisor, audiencia configurada, expiración y los
   claims requeridos; rechaza tokens sin `sub`, correo o `email_verified=true`.
4. El backend busca la identidad externa por proveedor y `sub`. Si no existe,
   busca una cuenta local por el correo verificado y vincula esa identidad; si
   tampoco existe, crea una cuenta cliente incompleta.
5. Se rechazan cuentas locales inactivas. Para cuentas válidas se carga el
   principal local y se emite el JWT propio mediante el servicio JWT existente.
6. La respuesta mantiene el contrato de autenticación de la API
   (`UsuarioDTOLoginResponse`), devolviendo el usuario local y el JWT propio.
   El frontend envía ese JWT en el encabezado `Authorization` en las
   siguientes solicitudes.
7. El usuario nuevo puede autenticarse y completar el perfil. La API debe
   impedir crear pedidos hasta que estén completos los datos que hoy exige el
   registro.

## Componentes

### 1. Contrato y configuración

- `POST /api/usuarios/oauth/google` recibe únicamente el ID token.
- Errores de token inválido, cuenta inactiva y falta de configuración usan
  respuestas genéricas; no revelan si el correo está registrado.
- El backend valida la audiencia configurada con `GOOGLE_CLIENT_ID`. No
  requiere `client_secret`.

### 2. Verificación de identidad

- La validación criptográfica usa las claves públicas de Google y comprueba
  emisor, audiencia, expiración y los claims de identidad requeridos.
- Usar exclusivamente los claims verificados. Normalizar el correo para
  localizar una cuenta sin cambiar a escondidas el correo ya persistido.
- No registrar el ID token, JWT, credenciales ni datos personales. No confiar
  en nombre, correo verificado ni `sub` enviados por separado en el cuerpo.

### 3. Persistencia y resolución de cuentas

- Una entidad de identidad externa vincula `Usuario` con proveedor (`google`)
  y `sub` de Google. La migración Flyway impone unicidad para proveedor/`sub`.
- Mantener `correo` como identificador de contacto local; el identificador
  estable de Google es `sub`, no el correo.
- Resolver la identidad existente por proveedor/`sub`. Si aún no está
  vinculada, buscar por correo verificado y vincularla al usuario local
  existente. Si no hay coincidencia, crear un usuario con rol `cliente` y
  guardar la identidad externa.
- Hacer atómica la creación/vinculación para que solicitudes concurrentes no
  creen usuarios duplicados. Tratar las violaciones de unicidad como conflicto
  de identidad y devolver un error seguro.
- Permitir que una cuenta solo Google no tenga contraseña local. Alinear la
  columna y entidad `Usuario`, y asegurar que el login con contraseña rechace
  estas cuentas de forma segura sin afectar las cuentas existentes.
- Copiar únicamente datos de perfil disponibles y válidos de Google. No
  inferir RUT, fecha de nacimiento, comuna ni otros datos que Google no
  garantiza.

### 4. Onboarding y permisos

- El perfil se completa con `PUT /api/usuarios/perfil`, sin exigir una
  contraseña local para cuentas OAuth.
- La respuesta del perfil admite comuna ausente hasta completar el onboarding.
- `perfilCompleto` refleja que estén presentes nombres, apellido paterno, RUT,
  dígito verificador, fecha de nacimiento, correo y comuna.
- Proteger la creación de pedidos para que un perfil incompleto no pueda
  comprar; mantener disponibles el catálogo, autenticación y operaciones
  necesarias para completar el perfil.
- Asignar siempre el rol `cliente` al alta pública. Los atributos y roles
  devueltos por Google nunca determinan permisos locales.

### 5. Seguridad, documentación y despliegue

- `/api/usuarios/oauth/google` es público; las demás rutas mantienen sus reglas
  y siguen usando JWT stateless.
- Las especificaciones, README y guía de despliegue describen el endpoint,
  respuestas y configuración OAuth.
- En despliegues se exige HTTPS y se restringen los orígenes autorizados del
  frontend. El ID token solo se usa durante el intercambio.

## Pruebas y criterios de aceptación

- Acepta un ID token válido de la audiencia configurada y devuelve un JWT
  propio utilizable en endpoints protegidos.
- Rechaza tokens con firma inválida, emisor o audiencia incorrectos,
  expirados, sin `sub`/correo o con correo no verificado.
- Para un `sub` ya vinculado, siempre inicia sesión en el mismo usuario aunque
  cambie el correo informado por Google; no crea duplicados.
- Vincula una cuenta existente por correo verificado, conserva sus datos y rol,
  y no eleva permisos.
- Crea una cuenta nueva con rol `cliente`, permite completar su perfil y bloquea
  la compra hasta completar los campos requeridos.
- Rechaza cuentas inactivas y evita duplicados ante solicitudes concurrentes.
- Verifica que el login tradicional y sus respuestas sigan funcionando; una
  cuenta solo Google no puede iniciar sesión con una contraseña local.
- Confirma con pruebas de seguridad que el endpoint OAuth es público y que las
  rutas protegidas siguen requiriendo el JWT propio.
- Comprueba que errores y logs no exponen tokens ni datos personales.

## Configuración requerida

- Variable de entorno `GOOGLE_CLIENT_ID`, leída por la propiedad
  `google.oauth.client-id`. Es el Client ID OAuth del frontend y la audiencia
  contra la que se validan los ID tokens. No es una credencial secreta.
- Si no está configurada, el backend puede iniciar, pero el endpoint OAuth
  responde `503 GOOGLE_NO_CONFIGURADO`; el login tradicional sigue disponible.
- En Google Cloud se deben autorizar únicamente los orígenes HTTPS del frontend
  correspondiente al entorno. No se necesita un `client_secret` en el backend.