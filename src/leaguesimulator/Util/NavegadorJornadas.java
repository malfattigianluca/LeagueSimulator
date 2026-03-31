package leaguesimulator.Util;

import leaguesimulator.Modelo.Partido;

import java.util.List;
import java.util.ListIterator;
import java.util.Scanner;

/**
 * Utilitario que encapsula la navegación interactiva por jornadas de partidos.
 *
 * El mismo patrón N/P/0 estaba duplicado 4 veces:
 *   - SimuladorLiga.mostrarPartidosJugados()
 *   - Torneo.mostrarPartidosTorneo()
 *   - TorneoMixto.mostrarPartidosTorneo() (fase grupos)
 *   - TorneoMixto.mostrarPartidosTorneo() (fase eliminatoria)
 *
 * Además, la implementación en SimuladorLiga tenía un bug: 3 checks hasPrevious()
 * con solo 2 llamadas a previous(), salteando una jornada al retroceder.
 * Esta versión corrige ese comportamiento.
 */
public class NavegadorJornadas {

    /** Función que sabe cómo renderizar una jornada específica. */
    @FunctionalInterface
    public interface MostrarJornada {
        void mostrar(List<Partido> jornada, int numeroJornada);
    }

    private NavegadorJornadas() {}

    /**
     * Navega interactivamente por una lista de jornadas.
     *
     * Controles:
     * - N: avanzar a la siguiente jornada.
     * - P: retroceder a la jornada anterior.
     * - 0: salir del navegador.
     *
     * @param jornadas       Lista de jornadas (cada una es una lista de partidos).
     * @param scanner        Scanner para leer la entrada del usuario.
     * @param mostrarJornada Función que imprime una jornada y su número.
     */
    public static void navegar(List<List<Partido>> jornadas, Scanner scanner, MostrarJornada mostrarJornada) {
        if (jornadas.isEmpty()) {
            Consola.mostrarMensaje("No hay jornadas para mostrar.");
            return;
        }

        ListIterator<List<Partido>> iterador = jornadas.listIterator();
        int jornadaActual = 0;

        // Muestra la primera jornada al entrar
        mostrarJornada.mostrar(iterador.next(), ++jornadaActual);

        while (true) {
            Consola.mostrarMensaje("\nIngrese N para siguiente, P para anterior, 0 para salir:");
            String entrada = scanner.nextLine().trim().toUpperCase();

            switch (entrada) {
                case "N" -> {
                    if (iterador.hasNext()) {
                        mostrarJornada.mostrar(iterador.next(), ++jornadaActual);
                    } else {
                        Consola.mostrarMensaje("No hay más jornadas siguientes.");
                    }
                }
                case "P" -> {
                    // El iterador se posiciona después del elemento devuelto por next().
                    // Para volver a la jornada anterior necesitamos retroceder 2 pasos:
                    //   1) previous() vuelve al elemento que acabamos de mostrar.
                    //   2) previous() obtiene el elemento anterior a ese.
                    iterador.previous(); // descarta el actual del cursor
                    if (iterador.hasPrevious()) {
                        mostrarJornada.mostrar(iterador.previous(), --jornadaActual);
                        iterador.next(); // reposiciona el cursor correctamente
                    } else {
                        iterador.next(); // restaura la posición
                        Consola.mostrarMensaje("Ya estás en la primera jornada.");
                    }
                }
                case "0" -> {
                    Consola.mostrarMensaje("Saliendo del visor de jornadas.");
                    return;
                }
                default -> Consola.mostrarMensaje("Opción inválida.");
            }
        }
    }
}
