# OAuth de Google: guía para el frontend

## Resumen del flujo

1. El frontend inicia Google Identity Services con el Client ID web configurado
   para el entorno.
2. Google devuelve un **ID token** (credential) al frontend.
3. El frontend envía ese ID token a `POST /api/usuarios/oauth/google`.
4. El backend valida firma, emisor, audiencia, expiración y correo verificado.
   Si la autenticación es válida, responde con el JWT propio de la API.
5. El frontend usa el JWT de la API para las solicitudes protegidas. No debe
   usar el ID token de Google como token Bearer de la API.

El backend no recibe `client_secret`; nunca se debe incluir un secreto de
Google en el código del navegador.

## Configuración previa

- Solicitar al equipo backend/operaciones el Client ID configurado en el
  entorno (`GOOGLE_CLIENT_ID`). Este valor no es secreto.
- Configurar en Google Cloud un cliente OAuth de tipo aplicación web y registrar
  los orígenes autorizados del frontend para cada entorno. En producción usar
  HTTPS.
- Configurar el backend con el mismo Client ID. El backend lo lee de
  `GOOGLE_CLIENT_ID` mediante la propiedad `google.oauth.client-id`.
- Confirmar que el navegador puede llamar al dominio de la API y que CORS está
  permitido por la infraestructura para el origen del frontend, método `POST`
  y encabezados `Content-Type` y `Authorization`. La aplicación backend no
  declara actualmente una política CORS propia.

## Solicitud al backend

Endpoint público:

```http
POST /api/usuarios/oauth/google
Content-Type: application/json
```

Enviar exactamente el ID token obtenido de Google en `idToken`:

```json
{
  "idToken": "<credential devuelta por Google Identity Services>"
}
```

`idToken` debe ser una cadena no vacía de hasta 8192 caracteres. No enviar el
correo, nombre, `sub`, access token ni los datos del perfil por separado como
sustitutos del ID token; el backend solo confía en los claims que verifica
criptográficamente.

En pseudocódigo, el intercambio es:

```javascript
const response = await fetch(`${API_URL}/api/usuarios/oauth/google`, {
  method: "POST",
  headers: { "Content-Type": "application/json" },
  body: JSON.stringify({ idToken: googleCredential }),
});

const result = await response.json();
```

`googleCredential` debe ser la credencial ID token que entrega el callback de
Google Identity Services. No registrar ni enviar ese valor a analítica.

## Respuesta exitosa y sesión

Con `200 OK`, el backend devuelve el formato de login existente:

```json
{
  "loggin": true,
  "token": "<JWT propio de la API>",
  "tipoToken": null,
  "expiraEn": null,
  "usuario": {
    "idUsuario": 42,
    "id": 42,
    "nombres": "Nombre",
    "aPaterno": "Apellido",
    "correo": "usuario@ejemplo.cl",
    "rol": "cliente",
    "rolDetalle": {
      "idRolUsuario": 3,
      "nombre": "cliente"
    },
    "perfilCompleto": false,
    "comuna": null
  }
}
```

Los campos de perfil omitidos o nulos pueden variar según el contenido de la
cuenta. `tipoToken` y `expiraEn` actualmente quedan en `null`; el token que debe
usar el frontend está en `token`.

Para solicitudes autenticadas, enviar:

```http
Authorization: Bearer <JWT propio de la API>
```

El JWT identifica al usuario local y aplica sus permisos. Al cerrar sesión,
eliminar la copia local del JWT y cualquier estado de usuario asociado. No
guardar el ID token de Google como sesión de la aplicación.

## Cuentas y perfil

- Si el `sub` de Google ya está vinculado, se inicia sesión en la misma cuenta
  local, aunque Google informe un correo distinto al usado anteriormente.
- Si no está vinculado, el backend asocia la cuenta local que tenga el mismo
  correo verificado.
- Si no existe una cuenta con ese correo, el backend crea una cuenta con rol
  `cliente`. Algunos campos no provistos por Google quedan vacíos. Los datos
  de Google solo pueden completar `nombres` y `aPaterno` cuando caben en el
  límite del modelo; el frontend debe permitir revisarlos y completarlos.
- Consultar `usuario.perfilCompleto` en la respuesta de login o perfil. Si es
  `false`, dirigir al usuario al formulario de completar perfil.
- La cuenta puede navegar y autenticarse con perfil incompleto, pero la API
  rechaza la creación de pedidos hasta que estén completos los campos
  requeridos: nombres, apellido paterno, fecha de nacimiento, correo y comuna.
  El RUT y su dígito verificador no son necesarios para completar el perfil.
- Para completar o actualizar el perfil, usar `PUT /api/usuarios/perfil` con
  el JWT propio. El contrato de `UsuarioDTORequest`, sus campos requeridos y
  las validaciones están descritos en [4-dtos.md](./4-dtos.md#cliente-perfil-direcciones-carrito-y-pedidos).
  Para una cuenta de Google, `password` puede omitirse. Al enviar el formulario,
  incluir los campos obligatorios del DTO aunque el flujo OAuth no haya
  requerido contraseña.
- Después de guardar el perfil, volver a consultar `GET /api/usuarios/perfil`
  y usar el nuevo `perfilCompleto` como fuente de verdad.

La respuesta al intentar crear un pedido con el perfil incompleto es `409
Conflict`, con el código `PERFIL_INCOMPLETO`. Mostrar el formulario de perfil
en lugar de reintentar el pedido sin cambios.

## Errores que debe manejar el frontend

| HTTP | Código | Acción sugerida |
|---|---|---|
| `400` | `ERROR_VALIDACION` | Revisar que se envió un `idToken` no vacío y válido como JSON. |
| `401` | `AUTENTICACION_GOOGLE_INVALIDA` | Informar que no se pudo iniciar sesión y permitir volver a intentarlo con Google. Aplica a token inválido/expirado, audiencia incorrecta, correo no verificado o cuenta inactiva. |
| `503` | `GOOGLE_NO_CONFIGURADO` | Mostrar que el inicio de sesión con Google no está disponible y ofrecer el login tradicional. Avisar al equipo responsable del despliegue. |
| `409` | `PERFIL_INCOMPLETO` | Se devuelve al crear pedidos, no al iniciar sesión. Abrir el formulario de completar perfil. |

El cuerpo de error de la API incluye `code` y `message`; usar `code` para el
manejo programático. No inferir el estado de autenticación únicamente a partir
del texto del mensaje.

## Lista de comprobación

- [ ] El Client ID de Google configurado en el frontend coincide con
      `GOOGLE_CLIENT_ID` del backend del mismo entorno.
- [ ] El origen del frontend está registrado en Google Cloud y tiene acceso
      CORS a la API.
- [ ] Se envía el ID token en `idToken`, sin aceptar como prueba de identidad
      datos recibidos del cliente.
- [ ] Se guarda y usa el JWT devuelto por la API; el ID token no se reutiliza
      como token de API ni se registra.
- [ ] Se manejan `perfilCompleto: false` y el error `PERFIL_INCOMPLETO`.
- [ ] Se prueban login exitoso, cuenta nueva, cuenta existente, token inválido,
      API sin configuración Google y cierre de sesión.
