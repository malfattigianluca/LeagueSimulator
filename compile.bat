@echo off
echo Compilando clases en orden...

javac -d . src/TP4/Excepciones/TorneoException.java
javac -d . src/TP4/Util/Validador.java
javac -d . src/TP4/TDA/ConjuntoGenericoTDA.java
javac -d . src/TP4/Implementacion/ConjuntoGenericoImpl.java
javac -d . src/TP4/Modelo/Jugador.java
javac -d . src/TP4/Modelo/Equipo.java
javac -d . src/TP4/Modelo/GestorEquipos.java
javac -d . src/TP4/Modelo/GestorJugadores.java
javac -d . src/TP4/Modelo/Partido.java
javac -d . src/TP4/Modelo/SimuladorLiga.java
javac -d . src/TP4/Modelo/Torneo.java
javac -d . src/TP4/App.java

echo Compilacion completada.
pause 