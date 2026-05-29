package dominio.estructuras;

import java.util.NoSuchElementException;

public class ListaDoble<T> {
    private Nodo<T> cabeza;
    private Nodo<T> cola;
    private Nodo<T> cursor;
    private int tamano;

    public void agregarFinal(T valor) {
        Nodo<T> nuevo = new Nodo<>(valor);
        if (cabeza == null) {
            cabeza = nuevo;
            cursor = nuevo;
        } else {
            cola.siguiente = nuevo;
            nuevo.anterior = cola;
        }
        cola = nuevo;
        tamano++;
    }

    public T actual() {
        if (cursor == null) {
            throw new NoSuchElementException("Lista vacia");
        }
        return cursor.valor;
    }

    public T siguiente() {
        if (cursor == null || cursor.siguiente == null) {
            throw new NoSuchElementException("No existe siguiente");
        }
        cursor = cursor.siguiente;
        return cursor.valor;
    }

    public T anterior() {
        if (cursor == null || cursor.anterior == null) {
            throw new NoSuchElementException("No existe anterior");
        }
        cursor = cursor.anterior;
        return cursor.valor;
    }

    public boolean tieneSiguiente() {
        return cursor != null && cursor.siguiente != null;
    }

    public boolean tieneAnterior() {
        return cursor != null && cursor.anterior != null;
    }

    public int tamano() {
        return tamano;
    }

    private static class Nodo<T> {
        private final T valor;
        private Nodo<T> anterior;
        private Nodo<T> siguiente;

        private Nodo(T valor) {
            this.valor = valor;
        }
    }
}
