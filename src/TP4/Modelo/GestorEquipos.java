package TP4.Modelo;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;


import TP4.Excepciones.TorneoException;
import TP4.Modelo.Jugador;
import TP4.Modelo.Equipo;


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
                    String nombre = datos[1];

                    Equipo equipo = new Equipo(idEquipo, nombre, liga);
                    equipo.setId(idEquipo++); // Asignar ID
                    equiposPorLiga.computeIfAbsent(liga, k -> new ArrayList<>()).add(equipo);
                }
            }
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

    public void mostrarEquiposPorLiga() {
        for (String liga : equiposPorLiga.keySet()) {
            System.out.println("Liga: " + liga);
            for (Equipo e : equiposPorLiga.get(liga)) {
                System.out.println(" - [ID: " + e.getId() + "] " + e.getNombre());
            }
        }
    }
}
