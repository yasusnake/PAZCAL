package pazcal;

import java.util.StringTokenizer;
import java.io.FileWriter;
import java.io.IOException;

public class AnalizadorSintactico {
    private static String nombreDelArchivo;
    private static String analizar;
    static FileWriter fileErr;
    
    // Declaración de obj
    static PilaChar pilaChar  = new PilaChar();
    static PilaString pilaStr = new PilaString();
    
    public static boolean ValidarExpresion() {
        PilaChar pila = new PilaChar();
        String cadena = analizar;
        
        char actual;
        char anterior;
        char siguiente;
        
//        System.out.println(cadena);
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
    
    public void ValidarExpresionXLinea(String cadena, int ln) throws IOException {        
        char actual;
        char anterior;
        char siguiente;
        String error;
        
//        System.out.println(cadena);
        int tam = cadena.length();
        
        for (int i = 0; i < cadena.length(); i++) {
            actual = cadena.charAt(i);
            anterior = i > 0 ? cadena.charAt(i - 1) : Character.MIN_VALUE;
            siguiente = (i+1) < tam ? cadena.charAt(i + 1) : Character.MAX_VALUE;
            
//            System.out.println(i + ": AN: " + anterior + " AC: " + actual + " SG: " + siguiente);
//            System.out.println(i + ": EXT: " + ant_1 + " :: " + ant_2 );

            if (actual == '(' || actual == '[' || actual == '{' || (actual == '(' && siguiente == '*')) {      
                if(actual == '(' && siguiente == '*') {
                    pilaChar.Insertar(actual);
                    pilaChar.Insertar(siguiente);
                } else {
                    pilaChar.Insertar(actual);
                }
            } 
            else if (anterior == '*' && actual == ')') {
                char ant_1 = pilaChar.extraer();
                char ant_2 = pilaChar.extraer();
                if (ant_1 != '*' && ant_2 != '(') {
                    error = "Error 001: Linea " + ln + " existe un error de apertura o cierre de () o faltante de *";
                    fileErr.write("\t\t\t" + error + "\n");
                    System.err.println(error);
                    break;
                }
            } 
            else if (actual == ']') {
                if (pilaChar.extraer() != '[') {
                    error = "Error 002: Linea " + ln + " existe un error de apertura o cierre de []";
                    fileErr.write("\t\t\t" + error + "\n");
                    System.err.println(error);
                    break;
                }
            } 
            else if (actual == '}') {
                if (pilaChar.extraer() != '{') {
                    error = "Error 000: Linea " + ln + " existe un error de apertura o cierre de {}";
                    fileErr.write("\t\t\t" + error + "\n");
                    System.err.println(error);
                    break;
                }
            } 
            else if (actual == ')') {
                char ant = pilaChar.extraer();
                if (ant != '(') {
                    if(ant == '*') {
                        error = "Error 003: Linea " + ln + " existe un error de faltante de un *";
                        fileErr.write("\t\t\t" + error + "\n");
                        System.err.println(error);
                    } else {
                        error = "Error 004: Linea " + ln + " existe un error de apertura o cierre de ()";
                        fileErr.write("\t\t\t" + error + "\n");
                        System.err.println(error);
                    }
                    break;
                }
            }
        }
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
                    pila.Insertar(token);
                    if (!"BEGIN".equals(pila.extraer())) {
                        System.err.println("ERROR - BEGIN/END");
                        return false;
                    }  
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
    
    public void ValidarExpresionStringXLinea(String linea, int ln) throws IOException {
        StringTokenizer cadena = new StringTokenizer(linea);
        String error;
        String token;
        String anterior;
        
//        System.out.println(analizar);
        while (cadena.hasMoreTokens()) {
            token = cadena.nextToken().toUpperCase();
            
//            System.out.println(token);
            if (null != token) {
                if (token.equals("BEGIN") || token.equals("REPEAT")) {
                    if (token.equals("REPEAT")) {
                        anterior = pilaStr.extraer();
                        pilaStr.Insertar(anterior);
                        if ("REPEAT".equals(anterior)) {
                            error = "Error 011: Linea " + ln + " no debe existir un ciclo REPEAT/UNTIL dentro de un ciclo REPEAT/UNTIL";
                            fileErr.write("\t\t\t" + error + "\n");
                            System.err.println(error);
                        }
                    }
                    
                    pilaStr.Insertar(token);
                }
                else if (token.equals("END")) {
                    if (!"BEGIN".equals(pilaStr.extraer())) {
                        error = "Error 005: Linea " + ln + " existe un error de apertura o cierre de BEGIN/END";
                        fileErr.write("\t\t\t" + error + "\n");
                        System.err.println(error);
                    }  
                    pilaStr.Insertar(token); 
                    
                    String nextToken;
                    
                    try {
                        nextToken = cadena.nextToken();
                    } catch (Exception e) {
                        nextToken = "";
                    }
                    
                    if (!nextToken.equals(".")) {
                        error = "Error 007: Linea " + ln + " falta del . despues del END";
                        fileErr.write("\t\t\t" + error + "\n");
                        System.err.println(error);
                    }
                }
                else if (token.equals(".")) {
                    if (!"END".equals(pilaStr.extraer())) {
                        error = "Error 006: Linea " + ln + " faltante del END";
                        fileErr.write("\t\t\t" + error + "\n");
                        System.err.println(error);
                    }
                }
                else if (token.equals("UNTIL")) {
                    if (!"REPEAT".equals(pilaStr.extraer())) {
                        error = "Error 010: Linea " + ln + " faltante del REPEAT";
                        fileErr.write("\t\t\t" + error + "\n");
                        System.err.println(error);
                    }
                    
                    String getLastChar = linea.substring(linea.length() - 1);
                    
                    if (!getLastChar.equals(";")) {
                        error = "Error 555: Linea " + ln + " falta punto y comma ;";
                        fileErr.write("\t\t\t" + error + "\n");
                        System.err.println(error);
                    }
                }
            }
        }
    }
    
    public void vaciasPilas() {
        pilaChar.PilaVacia();
        pilaStr.PilaVacia();
    }
    
    public void setFileErr(String nameFile, FileWriter err) {
        nombreDelArchivo = nameFile;
        fileErr = err;
    }
    
    public void analizadorSintactico(String cadena) throws IOException {
        analizar = cadena;
        Boolean error_1 = !ValidarExpresion();
        Boolean error_2 = !ValidarExpresionString();
        
        if (error_1 || error_2) {
            System.err.println("¡ERROR! en la compilación.");
        }
    }
}
