package com.hotel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

/**
 * Gestió de reserves d'un hotel.
 */
public class App {

    // --------- CONSTANTS I VARIABLES GLOBALS ---------

    // Tipus d'habitació
    public static final String TIPUS_ESTANDARD = "Estàndard";
    public static final String TIPUS_SUITE = "Suite";
    public static final String TIPUS_DELUXE = "Deluxe";

    // Serveis addicionals
    public static final String SERVEI_ESMORZAR = "Esmorzar";
    public static final String SERVEI_GIMNAS = "Gimnàs";
    public static final String SERVEI_SPA = "Spa";
    public static final String SERVEI_PISCINA = "Piscina";

    // Capacitat inicial
    public static final int CAPACITAT_ESTANDARD = 30;
    public static final int CAPACITAT_SUITE = 20;
    public static final int CAPACITAT_DELUXE = 10;

    // IVA
    public static final float IVA = 0.21f;

    // Scanner únic
    public static Scanner sc = new Scanner(System.in);

    // HashMaps de consulta
    public static HashMap<String, Float> preusHabitacions = new HashMap<String, Float>();
    public static HashMap<String, Integer> capacitatInicial = new HashMap<String, Integer>();
    public static HashMap<String, Float> preusServeis = new HashMap<String, Float>();

    // HashMaps dinàmics
    public static HashMap<String, Integer> disponibilitatHabitacions = new HashMap<String, Integer>();
    public static HashMap<Integer, ArrayList<String>> reserves = new HashMap<Integer, ArrayList<String>>();

    // Generador de nombres aleatoris per als codis de reserva
    public static Random random = new Random();

    // --------- MÈTODE MAIN ---------

    /**
     * Mètode principal. Mostra el menú en un bucle i gestiona l'opció triada
     * fins que l'usuari decideix eixir.
     */
    public static void main(String[] args) {
        inicialitzarPreus();

        int opcio = 0;
        do {
            mostrarMenu();
            opcio = llegirEnter("Seleccione una opció: ");
            gestionarOpcio(opcio);
        } while (opcio != 6);

        System.out.println("Eixint del sistema... Gràcies per utilitzar el gestor de reserves!");
    }

    // --------- MÈTODES DEMANATS ---------

    /**
     * Configura els preus de les habitacions, serveis addicionals i
     * les capacitats inicials en els HashMaps corresponents.
     */
    public static void inicialitzarPreus() {
        // Preus habitacions
        preusHabitacions.put(TIPUS_ESTANDARD, 50f);
        preusHabitacions.put(TIPUS_SUITE, 100f);
        preusHabitacions.put(TIPUS_DELUXE, 150f);

        // Capacitats inicials
        capacitatInicial.put(TIPUS_ESTANDARD, CAPACITAT_ESTANDARD);
        capacitatInicial.put(TIPUS_SUITE, CAPACITAT_SUITE);
        capacitatInicial.put(TIPUS_DELUXE, CAPACITAT_DELUXE);

        // Disponibilitat inicial (comença igual que la capacitat)
        disponibilitatHabitacions.put(TIPUS_ESTANDARD, CAPACITAT_ESTANDARD);
        disponibilitatHabitacions.put(TIPUS_SUITE, CAPACITAT_SUITE);
        disponibilitatHabitacions.put(TIPUS_DELUXE, CAPACITAT_DELUXE);

        // Preus serveis
        preusServeis.put(SERVEI_ESMORZAR, 10f);
        preusServeis.put(SERVEI_GIMNAS, 15f);
        preusServeis.put(SERVEI_SPA, 20f);
        preusServeis.put(SERVEI_PISCINA, 25f);
    }

    /**
     * Mostra el menú principal amb les opcions disponibles per a l'usuari.
     */
    public static void mostrarMenu() {
        System.out.println("\n===== MENÚ PRINCIPAL =====");
        System.out.println("1. Reservar una habitació");
        System.out.println("2. Alliberar una habitació");
        System.out.println("3. Consultar disponibilitat");
        System.out.println("4. Llistar reserves per tipus");
        System.out.println("5. Obtindre una reserva");
        System.out.println("6. Ixir");
    }

    /**
     * Processa l'opció seleccionada per l'usuari i crida el mètode corresponent.
     */
    public static void gestionarOpcio(int opcio) {
       switch (opcio) {
        case 1:
            reservarHabitacio();
            break;
        case 2:
            alliberarHabitacio();
            break;
        case 3:
            consultarDisponibilitat();
            break;
        case 4:
            obtindreReservaPerTipus();
            break;
        case 5:
            obtindreReserva();
            break;
       }
    }

    /**
     * Gestiona tot el procés de reserva: selecció del tipus d'habitació,
     * serveis addicionals, càlcul del preu total i generació del codi de reserva.
     */
    public static void reservarHabitacio() {
        System.out.println("\n===== RESERVAR HABITACIÓ =====");
           
    
    String tipus = seleccionarTipusHabitacioDisponible();
    
    if (tipus == null) {
        System.out.println("No s'ha pogut realitzar la reserva.");
        return;
    }
    
    ArrayList<String> serveis = seleccionarServeis();
    
    float preuTotal = calcularPreuTotal(tipus, serveis);
    
    System.out.println("\nCalculem el total...");
    System.out.println("Preu habitació: " + preusHabitacions.get(tipus) + "€");
    
    if (serveis.size() > 0) {
        System.out.print("Serveis: ");
        for (int i = 0; i < serveis.size(); i++) {
            System.out.print(serveis.get(i) + " (" + preusServeis.get(serveis.get(i)) + "€)");
            if (i < serveis.size() - 1) {
                System.out.print(", ");
            }
        }
        System.out.println();
    }
    
    float subtotal = preuTotal / (1 + IVA);
    System.out.println("Subtotal: " + subtotal + "€");
    System.out.println("IVA (21%): " + (preuTotal - subtotal) + "€");
    System.out.println("TOTAL: " + preuTotal + "€");
    
    int codi = generarCodiReserva();

    ArrayList<String> dadesReserva = new ArrayList<String>();
    dadesReserva.add(tipus);                     
    dadesReserva.add(String.valueOf(preuTotal)); 
   
    for (int i = 0; i < serveis.size(); i++) {
        dadesReserva.add(serveis.get(i));
    }

    reserves.put(codi, dadesReserva);

    int disponiblesActuals = disponibilitatHabitacions.get(tipus);
    disponibilitatHabitacions.put(tipus, disponiblesActuals - 1);
    
    System.out.println("\nReserva creada amb èxit!");
    System.out.println("Codi de reserva: " + codi);
        
    }

    /**
     * Pregunta a l'usuari un tipus d'habitació en format numèric i
     * retorna el nom del tipus.
     */
    public static String seleccionarTipusHabitacio() {
        int opcio = llegirEnter("Seleccione tipus d'habitació (1-3): ");
    
    // Convertim el número al String corresponent
    switch (opcio) {
        case 1:
            return TIPUS_ESTANDARD;
        case 2:
            return TIPUS_SUITE;
        case 3:
            return TIPUS_DELUXE;
    }
        return null;
    }

    /**
     * Mostra la disponibilitat i el preu de cada tipus d'habitació,
     * demana a l'usuari un tipus i només el retorna si encara hi ha
     * habitacions disponibles. En cas contrari, retorna null.
     */
    public static String seleccionarTipusHabitacioDisponible() {
        System.out.println("\nTipus d'habitació disponibles:");
       // Mostrem la info de cada tipus
    System.out.println("1. "); mostrarInfoTipus(TIPUS_ESTANDARD);
    System.out.println("2. "); mostrarInfoTipus(TIPUS_SUITE);
    System.out.println("3. "); mostrarInfoTipus(TIPUS_DELUXE);
    
    // Demanem selecció
    String tipusSeleccionat = seleccionarTipusHabitacio();

    if (tipusSeleccionat == null) {
        return null;
    }
    int disponibles = disponibilitatHabitacions.get(tipusSeleccionat);
    
    if (disponibles > 0) {
        return tipusSeleccionat;
    } else {
        System.out.println("No queden habitacions disponibles d'aquest tipus.");
        return null;
    }
    }

    /**
     * Permet triar serveis addicionals (entre 0 i 4, sense repetir) i
     * els retorna en un ArrayList de String.
     */
    public static ArrayList<String> seleccionarServeis() {
        ArrayList<String> serveisSeleccionats = new ArrayList<String>();
    
        System.out.println("\nServeis addicionals:");
        System.out.println("0. Finalitzar");
        System.out.println("1. Esmorzar (10€)");
        System.out.println("2. Gimnàs (15€)");
        System.out.println("3. Spa (20€)");
        System.out.println("4. Piscina (25€)");
        
        boolean continuar = true;
        
        while (continuar && serveisSeleccionats.size() < 4) {
            System.out.print("Vol afegir un servei? (s/n): ");
            String resposta = sc.next();
            
            if (resposta.equalsIgnoreCase("s")) {
                int opcio = llegirEnter("Seleccione servei (1-4): ");
                String servei = null;
                        switch (opcio) {
                case 1: 
                    servei = SERVEI_ESMORZAR; 
                break;
                case 2: 
                    servei = SERVEI_GIMNAS; 
                break;
                case 3: 
                    servei = SERVEI_SPA; 
                break;
                case 4: 
                    servei = SERVEI_PISCINA; 
                break;
                default:
                    System.out.println("Opció no vàlida.");
                    servei = null;
            }
           
            if (servei != null) {
                if (!serveisSeleccionats.contains(servei)) {
                    serveisSeleccionats.add(servei);
                    System.out.println("Servei afegit: " + servei);
                } else {
                    System.out.println("Aquest servei ja està seleccionat.");
                }
            }
        } else {
            continuar = false;
        }
    }
    
    return serveisSeleccionats;
    }

    /**
     * Calcula i retorna el cost total de la reserva, incloent l'habitació,
     * els serveis seleccionats i l'IVA.
     */
    public static float calcularPreuTotal(String tipusHabitacio, ArrayList<String> serveisSeleccionats) {
      float preuBase = preusHabitacions.get(tipusHabitacio);
    
    float preuServeis = 0;
    for (int i = 0; i < serveisSeleccionats.size(); i++) {
        String servei = serveisSeleccionats.get(i);
        preuServeis += preusServeis.get(servei);
    }
   
    float subtotal = preuBase + preuServeis;
 
    float total = subtotal + (subtotal * IVA);
    
    return total;
    }

    /**
     * Genera i retorna un codi de reserva únic de tres xifres
     * (entre 100 i 999) que no estiga repetit.
     */
    public static int generarCodiReserva() {
         int codi;
    
    do {
        // Genera número entre 100 i 999
        codi = random.nextInt(900) + 100;
    } while (reserves.containsKey(codi));  // Repeteix si ja existeix
    
    return codi;
    }

    /**
     * Permet alliberar una habitació utilitzant el codi de reserva
     * i actualitza la disponibilitat.
     */
    public static void alliberarHabitacio() {
        System.out.println("\n===== ALLIBERAR HABITACIÓ =====");
         //Demanar codi, tornar habitació i eliminar reserva
        int codi = llegirEnter("Introdueix el codi de reserva: ");
  
    if (reserves.containsKey(codi)) {
        // Obtenim les dades per saber el tipus d'habitació
        ArrayList<String> dadesReserva = reserves.get(codi);
        String tipus = dadesReserva.get(0);  // posició 0 = tipus

        reserves.remove(codi);

        int disponiblesActuals = disponibilitatHabitacions.get(tipus);
        disponibilitatHabitacions.put(tipus, disponiblesActuals + 1);
           
        System.out.println("Reserva trobada!");
        System.out.println("Habitació alliberada correctament.");
        System.out.println("Disponibilitat actualitzada.");
    } else {
        System.out.println("No s'ha trobat cap reserva amb aquest codi.");
    }
    }

    /**
     * Mostra la disponibilitat actual de les habitacions (lliures i ocupades).
     */
    public static void consultarDisponibilitat() {
        // Mostrar lliures i ocupades
        System.out.println("\n===== DISPONIBILITAT D'HABITACIONS =====");
    System.out.println("Tipus\t\tLliures\tOcupades");
    System.out.println("--------------------------------");
    
    mostrarDisponibilitatTipus(TIPUS_ESTANDARD);
    mostrarDisponibilitatTipus(TIPUS_SUITE);
    mostrarDisponibilitatTipus(TIPUS_DELUXE);
    }

    /**
     * Funció recursiva. Mostra les dades de totes les reserves
     * associades a un tipus d'habitació.
     */
    public static void llistarReservesPerTipus(int[] codis, String tipus) {
         // Implementar recursivitat

        if (codis.length == 0) {
        System.out.println("(No hi ha més reserves d'aquest tipus.)");
        return;
    }

    int codiActual = codis[0];
    ArrayList<String> dades = reserves.get(codiActual);
    String tipusReserva = dades.get(0); 
        
    if (tipusReserva.equals(tipus)) {
        mostrarDadesReserva(codiActual);
    }
    
    int[] nousCodis = new int[codis.length - 1];
    
    System.arraycopy(codis, 1, nousCodis, 0, nousCodis.length);

    llistarReservesPerTipus(nousCodis, tipus);
        
    }

    /**
     * Permet consultar els detalls d'una reserva introduint el codi.
     */
    public static void obtindreReserva() {
        System.out.println("\n===== CONSULTAR RESERVA =====");
        // Mostrar dades d'una reserva concreta
        int codi = llegirEnter("Introdueix el codi de reserva: ");
            mostrarDadesReserva(codi);
 
    }

    /**
     * Mostra totes les reserves existents per a un tipus d'habitació
     * específic.
     */
    public static void obtindreReservaPerTipus() {
        System.out.println("\n===== CONSULTAR RESERVES PER TIPUS =====");
        // Llistar reserves per tipus
        System.out.println("Seleccione tipus:");
    System.out.println("1. Estàndard");
    System.out.println("2. Suite");
    System.out.println("3. Deluxe");
    
    String tipus = seleccionarTipusHabitacio();
    
    if (tipus == null) {
        return;
    }
        int numReserves = reserves.size();
    
    if (numReserves == 0) {
        System.out.println("No hi ha cap reserva registrada.");
        return;
    }
    
    int[] codis = new int[numReserves];

    int index = 0;
    for (Integer codi : reserves.keySet()) {
        codis[index] = codi;
        index++;
    }
  
    System.out.println("\nReserves del tipus \"" + tipus + "\":");
    llistarReservesPerTipus(codis, tipus);
        
    }

    /**
     * Consulta i mostra en detall la informació d'una reserva.
     */
    public static void mostrarDadesReserva(int codi) {
       // Imprimir tota la informació d'una reserva

        if (!reserves.containsKey(codi)) {
        System.out.println("No s'ha trobat cap reserva amb el codi: " + codi);
        return;
    }
    
    // Obtenim l'ArrayList amb les dades
    ArrayList<String> dades = reserves.get(codi);
    
    System.out.println("\n--- Reserva " + codi + " ---");
    System.out.println("Tipus d'habitació: " + dades.get(0));  // posició 0
    System.out.println("Cost total: " + dades.get(1) + "€");   // posició 1
    
    // Serveis (posicions 2, 3, 4, 5 si existeixen)
    if (dades.size() > 2) {
        System.out.println("Serveis addicionals:");
        for (int i = 2; i < dades.size(); i++) {
            System.out.println("  - " + dades.get(i));
        }
    } else {
        System.out.println("Serveis addicionals: Cap");
    }
        
    }

    // --------- MÈTODES AUXILIARS (PER MILLORAR LEGIBILITAT) ---------

    /**
     * Llig un enter per teclat mostrant un missatge i gestiona possibles
     * errors d'entrada.
     */
    static int llegirEnter(String missatge) {
        int valor = 0;
        boolean correcte = false;
        while (!correcte) {
                System.out.print(missatge);
                valor = sc.nextInt();
                correcte = true;
        }
        return valor;
    }

    /**
     * Mostra per pantalla informació d'un tipus d'habitació: preu i
     * habitacions disponibles.
     */
    static void mostrarInfoTipus(String tipus) {
        int disponibles = disponibilitatHabitacions.get(tipus);
        int capacitat = capacitatInicial.get(tipus);
        float preu = preusHabitacions.get(tipus);
        System.out.println("- " + tipus + " (" + disponibles + " disponibles de " + capacitat + ") - " + preu + "€");
    }

    /**
     * Mostra la disponibilitat (lliures i ocupades) d'un tipus d'habitació.
     */
    static void mostrarDisponibilitatTipus(String tipus) {
        int lliures = disponibilitatHabitacions.get(tipus);
        int capacitat = capacitatInicial.get(tipus);
        int ocupades = capacitat - lliures;

        String etiqueta = tipus;
        if (etiqueta.length() < 8) {
            etiqueta = etiqueta + "\t"; // per a quadrar la taula
        }

        System.out.println(etiqueta + "\t" + lliures + "\t" + ocupades);
    }
}
