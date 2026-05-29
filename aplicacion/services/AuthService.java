package aplicacion.services;

import dominio.models.Usuario;
import persistencia.dao.UsuarioDAO;

import java.sql.SQLException;
import java.util.Optional;

public class AuthService {
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private static final int ROL_ADMINISTRADOR = 1;
    private static final int ROL_EMPLEADO = 2;
    private static final int ROL_ESTUDIANTE = 3;

    public Usuario registrarEstudiante(String nombre, String email, String password, String telefono, String carrera) throws SQLException {
        return registrar(nombre, email, password, ROL_ESTUDIANTE, telefono, carrera, null, null);
    }

    public Usuario crearEmpleado(String nombre, String email, String password, String telefono, String departamento, String cargo) throws SQLException {
        return registrar(nombre, email, password, ROL_EMPLEADO, telefono, departamento, cargo, departamento);
    }

    public Usuario crearAdministrador(String nombre, String email, String password) throws SQLException {
        return registrar(nombre, email, password, ROL_ADMINISTRADOR, "", "", null, null);
    }

    private Usuario registrar(String nombre, String email, String password, int rolId, String telefono, String carrera, String cargo, String departamento) throws SQLException {
        if (nombre == null || nombre.isBlank() || email == null || !email.contains("@") || password == null || password.length() < 6) {
            throw new IllegalArgumentException("Datos de registro invalidos");
        }
        if (rolId != ROL_ADMINISTRADOR && rolId != ROL_EMPLEADO && rolId != ROL_ESTUDIANTE) {
            throw new IllegalArgumentException("Rol no permitido");
        }
        Usuario usuario = new Usuario();
        usuario.setNombre(nombre.trim());
        usuario.setEmail(email.trim().toLowerCase());
        usuario.setPassword(password);
        usuario.setRolId(rolId);
        return usuarioDAO.registrar(usuario, telefono, carrera, cargo, departamento);
    }

    public Optional<Usuario> autenticar(String email, String password) throws SQLException {
        if (email == null || password == null) {
            return Optional.empty();
        }
        return usuarioDAO.autenticar(email.trim().toLowerCase(), password);
    }

    public java.util.List<Usuario> listarUsuarios() throws SQLException {
        return usuarioDAO.listar();
    }
}
