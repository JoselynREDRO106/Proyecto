package persistencia.dao;

import dominio.estructuras.ListaCircular;
import dominio.estructuras.ListaSimple;
import dominio.models.Turno;
import dominio.models.Ventanilla;
import persistencia.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class TurnoDAO {
    public Turno crearTurno(int estudianteId) throws SQLException {
        String sql = "INSERT INTO Turno (numero, estudiante_id, estado) VALUES ((SELECT COALESCE(MAX(numero), 0) + 1 FROM Turno), ?, 'pendiente')";
        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, estudianteId);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    Turno turno = new Turno();
                    turno.setId(keys.getInt(1));
                    turno.setEstudianteId(estudianteId);
                    turno.setEstado("pendiente");
                    return turno;
                }
            }
            throw new SQLException("No se pudo crear el turno");
        }
    }

    public ListaSimple<Turno> listarPendientes() throws SQLException {
        ListaSimple<Turno> turnos = new ListaSimple<>();
        String sql = "SELECT id, numero, estudiante_id, empleado_id, estado, ventanilla_id, fecha_creacion FROM Turno WHERE estado = 'pendiente' ORDER BY numero ASC";
        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Turno t = new Turno();
                t.setId(rs.getInt("id"));
                t.setNumero(rs.getInt("numero"));
                t.setEstudianteId(rs.getInt("estudiante_id"));
                int empleadoId = rs.getInt("empleado_id");
                t.setEmpleadoId(rs.wasNull() ? null : empleadoId);
                t.setEstado(rs.getString("estado"));
                int ventanillaId = rs.getInt("ventanilla_id");
                t.setVentanillaId(rs.wasNull() ? null : ventanillaId);
                t.setFechaCreacion(rs.getTimestamp("fecha_creacion").toLocalDateTime());
                turnos.agregarFinal(t);
            }
        }
        return turnos;
    }

    public ListaCircular<Ventanilla> ventanillasActivas() throws SQLException {
        ListaCircular<Ventanilla> ventanillas = new ListaCircular<>();
        for (Ventanilla ventanilla : listarVentanillasActivas()) {
            ventanillas.agregar(ventanilla);
        }
        return ventanillas;
    }

    public List<Ventanilla> listarVentanillasActivas() throws SQLException {
        List<Ventanilla> ventanillas = new ArrayList<>();
        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement("SELECT id, nombre, ubicacion, activa FROM Ventanilla WHERE activa = TRUE ORDER BY id");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Ventanilla v = new Ventanilla();
                v.setId(rs.getInt("id"));
                v.setNombre(rs.getString("nombre"));
                v.setUbicacion(rs.getString("ubicacion"));
                v.setActiva(rs.getBoolean("activa"));
                ventanillas.add(v);
            }
        }
        return ventanillas;
    }

    public Ventanilla crearVentanilla(String nombre, String ubicacion) throws SQLException {
        String sql = "INSERT INTO Ventanilla (nombre, ubicacion, activa) VALUES (?, ?, TRUE)";
        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, nombre);
            ps.setString(2, ubicacion);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    Ventanilla ventanilla = new Ventanilla();
                    ventanilla.setId(keys.getInt(1));
                    ventanilla.setNombre(nombre);
                    ventanilla.setUbicacion(ubicacion);
                    ventanilla.setActiva(true);
                    return ventanilla;
                }
            }
            throw new SQLException("No se pudo crear la ventanilla");
        }
    }

    public void marcarAtendido(int turnoId, int ventanillaId) throws SQLException {
        marcarAtendido(turnoId, ventanillaId, null);
    }

    public void marcarAtendido(int turnoId, int ventanillaId, Integer empleadoId) throws SQLException {
        String sql = "UPDATE Turno SET estado = 'atendido', ventanilla_id = ?, empleado_id = ?, fecha_atencion = CURRENT_TIMESTAMP WHERE id = ? AND estado = 'pendiente'";
        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, ventanillaId);
            if (empleadoId == null) {
                ps.setNull(2, java.sql.Types.INTEGER);
            } else {
                ps.setInt(2, empleadoId);
            }
            ps.setInt(3, turnoId);
            int actualizados = ps.executeUpdate();
            if (actualizados == 0) {
                throw new SQLException("El turno ya fue procesado o no existe");
            }
        }
    }
}
