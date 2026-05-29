package dominio.estructuras;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class ListaSecuencial<T> implements Iterable<T> {
    private Object[] datos;
    private int tamano;

    public ListaSecuencial() {
        this(10);
    }

    public ListaSecuencial(int capacidadInicial) {
        datos = new Object[Math.max(1, capacidadInicial)];
    }

    public void agregar(T valor) {
        asegurarCapacidad(tamano + 1);
        datos[tamano++] = valor;
    }

    public T obtener(int indice) {
        validarIndice(indice);
        return elemento(indice);
    }

    public T eliminar(int indice) {
        validarIndice(indice);
        T valor = elemento(indice);
        int cantidadMover = tamano - indice - 1;
        if (cantidadMover > 0) {
            System.arraycopy(datos, indice + 1, datos, indice, cantidadMover);
        }
        datos[--tamano] = null;
        return valor;
    }

    public int tamano() {
        return tamano;
    }

    public boolean estaVacia() {
        return tamano == 0;
    }

    private void asegurarCapacidad(int requerida) {
        if (requerida <= datos.length) {
            return;
        }
        Object[] nuevo = new Object[datos.length * 2];
        System.arraycopy(datos, 0, nuevo, 0, tamano);
        datos = nuevo;
    }

    private void validarIndice(int indice) {
        if (indice < 0 || indice >= tamano) {
            throw new IndexOutOfBoundsException("Indice fuera de rango: " + indice);
        }
    }

    @SuppressWarnings("unchecked")
    private T elemento(int indice) {
        return (T) datos[indice];
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<>() {
            private int cursor;

            @Override
            public boolean hasNext() {
                return cursor < tamano;
            }

            @Override
            public T next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                return elemento(cursor++);
            }
        };
    }
}
