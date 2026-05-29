package dominio.estructuras;

import java.util.NoSuchElementException;

public class ListaCircular<T> {
    private Nodo<T> actual;
    private int tamano;

    public void agregar(T valor) {
        Nodo<T> nuevo = new Nodo<>(valor);
        if (actual == null) {
            actual = nuevo;
            nuevo.siguiente = nuevo;
        } else {
            nuevo.siguiente = actual.siguiente;
            actual.siguiente = nuevo;
        }
        tamano++;
    }

    public T siguiente() {
        if (actual == null) {
            throw new NoSuchElementException("Lista circular vacia");
        }
        actual = actual.siguiente;
        return actual.valor;
    }

    public int tamano() {
        return tamano;
    }

    public boolean estaVacia() {
        return tamano == 0;
    }

    private static class Nodo<T> {
        private final T valor;
        private Nodo<T> siguiente;

        private Nodo(T valor) {
            this.valor = valor;
        }
    }
}
