# Informe de codificación y pruebas de EmpleApp

Evidencia GA7-220501096-AA3-EV02  
Módulos de software stand-alone, web y móvil codificados y probados

Leslie Mayerly Cruz Gañán  
Tecnología en Análisis y Desarrollo de Software  
Servicio Nacional de Aprendizaje — SENA  
Ficha: [completar]  
Instructor: [completar]  
Sabaneta, Antioquia  
13 de noviembre de 2026

## Introducción

El presente informe documenta la implementación actual del módulo web de EmpleApp y su verificación frente a los requisitos definidos durante la planeación del proyecto. La aplicación busca apoyar la gestión de servicios domésticos mediante el registro de usuarios, empleadores, trabajadores, servicios y operaciones relacionadas con su contratación.

Durante la construcción se ajustó la propuesta tecnológica inicial. La versión revisada utiliza Java y Spring Boot, plantillas Thymeleaf para la interfaz web y MySQL para el almacenamiento. El alcance desarrollado se concentra en operaciones de creación, consulta, actualización y eliminación de registros. Las funcionalidades originalmente previstas que todavía no están implementadas se identifican expresamente para conservar la trazabilidad.

Este documento diferencia la revisión del código, las pruebas automatizadas y las comprobaciones manuales de interfaz. Las capturas y el video se incorporarán a partir de la ejecución real de la aplicación. Los espacios señalados para su incorporación no constituyen evidencia de una prueba realizada.

## Objetivo general

Documentar la codificación y verificación del módulo web de EmpleApp, relacionando sus funcionalidades con los requisitos del proyecto e identificando los resultados de las pruebas, las validaciones y las evidencias de control de versiones.

## Objetivos específicos

Describir la arquitectura y el alcance implementado. Relacionar los requisitos originales con las operaciones disponibles. Verificar el comportamiento del código mediante pruebas automatizadas y definir las comprobaciones manuales complementarias. Documentar las validaciones de entradas y los aspectos pendientes de implementación. Organizar las capturas, el registro de versionamiento y la demostración audiovisual.

## Alcance y actualización tecnológica

La entrega corresponde al módulo web. No se acredita una aplicación móvil nativa ni un programa de escritorio independiente. El acceso desde el navegador de un teléfono, si se verifica, constituye una comprobación de la versión web y no demuestra la existencia de una aplicación móvil nativa.

| Componente | Implementación actual |
| --- | --- |
| Lenguaje y entorno | Java; compilación configurada para Java 21 |
| Framework | Spring Boot 4.1.1 |
| Presentación | HTML y plantillas Thymeleaf |
| Persistencia | Spring Data JPA y MySQL |
| Validaciones | Jakarta Bean Validation y restricciones de base de datos |
| Pruebas | JUnit, Spring Boot Test, MockMvc y H2 en memoria |
| Construcción | Maven Wrapper |
| Versionamiento | Git |
| Dirección local | http://localhost:8080/ cuando el servidor inicia correctamente |

La arquitectura se organiza en modelo, repositorio, servicio y controlador. Las entidades representan los datos y sus relaciones; los repositorios permiten su persistencia; los servicios exponen las operaciones de gestión, y los controladores atienden las solicitudes de las vistas web y de la API REST.

La aplicación administra once entidades: Rol, Usuario, Empleador, Trabajador, DocumentoTrabajador, TipoServicio, EstadoServicio, Servicio, Postulacion, Contratacion y Pago. Para cada entidad existen vistas de registro, actualización, consulta general, consulta por identificador, detalle y eliminación.

## Relación con los requisitos originales

Los códigos RF y HU se conservan del informe de planeación. La existencia de una pantalla relacionada no equivale al cumplimiento completo del requisito. El estado de implementación se distingue del resultado de una prueba.

| Requisito e historia | Alcance actual y aspectos pendientes |
| --- | --- |
| RF-01 / HU-01 Registro de prestador | Parcial. Se administran usuarios y trabajadores con experiencia y disponibilidad. Pendientes el perfil completo con foto, habilidades, tarifas y ubicación, y el flujo integral de registro. |
| RF-02 / HU-01 Registro de cliente | Parcial. Se administran usuarios y empleadores con dirección y teléfono. Pendientes las preferencias de servicio y el flujo de cuenta del cliente. |
| RF-03 / HU-01 Autenticación | Pendiente. El registro de usuarios y roles no implementa inicio de sesión ni emisión de JWT. |
| RF-04 / HU-02 Búsqueda | Pendiente el buscador combinado por servicio, ubicación, precio y disponibilidad, con orden por proximidad y calificación. La consulta por ID es una operación distinta. |
| RF-05 / HU-04 Publicación de oferta | Parcial. Servicio permite registrar empleador, tipo, estado, fecha, horas, ubicación y valor. Pendientes la comprobación integral de reglas y el flujo de publicación del cliente. |
| RF-06 / HU-04 Solicitud | Parcial en los datos relacionados. Existen postulaciones y contrataciones; no equivalen a una solicitud directa con validación de disponibilidad del prestador. |
| RF-07 / HU-03 Notificaciones | Pendiente. No se acredita envío de alertas ni aceptación o rechazo desde una notificación. |
| RF-08 / HU-05 Calificaciones | Pendiente. No se acredita puntuación, comentario ni edición durante 24 horas. |
| RF-09 / Sin HU asociada | Parcial. Pago administra importes y fecha asociados a una contratación. No acredita transferencia bancaria ni estado de transacción. |
| RF-10 / Sin HU asociada | Parcial en gestión de registros. Pendientes métricas, PQRS, reportes y control de acceso administrativo. |
| RF-11 / HU-01 Documentos | Parcial. Se dispone de carga documental asociada al trabajador. Pendiente el flujo de revisión y aprobación por administrador. |
| RF-12 / HU-02 Historial | Parcial. Existen consultas de servicios y contrataciones. Pendiente el historial consolidado por usuario con filtros de fecha y estado. |

## Metodología de pruebas

Las pruebas automatizadas se ejecutan mediante `.\mvnw.cmd test`. Utilizan una base H2 en memoria para no modificar MySQL. MockMvc permite comprobar controladores y renderización de plantillas sin abrir un navegador. Sus resultados no sustituyen la verificación visual, la integración con MySQL ni la grabación del video.

Las pruebas manuales deben realizarse con datos ficticios y respetar el orden de las relaciones: roles y usuarios; empleadores y trabajadores; tipos y estados; servicios; documentos y postulaciones; contrataciones y pagos. Para cada ejecución se registrará fecha, datos de entrada, resultado obtenido, estado y captura.

## Resultados automatizados

Se inició una ejecución de Maven el 13 de noviembre de 2026. La aplicación de pruebas logró conectar con H2 e iniciar su contexto, pero la ejecución dejó de producir resultados después del aviso de autoacoplamiento de Mockito y se interrumpió sin obtener un resumen final. El estado de esta ejecución es inconcluso; no se contabilizan casos aprobados. Debe repetirse el comando en el equipo de entrega y anexar el resumen de Maven. Los archivos de reporte previos no se consideran evidencia de esta ejecución.

La suite EmpleAppApplicationTests contiene cuatro pruebas: carga del contexto, persistencia de usuario con rol, rechazo de campos obligatorios y correo inválido, y operaciones JSON del controlador de roles con rechazo de un nombre vacío.

La suite PlantillasTests contiene once ejecuciones parametrizadas para las entidades y cuatro pruebas adicionales. Verifica las seis vistas por entidad, vistas de selección sin ID, la página inicial, el error del formulario de rol vacío, el ciclo de registro, actualización, consulta y eliminación de un rol, el registro de usuario con relación, el registro de una fecha HTML de postulación y el mensaje ante un ID inexistente.

Estas pruebas no verifican el ciclo completo de modificación de las once entidades, la seguridad de autenticación, las reglas de disponibilidad, los cálculos de pagos ni la presentación en dispositivos reales.

## Requisitos y capturas de interfaz

### RF-01 y HU-01 Registro del prestador

La historia resumida corresponde al registro de un prestador con perfil completo. En el alcance actual se debe crear primero un usuario asociado al rol Trabajador y luego registrar su experiencia y disponibilidad en la sección de trabajadores. Se consultará el registro para comprobar su relación con el usuario. La prueba manual y la comprobación del perfil completo quedan pendientes.

Figura 1. [Insertar captura real del registro de usuario y consulta del trabajador creado. Añadir resultado obtenido y fecha.]

### RF-02 y HU-01 Registro del empleador

El requisito permite registrar al cliente con sus datos. En la versión actual se probará el alta de usuario y su asociación con un empleador, incluyendo dirección y teléfono. Se verificará su aparición en la consulta. Las preferencias de servicio quedan pendientes.

Figura 2. [Insertar captura real del formulario y del empleador guardado.]

### RF-03 y HU-01 Autenticación

El requisito exige correo, contraseña y JWT con vigencia de 24 horas. La versión actual no implementa este flujo, por lo que el caso no puede declararse aprobado. La interfaz CRUD de usuario no constituye una pantalla de inicio de sesión.

Evidencia: pendiente de implementación. No corresponde insertar una captura de otra función como prueba de autenticación.

### RF-04 y HU-02 Búsqueda de prestadores

La historia contempla filtros combinados de búsqueda. La versión actual permite consultar registros, pero no acredita los filtros ni el ordenamiento especificado. Puede documentarse la consulta disponible como funcionalidad complementaria sin atribuirle el cumplimiento del RF-04.

Figura 3. [Insertar captura real de consulta de trabajadores, indicando su alcance limitado.]

### RF-05 y HU-04 Registro de servicio

El cliente requiere publicar una oferta con tipo de servicio, fecha, duración, dirección y remuneración. La prueba del alcance actual consiste en seleccionar empleador, tipo y estado, ingresar fecha, hora inicial, hora final, ubicación y valor, guardar y consultar el servicio. Se comprobará que la información se conserve. La coherencia del horario requiere una prueba adicional.

Figura 4. [Insertar captura real del formulario de servicio y del registro consultado.]

### RF-06 y HU-04 Solicitud y contratación

El requisito exige solicitar un servicio y comprobar disponibilidad. Las operaciones actuales de postulación y contratación permiten asociar trabajador y servicio, pero no acreditan el flujo completo. Se probará el registro y consulta de esas relaciones. La detección de cruces horarios queda pendiente.

Figura 5. [Insertar captura real de postulación y contratación; describir únicamente las relaciones verificadas.]

### RF-07 y HU-03 Notificaciones

La historia requiere alertar al prestador y permitir aceptación o rechazo. La funcionalidad está pendiente y no cuenta con un resultado de ejecución aprobado ni con una interfaz que permita acreditarla.

### RF-08 y HU-05 Calificaciones

La historia requiere registrar estrellas y comentarios sobre un servicio completado. La funcionalidad y la ventana de edición de 24 horas están pendientes. No se acredita su cumplimiento mediante las consultas de servicios.

### RF-09 Gestión de pagos

La prueba actual consiste en seleccionar una contratación e ingresar valor del servicio, porcentaje, comisión, valor del trabajador y fecha. Como datos de ejemplo pueden utilizarse 100000, 10, 10000 y 90000, respectivamente. Se verificará la persistencia, sin afirmar que el sistema calcula automáticamente la comisión o ejecuta transferencias.

Figura 6. [Insertar captura real del registro y consulta del pago.]

### RF-10 Administración

La versión permite administrar las entidades desde el índice. Se revisará la navegación y el CRUD disponible. La existencia del índice no acredita autorización por rol, métricas, PQRS ni reportes estadísticos.

Figura 7. [Insertar captura real del índice y de una operación administrativa.]

### RF-11 y HU-01 Documentos del trabajador

El requisito permite cargar documentos para su validación. Se seleccionará un trabajador y se cargará un archivo ficticio, sin información personal. Se consultará el documento y se verificará su asociación. La aprobación documental por administrador está pendiente.

Figura 8. [Insertar captura real de carga y consulta del documento.]

### RF-12 y HU-02 Consulta de servicios y contratos

Se probarán las consultas generales y por identificador de servicios y contrataciones. Se comprobará que los datos recuperados correspondan al registro seleccionado. El historial consolidado y sus filtros están pendientes.

Figura 9. [Insertar captura real de las consultas de servicio y contratación.]

### Catálogos complementarios

Se verificará el registro, consulta, actualización y eliminación de un rol de prueba, un tipo de servicio y un estado. Antes de eliminarlos se comprobará que no estén asociados a otros registros.

Figura 10. [Insertar capturas reales de catálogos y de las operaciones realizadas.]

## Pruebas de validación

Los siguientes casos complementan la suite automatizada. Se encuentran pendientes de ejecución manual salvo los comportamientos expresamente cubiertos por las pruebas descritas anteriormente. No se atribuye un resultado aprobado a una regla por el solo hecho de estar propuesta.

| Caso | Entrada o acción | Comportamiento que se debe verificar |
| --- | --- | --- |
| V-01 Obligatorios | Nombre de usuario o rol vacío; usuario sin rol | Rechazar datos obligatorios. La suite cubre validación del modelo de usuario y formulario/API de rol. |
| V-02 Correo | correo-invalido | Detectar formato inválido. Cubierto en el validador del modelo; falta captura en interfaz. |
| V-03 Longitudes | Rol con 50 y 51 caracteres; nombre de usuario con 100 y 101 | Aceptar el límite y rechazar su exceso. |
| V-04 Ubicación | Texto de 255 y 256 caracteres | Verificar el límite de longitud de Servicio. |
| V-05 Caracteres | María Muñoz; dirección con # y guion | Comprobar conservación de tildes, ñ y signos válidos. No rechazar caracteres indiscriminadamente. |
| V-06 Contenido HTML | Texto literal <script>alert(1)</script> en un campo de texto de prueba | Verificar que no se ejecute código ni se altere la página. Resultado pendiente. |
| V-07 Números | Texto en importe y valores con tres decimales | Comprobar conversión y restricción de hasta dos decimales en importes. |
| V-08 Límite numérico | 99999999.99 y 100000000.00 en valor de servicio | Verificar los ocho dígitos enteros y dos decimales definidos por el modelo. |
| V-09 Fecha | Fecha válida y 2026-02-30 | Verificar aceptación de fecha real y rechazo de formato o fecha imposible, también mediante solicitud al servidor. |
| V-10 Horario | Hora final anterior a la inicial | Detectar si existe rechazo. La revisión del modelo no acredita una regla de orden entre horas; registrar hallazgo si se acepta. |
| V-11 Signos y porcentaje | Importe negativo; comisión de 101 | Comprobar comportamiento y registrar la falta de reglas si se aceptan. @Digits no impone positividad ni un máximo de 100. |
| V-12 Duplicidad | Reutilizar un correo existente | Verificar la restricción de unicidad y que la interfaz maneje el error de forma comprensible. |
| V-13 Relaciones | Usuario sin rol; contratación sin servicio o trabajador | Rechazar relaciones obligatorias; comprobar también ID de relación inexistente. |
| V-14 Identificador | Consultar 2147483647 sin registro asociado | Mostrar aviso controlado. Cubierto para rol en la suite. |
| V-15 Archivos | Archivo ficticio permitido, archivo vacío y archivo sobredimensionado | Verificar tipo, tamaño y mensajes según la configuración; no presuponer validaciones no comprobadas. |

Figura 11 y siguientes. [Insertar una captura de cada validación ejecutada, con entrada, resultado obtenido, fecha y estado.]

## Hallazgos y limitaciones

El intento de arranque con la conexión local a MySQL presentó Communications link failure. En esta sesión también se observó una restricción de acceso al puerto local. Por tanto, debe verificarse el arranque desde el equipo de ejecución y no se acredita todavía una sesión visual funcional contra MySQL.

El modelo de Servicio contiene restricciones de precisión y longitud, pero no acredita validación de fecha futura ni de hora final posterior a la inicial. El modelo de Pago utiliza restricciones de dígitos; estas no garantizan importes positivos, porcentaje entre 0 y 100 ni coherencia matemática entre comisión y valor del trabajador. Estos aspectos deberán comprobarse y corregirse según las reglas de negocio.

No se han acreditado autenticación, autorización por roles, JWT, bcrypt, notificaciones, calificaciones, métricas ni filtros avanzados. Los hashes ficticios del script de ejemplo no constituyen implementación de seguridad. Las pruebas H2 tampoco certifican por sí solas la compatibilidad del script SQL con la instancia MySQL del usuario.

## Control de versiones

El proyecto dispone de un repositorio Git. En la consulta del historial se identificó el commit 38c3b59, con el mensaje Entrega inicial de EmpleApp con Spring Boot y vistas Thymeleaf. Este dato acredita un registro local; no demuestra por sí solo publicación en GitHub ni cambios posteriores confirmados.

Para la entrega se anexarán capturas reales de `git status`, `git log --oneline` y `git diff --stat`. Después de revisar los archivos correspondientes a la evidencia, se registrarán los cambios con un mensaje descriptivo. Si se utiliza un repositorio remoto, se añadirá su dirección verificada y la evidencia de la publicación. No se incluirán contraseñas ni datos personales en los cambios.

Figura final de versionamiento. [Insertar captura del historial, identificador de la revisión entregada y estado del repositorio.]

## Guion del video de demostración

La grabación puede organizarse en aproximadamente diez a doce minutos, ajustando la duración a las indicaciones del instructor. Se iniciará con la presentación de la aprendiz, el código de la evidencia, el propósito del proyecto y la explicación de que se demuestra el módulo web actual.

A continuación se mostrará el servidor iniciado y el índice de EmpleApp. Se recorrerán los catálogos y el registro de usuario, empleador y trabajador; luego se demostrará la carga documental, el registro del servicio, la postulación, la contratación y el pago. En cada recorrido se mostrará el guardado y la consulta, y se realizarán ejemplos de actualización y eliminación de registros ficticios sin dependencias.

La demostración de validaciones incluirá un campo obligatorio vacío, correo inválido, exceso de longitud, entrada numérica incorrecta, fecha inválida y consulta de ID inexistente. Se explicará el resultado real, incluyendo cualquier comportamiento pendiente de corregir. Al finalizar se mostrarán la ejecución de Maven y el historial de Git, y se señalarán las funcionalidades pendientes sin presentarlas como implementadas.

Enlace del video: [incorporar enlace con permisos de visualización comprobados].

## Conclusiones

EmpleApp cuenta con una base web organizada en capas para administrar once entidades relacionadas. El código incorpora validaciones declarativas y pruebas automatizadas orientadas a persistencia, controladores y plantillas. Su alcance actual constituye un avance de construcción frente al sistema previsto en la planeación.

La trazabilidad evidencia cumplimiento parcial de varios requisitos y funcionalidades todavía pendientes. Para cerrar esta evidencia se requiere incorporar las capturas reales, completar los resultados de las validaciones manuales, comprobar la operación con MySQL y adjuntar el video y el registro de la revisión entregada. La aprobación completa del módulo deberá sustentarse en esos resultados y no únicamente en la existencia de formularios.

## Fuentes del proyecto

Cruz Gañán, L. M. (2026). Informe de entregables para el proyecto de desarrollo de software [Documento académico, evidencia GA4-220501095-AA2-EV02]. Servicio Nacional de Aprendizaje.

Cruz Gañán, L. M. (2026). EmpleApp [Código fuente del proyecto]. Repositorio Git local, revisión 38c3b59 y archivos de trabajo consultados.

Las pruebas se documentan a partir de EmpleAppApplicationTests.java, PlantillasTests.java y los reportes de ejecución de Maven. No se incorporan resultados históricos del informe de planeación como si fueran ejecuciones de la versión actual.
