-- MySQL 8.0.16 o posterior. Ejecutar SOLO tras revisar revision_integridad.sql,
-- respaldar datos y resolver registros incompatibles. No modifica ni borra importes historicos.
-- DDL en MySQL realiza commits implicitos. No ejecutar repetidamente: nombres de CHECK son unicos.
USE empleapp;
ALTER TABLE Servicio ADD CONSTRAINT ck_servicio_horario CHECK (
 (fecha IS NOT NULL AND hora_inicio IS NOT NULL AND hora_fin IS NOT NULL AND hora_fin > hora_inicio));
ALTER TABLE Servicio ADD CONSTRAINT ck_servicio_valor CHECK (valor_ofertado IS NOT NULL AND valor_ofertado > 0 AND ubicacion IS NOT NULL AND TRIM(ubicacion) <> '');
ALTER TABLE Pago ADD CONSTRAINT ck_pago_importes CHECK (
 valor_servicio IS NOT NULL AND valor_servicio > 0 AND porcentaje_comision_app IS NOT NULL
 AND porcentaje_comision_app = 10 AND valor_comision IS NOT NULL AND valor_trabajador IS NOT NULL
 AND valor_comision = ROUND(valor_servicio * 0.10, 2)
 AND valor_trabajador = valor_servicio - valor_comision);
