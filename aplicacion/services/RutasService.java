package aplicacion.services;

import dominio.estructuras.Grafo;
import dominio.estructuras.ListaSecuencial;
import dominio.models.ConexionRuta;
import dominio.models.PuntoRuta;
import persistencia.dao.RutaDAO;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class RutasService {
    private final RutaDAO rutaDAO = new RutaDAO();

    public RutaCalculada calcularRutaMasCorta(int origenId, int destinoId) throws SQLException {
        ListaSecuencial<PuntoRuta> puntos = rutaDAO.listarPuntos();
        ListaSecuencial<ConexionRuta> conexiones = rutaDAO.listarConexiones();
        Map<Integer, PuntoRuta> porId = new HashMap<>();
        Grafo grafo = new Grafo();

        for (PuntoRuta punto : puntos) {
            porId.put(punto.getId(), punto);
            grafo.agregarVertice(punto.getId());
        }
        for (ConexionRuta conexion : conexiones) {
            grafo.agregarArista(conexion.getOrigenId(), conexion.getDestinoId(), conexion.getDistanciaMetros(), conexion.isBidireccional());
        }

        Grafo.Ruta ruta = grafo.rutaMasCorta(origenId, destinoId);
        ListaSecuencial<PuntoRuta> camino = new ListaSecuencial<>();
        for (Integer id : ruta.puntos()) {
            PuntoRuta punto = porId.get(id);
            if (punto != null) {
                camino.agregar(punto);
            }
        }
        return new RutaCalculada(camino, ruta.distanciaTotal());
    }

    public ListaSecuencial<PuntoRuta> puntos() throws SQLException {
        return rutaDAO.listarPuntos();
    }

    public record RutaCalculada(ListaSecuencial<PuntoRuta> camino, int distanciaMetros) {
    }
}
