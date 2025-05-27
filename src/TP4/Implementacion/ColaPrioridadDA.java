package TP4.Implementacion;

import TP4.TDA.ColaPrioridadTDA;

public class ColaPrioridadDA implements ColaPrioridadTDA {
    int[] elementos;
    int[] prioridades;
    int indice;

    public void InicializarCola() {
        indice = 0;
        elementos = new int[100];
        prioridades = new int[100];
    }

    public void AcolarPrioridad(int x, int prioridad) {
        // desplaza a derecha los elementos de la cola mientras
        // estos tengan mayor o igual prioridad que la de x
        int j = indice;
        for (; j > 0 && prioridades[j - 1] >= prioridad; j--) {
            elementos[j] = elementos[j - 1];
            prioridades[j] = prioridades[j - 1];
        }
        elementos[j] = x;
        prioridades[j] = prioridad;
        indice++;
    }

    public void Desacolar() {
        indice--;
    }

    public int Primero() {
        return elementos[indice - 1];
    }

    public boolean ColaVacia() {
        return (indice == 0);
    }

    public int Prioridad() {
        return prioridades[indice - 1];
    }
}
    