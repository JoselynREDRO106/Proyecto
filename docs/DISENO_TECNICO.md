# Diseno tecnico - SmartCampus UTA Web

## Capas

Presentacion:
- `presentacion/controllers/SmartCampusController.java`: servlet frontal para endpoints JSON bajo `/api/*`.
- `presentacion/web`: frontend HTML, CSS y JS. La navegacion por rol se calcula en `assets/js/auth.js` y `assets/js/componentes/navbar.js`.

Aplicacion:
- `AuthService`: registro de estudiantes, creacion de empleados por administrador y autenticacion.
- `TramiteService`: solicitudes, listado global, estado, historial, expediente y deshacer.
- `AtencionService`: cola FIFO de turnos y asignacion de ventanillas con lista circular.
- `DocumentoService`: arbol de categorias documentales.
- `RutasService`: grafo del campus y ruta mas corta.

Dominio:
- `models`: POJO para `Usuario`, `Tramite`, `Turno`, `Documento`, `PuntoRuta`, etc.
- `estructuras`: implementaciones propias de lista secuencial, lista simple, lista doble, lista circular, pila, cola, arbol y grafo.

Persistencia:
- `DatabaseConnection`: conexion JDBC a PostgreSQL/Supabase mediante variables de entorno.
- `dao`: consultas parametrizadas con `PreparedStatement`.
- `repositories/Queries.sql`: esquema base.
- `repositories/SeedData.sql`: datos minimos alineados con los roles `ADMIN=1`, `EMPLEADO=2`, `ESTUDIANTE=3`.

## Casos por estructura

| Estructura | Clase | Caso de uso |
| --- | --- | --- |
| Lista secuencial | `ListaSecuencial` | Catalogos compactos: puntos de ruta y categorias documentales. |
| Lista simple | `ListaSimple` | Historial dinamico de cambios de tramite. |
| Lista doble | `ListaDoble` | Expediente navegable adelante/atras. |
| Lista circular | `ListaCircular` | Rotacion de ventanillas activas. |
| Cola | `Cola` | Turnos pendientes con atencion FIFO. |
| Pila | `Pila` | Deshacer el ultimo cambio de estado. |
| Arbol | `Arbol` | Jerarquia padre-hijo de categorias documentales. |
| Grafo | `Grafo` | Mapa del campus con Dijkstra para ruta mas corta. |

## Endpoints activos

| Metodo | Ruta | Descripcion |
| --- | --- | --- |
| POST | `/api/auth/registro` | Registra estudiantes. |
| POST | `/api/auth/login` | Verifica credenciales y devuelve usuario. |
| POST | `/api/auth/logout` | Respuesta simple para cierre de sesion del frontend. |
| GET | `/api/auth/usuarios` | Lista usuarios para administrador. |
| POST | `/api/auth/empleados` | Crea empleados desde administracion. |
| GET | `/api/tramites` | Lista tramites para paneles de administrador y empleado. |
| POST | `/api/tramites` | Registra solicitud de tramite. |
| GET | `/api/tramites/estado?tramiteId=1` | Consulta estado de un tramite. |
| GET | `/api/tramites/historial?tramiteId=1` | Lista historial del tramite. |
| POST | `/api/tramites/estado` | Cambia estado y registra historial. |
| POST | `/api/tramites/deshacer` | Revierte ultimo cambio usando pila. |
| GET | `/api/turnos/pendientes` | Lista cola pendiente. |
| POST | `/api/turnos` | Crea turno pendiente. |
| POST | `/api/turnos/atender` | Desencola y asigna ventanilla. |
| GET | `/api/documentos/arbol` | Devuelve arbol documental. |
| GET | `/api/rutas/puntos` | Lista puntos del campus. |
| GET | `/api/rutas/calcular?origen=1&destino=2` | Calcula ruta mas corta. |
| GET | `/api/reportes/turnos-atendidos` | Devuelve total usado por KPIs. |
| GET | `/api/reportes/tramites-periodo` | Devuelve total de tramites registrados. |
| GET | `/api/reportes/usuarios-por-rol` | Devuelve conteo por rol. |

## Frontend por rol

`auth.js` normaliza el usuario devuelto por el backend y redirige asi:

- Rol `1`: `roles/administrador/dashboard.html`
- Rol `2`: `roles/empleado/dashboard.html`
- Rol `3`: `roles/estudiante/dashboard.html`

Las pantallas de administrador y empleado ya consumen rutas existentes para reportes, turnos pendientes y tramites globales, evitando errores de `Ruta no encontrada` por endpoints no implementados.

## Conexion Supabase

JDBC usa:

```text
jdbc:postgresql://db.gjqrqfxgzewbrhovwtyi.supabase.co:5432/postgres?sslmode=require
```

Las credenciales se leen desde variables de entorno. No se deben guardar passwords reales en el repositorio.
