const SMARTCAMPUS_API_BASE_URL = window.API_BASE_URL || "http://localhost:8080/SmartCampusUTA";

const authHeaders = () => {
  const token = localStorage.getItem("token");
  const rawUser = localStorage.getItem("usuario");
  let role = "";
  try {
    role = rawUser ? String(JSON.parse(rawUser).rol_id || "") : "";
  } catch {
    role = "";
  }
  let userId = "";
  try {
    userId = rawUser ? String(JSON.parse(rawUser).id || "") : "";
  } catch {
    userId = "";
  }
  return {
    ...(token ? { Authorization: `Bearer ${token}` } : {}),
    ...(role ? { "X-User-Role": role } : {}),
    ...(userId ? { "X-User-Id": userId } : {})
  };
};

const jsonHeaders = () => ({ ...authHeaders(), "Content-Type": "application/json" });

// ========== AUTENTICACION ==========
const registrar = (data) => fetch(`${SMARTCAMPUS_API_BASE_URL}/api/auth/registro`, { method: "POST", body: JSON.stringify(data), headers: { "Content-Type": "application/json" } });
const login = (data) => fetch(`${SMARTCAMPUS_API_BASE_URL}/api/auth/login`, { method: "POST", body: JSON.stringify(data), headers: { "Content-Type": "application/json" } });
const logout = () => fetch(`${SMARTCAMPUS_API_BASE_URL}/api/auth/logout`, { method: "POST", headers: authHeaders() });
const listarUsuarios = () => fetch(`${SMARTCAMPUS_API_BASE_URL}/api/auth/usuarios`, { headers: authHeaders() });
const obtenerUsuario = (id) => fetch(`${SMARTCAMPUS_API_BASE_URL}/api/auth/usuarios/${id}`, { headers: authHeaders() });
const actualizarUsuario = (id, data) => fetch(`${SMARTCAMPUS_API_BASE_URL}/api/auth/usuarios/${id}`, { method: "PUT", body: JSON.stringify(data), headers: jsonHeaders() });
const eliminarUsuario = (id) => fetch(`${SMARTCAMPUS_API_BASE_URL}/api/auth/usuarios/${id}`, { method: "DELETE", headers: authHeaders() });
const crearEmpleado = (data) => fetch(`${SMARTCAMPUS_API_BASE_URL}/api/auth/empleados`, { method: "POST", body: JSON.stringify(data), headers: jsonHeaders() });

// ========== TRAMITES ==========
const crearTramite = (data) => fetch(`${SMARTCAMPUS_API_BASE_URL}/api/tramites`, { method: "POST", body: JSON.stringify(data), headers: jsonHeaders() });
const obtenerHistorialTramites = (tramiteId) => {
  if (!tramiteId) return fetch(`${SMARTCAMPUS_API_BASE_URL}/api/tramites`, { headers: authHeaders() });
  return fetch(`${SMARTCAMPUS_API_BASE_URL}/api/tramites/historial?tramiteId=${encodeURIComponent(tramiteId)}`, { headers: authHeaders() });
};
const consultarEstadoTramite = (tramiteId) => fetch(`${SMARTCAMPUS_API_BASE_URL}/api/tramites/estado?tramiteId=${tramiteId}`, { headers: authHeaders() });
const deshacerUltimaAccion = () => fetch(`${SMARTCAMPUS_API_BASE_URL}/api/tramites/deshacer`, { method: "POST", headers: authHeaders() });
const listarTiposTramite = () => fetch(`${SMARTCAMPUS_API_BASE_URL}/api/tramites/tipo`, { headers: authHeaders() });
const cambiarEstadoTramite = (tramiteId, estado, comentario = "") => {
  const usuarioId = window.SmartCampusAuth?.getSession()?.usuario?.id || 0;
  return fetch(`${SMARTCAMPUS_API_BASE_URL}/api/tramites/estado`, {
    method: "POST",
    body: JSON.stringify({ tramiteId, usuarioId, estado, comentario }),
    headers: jsonHeaders()
  });
};
const asignarEmpleadoTramite = (tramiteId, empleadoId, comentario = "") => fetch(`${SMARTCAMPUS_API_BASE_URL}/api/tramites/asignar`, {
  method: "POST",
  body: JSON.stringify({ tramiteId, empleadoId, comentario }),
  headers: jsonHeaders()
});
const obtenerTramitesAsignados = () => fetch(`${SMARTCAMPUS_API_BASE_URL}/api/tramites/asignados`, { headers: authHeaders() });

// ========== TURNOS ==========
const solicitarTurno = (data) => fetch(`${SMARTCAMPUS_API_BASE_URL}/api/turnos`, { method: "POST", body: JSON.stringify(data), headers: jsonHeaders() });
const consultarEstadoTurno = (turnoId) => fetch(`${SMARTCAMPUS_API_BASE_URL}/api/turnos/estado?turnoId=${turnoId}`, { headers: authHeaders() });
const obtenerTurnosUsuario = () => fetch(`${SMARTCAMPUS_API_BASE_URL}/api/turnos/pendientes`, { headers: authHeaders() });
const atenderSiguienteTurno = () => fetch(`${SMARTCAMPUS_API_BASE_URL}/api/turnos/atender`, { method: "POST", headers: authHeaders() });
const obtenerSiguienteTurno = () => fetch(`${SMARTCAMPUS_API_BASE_URL}/api/turnos/siguiente`, { headers: authHeaders() });
const cancelarTurno = (turnoId) => fetch(`${SMARTCAMPUS_API_BASE_URL}/api/turnos/cancelar?turnoId=${turnoId}`, { method: "DELETE", headers: authHeaders() });
const listarVentanillas = () => fetch(`${SMARTCAMPUS_API_BASE_URL}/api/ventanillas`, { headers: authHeaders() });
const obtenerRotacionVentanillas = () => fetch(`${SMARTCAMPUS_API_BASE_URL}/api/ventanillas/rotacion`, { headers: authHeaders() });
const crearVentanilla = (data) => fetch(`${SMARTCAMPUS_API_BASE_URL}/api/ventanillas`, { method: "POST", body: JSON.stringify(data), headers: jsonHeaders() });
const rotarVentanilla = () => fetch(`${SMARTCAMPUS_API_BASE_URL}/api/ventanillas/rotar`, { method: "POST", headers: authHeaders() });

// ========== DOCUMENTOS (ARBOL) ==========
const obtenerArbolDocumentos = () => fetch(`${SMARTCAMPUS_API_BASE_URL}/api/documentos/arbol`, { headers: authHeaders() });
const obtenerNodoDocumento = (id) => fetch(`${SMARTCAMPUS_API_BASE_URL}/api/documentos/nodo/${id}`, { headers: authHeaders() });
const crearNodoDocumento = (data) => fetch(`${SMARTCAMPUS_API_BASE_URL}/api/documentos/nodo`, { method: "POST", body: JSON.stringify(data), headers: jsonHeaders() });
const actualizarNodoDocumento = (id, data) => fetch(`${SMARTCAMPUS_API_BASE_URL}/api/documentos/nodo/${id}`, { method: "PUT", body: JSON.stringify(data), headers: jsonHeaders() });
const eliminarNodoDocumento = (id) => fetch(`${SMARTCAMPUS_API_BASE_URL}/api/documentos/nodo/${id}`, { method: "DELETE", headers: authHeaders() });

// ========== RUTAS (GRAFO) ==========
const calcularRuta = (origen, destino) => fetch(`${SMARTCAMPUS_API_BASE_URL}/api/rutas/calcular?origen=${encodeURIComponent(origen)}&destino=${encodeURIComponent(destino)}`, { headers: authHeaders() });
const listarNodosRutas = () => fetch(`${SMARTCAMPUS_API_BASE_URL}/api/rutas/puntos`, { headers: authHeaders() });
const listarConexionesRutas = () => Promise.resolve(new Response(JSON.stringify([]), { status: 200, headers: { "Content-Type": "application/json" } }));

// ========== REPORTES ==========
const reporteTurnosAtendidos = () => fetch(`${SMARTCAMPUS_API_BASE_URL}/api/reportes/turnos-atendidos`, { headers: authHeaders() });
const reporteTramitesPeriodo = (fechaInicio, fechaFin) => fetch(`${SMARTCAMPUS_API_BASE_URL}/api/reportes/tramites-periodo?inicio=${fechaInicio}&fin=${fechaFin}`, { headers: authHeaders() });
const reporteUsuariosPorRol = () => fetch(`${SMARTCAMPUS_API_BASE_URL}/api/reportes/usuarios-por-rol`, { headers: authHeaders() });

const parseJson = async (response) => {
  const text = await response.text();
  const data = text ? JSON.parse(text) : null;
  if (!response.ok) throw new Error(data?.error || data?.mensaje || `HTTP ${response.status}`);
  return data;
};

window.SmartCampusAPI = {
  registrar,
  login,
  logout,
  listarUsuarios,
  obtenerUsuario,
  actualizarUsuario,
  eliminarUsuario,
  crearEmpleado,
  crearTramite,
  obtenerHistorialTramites,
  consultarEstadoTramite,
  deshacerUltimaAccion,
  listarTiposTramite,
  cambiarEstadoTramite,
  asignarEmpleadoTramite,
  obtenerTramitesAsignados,
  solicitarTurno,
  consultarEstadoTurno,
  obtenerTurnosUsuario,
  atenderSiguienteTurno,
  obtenerSiguienteTurno,
  cancelarTurno,
  listarVentanillas,
  obtenerRotacionVentanillas,
  crearVentanilla,
  rotarVentanilla,
  obtenerArbolDocumentos,
  obtenerNodoDocumento,
  crearNodoDocumento,
  actualizarNodoDocumento,
  eliminarNodoDocumento,
  calcularRuta,
  listarNodosRutas,
  listarConexionesRutas,
  reporteTurnosAtendidos,
  reporteTramitesPeriodo,
  reporteUsuariosPorRol,
  parseJson
};
