package TP4.Modelo;

import TP4.TDA.ConjuntoGenericoTDA;
import TP4.Implementacion.ConjuntoGenericoImpl;
import TP4.Excepciones.TorneoException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class GestorJugadores {
  private static GestorJugadores instancia;
  private ConjuntoGenericoTDA<Jugador> jugadores;
  public int idJugador;
  private GestorEquipos gestorEquipos;

  public GestorJugadores(GestorEquipos gestorEquipos) {
    this.jugadores = new ConjuntoGenericoImpl<>();
    this.idJugador = 1;
    this.gestorEquipos = gestorEquipos;
  }

  public static GestorJugadores getInstancia() {
    if (instancia == null) {
      instancia = new GestorJugadores(new GestorEquipos());
    }
    return instancia;
  }

  public Jugador crearJugador(String nombre, int numeroCamiseta, String posicion, String liga, String altura, int edad,
      int goles, int asistencias, int amarillas, int rojas) throws TorneoException {
    Jugador nuevoJugador = new Jugador(++idJugador, nombre, numeroCamiseta, posicion, liga, altura, edad, goles,
        asistencias, amarillas, rojas);
    jugadores.insertar(nuevoJugador);
    return nuevoJugador;
  }

  public void cargarJugadores(String rutaArchivo) throws TorneoException {
    try (BufferedReader reader = new BufferedReader(new FileReader(rutaArchivo))) {
      String linea;
      int numeroLinea = 0;
      while ((linea = reader.readLine()) != null) {
        numeroLinea++;
        if (linea.trim().isEmpty()) {
          continue;
        }
        String[] datos = linea.split(";");
        if (datos.length == 11) {
          try {
            String liga = datos[0].trim();
            String nombreEquipo = datos[1].trim();
            String nombreJugador = datos[2].trim();
            String posicion = datos[3].trim();
            int numeroCamiseta;
            if (datos[4].equals("-")) {
              numeroCamiseta = -1; // Asignar -1 para "-"
            } else {
              numeroCamiseta = Integer.parseInt(datos[4].trim());
            }
            int edad = Integer.parseInt(datos[5].trim());
            String altura = datos[6].trim();
            if (altura.equalsIgnoreCase("Desconocido")) {
              altura = "N/A";
            } else {
              altura = altura.replace(",", ".").replace("m", "");
            }
            int goles = Integer.parseInt(datos[7].trim());
            int asistencias = Integer.parseInt(datos[8].trim());
            int rojas = Integer.parseInt(datos[9].trim());
            int amarillas = Integer.parseInt(datos[10].trim());

            // Crear jugador
            Jugador jugador;
            try {
              jugador = new Jugador(idJugador++, nombreJugador, numeroCamiseta, posicion, liga, altura, edad,
                  goles, asistencias, amarillas, rojas);
            } catch (TorneoException e) {
              System.err.println(
                  "Error en la línea " + numeroLinea + ": " + e.getMessage() + " para el jugador " + nombreJugador);
              continue; // Omitir jugador con número de camiseta inválido
            }
            jugadores.insertar(jugador);

            // Asociar al equipo
            boolean equipoEncontrado = false;
            for (List<Equipo> equipos : gestorEquipos.getEquiposPorLiga().values()) {
              for (Equipo equipo : equipos) {
                if (equipo.getNombre().equalsIgnoreCase(nombreEquipo) && equipo.getLiga().equalsIgnoreCase(liga)) {
                  try {
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
                          try {
                            jugador.setNumeroCamiseta(numeroCamiseta);
                          } catch (TorneoException ex) {
                            System.err
                                .println("Error al asignar camiseta para " + nombreJugador + ": " + ex.getMessage());
                            break;
                          }
                          intentos++;
                        } else {
                          throw e;
                        }
                      }
                    }
                    if (!anadido) {
                      System.err.println("No se pudo asignar un número de camiseta único para " + nombreJugador
                          + " en la línea " + numeroLinea);
                      idJugador--;
                      jugadores.eliminar(jugador);
                    }
                  } catch (TorneoException e) {
                    System.err.println("Error en la línea " + numeroLinea + ": " + e.getMessage());
                    idJugador--;
                    jugadores.eliminar(jugador);
                  }
                  break;
                }
              }
              if (equipoEncontrado)
                break;
            }
            if (!equipoEncontrado) {
              System.err
                  .println("Equipo no encontrado para el jugador en la línea " + numeroLinea + ": " + nombreEquipo);
              idJugador--;
              jugadores.eliminar(jugador);
            }
          } catch (NumberFormatException e) {
            System.err.println(
                "Error en la línea " + numeroLinea + ": Formato inválido en los datos numéricos para " + linea);
            continue;
          }
        } else {
          System.err.println("Formato inválido en la línea " + numeroLinea + ": " + linea);
          throw new TorneoException("Formato inválido en la línea " + numeroLinea);
        }
      }
    } catch (IOException e) {
      throw new TorneoException("Error al leer el archivo de jugadores: " + e.getMessage());
    }
  }

  public void agregarJugadorManualmente(Scanner scanner) throws TorneoException {
    System.out.println("\n--- Agregar Jugador Manualmente ---");
    System.out.print("Ingrese el nombre del jugador: ");
    String nombreJugador = scanner.nextLine().trim();
    System.out.print("Ingrese el número de camiseta (1-99): ");
    int numeroCamiseta;
    try {
      String camisetaInput = scanner.nextLine().trim();
      if (camisetaInput.equals("-")) {
        throw new TorneoException("El número de camiseta no puede ser '-'.");
      }
      numeroCamiseta = Integer.parseInt(camisetaInput);
    } catch (NumberFormatException e) {
      throw new TorneoException("Número de camiseta inválido.");
    }
    System.out.print("Ingrese la posición: ");
    String posicion = scanner.nextLine().trim();
    System.out.print("Ingrese la liga: ");
    String liga = scanner.nextLine().trim();
    System.out.print("Ingrese el nombre del equipo: ");
    String equipoNombre = scanner.nextLine().trim();
    System.out.print("Ingrese la edad: ");
    int edad;
    try {
      edad = Integer.parseInt(scanner.nextLine().trim());
    } catch (NumberFormatException e) {
      throw new TorneoException("Edad inválida.");
    }
    System.out.print("Ingrese la altura (en metros, ej. 1.85, o 'N/A'): ");
    String altura = scanner.nextLine().trim();
    if (altura.equalsIgnoreCase("Desconocido")) {
      altura = "N/A";
    } else if (!altura.equalsIgnoreCase("N/A")) {
      altura = altura.replace(",", ".").replace("m", "");
      if (!altura.matches("\\d+\\.\\d{2}")) {
        throw new TorneoException("La altura debe ser 'N/A' o un valor numérico (ej. 1.85)");
      }
    }
    System.out.print("Ingrese los goles: ");
    int goles;
    try {
      goles = Integer.parseInt(scanner.nextLine().trim());
    } catch (NumberFormatException e) {
      throw new TorneoException("Goles inválidos.");
    }
    System.out.print("Ingrese las asistencias: ");
    int asistencias;
    try {
      asistencias = Integer.parseInt(scanner.nextLine().trim());
    } catch (NumberFormatException e) {
      throw new TorneoException("Asistencias inválidas.");
    }
    System.out.print("Ingrese las tarjetas rojas: ");
    int rojas;
    try {
      rojas = Integer.parseInt(scanner.nextLine().trim());
    } catch (NumberFormatException e) {
      throw new TorneoException("Tarjetas rojas inválidas.");
    }
    System.out.print("Ingrese las tarjetas amarillas: ");
    int amarillas;
    try {
      amarillas = Integer.parseInt(scanner.nextLine().trim());
    } catch (NumberFormatException e) {
      throw new TorneoException("Tarjetas amarillas inválidas.");
    }

    Jugador jugador = new Jugador(idJugador++, nombreJugador, numeroCamiseta, posicion, liga, altura, edad,
        goles, asistencias, amarillas, rojas);
    jugadores.insertar(jugador);

    boolean equipoEncontrado = false;
    for (List<Equipo> equipos : gestorEquipos.getEquiposPorLiga().values()) {
      for (Equipo equipo : equipos) {
        if (equipo.getNombre().equalsIgnoreCase(equipoNombre) && equipo.getLiga().equalsIgnoreCase(liga)) {
          try {
            equipo.agregarJugador(jugador);
            equipoEncontrado = true;
          } catch (TorneoException e) {
            System.err.println("Error al agregar jugador: " + e.getMessage());
            idJugador--;
            jugadores.eliminar(jugador);
            throw e;
          }
          break;
        }
      }
      if (equipoEncontrado)
        break;
    }
    if (!equipoEncontrado) {
      idJugador--;
      jugadores.eliminar(jugador);
      throw new TorneoException("Equipo no encontrado: " + equipoNombre + " en la liga " + liga);
    }

    System.out.println("Jugador agregado exitosamente: " + jugador);
  }

  public void eliminarJugador(Jugador jugador) {
    if (jugador != null && jugador.getEquipo() == null) {
      jugadores.eliminar(jugador);
    }
  }

  public void mostrarJugadores() {
    for (Jugador j : jugadores.getVertices()) {
      System.out.println("ID: " + j.getId() + " | Nombre: " + j.getNombre() +
          " | Nº Camiseta: " + j.getNumeroCamiseta() +
          " | Posición: " + j.getPosicion() +
          (j.getEquipo() != null ? " | Equipo: " + j.getEquipo().getNombre() : ""));
    }
  }

  public Jugador buscarJugadorPorNombre(String nombre) {
    for (Jugador j : jugadores.getVertices()) {
      if (j.getNombre().contains(nombre)) {
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
        System.out.println("  - Tarjetas amarillas: " + j.getAmarillas());
        System.out.println("  - Tarjetas rojas: " + j.getRojas());
        System.out.println("=============================");
        return j;
      }
    }
    return null;
  }

  public boolean existeJugador(String jugador) {
    return buscarJugadorPorNombre(jugador) != null;
  }

  public int cantidadJugadores() {
    return jugadores.tamanio();
  }

  public ConjuntoGenericoTDA<Jugador> getJugadores() {
    return jugadores;
  }
}