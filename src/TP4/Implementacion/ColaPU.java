package TP4.Implementacion;

import TP4.TDA.ColaTDA;

public class ColaPU implements ColaTDA {
    private int[] a;
    private int indice;
    private static final int MAX = 100;
    
    public void InicializarCola() {
        a = new int[MAX];
        indice = 0;
    }
    
    public void Acolar(int x) {
        for (int i = indice - 1; i >= 0; i--) {
            a[i + 1] = a[i];
        }
        a[0] = x;
        indice++;
    }
    
    public void Desacolar() {
        indice--;
    }
    
    public boolean ColaVacia() {
        return (indice == 0);
    }
    
    public int Primero() {
        return a[indice - 1];
    }
    
    public boolean isFull() {
        return (indice == MAX);
    }
    
    public void display() {
        if (indice == 0) {
            System.out.println("La cola esta vacia");
            return;
        }
        System.out.println("Elementos de la cola:");
        for (int i = indice - 1; i >= 0; i--) {
            System.out.print(a[i] + " ");
        }
        System.out.println();
    }
} 