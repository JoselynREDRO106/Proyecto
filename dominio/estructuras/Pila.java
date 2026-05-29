package dominio.estructuras;

import java.util.NoSuchElementException;

public class Pila<T> {
    private Nodo<T> cima;
    private int tamano;

    public void apilar(T valor) {
        Nodo<T> nuevo = new Nodo<>(valor);
        nuevo.siguiente = cima;
        cima = nuevo;
        tamano++;
    }

    public T desapilar() {
        if (cima == null) {
            throw new NoSuchElementException("Pila vacia");
        }
        T valor = cima.valor;
        cima = cima.siguiente;
        tamano--;
        return valor;
    }

    public boolean estaVacia() {
        return cima == null;
    }

    public int tamano() {
        return tamano;
    }

    private static class Nodo<T> {
        private final T valor;
        private Nodo<T> siguiente;

        private Nodo(T valor) {
            this.valor = valor;
        }
    }
}
