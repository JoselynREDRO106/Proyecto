package persistencia.dao;

import dominio.estructuras.ListaSecuencial;
import dominio.models.CategoriaDocumento;
import persistencia.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DocumentoDAO {
    public ListaSecuencial<CategoriaDocumento> listarCategorias() throws SQLException {
        ListaSecuencial<CategoriaDocumento> categorias = new ListaSecuencial<>();
        String sql = "SELECT id, nombre, descripcion, padre_id, nivel FROM CategoriaDocumento ORDER BY nivel, nombre";
        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                CategoriaDocumento c = new CategoriaDocumento();
                c.setId(rs.getInt("id"));
                c.setNombre(rs.getString("nombre"));
                c.setDescripcion(rs.getString("descripcion"));
                int padreId = rs.getInt("padre_id");
                c.setPadreId(rs.wasNull() ? null : padreId);
                c.setNivel(rs.getInt("nivel"));
                categorias.agregar(c);
            }
        }
        return categorias;
    }
}
