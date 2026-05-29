-- Datos minimos para probar SmartCampus UTA Web.
-- Ejecutar despues de Queries.sql.

INSERT INTO Rol (id, nombre, descripcion) VALUES
    (1, 'ADMIN', 'Administrador del sistema'),
    (2, 'EMPLEADO', 'Usuario empleado'),
    (3, 'ESTUDIANTE', 'Usuario estudiante')
ON CONFLICT (id) DO UPDATE
SET nombre = EXCLUDED.nombre,
    descripcion = EXCLUDED.descripcion;

SELECT setval('rol_id_seq', GREATEST((SELECT MAX(id) FROM Rol), 3));

INSERT INTO Persona (id, nombre, cedula, telefono, direccion) VALUES
    (1, 'Estudiante Demo', '1800000001', '0999999999', 'Ambato'),
    (2, 'Empleado Demo', '1800000002', '0999999998', 'Ambato')
ON CONFLICT (id) DO NOTHING;

SELECT setval('persona_id_seq', GREATEST((SELECT MAX(id) FROM Persona), 2));

INSERT INTO Estudiante (id, carrera, semestre, matricula)
VALUES (1, 'Software', 3, 'UTA-001')
ON CONFLICT (id) DO NOTHING;

INSERT INTO Empleado (id, cargo, departamento)
VALUES (2, 'Empleado', 'Atencion estudiantil')
ON CONFLICT (id) DO NOTHING;

INSERT INTO CategoriaTramite (id, nombre, descripcion)
VALUES (1, 'Academico', 'Tramites academicos')
ON CONFLICT (id) DO NOTHING;

SELECT setval('categoriatramite_id_seq', GREATEST((SELECT MAX(id) FROM CategoriaTramite), 1));

INSERT INTO TipoTramite (id, nombre, descripcion, categoria_id)
VALUES (1, 'Solicitud de certificado', 'Emision de certificado academico', 1)
ON CONFLICT (id) DO NOTHING;

SELECT setval('tipotramite_id_seq', GREATEST((SELECT MAX(id) FROM TipoTramite), 1));

INSERT INTO Ventanilla (id, nombre, ubicacion, activa)
VALUES (1, 'Ventanilla 1', 'Bloque Administrativo', TRUE)
ON CONFLICT (id) DO NOTHING;

SELECT setval('ventanilla_id_seq', GREATEST((SELECT MAX(id) FROM Ventanilla), 1));

INSERT INTO CategoriaDocumento (id, nombre, descripcion, padre_id, nivel) VALUES
    (1, 'Expedientes', 'Documentos de tramites', NULL, 0),
    (2, 'Academicos', 'Certificados y solicitudes', 1, 1)
ON CONFLICT (id) DO NOTHING;

SELECT setval('categoriadocumento_id_seq', GREATEST((SELECT MAX(id) FROM CategoriaDocumento), 2));

INSERT INTO PuntoRuta (id, nombre, descripcion, tipo) VALUES
    (1, 'Entrada principal', 'Acceso campus', 'otro'),
    (2, 'Biblioteca', 'Biblioteca central', 'biblioteca'),
    (3, 'Bloque Administrativo', 'Atencion estudiantil', 'edificio')
ON CONFLICT (id) DO NOTHING;

SELECT setval('puntoruta_id_seq', GREATEST((SELECT MAX(id) FROM PuntoRuta), 3));

INSERT INTO ConexionRuta (punto_origen_id, punto_destino_id, distancia_metros, tiempo_estimado_minutos)
VALUES
    (1, 2, 250, 4),
    (2, 3, 180, 3),
    (1, 3, 360, 6)
ON CONFLICT (punto_origen_id, punto_destino_id) DO NOTHING;
