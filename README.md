# EmpleApp

Aplicación Spring Boot con 11 entidades correspondientes a DataBase.sql.

El registro y login incluyen BCrypt, sesiones del servidor, CSRF y permisos por rol.
Consulta [sesiones y permisos](docs/SEGURIDAD.md) para las rutas `/me`, el cierre de sesión
y el nuevo flujo de pruebas en Postman. El CRUD general requiere administrador.

## Ejecutar con MySQL

1. Inicia MySQL e importa `DataBase.sql` para crear la base `empleapp`, las tablas y los datos iniciales.
2. Configura las credenciales en PowerShell:

```powershell
$env:DB_USERNAME = 'root'
$env:DB_PASSWORD = 'tu_clave'
$env:DB_URL = 'jdbc:mysql://localhost:3306/empleapp?serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true'
./mvnw.cmd spring-boot:run
```

Se requiere Java 21 o superior. Hibernate valida el esquema existente al iniciar.
Las pruebas utilizan H2 en memoria y no modifican MySQL.

## Organización

- `modelo`: entidades JPA con claves autogeneradas, relaciones, getters, setters y validaciones.
- `repositorio`: interfaces que extienden `JpaRepository`.
- `servicio`: interfaces con listar, guardar, buscar y eliminar por entidad; implementaciones con `@Service` e inyección de repositorios mediante `@Autowired`.
- `controlador`: controladores REST con validación de entradas.

## Plantillas web

Al iniciar la aplicación, abre `http://localhost:8080/` para acceder al índice.
Cada entidad cuenta con seis plantillas en `src/main/resources/templates`:
`registrar`, `actualizar`, `consultar`, `consultar-id`, `detalle` y `eliminar`.
Por ejemplo, `rol-registrar.html` se abre desde `/rol/registrar` y
`documento-trabajador-consultar.html` desde `/documento-trabajador/consultar`.

Los controladores originales `*Controlador` sirven las vistas Thymeleaf y conservan
la API JSON en `/api/...`. Los formularios validan los campos,
cargan las relaciones en selectores y requieren confirmación para eliminar.
En documentos se puede cargar un archivo; dejarlo vacío al actualizar conserva el anterior.
En usuarios, el hash se introduce de nuevo al actualizar y no se muestra en las consultas.

## Operaciones de la API

Cada recurso admite GET y POST en su ruta base, y GET, PUT y DELETE en `/{id}`.
POST devuelve 201; DELETE devuelve 204; un identificador inexistente devuelve 404.
PUT reemplaza los campos del registro y requiere todos los campos obligatorios.

Rutas: `/api/rol`, `/api/usuario`, `/api/empleador`, `/api/trabajador`,
`/api/documentotrabajador`, `/api/tiposervicio`, `/api/estadoservicio`,
`/api/servicio`, `/api/postulacion`, `/api/contratacion` y `/api/pago`.

Crear un rol con POST a `http://localhost:8080/api/rol`:

```json
{"nombreRol": "Supervisor"}
```

Crear un usuario con POST a `http://localhost:8080/api/usuario`:

```json
{
  "nombre": "Ana",
  "correo": "ana@example.com",
  "contrasenaHash": "hash_previamente_generado",
  "rol": {"idRol": 2}
}
```

Las relaciones se envían como objetos con el ID de un registro existente.
El registro público recibe `contrasena` y calcula su hash BCrypt. La ruta CRUD de
usuarios es administrativa y conserva su campo `contrasenaHash`. El hash no se devuelve en JSON.
Las fechas de carga, postulación, contratación y pago se asignan al crear si se omiten.
Los archivos binarios se representan en JSON como Base64.
Las rutas protegidas requieren una sesión y aplican los permisos descritos en `docs/SEGURIDAD.md`.

## Pruebas

```powershell
./mvnw.cmd test
```

## Cargar registros de ejemplo

Despues de crear las tablas con `DataBase.sql`, abre `DatosEjemplo.sql` en MySQL
Workbench y ejecuta el script completo sobre tu conexion local. No vuelve a crear
las tablas ni elimina registros. Inserta datos ficticios respetando las claves
foraneas dentro de una transaccion; puede repetirse sin duplicar estos ejemplos.
Si aparece un error, ejecuta `ROLLBACK;` antes de corregirlo y volver a intentar.

En una base recien creada quedan 3 roles, 7 usuarios (un administrador, tres
empleadores y tres trabajadores), y 3 registros en cada una de las otras tablas.
Los documentos son fichas de ejemplo sin archivo adjunto. Los tres servicios
estan finalizados y sus pagos descuentan una comision del 10 %. Los correos usan
`example.com` y no corresponden a cuentas reales. Los usuarios incluyen un hash
SHA-256 de demostracion; esos hashes antiguos no son compatibles con el login BCrypt actual.

La consulta al final del script muestra el total de registros por tabla.

## Validaciones y revision de integridad

Los servicios se programan en una sola fecha. Son obligatorios la fecha, ambas horas, la ubicacion y el valor; la hora final debe ser posterior a la inicial.
No se soportan turnos que crucen la medianoche. El valor ofertado
debe ser positivo y tener hasta ocho enteros y dos decimales.

El pago requiere un importe positivo. La comision fija provisional es del 10 %,
conforme a los datos de ejemplo del proyecto, centralizada en `Pago.COMISION_FIJA`.
El servidor calcula la comision con redondeo HALF_UP a dos decimales y obtiene el
neto por diferencia para conservar el total. La API ignora los importes derivados
y el formulario no permite editarlos. La vista muestra una previsualizacion.
Al actualizar un pago se recalculan estos importes con la tasa fija.

Las relaciones se presentan como ID y nombre o descripcion para distinguir
personas con el mismo nombre. Los correos se normalizan a minusculas y se
recortan espacios externos; los nombres conservan tildes y se recortan por fuera.

Ejecutar `sql/revision_integridad.sql` en MySQL para detectar horarios, pagos,
correos y asociaciones inconsistentes. `sql/restricciones_propuestas.sql` agrega
CHECK de horario, importes y comision para MySQL 8.0.16 o superior. No se ha
aplicado automaticamente: primero respaldar y revisar resultados de auditoria.
Los registros antiguos no se corrigen ni se eliminan de forma automatica.

Quedan por definir las reglas de un pago por contrato frente a pagos parciales,
una sola contratacion por servicio frente a equipos, perfiles duplicados,
cruces de disponibilidad, roles permitidos y estados/transiciones. Solo tras
confirmar esas reglas deben agregarse indices UNIQUE. Tambien quedan por mejorar
la revision de archivos y la auditoria de pagos. La primera etapa de sesiones y
permisos ya esta implementada y se documenta en `docs/SEGURIDAD.md`.

Maven carga Mockito como agente al iniciar las pruebas para evitar el
autoacoplamiento que puede detener su ejecucion en Java reciente.
