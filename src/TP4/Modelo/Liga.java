package TP4.Modelo;

import TP4.Util.Constantes;
import TP4.Excepciones.TorneoException;
import TP4.Util.Validador;
import java.util.ArrayList;
import java.util.List;

public class Liga {
  private String nombre;
  private List<Equipo> equipos;

  public Liga(String nombre) throws TorneoException {
    Validador.validarLiga(nombre);
    this.nombre = nombre;
    this.equipos = new ArrayList<>();
  }

  public void agregarEquipo(Equipo equipo) throws TorneoException {
    if (equipo.getLiga().equals(this.nombre)) {
      equipos.add(equipo);
    } else {
      throw new TorneoException("El equipo no pertenece a esta liga");
    }
  }

  public void eliminarEquipo(int idEquipo) {
    equipos.removeIf(equipo -> equipo.getId() == idEquipo);
  }

  public Equipo buscarEquipo(int idEquipo) {
    for (Equipo equipo : equipos) {
      if (equipo.getId() == idEquipo) {
        return equipo;
      }
    }
    return null;
  }

  public List<Equipo> getEquipos() {
    return new ArrayList<>(equipos);
  }

  public String getNombre() {
    return nombre;
  }

  public int cantidadEquipos() {
    return equipos.size();
  }

  @Override
  public String toString() {
    return nombre;
  }
}