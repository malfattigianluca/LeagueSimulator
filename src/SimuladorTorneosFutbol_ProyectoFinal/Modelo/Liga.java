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
   * Devuelve una representación en cadena del nombre de la liga.
   *
   * @return Nombre de la liga.
   */
  @Override
  public String toString() {
    return nombre;
  }
}