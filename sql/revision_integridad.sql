-- Auditoria de solo lectura. Ejecutar antes de proponer restricciones nuevas.
USE empleapp;
SELECT * FROM Servicio WHERE
 (hora_inicio IS NOT NULL OR hora_fin IS NOT NULL) AND
 (fecha IS NULL OR hora_inicio IS NULL OR hora_fin IS NULL OR hora_fin <= hora_inicio);
SELECT * FROM Servicio WHERE valor_ofertado IS NULL OR valor_ofertado <= 0 OR fecha IS NULL
 OR hora_inicio IS NULL OR hora_fin IS NULL OR ubicacion IS NULL OR TRIM(ubicacion) = '';
SELECT id_pago, id_contratacion, valor_servicio, porcentaje_comision_app, valor_comision, valor_trabajador
FROM Pago WHERE valor_servicio IS NULL OR valor_servicio <= 0 OR porcentaje_comision_app IS NULL
 OR porcentaje_comision_app <> 10 OR valor_comision IS NULL OR valor_trabajador IS NULL
 OR valor_comision <> ROUND(valor_servicio * 0.10, 2)
 OR valor_trabajador <> valor_servicio - valor_comision;
SELECT LOWER(TRIM(correo)) correo_normalizado, COUNT(*) cantidad FROM Usuario
GROUP BY LOWER(TRIM(correo)) HAVING COUNT(*) > 1;
SELECT id_usuario, COUNT(*) cantidad FROM Trabajador GROUP BY id_usuario HAVING COUNT(*) > 1;
SELECT id_usuario, COUNT(*) cantidad FROM Empleador GROUP BY id_usuario HAVING COUNT(*) > 1;
SELECT id_servicio, id_trabajador, COUNT(*) cantidad FROM Postulacion
GROUP BY id_servicio, id_trabajador HAVING COUNT(*) > 1;
SELECT id_servicio, COUNT(*) cantidad FROM Contratacion GROUP BY id_servicio HAVING COUNT(*) > 1;
SELECT id_contratacion, COUNT(*) cantidad FROM Pago GROUP BY id_contratacion HAVING COUNT(*) > 1;
