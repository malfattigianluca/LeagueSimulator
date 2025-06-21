package SimuladorTorneosFutbol_ProyectoFinal.Modelo;

import SimuladorTorneosFutbol_ProyectoFinal.Excepciones.TorneoException;
import SimuladorTorneosFutbol_ProyectoFinal.Util.Validador;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa una liga de fútbol que agrupa un conjunto de equipos.
 * Cada liga tiene un nombre único y una lista de equipos que pertenecen a ella.
 *
 * Esta clase permite agregar, eliminar y buscar equipos, así como consultar su cantidad.
 * La validación asegura que los equipos agregados coincidan con el nombre de la liga.
 *
 * Se utiliza en el sistema para organizar torneos y simulaciones por ligas.
 *
 * @author
 */
public class Liga {

  private String nombre;
  private List<Equipo> equipos;

  /**
   * Crea una nueva liga con el nombre especificado, validando su formato.
   *
   * @param nombre Nombre de la liga.
   * @throws TorneoException Si el nombre no cumple con los criterios definidos en el validador.
   */
  public Liga(String nombre) throws TorneoException {
    Validador.validarLiga(nombre);
    this.nombre = nombre;
    this.equipos = new ArrayList<>();
  }

  /**
   * Agrega un equipo a la liga, si su atributo de liga coincide con el nombre de esta liga.
   *
   * @param equipo Equipo a agregar.
   * @throws TorneoException Si el equipo no pertenece a esta liga.
   */
  public void agregarEquipo(Equipo equipo) throws TorneoException {
    if (equipo.getLiga().equals(this.nombre)) {
      equipos.add(equipo);
    } else {
      throw new TorneoException("El equipo no pertenece a esta liga");
    }
  }

  /**
   * Elimina de la lista al equipo cuyo ID coincida con el especificado.
   *
   * @param idEquipo ID del equipo a eliminar.
   */
  public void eliminarEquipo(int idEquipo) {
    equipos.removeIf(equipo -> equipo.getId() == idEquipo);
  }


  /**
   * Busca un equipo por su ID dentro de la liga.
   *
   * @param idEquipo ID del equipo a buscar.
   * @return El equipo si fue encontrado, o null en caso contrario.
   */
  public Equipo buscarEquipo(int idEquipo) {
    for (Equipo equipo : equipos) {
      if (equipo.getId() == idEquipo) {
        return equipo;
      }
    }
    return null;
  }

  /**
   * Devuelve una copia de la lista de equipos de la liga.
   *
   * @return Lista de equipos pertenecientes a la liga.
   */
  public List<Equipo> getEquipos() {
    return new ArrayList<>(equipos);
  }

  /**
   * Devuelve el nombre de la liga.
   *
   * @return Nombre de la liga.
   */
  public String getNombre() {
    return nombre;
  }

  /**
   * Devuelve la cantidad de equipos registrados en esta liga.
   *
   * @return Número total de equipos en la liga.
   */
  public int cantidadEquipos() {
    return equipos.size();
  }

  /**
   * Devuelve una representación en cadena del nombre de la liga.
   *
   * @return Nombre de la liga.
   */
  @Override
  public String toString() {
    return nombre;
  }
}