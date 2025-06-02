package TP4.Modelo;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;


import TP4.Excepciones.TorneoException;
import TP4.Modelo.Jugador;
import TP4.Modelo.Equipo;
import TP4.TDA.ConjuntoGenericoTDA;
import TP4.Implementacion.ConjuntoGenericoImpl;


public class GestorEquipos {
    private Map<String, List<Equipo>> equiposPorLiga;
    private int idEquipo;

    public GestorEquipos() {
        this.equiposPorLiga = new HashMap<>();
        this.idEquipo = 1;
    }

    public void cargarEquiposPorLiga(String rutaArchivo) {
        try (BufferedReader reader = new BufferedReader(new FileReader(rutaArchivo))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] datos = linea.split(";");
                if (datos.length >= 2) {
                    String liga = datos[0];
                    String nombre = datos[2];
                    String pais = datos[3];
                    String escudo = datos[4];
                    int elo = (int) Double.parseDouble(datos[5].trim());
                    Equipo equipo = new Equipo(idEquipo, nombre, elo, liga, escudo, pais);
                    equipo.setId(idEquipo++);
                    equiposPorLiga.computeIfAbsent(liga, k -> new ArrayList<>()).add(equipo);
                }
            }
            System.out.println("---Archivo cargado correctamente---");
        } catch (IOException e) {
            System.err.println("Error al leer equipos: " + e.getMessage());
        } catch (TorneoException e) {
            throw new RuntimeException(e);
        }
    }

    public void agregarEquipo(Equipo equipo) {
        String liga = equipo.getLiga();
        equipo.setId(idEquipo++); // Asignar ID
        equiposPorLiga.computeIfAbsent(liga, k -> new ArrayList<>()).add(equipo);
    }

    public Equipo buscarEquipoPorNombre(String nombre) {
        for (List<Equipo> lista : equiposPorLiga.values()) {
            for (Equipo e : lista) {
                if (e.getNombre().equalsIgnoreCase(nombre)) {
                    return e;
                }
            }
        }
        return null;
    }

    public boolean eliminarEquipo(String nombre) {
        for (String liga : equiposPorLiga.keySet()) {
            List<Equipo> lista = equiposPorLiga.get(liga);
            Iterator<Equipo> it = lista.iterator();
            while (it.hasNext()) {
                Equipo e = it.next();
                if (e.getNombre().equalsIgnoreCase(nombre)) {
                    it.remove();
                    return true;
                }
            }
        }
        return false;
    }

    public List<Equipo> listarEquipos() {
        List<Equipo> todos = new ArrayList<>();
        for (List<Equipo> lista : equiposPorLiga.values()) {
            todos.addAll(lista);
        }
        return todos;
    }

    public List<Equipo> filtrarPorLiga(String liga) {
        return equiposPorLiga.getOrDefault(liga, new ArrayList<>());
    }

    public void asignarJugadorAEquipo(Jugador jugador) throws TorneoException {
        Equipo equipoJugador = jugador.getEquipo();
        if (equipoJugador != null) {
            Equipo equipoRegistrado = buscarEquipoPorNombre(equipoJugador.getNombre());
            if (equipoRegistrado != null) {
                equipoRegistrado.agregarJugador(jugador);
            } else {
                agregarEquipo(equipoJugador);
                equipoJugador.agregarJugador(jugador);
            }
        }
    }

    public boolean existeEquipo(String nombre) {
        return buscarEquipoPorNombre(nombre) != null;
    }

    public void mostrarEquiposPorLiga(String nombreLiga) {
        for (String liga : equiposPorLiga.keySet()) {
            if (liga.equalsIgnoreCase(nombreLiga)) {
                System.out.println("\nLiga: " + liga);
                System.out.printf("%-5s %-20s %-30s %-10s%n", "ID", "Escudo", "Nombre", "Elo");
                System.out.println("---------------------------------------------------------------");
                for (Equipo e : equiposPorLiga.get(liga)) {
                    System.out.printf("%-5d %-20s %-30s %-10d%n",
                            e.getId(), e.getEscudo(), e.getNombre(), e.getElo());
                }
                return;
            }
        }
        System.out.println("No se encontraron equipos para la liga: " + nombreLiga);
    }


    public void mostrarEquipo(String nombreEquipo) {
        boolean encontrado = false;
        for (String liga : equiposPorLiga.keySet()) {
            for (Equipo equipo : equiposPorLiga.get(liga)) {
                if (equipo.getNombre().equalsIgnoreCase(nombreEquipo)) {
                    System.out.println("\nEquipo: " + equipo.getNombre() + " (" + liga + ")");
                    ConjuntoGenericoTDA<Jugador> jugadoresConjunto = equipo.getJugadores();
                    if (jugadoresConjunto.estaVacio()) {
                        System.out.println("No hay jugadores registrados para este equipo.");
                    } else {
                        System.out.printf("%-20s %-30s %-30s %-15s %-10s %-10s %-10s%n",
                                "Liga", "Equipo", "Nombre", "Posición", "Camiseta", "Edad", "Altura");
                        System.out.println("------------------------------------------------------------------------------------");
                        List<Jugador> jugadores = jugadoresConjunto.getVertices(); // Obtener la lista
                        for (Jugador jugador : jugadores) {
                            System.out.printf("%-20s %-30s %-30s %-15s %-10d %-10d %-10.2f%n",
                                    jugador.getLiga(), jugador.getEquipo(), jugador.getNombre(),
                                    jugador.getPosicion(), jugador.getNumeroCamiseta(), jugador.getEdad(), jugador.getAltura());
                        }
                    }
                    encontrado = true;
                    return;
                }
            }
        }
        if (!encontrado) {
            System.out.println("No se encontró el equipo: " + nombreEquipo);
        }
    }

    public Map<String, List<Equipo>> getEquiposPorLiga() {
        return equiposPorLiga;
    }
}
