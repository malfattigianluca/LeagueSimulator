@echo off
echo Compilando clases en orden...

javac -d . src/SimuladorTorneosFutbol_ProyectoFinal/Excepciones/TorneoException.java
javac -d . src/SimuladorTorneosFutbol_ProyectoFinal/Util/Validador.java
javac -d . src/SimuladorTorneosFutbol_ProyectoFinal/TDA/ConjuntoGenericoTDA.java
javac -d . src/SimuladorTorneosFutbol_ProyectoFinal/Implementacion/ConjuntoGenericoImpl.java
javac -d . src/SimuladorTorneosFutbol_ProyectoFinal/Modelo/Jugador.java
javac -d . src/SimuladorTorneosFutbol_ProyectoFinal/Modelo/Equipo.java
javac -d . src/SimuladorTorneosFutbol_ProyectoFinal/Modelo/GestorEquipos.java
javac -d . src/SimuladorTorneosFutbol_ProyectoFinal/Modelo/GestorJugadores.java
javac -d . src/SimuladorTorneosFutbol_ProyectoFinal/Modelo/Partido.java
javac -d . src/SimuladorTorneosFutbol_ProyectoFinal/Modelo/SimuladorLiga.java
javac -d . src/SimuladorTorneosFutbol_ProyectoFinal/Modelo/Torneo.java
javac -d . src/SimuladorTorneosFutbol_ProyectoFinal/App.java

echo Compilacion completada.
pause 