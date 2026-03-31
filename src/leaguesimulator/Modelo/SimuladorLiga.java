package leaguesimulator.Modelo;

import leaguesimulator.Excepciones.TorneoException;
import leaguesimulator.Util.NavegadorJornadas;
import leaguesimulator.Util.SimuladorPartido;

import java.util.*;

/**
 * Simula una liga de fútbol estructurada por jornadas, con modalidad ida y vuelta.
 *
 * La clase administra:
 * - Lista de equipos participantes.
 * - Lista de partidos jugados.
 * - Jornadas (fechas) compuestas por varios partidos.
 * - Un calendario estructurado con enfrentamientos predefinidos.
 *
 * La lógica de simulación (ELO + Poisson) está delegada a {@link SimuladorPartido}.
 * La navegación interactiva por jornadas está delegada a {@link NavegadorJornadas}.
 */
public class SimuladorLiga {

    private final String             nombre;
    private final List<Equipo>       equipos;
    private final List<Partido>      partidos;
    private final List<List<Partido>> jornadas;
    private       List<List<Enfrentamiento>> calendario;
    private       int                jornadaActual;


    public SimuladorLiga(String nombre) {
        this.nombre        = nombre;
        this.equipos       = new ArrayList<>();
        this.partidos      = new ArrayList<>();
        this.jornadas      = new ArrayList<>();
        this.calendario    = new ArrayList<>();
        this.jornadaActual = 0;
    }


    /**
     * Agrega un equipo a la liga, si no fue incorporado previamente.
     */
    public void agregarEquipo(Equipo equipo) {
        if (!equipos.contains(equipo)) {
            equipos.add(equipo);
        }
    }


    /**
     * Genera el calendario completo en formato ida y vuelta.
     *
     * Usa un algoritmo de rotación round-robin: todos los equipos se enfrentan
     * entre sí exactamente una vez en la fase de ida y una vez en la vuelta.
     *
     * @throws TorneoException Si hay menos de 2 equipos o el número es impar.
     */
    public void generarCalendario() throws TorneoException {
        if (equipos.size() < 2) {
            throw new TorneoException("Se necesitan al menos 2 equipos para generar un calendario");
        }
        if (equipos.size() % 2 != 0) {
            throw new TorneoException("El número de equipos debe ser par para generar un calendario");
        }

        calendario.clear();

        int numEquipos     = equipos.size();
        int numJornadasIda = numEquipos - 1;
        List<Equipo> rotables = new ArrayList<>(equipos);

        List<List<Enfrentamiento>> ida = new ArrayList<>();
        for (int jornada = 0; jornada < numJornadasIda; jornada++) {
            List<Enfrentamiento> enfrentamientosJornada = new ArrayList<>();
            for (int i = 0; i < numEquipos / 2; i++) {
                enfrentamientosJornada.add(new Enfrentamiento(rotables.get(i), rotables.get(numEquipos - 1 - i)));
            }
            ida.add(enfrentamientosJornada);
            Collections.rotate(rotables.subList(1, numEquipos), 1);
        }

        List<List<Enfrentamiento>> vuelta = new ArrayList<>();
        for (List<Enfrentamiento> jornada : ida) {
            List<Enfrentamiento> jornadaVuelta = new ArrayList<>();
            for (Enfrentamiento enf : jornada) {
                jornadaVuelta.add(new Enfrentamiento(enf.visitante, enf.local));
            }
            vuelta.add(jornadaVuelta);
        }

        calendario.addAll(ida);
        calendario.addAll(vuelta);
    }


    /**
     * Simula la jornada actual del calendario.
     *
     * Para cada enfrentamiento usa {@link SimuladorPartido#simular} que aplica
     * probabilidades ELO, genera goles con Poisson y actualiza el ELO de los equipos.
     *
     * @return Lista de partidos jugados en esta jornada.
     * @throws TorneoException Si hay menos de 2 equipos o ya se jugaron todas las jornadas.
     */
    public List<Partido> simularJornada() throws TorneoException {
        if (equipos.size() < 2) {
            throw new TorneoException("Se necesitan al menos 2 equipos para simular una jornada");
        }
        if (jornadaActual >= calendario.size()) {
            throw new TorneoException("No hay más jornadas para simular");
        }

        List<Partido> partidosDeLaJornada = new ArrayList<>();
        List<Enfrentamiento> jornadaPredefinida = calendario.get(jornadaActual);

        for (Enfrentamiento enfrentamiento : jornadaPredefinida) {
            Partido partido = SimuladorPartido.simular(enfrentamiento.local, enfrentamiento.visitante);
            partidos.add(partido);
            partidosDeLaJornada.add(partido);
        }

        jornadaActual++;
        return partidosDeLaJornada;
    }


    /**
     * Registra una jornada ya simulada en el historial.
     */
    public void agregarJornada(List<Partido> jornada) {
        jornadas.add(jornada);
    }


    /**
     * Reinicia las estadísticas de todos los equipos participantes.
     */
    public void reiniciar() {
        for (Equipo equipo : equipos) {
            equipo.reiniciarEstadisticas();
        }
    }


    /**
     * Muestra la tabla de posiciones actual ordenada por puntos, diferencia de goles
     * y goles a favor (en ese orden de prioridad).
     */
    public void mostrarTabla() {
        System.out.println("\n=== Tabla de Posiciones: " + nombre + " ===");
        System.out.printf("%-30s %-5s %-5s %-5s %-5s %-5s %-5s %-5s %-5s %-6s%n",
                "Equipo", "Pts", "PJ", "PG", "PE", "PP", "GF", "GC", "DG", "ELO");
        System.out.println("----------------------------------------------------------------------------------");

        List<Equipo> ordenados = new ArrayList<>(equipos);
        ordenados.sort((e1, e2) -> {
            if (e1.getPuntos() != e2.getPuntos()) return e2.getPuntos() - e1.getPuntos();
            int dg1 = e1.getDiferenciaGoles(), dg2 = e2.getDiferenciaGoles();
            if (dg1 != dg2) return dg2 - dg1;
            return e2.getGolesAFavor() - e1.getGolesAFavor();
        });

        for (Equipo e : ordenados) {
            System.out.printf("%-30s %-5d %-5d %-5d %-5d %-5d %-5d %-5d %-5d %-6d%n",
                    e.getNombre(), e.getPuntos(), e.getPartidosJugados(),
                    e.getPartidosGanados(), e.getPartidosEmpatados(), e.getPartidosPerdidos(),
                    e.getGolesAFavor(), e.getGolesEnContra(), e.getDiferenciaGoles(), e.getElo());
        }
    }


    /**
     * Permite navegar interactivamente por las jornadas jugadas (N/P/0).
     * Delega la lógica de navegación a {@link NavegadorJornadas}.
     *
     * @param scanner Scanner para leer la entrada del usuario.
     */
    public void mostrarPartidosJugados(Scanner scanner) {
        if (jornadas.isEmpty()) {
            System.out.println("No se han simulado jornadas aún.");
            return;
        }
        NavegadorJornadas.navegar(jornadas, scanner, this::mostrarJornada);
    }


    /** Imprime los partidos de una jornada en formato tabla. */
    private void mostrarJornada(List<Partido> jornada, int numero) {
        System.out.printf("\n=== Jornada %d ===\n", numero);
        System.out.printf("%-28s %3s - %-3s %-28s%n", "Equipo Local", "G", "G", "Equipo Visitante");
        System.out.println("---------------------------------------------------------------");
        for (Partido p : jornada) {
            System.out.printf("%-28s %3d - %-3d %-28s%n",
                    p.getLocal().getNombre(), p.getGolesLocal(),
                    p.getGolesVisitante(), p.getVisitante().getNombre());
        }
    }


    public String getNombre()         { return nombre; }
    public int    getTotalJornadas()  { return calendario.size(); }
    public List<Partido> getPartidos(){ return new ArrayList<>(partidos); }


    /**
     * Representa un enfrentamiento programado entre dos equipos en el calendario.
     */
    private static class Enfrentamiento {
        final Equipo local;
        final Equipo visitante;

        Enfrentamiento(Equipo local, Equipo visitante) {
            this.local     = local;
            this.visitante = visitante;
        }
    }
}
