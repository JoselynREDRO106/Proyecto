package persistencia.dao;

import dominio.models.Usuario;
import persistencia.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

public class UsuarioDAO {
    private static final int ROL_EMPLEADO = 2;
    private static final int ROL_ESTUDIANTE = 3;

    public Usuario registrar(Usuario usuario, String telefono, String carrera, String cargo, String departamento) throws SQLException {
        String sqlUsuario = "INSERT INTO Usuario (nombre, email, password, rol_id, activo) VALUES (?, ?, ?, ?, TRUE)";
        String sqlPersona = "INSERT INTO Persona (id, nombre, cedula, telefono, direccion) VALUES (?, ?, ?, ?, ?)";
        String sqlEstudiante = "INSERT INTO Estudiante (id, carrera, semestre, matricula) VALUES (?, ?, 1, ?)";
        String sqlEmpleado = "INSERT INTO Empleado (id, cargo, departamento) VALUES (?, ?, ?)";
        String sqlPersonaSeq = "SELECT setval('persona_id_seq', GREATEST((SELECT MAX(id) FROM Persona), 1))";

        try (Connection cn = DatabaseConnection.getConnection()) {
            cn.setAutoCommit(false);
            try (PreparedStatement psUsuario = cn.prepareStatement(sqlUsuario, Statement.RETURN_GENERATED_KEYS);
                 PreparedStatement psPersona = cn.prepareStatement(sqlPersona);
                 PreparedStatement psEstudiante = cn.prepareStatement(sqlEstudiante);
                 PreparedStatement psEmpleado = cn.prepareStatement(sqlEmpleado);
                 PreparedStatement psPersonaSeq = cn.prepareStatement(sqlPersonaSeq)) {

                psUsuario.setString(1, usuario.getNombre());
                psUsuario.setString(2, usuario.getEmail());
                psUsuario.setString(3, PasswordUtil.hash(usuario.getPassword()));
                psUsuario.setInt(4, usuario.getRolId());
                psUsuario.executeUpdate();

                try (ResultSet keys = psUsuario.getGeneratedKeys()) {
                    if (!keys.next()) {
                        throw new SQLException("No se pudo crear el usuario");
                    }
                    usuario.setId(keys.getInt(1));
                }

                psPersona.setInt(1, usuario.getId());
                psPersona.setString(2, usuario.getNombre());
                psPersona.setString(3, cedulaGenerada(usuario));
                psPersona.setString(4, valorOmitido(telefono, "Sin telefono"));
                psPersona.setString(5, "Ambato");
                psPersona.executeUpdate();

                if (usuario.getRolId() == ROL_ESTUDIANTE) {
                    psEstudiante.setInt(1, usuario.getId());
                    psEstudiante.setString(2, valorOmitido(carrera, "Software"));
                    psEstudiante.setString(3, "UTA-" + usuario.getId());
                    psEstudiante.executeUpdate();
                } else if (usuario.getRolId() == ROL_EMPLEADO) {
                    psEmpleado.setInt(1, usuario.getId());
                    psEmpleado.setString(2, valorOmitido(cargo, "Empleado autorizado"));
                    psEmpleado.setString(3, valorOmitido(departamento, valorOmitido(carrera, "FISEI")));
                    psEmpleado.executeUpdate();
                }

                psPersonaSeq.executeQuery();
                cn.commit();
            } catch (SQLException ex) {
                cn.rollback();
                throw ex;
            }
        }

        usuario.setPassword(null);
        usuario.setActivo(true);
        return usuario;
    }

    public Optional<Usuario> autenticar(String email, String passwordPlano) throws SQLException {
        String sql = "SELECT id, nombre, email, password, rol_id, activo FROM Usuario WHERE email = ? AND activo = TRUE";
        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && PasswordUtil.verificar(passwordPlano, rs.getString("password"))) {
                    Usuario usuario = mapear(rs);
                    usuario.setPassword(null);
                    return Optional.of(usuario);
                }
                return Optional.empty();
            }
        }
    }

    public java.util.List<Usuario> listar() throws SQLException {
        java.util.List<Usuario> usuarios = new java.util.ArrayList<>();
        String sql = "SELECT id, nombre, email, rol_id, activo FROM Usuario ORDER BY id";
        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Usuario usuario = new Usuario();
                usuario.setId(rs.getInt("id"));
                usuario.setNombre(rs.getString("nombre"));
                usuario.setEmail(rs.getString("email"));
                usuario.setRolId(rs.getInt("rol_id"));
                usuario.setActivo(rs.getBoolean("activo"));
                usuarios.add(usuario);
            }
        }
        return usuarios;
    }

    private Usuario mapear(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setId(rs.getInt("id"));
        usuario.setNombre(rs.getString("nombre"));
        usuario.setEmail(rs.getString("email"));
        usuario.setPassword(rs.getString("password"));
        usuario.setRolId(rs.getInt("rol_id"));
        usuario.setActivo(rs.getBoolean("activo"));
        return usuario;
    }

    private String cedulaGenerada(Usuario usuario) {
        return "USR-" + usuario.getId();
    }

    private String valorOmitido(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value.trim();
    }
}
