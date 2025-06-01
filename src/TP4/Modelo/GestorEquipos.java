package TP4.Modelo;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import TP4.Excepciones.TorneoException;
import TP4.Modelo.Jugador;
import TP4.Modelo.Equipo;


public class GestorEquipos {
    private List<Equipo> equipos;
    private int id=1;

    public GestorEquipos(){
        this.equipos= new ArrayList<>();
    }
    public List<Equipo> cargarEquipos(String archivo) {
        List<Equipo> cargados = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] datos = linea.split(";");
                if (datos.length >= 2) {
                    String liga = datos[0];
                    String nombreEquipo = datos[1];

                    if (!existeEquipo(nombreEquipo)) {
                        Equipo e = new Equipo(id, nombreEquipo, liga);
                        equipos.add(e);
                        cargados.add(e);
                        id++;
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error al leer equipos: " + e.getMessage());
        } catch (TorneoException e) {
            throw new RuntimeException(e);
        }

        return cargados;
    }

    public void agregarEquipo(Equipo equipo) {
        if (!existeEquipo(equipo.getNombre())) {
            equipos.add(equipo);
            id++;
        }
    }

    public Equipo buscarEquipoPorNombre(String nombre) {
        for (Equipo e : equipos) {
            if (e.getNombre().equalsIgnoreCase(nombre)) {
                return e;
            }
        }
        return null;
    }

    public boolean eliminarEquipo(String nombre) {
        Equipo equipo = buscarEquipoPorNombre(nombre);
        if (equipo != null) {
            equipos.remove(equipo);
            return true;
        }
        return false;
    }

    public List<Equipo> listarEquipos() {
        return new ArrayList<>(equipos);
    }

    public List<Equipo> filtrarPorLiga(String liga) {
        List<Equipo> resultado = new ArrayList<>();
        for (Equipo e : equipos) {
            if (e.getLiga().equalsIgnoreCase(liga)) {
                resultado.add(e);
            }
        }
        return resultado;
    }

    public void asignarJugadorAEquipo(Jugador jugador) throws TorneoException {
        Equipo equipoDelJugador = jugador.getEquipo();

        if (equipoDelJugador != null) {
            Equipo equipoRegistrado = buscarEquipoPorNombre(equipoDelJugador.getNombre());

            if (equipoRegistrado != null) {
                equipoRegistrado.agregarJugador(jugador);
            } else {
                // Si no existe, lo agregamos y luego asignamos
                equipos.add(equipoDelJugador);
                equipoDelJugador.agregarJugador(jugador);
            }
        }
    }

    public boolean existeEquipo(String nombre) {
        return buscarEquipoPorNombre(nombre) != null;
    }
}
