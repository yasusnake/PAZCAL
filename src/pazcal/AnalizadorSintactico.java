package pazcal;

import java.util.StringTokenizer;
import java.io.FileWriter;
import java.io.IOException;

public class AnalizadorSintactico {
    private static String nombreDelArchivo;
    private static String analizar;
    static FileWriter fileErr;
    
    public static boolean ValidarExpresion() {
        PilaChar pila = new PilaChar();
        String cadena = analizar;
        
        char actual;
        char anterior;
        char siguiente;
        
//        System.out.println(cadena.length());
        int tam = cadena.length();
        
        for (int i = 0; i < cadena.length(); i++) {
            actual = cadena.charAt(i);
            anterior = i > 0 ? cadena.charAt(i - 1) : Character.MIN_VALUE;
            siguiente = (i+1) < tam ? cadena.charAt(i + 1) : Character.MAX_VALUE;
            
//            System.out.println(i + ": AN: " + anterior + " AC: " + actual + " SG: " + siguiente);
//            System.out.println(i + ": EXT: " + ant_1 + " :: " + ant_2 );

            if (actual == '(' || actual == '[' || actual == '{' || (actual == '(' && siguiente == '*')) {      
                if(actual == '(' && siguiente == '*') {
                    pila.Insertar(actual);
                    pila.Insertar(siguiente);
                } else {
                    pila.Insertar(actual);
                }
            } 
            else if (anterior == '*' && actual == ')') {
                char ant_1 = pila.extraer();
                char ant_2 = pila.extraer();
                if (ant_1 != '*' && ant_2 != '(') {
                    System.err.println("ERROR: (*..*)");
                    return false;
                }
            } 
            else if (actual == ']') {
                if (pila.extraer() != '[') {
                    System.err.println("ERROR: []");
                    return false;
                }
            } else if (actual == '}') {
                if (pila.extraer() != '{') {
                    System.err.println("ERROR: {}");
                    return false;
                }
            } else if (actual == ')') {
                char ant = pila.extraer();
                if (ant != '(') {
                    if(ant == '*') {
                        System.err.println("ERROR_: (**)");
                    } else {
                        System.err.println("ERROR: ()");
                    }
                    return false;
                }
            }
        }
        return pila.PilaVacia();
    }
    
    public static boolean ValidarExpresionString() {
        PilaString pila = new PilaString();
        StringTokenizer cadena = new StringTokenizer(analizar);
        
//        System.out.println(analizar);
        while (cadena.hasMoreTokens()) {
            String token = cadena.nextToken().toUpperCase();
            
//            System.out.println(token);
            if (null != token) switch (token) {
                case "BEGIN":
                    pila.Insertar(token);
                    break;
                case "END":
                    if (!"BEGIN".equals(pila.extraer())) {
                        System.err.println("ERROR - BEGIN/END");
                        return false;
                    }  
                    pila.Insertar(token); 
                    break;
                case ".":
                    if (!"END".equals(pila.extraer())) {
                        System.err.println("ERROR - END .");
                        return false;
                    }
                    break;
                default:
                    break;
            }
        }
        return pila.PilaVacia();
    }
    
    public static void analizadorSintactico(String cadena, String nameFile, FileWriter err) throws IOException {
        nombreDelArchivo = nameFile;
        fileErr = err;
        analizar = cadena;
        
        if (ValidarExpresion()) {
            System.out.println("La formula esta escrita correctamente.");
        } else {
            System.out.println("¡ERROR!, la formula NO esta escrita correctamente.");
        }
        
        if (ValidarExpresionString()) {
            System.out.println("La formula esta escrita correctamente.");
        } else {
            System.out.println("¡ERROR!, la formula NO esta escrita correctamente.");
        }
    }
}
