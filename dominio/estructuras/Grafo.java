package dominio.estructuras;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public class Grafo {
    private final Map<Integer, ListaSimple<Arista>> adyacencias = new HashMap<>();

    public void agregarVertice(int id) {
        adyacencias.putIfAbsent(id, new ListaSimple<>());
    }

    public void agregarArista(int origen, int destino, int peso, boolean bidireccional) {
        agregarVertice(origen);
        agregarVertice(destino);
        adyacencias.get(origen).agregarFinal(new Arista(destino, peso));
        if (bidireccional) {
            adyacencias.get(destino).agregarFinal(new Arista(origen, peso));
        }
    }

    public Ruta rutaMasCorta(int origen, int destino) {
        Map<Integer, Integer> distancias = new HashMap<>();
        Map<Integer, Integer> previos = new HashMap<>();
        PriorityQueue<NodoDistancia> pendientes = new PriorityQueue<>(Comparator.comparingInt(NodoDistancia::distancia));

        for (Integer id : adyacencias.keySet()) {
            distancias.put(id, Integer.MAX_VALUE);
        }
        distancias.put(origen, 0);
        pendientes.add(new NodoDistancia(origen, 0));

        while (!pendientes.isEmpty()) {
            NodoDistancia actual = pendientes.poll();
            if (actual.distancia() > distancias.getOrDefault(actual.id(), Integer.MAX_VALUE)) {
                continue;
            }
            if (actual.id() == destino) {
                break;
            }
            for (Arista arista : adyacencias.getOrDefault(actual.id(), new ListaSimple<>())) {
                int nuevaDistancia = actual.distancia() + arista.peso();
                if (nuevaDistancia < distancias.getOrDefault(arista.destino(), Integer.MAX_VALUE)) {
                    distancias.put(arista.destino(), nuevaDistancia);
                    previos.put(arista.destino(), actual.id());
                    pendientes.add(new NodoDistancia(arista.destino(), nuevaDistancia));
                }
            }
        }

        if (!distancias.containsKey(destino) || distancias.get(destino) == Integer.MAX_VALUE) {
            return new Ruta(List.of(), Integer.MAX_VALUE);
        }

        List<Integer> camino = new ArrayList<>();
        Integer actual = destino;
        while (actual != null) {
            camino.add(0, actual);
            actual = previos.get(actual);
        }
        return new Ruta(camino, distancias.get(destino));
    }

    public record Arista(int destino, int peso) {
    }

    private record NodoDistancia(int id, int distancia) {
    }

    public record Ruta(List<Integer> puntos, int distanciaTotal) {
    }
}
