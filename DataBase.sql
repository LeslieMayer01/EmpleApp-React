CREATE DATABASE empleapp;
USE empleapp;

-- Tabla Rol
CREATE TABLE Rol (
    id_rol INT AUTO_INCREMENT PRIMARY KEY,
    nombre_rol VARCHAR(50) NOT NULL
);

-- Tabla Usuario
CREATE TABLE Usuario (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    correo VARCHAR(100) NOT NULL UNIQUE,
    contraseña_hash VARCHAR(255) NOT NULL,
    id_rol INT NOT NULL,

    CONSTRAINT fk_usuario_rol
        FOREIGN KEY (id_rol)
        REFERENCES Rol(id_rol)
);

-- Tabla Empleador
CREATE TABLE Empleador (
    id_empleador INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT NOT NULL,
    direccion VARCHAR(255),
    telefono VARCHAR(20),

    CONSTRAINT fk_empleador_usuario
        FOREIGN KEY (id_usuario)
        REFERENCES Usuario(id_usuario)
);

-- Tabla Trabajador
CREATE TABLE Trabajador (
    id_trabajador INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT NOT NULL,
    experiencia TEXT,
    disponibilidad VARCHAR(100),

    CONSTRAINT fk_trabajador_usuario
        FOREIGN KEY (id_usuario)
        REFERENCES Usuario(id_usuario)
);

-- Tabla DocumentoTrabajador
CREATE TABLE DocumentoTrabajador (
    id_documento INT AUTO_INCREMENT PRIMARY KEY,
    id_trabajador INT NOT NULL,
    tipo_documento VARCHAR(100),
    archivo_blob LONGBLOB,
    fecha_carga DATETIME DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_documento_trabajador
        FOREIGN KEY (id_trabajador)
        REFERENCES Trabajador(id_trabajador)
);

-- Tabla TipoServicio
CREATE TABLE TipoServicio (
    id_tipo_servicio INT AUTO_INCREMENT PRIMARY KEY,
    nombre_tipo VARCHAR(100) NOT NULL,
    descripcion TEXT
);

-- Tabla EstadoServicio
CREATE TABLE EstadoServicio (
    id_estado INT AUTO_INCREMENT PRIMARY KEY,
    nombre_estado VARCHAR(50) NOT NULL
);

-- Tabla Servicio
CREATE TABLE Servicio (
    id_servicio INT AUTO_INCREMENT PRIMARY KEY,
    id_empleador INT NOT NULL,
    id_tipo_servicio INT NOT NULL,
    id_estado INT NOT NULL,
    fecha DATE,
    hora_inicio TIME,
    hora_fin TIME,
    ubicacion VARCHAR(255),
    valor_ofertado DECIMAL(10,2),

    CONSTRAINT fk_servicio_empleador
        FOREIGN KEY (id_empleador)
        REFERENCES Empleador(id_empleador),

    CONSTRAINT fk_servicio_tipo
        FOREIGN KEY (id_tipo_servicio)
        REFERENCES TipoServicio(id_tipo_servicio),

    CONSTRAINT fk_servicio_estado
        FOREIGN KEY (id_estado)
        REFERENCES EstadoServicio(id_estado)
);

-- Tabla Postulación
CREATE TABLE Postulacion (
    id_postulacion INT AUTO_INCREMENT PRIMARY KEY,
    id_servicio INT NOT NULL,
    id_trabajador INT NOT NULL,
    fecha_postulacion DATETIME DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_postulacion_servicio
        FOREIGN KEY (id_servicio)
        REFERENCES Servicio(id_servicio),

    CONSTRAINT fk_postulacion_trabajador
        FOREIGN KEY (id_trabajador)
        REFERENCES Trabajador(id_trabajador)
);

-- Tabla Contratación
CREATE TABLE Contratacion (
    id_contratacion INT AUTO_INCREMENT PRIMARY KEY,
    id_servicio INT NOT NULL,
    id_trabajador INT NOT NULL,
    fecha_contratacion DATETIME DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_contratacion_servicio
        FOREIGN KEY (id_servicio)
        REFERENCES Servicio(id_servicio),

    CONSTRAINT fk_contratacion_trabajador
        FOREIGN KEY (id_trabajador)
        REFERENCES Trabajador(id_trabajador)
);

-- Tabla Pago
CREATE TABLE Pago (
    id_pago INT AUTO_INCREMENT PRIMARY KEY,
    id_contratacion INT NOT NULL,
    valor_servicio DECIMAL(10,2),
    porcentaje_comision_app DECIMAL(5,2),
    valor_comision DECIMAL(10,2),
    valor_trabajador DECIMAL(10,2),
    fecha_pago DATETIME DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_pago_contratacion
        FOREIGN KEY (id_contratacion)
        REFERENCES Contratacion(id_contratacion)
);

-- Datos iniciales para Rol
INSERT INTO Rol (nombre_rol)
VALUES
('Administrador'),
('Empleador'),
('Trabajador');

-- Datos iniciales para EstadoServicio
INSERT INTO EstadoServicio (nombre_estado)
VALUES
('Pendiente'),
('En proceso'),
('Finalizado');

-- Datos iniciales para TipoServicio
INSERT INTO TipoServicio (nombre_tipo, descripcion)
VALUES
('Aseo', 'Servicios de limpieza y aseo'),
('Cuidado de niños', 'Niñera o cuidado infantil'),
('Jardinería', 'Mantenimiento de jardines');
