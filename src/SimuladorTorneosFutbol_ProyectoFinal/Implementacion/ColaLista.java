package SimuladorTorneosFutbol_ProyectoFinal.Implementacion;

public class ColaLista {
    private class Nodo {
        Object dato;
        Nodo siguiente;

        Nodo(Object dato) {
            this.dato = dato;
            this.siguiente = null;
        }
    }

    private Nodo primero;
    private Nodo ultimo;

    public ColaLista() {
        primero = null;
        ultimo = null;
    }

    public void acolar(Object x) {
        Nodo nuevo = new Nodo(x);
        if (ultimo == null) {
            primero = nuevo;
        } else {
            ultimo.siguiente = nuevo;
        }
        ultimo = nuevo;
    }

    public void desacolar() {
        if (primero != null) {
            primero = primero.siguiente;
            if (primero == null) {
                ultimo = null;
            }
        }
    }

    public Object primero() {
        if (primero != null) {
            return primero.dato;
        }
        return null;
    }

    public boolean colaVacia() {
        return primero == null;
    }
} 