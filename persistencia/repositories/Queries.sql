-- Copia normalizada del script base entregado para SmartCampus UTA Web.
-- Ejecutar este archivo en el SQL editor de Supabase antes de iniciar la app.
-- El script original esta en: C:\Users\Joss\Desktop\script de la base de datos.sql

SET search_path TO public;

CREATE TABLE IF NOT EXISTS Rol (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    descripcion TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS Usuario (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    rol_id INT NOT NULL REFERENCES Rol(id) ON DELETE RESTRICT,
    activo BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS Persona (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    cedula VARCHAR(20) UNIQUE NOT NULL,
    telefono VARCHAR(20),
    direccion TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS Estudiante (
    id INT PRIMARY KEY REFERENCES Persona(id) ON DELETE CASCADE,
    carrera VARCHAR(100) NOT NULL,
    semestre INT DEFAULT 1,
    matricula VARCHAR(20) UNIQUE
);

CREATE TABLE IF NOT EXISTS Empleado (
    id INT PRIMARY KEY REFERENCES Persona(id) ON DELETE CASCADE,
    cargo VARCHAR(100) NOT NULL,
    departamento VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS CategoriaTramite (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    activo BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS TipoTramite (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    duracion_estimada_dias INT DEFAULT 5,
    costo DECIMAL(10, 2) DEFAULT 0.00,
    requiere_aprobacion BOOLEAN DEFAULT TRUE,
    activo BOOLEAN DEFAULT TRUE,
    categoria_id INT REFERENCES CategoriaTramite(id) ON DELETE SET NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS Requisito (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(200) NOT NULL,
    descripcion TEXT,
    obligatorio BOOLEAN DEFAULT TRUE,
    tipo_documento VARCHAR(20) DEFAULT 'PDF',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CHECK (tipo_documento IN ('PDF', 'IMG', 'DOC', 'XLS', 'OTRO'))
);

CREATE TABLE IF NOT EXISTS TramiteRequisito (
    id SERIAL PRIMARY KEY,
    tipo_tramite_id INT NOT NULL REFERENCES TipoTramite(id) ON DELETE CASCADE,
    requisito_id INT NOT NULL REFERENCES Requisito(id) ON DELETE CASCADE,
    orden INT DEFAULT 0,
    UNIQUE(tipo_tramite_id, requisito_id)
);

CREATE TABLE IF NOT EXISTS Tramite (
    id SERIAL PRIMARY KEY,
    codigo_unico VARCHAR(50) UNIQUE NOT NULL,
    tipo_tramite_id INT NOT NULL REFERENCES TipoTramite(id) ON DELETE RESTRICT,
    estudiante_id INT NOT NULL REFERENCES Estudiante(id) ON DELETE RESTRICT,
    empleado_id INT REFERENCES Empleado(id) ON DELETE SET NULL,
    descripcion TEXT,
    fecha_solicitud TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_limite TIMESTAMP,
    fecha_resolucion TIMESTAMP,
    estado VARCHAR(20) DEFAULT 'pendiente',
    prioridad VARCHAR(10) DEFAULT 'media',
    observaciones TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CHECK (estado IN ('pendiente', 'asignado', 'en_revision', 'atendido', 'finalizado', 'requiere_correccion', 'aprobado', 'rechazado')),
    CHECK (prioridad IN ('baja', 'media', 'alta', 'urgente'))
);

CREATE TABLE IF NOT EXISTS Solicitud (
    id SERIAL PRIMARY KEY,
    descripcion TEXT NOT NULL,
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    estado VARCHAR(50) DEFAULT 'pendiente',
    usuario_id INT NOT NULL REFERENCES Usuario(id) ON DELETE RESTRICT,
    tramite_id INT REFERENCES Tramite(id) ON DELETE SET NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS Turno (
    id SERIAL PRIMARY KEY,
    numero INT NOT NULL,
    estudiante_id INT NOT NULL REFERENCES Estudiante(id) ON DELETE RESTRICT,
    empleado_id INT REFERENCES Empleado(id) ON DELETE SET NULL,
    estado VARCHAR(20) DEFAULT 'pendiente',
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_atencion TIMESTAMP,
    ventanilla_id INT,
    CHECK (estado IN ('pendiente', 'asignado', 'en_revision', 'atendido', 'finalizado', 'cancelado'))
);

CREATE TABLE IF NOT EXISTS Ventanilla (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    ubicacion VARCHAR(100),
    activa BOOLEAN DEFAULT TRUE,
    empleado_actual_id INT REFERENCES Empleado(id) ON DELETE SET NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS CategoriaDocumento (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    padre_id INT REFERENCES CategoriaDocumento(id) ON DELETE CASCADE,
    nivel INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS Documento (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(200) NOT NULL,
    ruta_archivo VARCHAR(500) NOT NULL,
    tipo_archivo VARCHAR(50),
    tamano INT,
    categoria_id INT NOT NULL REFERENCES CategoriaDocumento(id) ON DELETE RESTRICT,
    tramite_id INT REFERENCES Tramite(id) ON DELETE SET NULL,
    usuario_subida_id INT NOT NULL REFERENCES Usuario(id) ON DELETE RESTRICT,
    fecha_subida TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version INT DEFAULT 1
);

CREATE TABLE IF NOT EXISTS DocumentoTramite (
    id SERIAL PRIMARY KEY,
    tramite_id INT NOT NULL REFERENCES Tramite(id) ON DELETE CASCADE,
    requisito_id INT NOT NULL REFERENCES Requisito(id) ON DELETE RESTRICT,
    documento_id INT NOT NULL REFERENCES Documento(id) ON DELETE CASCADE,
    fecha_entrega TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    observacion TEXT
);

CREATE TABLE IF NOT EXISTS PuntoRuta (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    latitud DECIMAL(10, 8),
    longitud DECIMAL(11, 8),
    tipo VARCHAR(20) DEFAULT 'otro',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CHECK (tipo IN ('edificio', 'cafeteria', 'biblioteca', 'laboratorio', 'auditorio', 'parqueadero', 'otro'))
);

CREATE TABLE IF NOT EXISTS ConexionRuta (
    id SERIAL PRIMARY KEY,
    punto_origen_id INT NOT NULL REFERENCES PuntoRuta(id) ON DELETE CASCADE,
    punto_destino_id INT NOT NULL REFERENCES PuntoRuta(id) ON DELETE CASCADE,
    distancia_metros INT NOT NULL,
    tiempo_estimado_minutos INT,
    es_bidireccional BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(punto_origen_id, punto_destino_id)
);

CREATE TABLE IF NOT EXISTS HistorialTramite (
    id SERIAL PRIMARY KEY,
    tramite_id INT NOT NULL REFERENCES Tramite(id) ON DELETE CASCADE,
    usuario_id INT NOT NULL REFERENCES Usuario(id) ON DELETE RESTRICT,
    estado_anterior VARCHAR(50),
    estado_nuevo VARCHAR(50),
    comentario TEXT,
    fecha_cambio TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS HistorialAccion (
    id SERIAL PRIMARY KEY,
    usuario_id INT NOT NULL REFERENCES Usuario(id) ON DELETE RESTRICT,
    accion VARCHAR(100) NOT NULL,
    entidad VARCHAR(50),
    entidad_id INT,
    detalles TEXT,
    ip_address VARCHAR(45),
    fecha_accion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS Notificacion (
    id SERIAL PRIMARY KEY,
    usuario_id INT NOT NULL REFERENCES Usuario(id) ON DELETE CASCADE,
    tramite_id INT REFERENCES Tramite(id) ON DELETE SET NULL,
    titulo VARCHAR(200) NOT NULL,
    mensaje TEXT NOT NULL,
    leida BOOLEAN DEFAULT FALSE,
    fecha_envio TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE Turno ADD COLUMN IF NOT EXISTS empleado_id INT REFERENCES Empleado(id) ON DELETE SET NULL;

ALTER TABLE Tramite DROP CONSTRAINT IF EXISTS tramite_estado_check;
ALTER TABLE Tramite ADD CONSTRAINT tramite_estado_check
    CHECK (estado IN ('pendiente', 'asignado', 'en_revision', 'atendido', 'finalizado', 'requiere_correccion', 'aprobado', 'rechazado'));

ALTER TABLE Turno DROP CONSTRAINT IF EXISTS turno_estado_check;
ALTER TABLE Turno ADD CONSTRAINT turno_estado_check
    CHECK (estado IN ('pendiente', 'asignado', 'en_revision', 'atendido', 'finalizado', 'cancelado'));

CREATE INDEX IF NOT EXISTS idx_tipotramite_activo ON TipoTramite(activo);
CREATE INDEX IF NOT EXISTS idx_tramite_estado ON Tramite(estado);
CREATE INDEX IF NOT EXISTS idx_tramite_empleado ON Tramite(empleado_id);
CREATE INDEX IF NOT EXISTS idx_tramite_codigo ON Tramite(codigo_unico);
CREATE INDEX IF NOT EXISTS idx_tramite_fecha_solicitud ON Tramite(fecha_solicitud);
CREATE INDEX IF NOT EXISTS idx_tramite_prioridad ON Tramite(prioridad);
CREATE INDEX IF NOT EXISTS idx_solicitud_estado ON Solicitud(estado);
CREATE INDEX IF NOT EXISTS idx_solicitud_fecha ON Solicitud(fecha);
CREATE INDEX IF NOT EXISTS idx_turno_estado ON Turno(estado);
CREATE INDEX IF NOT EXISTS idx_turno_empleado ON Turno(empleado_id);
CREATE INDEX IF NOT EXISTS idx_turno_numero ON Turno(numero);
CREATE INDEX IF NOT EXISTS idx_categoriadoc_padre ON CategoriaDocumento(padre_id);
CREATE INDEX IF NOT EXISTS idx_documento_categoria ON Documento(categoria_id);
CREATE INDEX IF NOT EXISTS idx_documento_tramite ON Documento(tramite_id);
CREATE INDEX IF NOT EXISTS idx_puntoruta_tipo ON PuntoRuta(tipo);
CREATE INDEX IF NOT EXISTS idx_conexion_origen ON ConexionRuta(punto_origen_id);
CREATE INDEX IF NOT EXISTS idx_conexion_destino ON ConexionRuta(punto_destino_id);
CREATE INDEX IF NOT EXISTS idx_historial_tramite ON HistorialTramite(tramite_id);
CREATE INDEX IF NOT EXISTS idx_historial_fecha ON HistorialTramite(fecha_cambio);
CREATE INDEX IF NOT EXISTS idx_historialaccion_usuario ON HistorialAccion(usuario_id);
CREATE INDEX IF NOT EXISTS idx_historialaccion_fecha ON HistorialAccion(fecha_accion);
CREATE INDEX IF NOT EXISTS idx_historialaccion_entidad ON HistorialAccion(entidad, entidad_id);
CREATE INDEX IF NOT EXISTS idx_notificacion_usuario_leida ON Notificacion(usuario_id, leida);
CREATE INDEX IF NOT EXISTS idx_notificacion_fecha ON Notificacion(fecha_envio);
