package persistencia.dao;

import dominio.estructuras.ListaSecuencial;
import dominio.models.ConexionRuta;
import dominio.models.PuntoRuta;
import persistencia.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RutaDAO {
    public ListaSecuencial<PuntoRuta> listarPuntos() throws SQLException {
        ListaSecuencial<PuntoRuta> puntos = new ListaSecuencial<>();
        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement("SELECT id, nombre, descripcion, tipo FROM PuntoRuta ORDER BY nombre");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                PuntoRuta p = new PuntoRuta();
                p.setId(rs.getInt("id"));
                p.setNombre(rs.getString("nombre"));
                p.setDescripcion(rs.getString("descripcion"));
                p.setTipo(rs.getString("tipo"));
                puntos.agregar(p);
            }
        }
        return puntos;
    }

    public ListaSecuencial<ConexionRuta> listarConexiones() throws SQLException {
        ListaSecuencial<ConexionRuta> conexiones = new ListaSecuencial<>();
        String sql = "SELECT punto_origen_id, punto_destino_id, distancia_metros, es_bidireccional FROM ConexionRuta";
        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ConexionRuta c = new ConexionRuta();
                c.setOrigenId(rs.getInt("punto_origen_id"));
                c.setDestinoId(rs.getInt("punto_destino_id"));
                c.setDistanciaMetros(rs.getInt("distancia_metros"));
                c.setBidireccional(rs.getBoolean("es_bidireccional"));
                conexiones.agregar(c);
            }
        }
        return conexiones;
    }
}
