# Sesiones y permisos de EmpleApp

El login ahora establece una sesión en el backend. React consulta la cuenta actual al cargar la aplicación y la sesión se conserva al recargar mientras siga vigente. No se almacenan contraseñas ni credenciales en localStorage.

## Rutas de esta etapa

| Método | Ruta | Acceso |
| --- | --- | --- |
| GET | `/api/usuario/csrf` | Público; devuelve el token y el nombre de la cabecera CSRF |
| POST | `/api/usuario/login` | Público con CSRF; verifica credenciales y establece la sesión |
| POST | `/api/usuario/registro` | Público con CSRF; permite trabajador o empleador |
| GET | `/api/usuario/me` | Cuenta autenticada; obtiene su usuario desde la sesión |
| POST | `/api/usuario/logout` | Con CSRF; invalida la sesión y devuelve 204 |
| GET y PUT | `/api/trabajador/me` | Trabajador; consulta o modifica su propio perfil |
| GET y PUT | `/api/empleador/me` | Empleador; consulta o modifica su propio perfil |

Los formularios de perfil solo admiten los campos editables. No pueden modificar el propietario, el ID ni el rol mediante un objeto anidado en el JSON. El identificador se obtiene de la sesión guardada por el servidor.

## Permisos iniciales

- Registro, login y obtención de CSRF son públicos. `GET /api/rol` sigue siendo público para cargar el formulario de registro.
- Los usuarios autenticados pueden consultar servicios, tipos y estados de servicio.
- Cada trabajador o empleador tiene acceso a su propio perfil mediante `/me`.
- El CRUD general de las once entidades y las vistas Thymeleaf de administración requieren el rol Administrador o Administrador Principal. Los permisos administrativos se consultan en la base de datos en cada solicitud.
- El empleador puede crear, editar y cancelar sus ofertas por `/api/servicio/mis-publicaciones`, con comprobación de propiedad en el servicio. Las consultas del catálogo usan DTOs sin datos personales de su cuenta. Consulta `PUBLICACIONES.md` para los filtros y reglas de edición.
- Postularse como trabajador y consultar contrataciones propias siguen pendientes. Sus operaciones generales continúan restringidas al administrador.
- El modo de vista previa del frontend solo muestra ejemplos, no habilita APIs ni permisos administrativos. Se activa desde el enlace del login y se desactiva al iniciar o cerrar sesión.

Se mantienen las rutas existentes, la estructura `@Controller` con `@ResponseBody` y las plantillas Thymeleaf. Una solicitud sin sesión devuelve 401 en las rutas protegidas; una cuenta sin permiso devuelve 403. Una solicitud que modifica datos sin token CSRF válido también devuelve 403.

## Flujo en React

Axios envía la cookie de sesión usando `withCredentials`. Antes de cada POST, PUT, PATCH o DELETE solicita un token con `GET /api/usuario/csrf` y añade la cabecera indicada por el backend. La cookie es HTTP-only y no se lee desde JavaScript. CORS permite credenciales exclusivamente desde `http://localhost:5173`, incluida la ruta de logout que procesa Spring Security antes de MVC.

El login renueva el ID de sesión y retira el token CSRF anterior. Cerrar sesión invalida el estado en el servidor; al recibir un 401, React retira la identidad local y vuelve al login para las vistas protegidas. La sesión vence tras 30 minutos de inactividad. Usa `localhost` en ambos servidores para el desarrollo local.

## Postman

Vuelve a importar `postman/RegistroLogin.postman_collection.json`. El script de la colección solicita CSRF automáticamente antes de modificar datos y Postman conserva la cookie en su cookie jar. No se debe desactivar esa cookie jar.

Para una prueba manual:

1. Envía `GET http://localhost:8080/api/usuario/csrf`.
2. Copia `token` a la cabecera `X-CSRF-TOKEN` de tu POST de login o registro. Conserva la cookie que recibió Postman.
3. Después del login, vuelve a obtener CSRF antes de modificar un perfil o cerrar sesión. El token anterior ya no es válido.
4. Consulta `GET /api/usuario/me` para comprobar la identidad de la sesión.
5. Envía `POST /api/usuario/logout` con un token actualizado. La respuesta es 204; una consulta posterior de `/me` devuelve 401.

La colección genera cuentas de prueba con correo único; ya no puede borrarlas como trabajador porque eliminar usuarios es una operación administrativa. En MySQL, la limpieza debe realizarla un administrador. La comprobación automática de esta implementación se hace en H2 temporal y no modifica MySQL. La colección CRUD general anterior no contempla estos permisos y debe actualizarse antes de volver a usarla.

## Validación y alcance

`./mvnw.cmd test` incluye pruebas con la cadena real de Spring Security: acceso anónimo, CSRF, CORS de login y logout, persistencia y rotación de sesión, cierre de sesión, perfiles propios, intentos de modificar otra cuenta y cambios de permisos en la base.

El frontend se verifica con `npm.cmd run build` y `npm.cmd run format:check`, ejecutados desde `front end`.

Esta etapa no incorpora recuperación de contraseña, cambios de contraseña, búsquedas avanzadas, selección de postulantes ni pagos reales. Para un despliegue fuera de localhost se deberá configurar HTTPS, cookies Secure y el origen real del frontend.

Referencias oficiales: [persistencia de sesión](https://docs.spring.io/spring-security/reference/servlet/authentication/session-management.html) y [CSRF](https://docs.spring.io/spring-security/reference/servlet/exploits/csrf.html).
