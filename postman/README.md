# Pruebas de la API de EmpleApp

Importa `EmpleApp.postman_collection.json` y `Local.postman_environment.json` en Postman. Selecciona el entorno local y configura `baseUrl` con `http://localhost:8080/api`.

Completa `adminCorreo` y `adminContrasena` localmente con una cuenta Administrador existente de EmpleApp. No son las credenciales de MySQL. No guardes ni publiques claves reales en Git. El registro público permite crear cuentas Trabajador y Empleador; no crea administradores.

Ejecuta la colección completa, en orden y con una iteración. Necesita los roles Trabajador y Empleador existentes. Mantén las cookies activadas y utiliza Authorization → No Auth. Los scripts obtienen CSRF antes de cada escritura y guardan los identificadores creados.

La colección cubre 69 endpoints mediante 130 solicitudes principales, incluidos casos negativos y limpieza. Fue verificada con Newman y una base H2 temporal: 204 solicitudes HTTP contando CSRF, 271 verificaciones y cero fallos. Esto no sustituye las capturas manuales ni una ejecución sobre MySQL.

Las pruebas crean y eliminan datos propios. Ensaya primero sobre una copia de la base y toma las capturas antes de la carpeta de limpieza. Si interrumpes la ejecución, conserva las variables con sus IDs y completa la limpieza con la cuenta Administrador antes de reiniciar la preparación. No reemplaces los IDs por registros ajenos.

Los códigos A01–A69 identifican las capturas, no el orden de ejecución. Sigue las carpetas de la colección. GET y DELETE no llevan JSON; logout y cancelar tampoco llevan cuerpo. Las solicitudes administrativas requieren Administrador; los perfiles y publicaciones propios requieren el rol correspondiente.

`RegistroLogin.postman_collection.json` conserva la colección específica anterior de registro y login. La colección completa es la recomendada para probar todos los módulos.
