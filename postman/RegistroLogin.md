# Pruebas de registro, login y sesión

La colección `RegistroLogin.postman_collection.json` está actualizada para sesiones de Spring Security y protección CSRF. Importa también `Local.postman_environment.json`, selecciona **EmpleApp local** y ejecuta todas las carpetas en orden con una iteración. El backend debe estar encendido en 8080; el frontend no es necesario para Postman.

## Qué cambió

- Antes de cada POST o PUT, el script de colección consulta `/api/usuario/csrf` y añade la cabecera que recibe. No desactives la cookie jar de Postman.
- El login conserva una sesión mediante cookie; `/api/usuario/me` obtiene la cuenta autenticada.
- `/api/trabajador/me` permite consultar y editar el perfil propio. Los listados generales requieren administrador.
- `/api/usuario/logout` termina la sesión. Una consulta de `/me` posterior debe devolver 401.
- La petición marcada `[Sin CSRF]` omite el token deliberadamente y debe devolver 403.

## Casos

| Caso | HTTP esperado |
| --- | --- |
| Consultar roles para el registro | 200 |
| Registro de trabajador | 201 |
| Correo duplicado | 409 |
| Datos de registro incompletos | 400 |
| Rol inexistente | 404 |
| Intento de registro como administrador | 400, si ese rol existe |
| Login correcto | 200 |
| Contraseña incorrecta o correo inexistente | 401 |
| Login incompleto | 400 |
| Consultar cuenta actual y perfil propio | 200 |
| Actualizar perfil propio | 200 |
| Listar o crear cuentas sin ser administrador | 403 |
| Perfil de empleador con cuenta trabajador | 403 |
| Cerrar sesión | 204 |
| Consultar cuenta después de logout | 401 |
| Registro sin token CSRF | 403 |

Los roles deben existir previamente en la base, como al importar `DataBase.sql`. La colección no crea roles porque esa operación es administrativa. La prueba de rol administrador se omite si no existe; una omisión no es un caso aprobado.

## Datos de prueba

Cada corrida genera un correo único con dominio `example.test`. No utiliza la cuenta demo de administrador ni credenciales de MySQL. Esta versión no borra automáticamente la cuenta ni su perfil: su eliminación requiere una sesión administrativa. Si ejecutas la colección sobre MySQL, conservarás una cuenta y un perfil de prueba por corrida. Ejecuta sobre una copia de pruebas cuando corresponda.

## Resultado verificado

La colección se ejecutó con Newman contra H2 en memoria en el puerto 8081: **19 peticiones principales, 31 peticiones HTTP contando la obtención de CSRF, 39 comprobaciones y ningún fallo**. En esa base existían los tres roles, por lo que no se omitió el caso de administrador. Los resultados están en `resultado-registro-login.json`. No se modificó MySQL.

Las 37 pruebas Java incluyen además comprobaciones con la cadena real de seguridad, edición del perfil de empleador, rechazo de cambios de propietario, permisos administrativos y CORS de logout. El navegador también verificó ingreso, persistencia tras recargar, edición propia y cierre de sesión.

Para pruebas manuales y la matriz de permisos consulta [Sesiones y permisos](../docs/SEGURIDAD.md). Para la entrega guarda capturas de registro 201, login 200, credenciales incorrectas 401 y duplicado 409. Los resultados anteriores a esta etapa corresponden a la API sin protección de sesión y no deben confundirse con esta corrida.
