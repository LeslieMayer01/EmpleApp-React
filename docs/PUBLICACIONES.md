# Publicaciones de servicios

El catálogo permite buscar y paginar servicios. Un empleador con sesión puede publicar, consultar sus ofertas, editarlas y cancelarlas. Las rutas administrativas y las vistas Thymeleaf anteriores se conservan. No se implementan en esta etapa los flujos de postulación ni contratación.

## Rutas

Todas requieren sesión. Para POST, PUT y PATCH también se necesita un token CSRF vigente.

| Método | Ruta | Acceso y comportamiento |
| --- | --- | --- |
| GET | `/api/servicio/buscar` | Usuario autenticado; catálogo paginado y filtrado |
| GET | `/api/servicio/mis-publicaciones` | Empleador; únicamente las ofertas vinculadas a su cuenta |
| POST | `/api/servicio/mis-publicaciones` | Empleador; crea una oferta y responde 201 |
| PUT | `/api/servicio/mis-publicaciones/{id}` | Empleador propietario; modifica los campos permitidos |
| PATCH | `/api/servicio/mis-publicaciones/{id}/cancelar` | Empleador propietario; cancela sin borrar el registro |

Las rutas anteriores `GET /api/servicio` y `GET /api/servicio/{id}` ahora devuelven DTOs con datos públicos. Conservan el formato de lista y detalle, respectivamente. El nuevo frontend utiliza `/buscar` para evitar descargar toda la tabla. El CRUD administrativo mantiene sus permisos anteriores.

## Publicar y editar

```json
{
  "idTipoServicio": 1,
  "fecha": "2027-01-15",
  "horaInicio": "08:00",
  "horaFin": "12:00",
  "ubicacion": "Medellín, sector Laureles",
  "valorOfertado": 120000
}
```

El ID del tipo debe existir en el catálogo. Elige una fecha actual o futura; las dos horas deben corresponder al mismo día y la final debe ser posterior a la inicial. La ubicación es obligatoria, con un máximo de 255 caracteres. El importe debe ser positivo y admitir como máximo ocho enteros y dos decimales.

El formulario no envía propietario ni estado. El backend utiliza el perfil de empleador asociado a la sesión y asigna el estado `Publicado`. Si falta esa etiqueta en el catálogo, la crea sin modificar los estados existentes. El mismo criterio se aplica a `Cancelado` al cancelar por primera vez. Los tipos de servicio los administra el administrador: si no hay ninguno, el formulario lo indica y no permite enviar una publicación.

Solo se editan o cancelan ofertas en estado `Publicado`. Si tienen postulaciones o contrataciones vinculadas, se rechaza la operación con 409. Estas consultas protegen los registros existentes, pero no agregan el proceso de postular ni contratar. Al implementar esos procesos posteriormente habrá que coordinar sus transacciones con la edición y cancelación.

## Filtros

Los parámetros son opcionales y se combinan:

- `busqueda`: texto en tipo de servicio, nombre del empleador o ubicación, hasta 100 caracteres.
- `tipoId` y `estadoId`: IDs de catálogo positivos.
- `ubicacion`: texto en la ubicación del servicio, hasta 255 caracteres.
- `fechaDesde` y `fechaHasta`: formato `AAAA-MM-DD`, con rango válido.
- `pagina`: comienza en 0. Por defecto, 0.
- `tamano`: de 1 a 50. Por defecto, 8.

Ejemplo: `GET /api/servicio/buscar?ubicacion=Medellin&pagina=0&tamano=8`. La búsqueda no elimina tildes: utiliza la escritura de los datos, por ejemplo `Medellín` cuando corresponda.

La respuesta contiene `contenido`, `pagina`, `tamano`, `totalElementos` y `totalPaginas`. Se ordena por ID descendente para mostrar primero los registros más recientes. Los filtros y el recorte de página se ejecutan en la base de datos, no en React.

## Datos públicos

Cada servicio incluye su ID, tipo, estado, fecha, horario, ubicación del servicio y valor ofertado. Del empleador se incluyen su ID de perfil y nombre para identificar la oferta. No se devuelve el correo, teléfono, dirección del perfil, rol ni hash de contraseña. La ubicación del servicio se conserva porque es un dato de la oferta.

## Frontend

En el perfil del empleador, **Publicar ahora** abre el formulario. En **Publicaciones** puede alternar entre el catálogo y **Mis publicaciones**. Las acciones de edición y cancelación aparecen únicamente en sus ofertas publicadas; el backend verifica de nuevo la propiedad antes de guardar. Cancelar exige confirmación y conserva la información.

El administrador y el trabajador pueden consultar el catálogo, pero no utilizan las rutas propias del empleador. El modo de vista previa sigue siendo ilustrativo y no modifica datos.

## Verificación

Las pruebas `PublicacionesTests` utilizan la cadena completa de seguridad y H2 con rollback. Verifican propietario y estado asignados por el servidor, ausencia de datos personales en los DTOs, permisos, filtros, comodines literales, paginación, campos inválidos y ofertas con relaciones existentes. La suite completa tiene 43 pruebas aprobadas. El frontend compila y la publicación desde navegador se verificó con H2 temporal, sin modificar MySQL.
