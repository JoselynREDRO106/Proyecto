package aplicacion.services;

import dominio.estructuras.Arbol;
import dominio.estructuras.ListaSecuencial;
import dominio.models.CategoriaDocumento;
import persistencia.dao.DocumentoDAO;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class DocumentoService {
    private final DocumentoDAO documentoDAO = new DocumentoDAO();

    public Arbol<CategoriaDocumento> arbolCategorias() throws SQLException {
        ListaSecuencial<CategoriaDocumento> categorias = documentoDAO.listarCategorias();
        CategoriaDocumento raizVirtual = new CategoriaDocumento();
        raizVirtual.setId(0);
        raizVirtual.setNombre("Documentos SmartCampus");
        Arbol<CategoriaDocumento> arbol = new Arbol<>(raizVirtual);
        Map<Integer, Arbol.NodoArbol<CategoriaDocumento>> nodos = new HashMap<>();
        nodos.put(0, arbol.raiz());

        for (CategoriaDocumento categoria : categorias) {
            Integer padreId = categoria.getPadreId() == null ? 0 : categoria.getPadreId();
            Arbol.NodoArbol<CategoriaDocumento> padre = nodos.getOrDefault(padreId, arbol.raiz());
            nodos.put(categoria.getId(), padre.agregarHijo(categoria));
        }
        return arbol;
    }
}
