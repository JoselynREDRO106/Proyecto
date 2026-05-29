package presentacion.controllers;

import aplicacion.services.AtencionService;
import aplicacion.services.AuthService;
import aplicacion.services.DocumentoService;
import aplicacion.services.RutasService;
import aplicacion.services.TramiteService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dominio.estructuras.Arbol;
import dominio.estructuras.ListaSecuencial;
import dominio.estructuras.ListaSimple;
import dominio.models.CategoriaDocumento;
import dominio.models.HistorialTramite;
import dominio.models.PuntoRuta;
import dominio.models.Tramite;
import dominio.models.Turno;
import dominio.models.Usuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@WebServlet("/api/*")
public class SmartCampusController extends HttpServlet {
    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private final AuthService authService = new AuthService();
    private final TramiteService tramiteService = new TramiteService();
    private final AtencionService atencionService = new AtencionService();
    private final DocumentoService documentoService = new DocumentoService();
    private final RutasService rutasService = new RutasService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String path = path(req);
            if ("/auth/usuarios".equals(path)) {
                exigirRol(req, 1);
                escribirJson(resp, authService.listarUsuarios());
                return;
            }
            if ("/tramites".equals(path)) {
                escribirJson(resp, tramiteService.listarTramites());
                return;
            }
            if ("/tramites/asignados".equals(path)) {
                exigirRol(req, 2);
                escribirJson(resp, tramiteService.listarAsignadosA(usuarioAutenticado(req)));
                return;
            }
            if ("/rutas/puntos".equals(path)) {
                escribirJson(resp, toList(rutasService.puntos()));
                return;
            }
            if ("/turnos/pendientes".equals(path)) {
                escribirJson(resp, toList(atencionService.cargarColaPendiente()));
                return;
            }
            if ("/ventanillas".equals(path)) {
                exigirRol(req, 1);
                escribirJson(resp, atencionService.listarVentanillasActivas());
                return;
            }
            if ("/ventanillas/rotacion".equals(path)) {
                exigirRol(req, 1);
                escribirJson(resp, atencionService.estadoVentanillas());
                return;
            }
            if ("/reportes/turnos-atendidos".equals(path)) {
                escribirJson(resp, Map.of("total", toList(atencionService.cargarColaPendiente()).size()));
                return;
            }
            if ("/reportes/tramites-periodo".equals(path)) {
                escribirJson(resp, Map.of("total", tramiteService.listarTramites().size()));
                return;
            }
            if ("/reportes/usuarios-por-rol".equals(path)) {
                escribirJson(resp, reporteUsuariosPorRol());
                return;
            }
            if ("/rutas/calcular".equals(path)) {
                int origen = entero(req, "origen");
                int destino = entero(req, "destino");
                RutasService.RutaCalculada ruta = rutasService.calcularRutaMasCorta(origen, destino);
                escribirJson(resp, Map.of("camino", toList(ruta.camino()), "distanciaMetros", ruta.distanciaMetros()));
                return;
            }
            if ("/documentos/arbol".equals(path)) {
                escribirJson(resp, nodo(documentoService.arbolCategorias().raiz()));
                return;
            }
            if ("/tramites/historial".equals(path)) {
                ListaSimple<HistorialTramite> historial = tramiteService.historialSimple(entero(req, "tramiteId"));
                escribirJson(resp, toList(historial));
                return;
            }
            if ("/tramites/estado".equals(path)) {
                escribirJson(resp, Map.of("estado", tramiteService.obtenerEstado(entero(req, "tramiteId"))));
                return;
            }
            error(resp, 404, "Ruta no encontrada");
        } catch (Exception ex) {
            manejarError(resp, ex);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String path = path(req);
            Map<String, Object> body = leerBody(req);
            switch (path) {
                case "/auth/registro" -> {
                    Usuario usuario = authService.registrarEstudiante(texto(body, "nombre"), texto(body, "email"), texto(body, "password"),
                            texto(body, "telefono"), texto(body, "carrera"));
                    escribirJson(resp, usuario);
                }
                case "/auth/empleados" -> {
                    exigirRol(req, 1);
                    Usuario usuario = authService.crearEmpleado(texto(body, "nombre"), texto(body, "email"), texto(body, "password"),
                            texto(body, "telefono"), texto(body, "departamento"), texto(body, "cargo"));
                    escribirJson(resp, usuario);
                }
                case "/auth/login" -> {
                    Optional<Usuario> usuario = authService.autenticar(texto(body, "email"), texto(body, "password"));
                    if (usuario.isPresent()) {
                        escribirJson(resp, Map.of("ok", true, "usuario", usuario.get()));
                    } else {
                        error(resp, 401, "Credenciales invalidas");
                    }
                }
                case "/auth/logout" -> escribirJson(resp, Map.of("ok", true));
                case "/tramites" -> {
                    Tramite tramite = tramiteService.registrarTramite(entero(body, "tipoTramiteId"), entero(body, "estudianteId"),
                            texto(body, "descripcion"), texto(body, "prioridad"));
                    escribirJson(resp, tramite);
                }
                case "/tramites/estado" -> {
                    int rol = rolAutenticado(req);
                    if (rol == 1) {
                        tramiteService.cambiarEstado(entero(body, "tramiteId"), usuarioAutenticado(req), texto(body, "estado"), texto(body, "comentario"));
                    } else if (rol == 2) {
                        tramiteService.cambiarEstadoAsignado(entero(body, "tramiteId"), usuarioAutenticado(req), texto(body, "estado"), texto(body, "comentario"));
                    } else {
                        throw new SecurityException("No autorizado para cambiar el estado del tramite");
                    }
                    escribirJson(resp, Map.of("ok", true));
                }
                case "/tramites/asignar" -> {
                    exigirRol(req, 1);
                    tramiteService.asignarEmpleado(entero(body, "tramiteId"), entero(body, "empleadoId"),
                            usuarioAutenticado(req), texto(body, "comentario"));
                    escribirJson(resp, Map.of("ok", true));
                }
                case "/tramites/deshacer" -> {
                    tramiteService.deshacerUltimoCambio();
                    escribirJson(resp, Map.of("ok", true));
                }
                case "/turnos" -> {
                    Turno turno = atencionService.generarTurno(entero(body, "estudianteId"));
                    escribirJson(resp, turno);
                }
                case "/turnos/atender" -> {
                    exigirRol(req, 2);
                    escribirJson(resp, atencionService.atenderSiguiente(usuarioAutenticado(req)));
                }
                case "/ventanillas" -> {
                    exigirRol(req, 1);
                    escribirJson(resp, atencionService.crearVentanilla(texto(body, "nombre"), texto(body, "ubicacion")));
                }
                case "/ventanillas/rotar" -> {
                    exigirRol(req, 1);
                    escribirJson(resp, atencionService.rotarVentanilla());
                }
                default -> error(resp, 404, "Ruta no encontrada");
            }
        } catch (Exception ex) {
            manejarError(resp, ex);
        }
    }

    private String path(HttpServletRequest req) {
        String info = req.getPathInfo();
        return info == null ? "/" : info;
    }

    private void exigirRol(HttpServletRequest req, int rolId) {
        if (rolAutenticado(req) != rolId) {
            throw new SecurityException("No autorizado para esta operacion");
        }
    }

    private int rolAutenticado(HttpServletRequest req) {
        String header = req.getHeader("X-User-Role");
        if (header == null || header.isBlank()) {
            throw new SecurityException("Rol autenticado no identificado");
        }
        return Integer.parseInt(header);
    }

    private int usuarioAutenticado(HttpServletRequest req) {
        String header = req.getHeader("X-User-Id");
        if (header == null || header.isBlank()) {
            throw new SecurityException("Usuario autenticado no identificado");
        }
        return Integer.parseInt(header);
    }

    private Map<String, Object> leerBody(HttpServletRequest req) throws IOException {
        if (req.getContentLength() == 0) {
            return Map.of();
        }
        return mapper.readValue(req.getInputStream(), Map.class);
    }

    private void escribirJson(HttpServletResponse resp, Object value) throws IOException {
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json");
        mapper.writeValue(resp.getOutputStream(), value);
    }

    private void error(HttpServletResponse resp, int status, String mensaje) throws IOException {
        resp.setStatus(status);
        escribirJson(resp, Map.of("error", mensaje));
    }

    private void manejarError(HttpServletResponse resp, Exception ex) throws IOException {
        int status = ex instanceof SecurityException ? 403 : ex instanceof IllegalArgumentException ? 400 : ex instanceof SQLException ? 503 : 500;
        error(resp, status, ex.getMessage());
    }

    private int entero(HttpServletRequest req, String nombre) {
        return Integer.parseInt(req.getParameter(nombre));
    }

    private int entero(Map<String, Object> body, String nombre) {
        Object value = body.get(nombre);
        if (value instanceof Number number) {
            return number.intValue();
        }
        return Integer.parseInt(String.valueOf(value));
    }

    private String texto(Map<String, Object> body, String nombre) {
        Object value = body.get(nombre);
        return value == null ? "" : String.valueOf(value);
    }

    private <T> List<T> toList(Iterable<T> iterable) {
        List<T> lista = new ArrayList<>();
        for (T item : iterable) {
            lista.add(item);
        }
        return lista;
    }

    private Map<String, Object> nodo(Arbol.NodoArbol<CategoriaDocumento> nodo) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("categoria", nodo.valor());
        List<Map<String, Object>> hijos = new ArrayList<>();
        for (Arbol.NodoArbol<CategoriaDocumento> hijo : nodo.hijos()) {
            hijos.add(nodo(hijo));
        }
        dto.put("hijos", hijos);
        return dto;
    }

    private Map<String, Object> reporteUsuariosPorRol() throws SQLException {
        Map<Integer, Integer> conteo = new LinkedHashMap<>();
        for (Usuario usuario : authService.listarUsuarios()) {
            conteo.merge(usuario.getRolId(), 1, Integer::sum);
        }
        return Map.of(
                "administradores", conteo.getOrDefault(1, 0),
                "empleados", conteo.getOrDefault(2, 0),
                "estudiantes", conteo.getOrDefault(3, 0),
                "totalUsuarios", conteo.values().stream().mapToInt(Integer::intValue).sum()
        );
    }
}
