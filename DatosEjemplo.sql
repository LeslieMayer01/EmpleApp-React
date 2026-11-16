-- Datos ficticios para desarrollo. Ejecutar despues de DataBase.sql.
-- Puede repetirse: conserva los registros existentes y no duplica estos ejemplos.
SET NAMES utf8mb4;
USE empleapp;
START TRANSACTION;

INSERT INTO Rol (nombre_rol)
SELECT 'Administrador'
WHERE NOT EXISTS (SELECT 1 FROM Rol WHERE nombre_rol = 'Administrador');
SET @rol1 = (SELECT id_rol FROM Rol WHERE nombre_rol = 'Administrador' ORDER BY id_rol LIMIT 1);

INSERT INTO Rol (nombre_rol)
SELECT 'Empleador'
WHERE NOT EXISTS (SELECT 1 FROM Rol WHERE nombre_rol = 'Empleador');
SET @rol2 = (SELECT id_rol FROM Rol WHERE nombre_rol = 'Empleador' ORDER BY id_rol LIMIT 1);

INSERT INTO Rol (nombre_rol)
SELECT 'Trabajador'
WHERE NOT EXISTS (SELECT 1 FROM Rol WHERE nombre_rol = 'Trabajador');
SET @rol3 = (SELECT id_rol FROM Rol WHERE nombre_rol = 'Trabajador' ORDER BY id_rol LIMIT 1);

INSERT INTO EstadoServicio (nombre_estado)
SELECT 'Pendiente'
WHERE NOT EXISTS (SELECT 1 FROM EstadoServicio WHERE nombre_estado = 'Pendiente');
SET @estado1 = (SELECT id_estado FROM EstadoServicio WHERE nombre_estado = 'Pendiente' ORDER BY id_estado LIMIT 1);

INSERT INTO EstadoServicio (nombre_estado)
SELECT 'En proceso'
WHERE NOT EXISTS (SELECT 1 FROM EstadoServicio WHERE nombre_estado = 'En proceso');
SET @estado2 = (SELECT id_estado FROM EstadoServicio WHERE nombre_estado = 'En proceso' ORDER BY id_estado LIMIT 1);

INSERT INTO EstadoServicio (nombre_estado)
SELECT 'Finalizado'
WHERE NOT EXISTS (SELECT 1 FROM EstadoServicio WHERE nombre_estado = 'Finalizado');
SET @estado3 = (SELECT id_estado FROM EstadoServicio WHERE nombre_estado = 'Finalizado' ORDER BY id_estado LIMIT 1);

INSERT INTO TipoServicio (nombre_tipo, descripcion)
SELECT 'Aseo', 'Servicios de limpieza y aseo'
WHERE NOT EXISTS (SELECT 1 FROM TipoServicio WHERE nombre_tipo = 'Aseo');
SET @tipo1 = (SELECT id_tipo_servicio FROM TipoServicio WHERE nombre_tipo = 'Aseo' ORDER BY id_tipo_servicio LIMIT 1);

INSERT INTO TipoServicio (nombre_tipo, descripcion)
SELECT 'Cuidado de niños', 'Niñera o cuidado infantil'
WHERE NOT EXISTS (SELECT 1 FROM TipoServicio WHERE nombre_tipo = 'Cuidado de niños');
SET @tipo2 = (SELECT id_tipo_servicio FROM TipoServicio WHERE nombre_tipo = 'Cuidado de niños' ORDER BY id_tipo_servicio LIMIT 1);

INSERT INTO TipoServicio (nombre_tipo, descripcion)
SELECT 'Jardinería', 'Mantenimiento de jardines'
WHERE NOT EXISTS (SELECT 1 FROM TipoServicio WHERE nombre_tipo = 'Jardinería');
SET @tipo3 = (SELECT id_tipo_servicio FROM TipoServicio WHERE nombre_tipo = 'Jardinería' ORDER BY id_tipo_servicio LIMIT 1);

INSERT INTO Usuario (nombre, correo, `contraseña_hash`, id_rol)
SELECT 'Administrador Demo', 'demo1@example.com', '95ca31eb706edfa6152d3255b79d83b349346d607fda23fa8104d565910a2db3', @rol1
WHERE NOT EXISTS (SELECT 1 FROM Usuario WHERE correo = 'demo1@example.com');
SET @usuario1 = (SELECT id_usuario FROM Usuario WHERE correo = 'demo1@example.com' ORDER BY id_usuario LIMIT 1);

INSERT INTO Usuario (nombre, correo, `contraseña_hash`, id_rol)
SELECT 'Ana Torres Demo', 'demo2@example.com', '95ca31eb706edfa6152d3255b79d83b349346d607fda23fa8104d565910a2db3', @rol2
WHERE NOT EXISTS (SELECT 1 FROM Usuario WHERE correo = 'demo2@example.com');
SET @usuario2 = (SELECT id_usuario FROM Usuario WHERE correo = 'demo2@example.com' ORDER BY id_usuario LIMIT 1);

INSERT INTO Usuario (nombre, correo, `contraseña_hash`, id_rol)
SELECT 'Carlos Ruiz Demo', 'demo3@example.com', '95ca31eb706edfa6152d3255b79d83b349346d607fda23fa8104d565910a2db3', @rol2
WHERE NOT EXISTS (SELECT 1 FROM Usuario WHERE correo = 'demo3@example.com');
SET @usuario3 = (SELECT id_usuario FROM Usuario WHERE correo = 'demo3@example.com' ORDER BY id_usuario LIMIT 1);

INSERT INTO Usuario (nombre, correo, `contraseña_hash`, id_rol)
SELECT 'Diana Perez Demo', 'demo4@example.com', '95ca31eb706edfa6152d3255b79d83b349346d607fda23fa8104d565910a2db3', @rol2
WHERE NOT EXISTS (SELECT 1 FROM Usuario WHERE correo = 'demo4@example.com');
SET @usuario4 = (SELECT id_usuario FROM Usuario WHERE correo = 'demo4@example.com' ORDER BY id_usuario LIMIT 1);

INSERT INTO Usuario (nombre, correo, `contraseña_hash`, id_rol)
SELECT 'Laura Gomez Demo', 'demo5@example.com', '95ca31eb706edfa6152d3255b79d83b349346d607fda23fa8104d565910a2db3', @rol3
WHERE NOT EXISTS (SELECT 1 FROM Usuario WHERE correo = 'demo5@example.com');
SET @usuario5 = (SELECT id_usuario FROM Usuario WHERE correo = 'demo5@example.com' ORDER BY id_usuario LIMIT 1);

INSERT INTO Usuario (nombre, correo, `contraseña_hash`, id_rol)
SELECT 'Miguel Castro Demo', 'demo6@example.com', '95ca31eb706edfa6152d3255b79d83b349346d607fda23fa8104d565910a2db3', @rol3
WHERE NOT EXISTS (SELECT 1 FROM Usuario WHERE correo = 'demo6@example.com');
SET @usuario6 = (SELECT id_usuario FROM Usuario WHERE correo = 'demo6@example.com' ORDER BY id_usuario LIMIT 1);

INSERT INTO Usuario (nombre, correo, `contraseña_hash`, id_rol)
SELECT 'Sara Lopez Demo', 'demo7@example.com', '95ca31eb706edfa6152d3255b79d83b349346d607fda23fa8104d565910a2db3', @rol3
WHERE NOT EXISTS (SELECT 1 FROM Usuario WHERE correo = 'demo7@example.com');
SET @usuario7 = (SELECT id_usuario FROM Usuario WHERE correo = 'demo7@example.com' ORDER BY id_usuario LIMIT 1);

INSERT INTO Empleador (id_usuario, direccion, telefono)
SELECT @usuario2, 'Direccion ficticia 1, Sabaneta', NULL
WHERE NOT EXISTS (SELECT 1 FROM Empleador WHERE id_usuario = @usuario2);
SET @empleador1 = (SELECT id_empleador FROM Empleador WHERE id_usuario = @usuario2 ORDER BY id_empleador LIMIT 1);

INSERT INTO Trabajador (id_usuario, experiencia, disponibilidad)
SELECT @usuario5, 'Dos años en limpieza residencial', 'Lunes a viernes, 08:00 a 17:00'
WHERE NOT EXISTS (SELECT 1 FROM Trabajador WHERE id_usuario = @usuario5);
SET @trabajador1 = (SELECT id_trabajador FROM Trabajador WHERE id_usuario = @usuario5 ORDER BY id_trabajador LIMIT 1);

INSERT INTO DocumentoTrabajador (id_trabajador, tipo_documento, archivo_blob, fecha_carga)
SELECT @trabajador1, 'Hoja de vida demo', NULL, '2026-11-01 08:00:00'
WHERE NOT EXISTS (SELECT 1 FROM DocumentoTrabajador WHERE id_trabajador = @trabajador1 AND tipo_documento = 'Hoja de vida demo');

INSERT INTO Empleador (id_usuario, direccion, telefono)
SELECT @usuario3, 'Direccion ficticia 2, Sabaneta', NULL
WHERE NOT EXISTS (SELECT 1 FROM Empleador WHERE id_usuario = @usuario3);
SET @empleador2 = (SELECT id_empleador FROM Empleador WHERE id_usuario = @usuario3 ORDER BY id_empleador LIMIT 1);

INSERT INTO Trabajador (id_usuario, experiencia, disponibilidad)
SELECT @usuario6, 'Tres años en cuidado infantil', 'Lunes a viernes, 08:00 a 17:00'
WHERE NOT EXISTS (SELECT 1 FROM Trabajador WHERE id_usuario = @usuario6);
SET @trabajador2 = (SELECT id_trabajador FROM Trabajador WHERE id_usuario = @usuario6 ORDER BY id_trabajador LIMIT 1);

INSERT INTO DocumentoTrabajador (id_trabajador, tipo_documento, archivo_blob, fecha_carga)
SELECT @trabajador2, 'Hoja de vida demo', NULL, '2026-11-01 08:00:00'
WHERE NOT EXISTS (SELECT 1 FROM DocumentoTrabajador WHERE id_trabajador = @trabajador2 AND tipo_documento = 'Hoja de vida demo');

INSERT INTO Empleador (id_usuario, direccion, telefono)
SELECT @usuario4, 'Direccion ficticia 3, Sabaneta', NULL
WHERE NOT EXISTS (SELECT 1 FROM Empleador WHERE id_usuario = @usuario4);
SET @empleador3 = (SELECT id_empleador FROM Empleador WHERE id_usuario = @usuario4 ORDER BY id_empleador LIMIT 1);

INSERT INTO Trabajador (id_usuario, experiencia, disponibilidad)
SELECT @usuario7, 'Cuatro años en jardineria', 'Lunes a viernes, 08:00 a 17:00'
WHERE NOT EXISTS (SELECT 1 FROM Trabajador WHERE id_usuario = @usuario7);
SET @trabajador3 = (SELECT id_trabajador FROM Trabajador WHERE id_usuario = @usuario7 ORDER BY id_trabajador LIMIT 1);

INSERT INTO DocumentoTrabajador (id_trabajador, tipo_documento, archivo_blob, fecha_carga)
SELECT @trabajador3, 'Hoja de vida demo', NULL, '2026-11-01 08:00:00'
WHERE NOT EXISTS (SELECT 1 FROM DocumentoTrabajador WHERE id_trabajador = @trabajador3 AND tipo_documento = 'Hoja de vida demo');

INSERT INTO Servicio (id_empleador, id_tipo_servicio, id_estado, fecha, hora_inicio, hora_fin, ubicacion, valor_ofertado)
SELECT @empleador1, @tipo1, @estado3, '2026-11-10', '08:00:00', '12:00:00', 'Ubicacion ficticia demo 1', 100000
WHERE NOT EXISTS (SELECT 1 FROM Servicio WHERE id_empleador = @empleador1 AND ubicacion = 'Ubicacion ficticia demo 1' AND fecha = '2026-11-10');
SET @servicio1 = (SELECT id_servicio FROM Servicio WHERE id_empleador = @empleador1 AND ubicacion = 'Ubicacion ficticia demo 1' AND fecha = '2026-11-10' ORDER BY id_servicio LIMIT 1);

INSERT INTO Postulacion (id_servicio, id_trabajador, fecha_postulacion)
SELECT @servicio1, @trabajador1, '2026-11-02 09:00:00'
WHERE NOT EXISTS (SELECT 1 FROM Postulacion WHERE id_servicio = @servicio1 AND id_trabajador = @trabajador1);

INSERT INTO Contratacion (id_servicio, id_trabajador, fecha_contratacion)
SELECT @servicio1, @trabajador1, '2026-11-03 10:00:00'
WHERE NOT EXISTS (SELECT 1 FROM Contratacion WHERE id_servicio = @servicio1 AND id_trabajador = @trabajador1);
SET @contratacion1 = (SELECT id_contratacion FROM Contratacion WHERE id_servicio = @servicio1 AND id_trabajador = @trabajador1 ORDER BY id_contratacion LIMIT 1);

INSERT INTO Pago (id_contratacion, valor_servicio, porcentaje_comision_app, valor_comision, valor_trabajador, fecha_pago)
SELECT @contratacion1, 100000, 10, 10000, 90000, '2026-11-11 10:00:00'
WHERE NOT EXISTS (SELECT 1 FROM Pago WHERE id_contratacion = @contratacion1);

INSERT INTO Servicio (id_empleador, id_tipo_servicio, id_estado, fecha, hora_inicio, hora_fin, ubicacion, valor_ofertado)
SELECT @empleador2, @tipo2, @estado3, '2026-11-10', '08:00:00', '12:00:00', 'Ubicacion ficticia demo 2', 200000
WHERE NOT EXISTS (SELECT 1 FROM Servicio WHERE id_empleador = @empleador2 AND ubicacion = 'Ubicacion ficticia demo 2' AND fecha = '2026-11-10');
SET @servicio2 = (SELECT id_servicio FROM Servicio WHERE id_empleador = @empleador2 AND ubicacion = 'Ubicacion ficticia demo 2' AND fecha = '2026-11-10' ORDER BY id_servicio LIMIT 1);

INSERT INTO Postulacion (id_servicio, id_trabajador, fecha_postulacion)
SELECT @servicio2, @trabajador2, '2026-11-02 09:00:00'
WHERE NOT EXISTS (SELECT 1 FROM Postulacion WHERE id_servicio = @servicio2 AND id_trabajador = @trabajador2);

INSERT INTO Contratacion (id_servicio, id_trabajador, fecha_contratacion)
SELECT @servicio2, @trabajador2, '2026-11-03 10:00:00'
WHERE NOT EXISTS (SELECT 1 FROM Contratacion WHERE id_servicio = @servicio2 AND id_trabajador = @trabajador2);
SET @contratacion2 = (SELECT id_contratacion FROM Contratacion WHERE id_servicio = @servicio2 AND id_trabajador = @trabajador2 ORDER BY id_contratacion LIMIT 1);

INSERT INTO Pago (id_contratacion, valor_servicio, porcentaje_comision_app, valor_comision, valor_trabajador, fecha_pago)
SELECT @contratacion2, 200000, 10, 20000, 180000, '2026-11-11 10:00:00'
WHERE NOT EXISTS (SELECT 1 FROM Pago WHERE id_contratacion = @contratacion2);

INSERT INTO Servicio (id_empleador, id_tipo_servicio, id_estado, fecha, hora_inicio, hora_fin, ubicacion, valor_ofertado)
SELECT @empleador3, @tipo3, @estado3, '2026-11-10', '08:00:00', '12:00:00', 'Ubicacion ficticia demo 3', 300000
WHERE NOT EXISTS (SELECT 1 FROM Servicio WHERE id_empleador = @empleador3 AND ubicacion = 'Ubicacion ficticia demo 3' AND fecha = '2026-11-10');
SET @servicio3 = (SELECT id_servicio FROM Servicio WHERE id_empleador = @empleador3 AND ubicacion = 'Ubicacion ficticia demo 3' AND fecha = '2026-11-10' ORDER BY id_servicio LIMIT 1);

INSERT INTO Postulacion (id_servicio, id_trabajador, fecha_postulacion)
SELECT @servicio3, @trabajador3, '2026-11-02 09:00:00'
WHERE NOT EXISTS (SELECT 1 FROM Postulacion WHERE id_servicio = @servicio3 AND id_trabajador = @trabajador3);

INSERT INTO Contratacion (id_servicio, id_trabajador, fecha_contratacion)
SELECT @servicio3, @trabajador3, '2026-11-03 10:00:00'
WHERE NOT EXISTS (SELECT 1 FROM Contratacion WHERE id_servicio = @servicio3 AND id_trabajador = @trabajador3);
SET @contratacion3 = (SELECT id_contratacion FROM Contratacion WHERE id_servicio = @servicio3 AND id_trabajador = @trabajador3 ORDER BY id_contratacion LIMIT 1);

INSERT INTO Pago (id_contratacion, valor_servicio, porcentaje_comision_app, valor_comision, valor_trabajador, fecha_pago)
SELECT @contratacion3, 300000, 10, 30000, 270000, '2026-11-11 10:00:00'
WHERE NOT EXISTS (SELECT 1 FROM Pago WHERE id_contratacion = @contratacion3);

COMMIT;

-- Verificacion: cantidad total de registros en las 11 tablas.
SELECT 'Rol' AS tabla, COUNT(*) AS registros FROM Rol
UNION ALL
SELECT 'Usuario' AS tabla, COUNT(*) AS registros FROM Usuario
UNION ALL
SELECT 'Empleador' AS tabla, COUNT(*) AS registros FROM Empleador
UNION ALL
SELECT 'Trabajador' AS tabla, COUNT(*) AS registros FROM Trabajador
UNION ALL
SELECT 'DocumentoTrabajador' AS tabla, COUNT(*) AS registros FROM DocumentoTrabajador
UNION ALL
SELECT 'TipoServicio' AS tabla, COUNT(*) AS registros FROM TipoServicio
UNION ALL
SELECT 'EstadoServicio' AS tabla, COUNT(*) AS registros FROM EstadoServicio
UNION ALL
SELECT 'Servicio' AS tabla, COUNT(*) AS registros FROM Servicio
UNION ALL
SELECT 'Postulacion' AS tabla, COUNT(*) AS registros FROM Postulacion
UNION ALL
SELECT 'Contratacion' AS tabla, COUNT(*) AS registros FROM Contratacion
UNION ALL
SELECT 'Pago' AS tabla, COUNT(*) AS registros FROM Pago;
