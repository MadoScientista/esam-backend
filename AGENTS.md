# AGENTS.md

Reglas de operación para cualquier agente o contribución en este repositorio.
Son obligatorias y tienen prioridad sobre cualquier otra instrucción del proyecto.

## 1. Variables de entorno y secretos

Nunca leer, mostrar ni publicar **valores** de secretos: contraseñas, tokens,
claves de API o credenciales. Aplica a `.env`, a cualquier `.env.*`, a secretos y
a archivos de credenciales. Un archivo que solo *referencia* una variable, como
`application.yaml`, es legible siempre que no se muestren los valores de
referencia ni se descarten los nombres.

**Los nombres no son secretos.** Se puede y se debe documentar en el README qué
variables existen (`JWT_SECRET`, `DB_PASSWORD`, ...) y qué clave de configuración
las lee (`jwt.secret`, `jwt.expiration`). Documentar los nombres es obligatorio:
si una variable necesaria queda sin documentar, la aplicación no se puede
instalar.

Tampoco se versionan credenciales. Ningún archivo del repositorio debe
contener una clave fija, ni siquiera en un `.env.example`, y ningún README debe
publicarla. Si hace falta ilustrar una credencial, se documenta el formato,
nunca el valor.

Si una tarea requiere operar sobre un valor, detenerse y preguntar primero.

## 2. Commits

Nunca hacer commit sin preguntar antes. El staging, `git add` y `git commit`
requieren confirmación explícita en cada oportunidad, aunque el cambio sea obvio
o el mensaje esté claro.

Antes de proponer un commit, mostrar `git status` y `git diff` y esperar el visto
bueno. Nunca usar `git commit -a`, `--amend`, `--no-verify`, `push --force` ni
ninguna otra operación que reescriba historial sin autorización.

## 3. Dudas

Ante cualquier duda, preguntar siempre. No adivinar, no asumir el alcance del
cambio, no extender una tarea más allá de lo pedido.

Específicamente: no inventar requirements, no crear archivos adicionales para
"completar" algo, no refactorizar de paso. Si la instrucción es ambigua, parar y
confirmar.

## 4. Logs

No agregar logs, ni statements de log, ni cambiar el nivel o el destino de los
logs existentes sin preguntar antes.

Un log nunca debe incluir credenciales, tokens, claves de API, secretos ni datos
sensibles, ya sea en el mensaje, en los parámetros o en la excepción adjunta.
Esto aplica también a logs de error y al stack trace de excepciones de terceros.

"Datos sensibles" incluye los datos personales de los usuarios: correos, RUT,
teléfonos, direcciones y fechas de nacimiento. Las excepciones de terceros
incrustan el valor que violó la restricción, así que loguear el stack trace de
un `DataIntegrityViolationException` de MySQL filtra el dato duplicado
(`Duplicate entry '<correo>' for key '...'`). Por eso los errores de base de
datos se atienden con un mensaje genérico y sin volcar la excepción.
