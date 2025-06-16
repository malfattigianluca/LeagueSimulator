package TP4.Modelo;

import TP4.TDA.ConjuntoGenericoTDA;
import TP4.Implementacion.ConjuntoGenericoImpl;
import TP4.Excepciones.TorneoException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Clase GestorJugadores.
 * Se encarga de gestionar la carga, creación, asociación y búsqueda de jugadores.
 */
public class GestorJugadores {
  private static GestorJugadores instancia;
  private ConjuntoGenericoTDA<Jugador> jugadores;
  public int idJugador;
  private GestorEquipos gestorEquipos;

  /**
   * Constructor de la clase GestorJugadores.
   * Inicializa el conjunto de jugadores, el identificador y establece el gestor de equipos.
   *
   * @param gestorEquipos Instancia del gestor de equipos para asociar jugadores.
   */
  public GestorJugadores(GestorEquipos gestorEquipos) {
    this.jugadores = new ConjuntoGenericoImpl<>();
    this.idJugador = 1;
    this.gestorEquipos = gestorEquipos;
  }

  /**
   * Devuelve la instancia singleton de GestorJugadores.
   * Si no existe, la crea utilizando un nuevo GestorEquipos.
   *
   * @return Instancia única de GestorJugadores.
   */
  public static GestorJugadores getInstancia() {
    if (instancia == null) {
      instancia = new GestorJugadores(new GestorEquipos());
    }
    return instancia;
  }

  /**
   * Carga los jugadores desde un archivo especificado.
   * Cada línea del archivo debe tener 11 datos separados por punto y coma (;).
   *
   * @param rutaArchivo Ruta del archivo con la información de los jugadores.
   * @throws TorneoException Si ocurre algún error al leer o procesar el archivo.
   */
  public void cargarJugadores(String rutaArchivo) throws TorneoException {
    try (BufferedReader reader = new BufferedReader(new FileReader(rutaArchivo))) {
      String linea;
      int numeroLinea = 0;
      while ((linea = reader.readLine()) != null) {
        numeroLinea++;
        if (linea.trim().isEmpty()) continue;
        String[] datos = linea.split(";");
        if (datos.length == 11) {
          try {
            String liga = datos[0].trim();
            String nombreEquipo = datos[1].trim();
            String nombreJugador = datos[2].trim();
            String posicion = datos[3].trim();
            int numeroCamiseta = datos[4].equals("-") ? -1 : Integer.parseInt(datos[4].trim());
            int edad = Integer.parseInt(datos[5].trim());
            String altura = datos[6].trim();
            if (altura.equalsIgnoreCase("Desconocido")) altura = "N/A";
            else altura = altura.replace(",", ".").replace("m", "");
            int goles = Integer.parseInt(datos[7].trim());
            int asistencias = Integer.parseInt(datos[8].trim());
            int rojas = Integer.parseInt(datos[9].trim());
            int amarillas = Integer.parseInt(datos[10].trim());

            Jugador jugador = new Jugador(idJugador++, nombreJugador, numeroCamiseta, posicion, liga, altura, edad, goles, asistencias, amarillas, rojas);
            jugadores.insertar(jugador);

            boolean equipoEncontrado = false;
            for (List<Equipo> equipos : gestorEquipos.getEquiposPorLiga().values()) {
              for (Equipo equipo : equipos) {
                if (equipo.getNombre().equalsIgnoreCase(nombreEquipo) && equipo.getLiga().equalsIgnoreCase(liga)) {
                  int intentos = 0;
                  boolean anadido = false;
                  while (!anadido && intentos < 100) {
                    try {
                      equipo.agregarJugador(jugador);
                      anadido = true;
                      equipoEncontrado = true;
                    } catch (TorneoException e) {
                      if (e.getMessage().contains("ya está en uso")) {
                        numeroCamiseta++;
                        jugador.setNumeroCamiseta(numeroCamiseta);
                        intentos++;
                      } else {
                        throw e;
                      }
                    }
                  }
                  if (!anadido) {
                    System.err.println("No se pudo asignar número único a: " + nombreJugador);
                    idJugador--;
                    jugadores.eliminar(jugador);
                  }
                  break;
                }
              }
              if (equipoEncontrado) break;
            }
            if (!equipoEncontrado) {
              System.err.println("Equipo no encontrado para el jugador en línea " + numeroLinea);
              idJugador--;
              jugadores.eliminar(jugador);
            }
          } catch (NumberFormatException e) {
            System.err.println("Error en la línea " + numeroLinea + ": Formato inválido.");
          } catch (TorneoException e) {
            System.err.println("Error en la línea " + numeroLinea + ": " + e.getMessage());
          }
        } else {
          System.err.println("Formato inválido en la línea " + numeroLinea);
          throw new TorneoException("Formato inválido en la línea " + numeroLinea);
        }
      }
    } catch (IOException e) {
      throw new TorneoException("Error al leer archivo de jugadores: " + e.getMessage());
    }
  }

  /**
   * Muestra en consola información básica de todos los jugadores.
   */
  public void mostrarJugadores() {
    for (Jugador j : jugadores.getVertices()) {
      System.out.println("ID: " + j.getId() + " | Nombre: " + j.getNombre() +
              " | Nº Camiseta: " + j.getNumeroCamiseta() +
              " | Posición: " + j.getPosicion() +
              (j.getEquipo() != null ? " | Equipo: " + j.getEquipo().getNombre() : ""));
    }
  }

  /**
   * Solicita al usuario un fragmento del nombre y muestra los jugadores que lo contienen.
   *
   * @param scanner Scanner para leer la entrada del usuario.
   */
  public void buscarJugadores(Scanner scanner) {
    System.out.print("Ingrese parte del nombre del jugador a buscar: ");
    String fragmento = scanner.nextLine().trim();
    List<Jugador> resultados = buscarJugadoresPorNombreParcial(fragmento);
    mostrarListaDeJugadores(resultados);
  }

  /**
   * Busca jugadores cuyo nombre contenga el fragmento especificado (búsqueda parcial).
   *
   * @param fragmento Fragmento del nombre a buscar.
   * @return Lista de jugadores que coinciden con el fragmento.
   */
  public List<Jugador> buscarJugadoresPorNombreParcial(String fragmento) {
    List<Jugador> resultados = new ArrayList<>();
    String fragmentoLower = fragmento.toLowerCase();
    for (Jugador jugador : jugadores.getVertices()) {
      String nombre = jugador.getNombre();
      if (nombre != null && nombre.toLowerCase().contains(fragmentoLower)) {
        resultados.add(jugador);
      }
    }
    return resultados;
  }

  /**
   * Muestra en consola una lista de jugadores, utilizando la función que imprime la información detallada.
   *
   * @param jugadores Lista de jugadores a mostrar.
   */
  public void mostrarListaDeJugadores(List<Jugador> jugadores) {
    if (jugadores.isEmpty()) {
      System.out.println("No se encontraron jugadores.");
      return;
    }
    for (Jugador j : jugadores) {
      mostrarInformacionJugador(j);
    }
  }

  /**
   * Muestra información detallada de un jugador.
   *
   * @param j Jugador del cual se mostrará la información.
   */
  private void mostrarInformacionJugador(Jugador j) {
    System.out.println("\n=== Información del Jugador ===");
    System.out.println("Nombre: " + j.getNombre());
    System.out.println("Número: " + (j.getNumeroCamiseta() == -1 ? "Sin dorsal" : j.getNumeroCamiseta()));
    System.out.println("Posición: " + j.getPosicion());
    System.out.println("Edad: " + j.getEdad());
    System.out.println("Altura: " + j.getAltura());
    System.out.println("Equipo: " + (j.getEquipo() != null ? j.getEquipo().getNombre() : "Sin equipo"));
    System.out.println("Liga: " + j.getLiga());
    System.out.println("Estadísticas:");
    System.out.println("  - Goles: " + j.getGoles());
    System.out.println("  - Asistencias: " + j.getAsistencias());
    System.out.println("  - Amarillas: " + j.getAmarillas());
    System.out.println("  - Rojas: " + j.getRojas());
    System.out.println("=============================");
  }

  /**
   * Retorna el conjunto que contiene todos los jugadores.
   *
   * @return Conjunto de jugadores.
   */
  public ConjuntoGenericoTDA<Jugador> getJugadores() {
    return jugadores;
  }

  /**
   * Crea un nuevo jugador y lo inserta en el conjunto.
   *
   * @param nombre          Nombre del jugador.
   * @param numeroCamiseta  Número de camiseta del jugador.
   * @param posicion        Posición en el campo.
   * @param liga            Liga a la que pertenece.
   * @param altura          Altura del jugador.
   * @param edad            Edad del jugador.
   * @param goles           Número de goles.
   * @param asistencias     Número de asistencias.
   * @param amarillas       Tarjetas amarillas.
   * @param rojas           Tarjetas rojas.
   * @return El jugador creado.
   * @throws TorneoException Si ocurre algún error durante la creación.
   */
  public Jugador crearJugador(String nombre, int numeroCamiseta, String posicion, String liga, String altura, int edad,
                              int goles, int asistencias, int amarillas, int rojas) throws TorneoException {
    Jugador nuevoJugador = new Jugador(++idJugador, nombre, numeroCamiseta, posicion, liga, altura, edad, goles,
            asistencias, amarillas, rojas);
    jugadores.insertar(nuevoJugador);
    return nuevoJugador;
  }

  /**
   * Elimina un jugador del conjunto, siempre y cuando no esté asociado a ningún equipo.
   *
   * @param jugador Jugador a eliminar.
   */
  public void eliminarJugador(Jugador jugador) {
    if (jugador != null && jugador.getEquipo() == null) {
      jugadores.eliminar(jugador);
    }
  }

  /**
   * Devuelve la cantidad de jugadores registrados.
   *
   * @return Número total de jugadores en el conjunto.
   */
  public int cantidadJugadores() {
    return jugadores.tamanio();
  }
}
