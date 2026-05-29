package dominio.estructuras;

public class Arbol<T> {
    private final NodoArbol<T> raiz;

    public Arbol(T valorRaiz) {
        raiz = new NodoArbol<>(valorRaiz);
    }

    public NodoArbol<T> raiz() {
        return raiz;
    }

    public static class NodoArbol<T> {
        private final T valor;
        private final ListaSimple<NodoArbol<T>> hijos = new ListaSimple<>();

        public NodoArbol(T valor) {
            this.valor = valor;
        }

        public NodoArbol<T> agregarHijo(T valor) {
            NodoArbol<T> hijo = new NodoArbol<>(valor);
            hijos.agregarFinal(hijo);
            return hijo;
        }

        public T valor() {
            return valor;
        }

        public ListaSimple<NodoArbol<T>> hijos() {
            return hijos;
        }
    }
}
