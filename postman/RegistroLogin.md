# Entrega de pruebas de registro y login

Esta es la colección que corresponde al alcance del profesor. La colección general de la carpeta es opcional y no se necesita para esta entrega.

## Archivos que debes importar

- `RegistroLogin.postman_collection.json`
- `Local.postman_environment.json`

## Pasos en Postman

1. Enciende MySQL y el backend. Espera `Started EmpleAppApplication`.
2. Pulsa **Import** en Postman y selecciona los dos archivos anteriores.
3. Selecciona el entorno **EmpleApp local**; `baseUrl` debe ser `http://localhost:8080/api`.
4. Abre **EmpleApp - Registro y login** y pulsa **Run**.
5. Ejecuta todas sus carpetas en orden, con una iteración. La preparación y la limpieza son necesarias para que los casos sean repetibles.

## Casos de la entrega

| Caso | Resultado esperado |
| --- | --- |
| Registro con datos válidos | 201 y usuario sin contraseña en la respuesta |
| Perfil vinculado automáticamente | 200 y perfil asociado a la cuenta nueva |
| Correo duplicado | 409 |
| Registro incompleto | 400 |
| Rol inexistente | 404 |
| Intento de registro como administrador | 400; se omite si ese rol no existe |
| Login correcto | 200 y usuario esperado sin contraseña en la respuesta |
| Contraseña incorrecta | 401 |
| Correo inexistente | 401 |
| Login incompleto | 400 |

La colección consulta los roles reales, genera un correo único para cada corrida y borra únicamente su cuenta y perfil de prueba al finalizar. Conserva los roles que existían antes de empezar. Si se interrumpe la corrida, termina la carpeta de limpieza con las mismas variables antes de repetirla.

## Evidencia

Guarda capturas del resumen del Runner y de las respuestas 201, 200, 401 y 409, mostrando también las comprobaciones de Postman. No incluyas contraseñas reales.

La colección específica se verificó con Newman en una base H2 temporal, sin modificar MySQL: **14 peticiones enviadas, 20 comprobaciones y ningún fallo**. La petición de rol administrador se omitió porque esa base temporal no tenía uno. Consulta `resultado-registro-login.json` para ver los resultados por petición. Ejecutarla en Postman sobre MySQL es la comprobación pendiente para tu entrega.
