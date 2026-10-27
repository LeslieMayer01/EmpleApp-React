# EmpleApp

Aplicación Spring Boot con 11 entidades correspondientes a DataBase.sql.

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
`contrasenaHash` contiene un hash generado previamente; esta base CRUD no implementa
registro, autenticación ni generación de hashes. El hash no se devuelve en JSON.
Las fechas de carga, postulación, contratación y pago se asignan al crear si se omiten.
Los archivos binarios se representan en JSON como Base64.
Los controladores no tienen autorización configurada; esta implementación es una base de desarrollo.

## Pruebas

```powershell
./mvnw.cmd test
```
