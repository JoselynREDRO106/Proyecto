package aplicacion.services;

import dominio.estructuras.Cola;
import dominio.estructuras.ListaCircular;
import dominio.estructuras.ListaSimple;
import dominio.models.Turno;
import dominio.models.Ventanilla;
import persistencia.dao.TurnoDAO;

import java.sql.SQLException;
import java.util.List;

public class AtencionService {
    private final TurnoDAO turnoDAO = new TurnoDAO();
    private int indiceVentanillaActual = -1;

    public Turno generarTurno(int estudianteId) throws SQLException {
        if (estudianteId <= 0) {
            throw new IllegalArgumentException("El estudiante es obligatorio");
        }
        return turnoDAO.crearTurno(estudianteId);
    }

    public ResultadoAtencion atenderSiguiente() throws SQLException {
        return atenderSiguiente(null);
    }

    public ResultadoAtencion atenderSiguiente(Integer empleadoId) throws SQLException {
        Cola<Turno> cola = cargarColaPendiente();
        ListaCircular<Ventanilla> ventanillas = turnoDAO.ventanillasActivas();
        if (cola.estaVacia()) {
            throw new IllegalStateException("No existen turnos pendientes");
        }
        if (ventanillas.estaVacia()) {
            throw new IllegalStateException("No existen ventanillas activas");
        }
        Turno turno = cola.desencolar();
        Ventanilla ventanilla = rotarVentanilla().actual();
        turnoDAO.marcarAtendido(turno.getId(), ventanilla.getId(), empleadoId);
        turno.setEstado("atendido");
        turno.setEmpleadoId(empleadoId);
        turno.setVentanillaId(ventanilla.getId());
        return new ResultadoAtencion(turno, ventanilla);
    }

    public List<Ventanilla> listarVentanillasActivas() throws SQLException {
        return turnoDAO.listarVentanillasActivas();
    }

    public Ventanilla crearVentanilla(String nombre, String ubicacion) throws SQLException {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre de la ventanilla es obligatorio");
        }
        String ubicacionNormalizada = ubicacion == null || ubicacion.isBlank() ? "FISEI" : ubicacion.trim();
        return turnoDAO.crearVentanilla(nombre.trim(), ubicacionNormalizada);
    }

    public synchronized EstadoVentanillas estadoVentanillas() throws SQLException {
        List<Ventanilla> ventanillas = listarVentanillasActivas();
        if (ventanillas.isEmpty()) {
            return new EstadoVentanillas(null, null, ventanillas);
        }
        int actual = indiceNormalizado(ventanillas.size());
        int siguiente = (actual + 1) % ventanillas.size();
        return new EstadoVentanillas(ventanillas.get(actual), ventanillas.get(siguiente), ventanillas);
    }

    public synchronized EstadoVentanillas rotarVentanilla() throws SQLException {
        List<Ventanilla> ventanillas = listarVentanillasActivas();
        if (ventanillas.isEmpty()) {
            throw new IllegalStateException("No existen ventanillas activas");
        }
        indiceVentanillaActual = (indiceNormalizado(ventanillas.size()) + 1) % ventanillas.size();
        int siguiente = (indiceVentanillaActual + 1) % ventanillas.size();
        return new EstadoVentanillas(ventanillas.get(indiceVentanillaActual), ventanillas.get(siguiente), ventanillas);
    }

    public Cola<Turno> cargarColaPendiente() throws SQLException {
        ListaSimple<Turno> pendientes = turnoDAO.listarPendientes();
        Cola<Turno> cola = new Cola<>();
        for (Turno turno : pendientes) {
            cola.encolar(turno);
        }
        return cola;
    }

    public record ResultadoAtencion(Turno turno, Ventanilla ventanilla) {
    }

    public record EstadoVentanillas(Ventanilla actual, Ventanilla siguiente, List<Ventanilla> ventanillas) {
    }

    private int indiceNormalizado(int total) {
        if (indiceVentanillaActual < 0 || indiceVentanillaActual >= total) {
            indiceVentanillaActual = 0;
        }
        return indiceVentanillaActual;
    }
}
