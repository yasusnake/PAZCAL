package pazcal;

import java.io.FileWriter;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.HashMap;

public class AnalizadorSintatico {
    private static int numLinea;
    private static String nombreDelArchivo;
    static FileWriter fileErr;
    
    private static final HashMap<String, String> KEYWORDS_TOKEN;
    static {
        Tokens tks = new Tokens(); 
        KEYWORDS_TOKEN = new HashMap<>();
        String word;

        String[] sc = tks.tokens;

        for (String sc1 : sc) {
            word = sc1;
            KEYWORDS_TOKEN.put(word, String.format("TK_%s", word.toUpperCase()));
        }
    }
    
//    private static void  analizarBeginLine(String linea) throws IOException {
//    }
    
    private static void analizarProgramLine(String linea) throws IOException {
        StringTokenizer st = new StringTokenizer(linea);
        Pattern pa = Pattern.compile("(PROGRAM)|([a-zA-Z]+)|([(]+)|(INPUT)|(,)|(OUTPUT)|([)]+)|(;)");
        Matcher mat = pa.matcher(linea);
        
        int n = 0;
        while (mat.find()) {
            String token;
            try {
                token = st.nextToken();
            } catch (Exception e) {
                token = "";
            }
            
            //revisa la variable
            if(mat.group(2) != null && n == 1) {
                if(mat.group().length() > 15) {
                    String error = "Error 206: Línea " + numLinea + " Variable en Program tiene más de 15 caracteres";
                    fileErr.write("\t\t\t" + error + "\n");
                    System.err.println(error);
                }
                
                if(token.matches(".*\\d.*")) {
                    String error = "Error 204: Línea " + numLinea + " Variable en solo tiene que tener letras";
                    fileErr.write("\t\t\t" + error + "\n");
                    System.err.println(error);
                }
            }
            
            n++;
        }   
        
        //Otras validaciones
        Boolean parnLeft  = linea.matches(".*\\(.*");
        Boolean parnRight = linea.matches(".*\\).*");
        Boolean comma     = linea.matches(".*,.*");
        Boolean bInput    = linea.matches("(.*)INPUT(.*)");
        Boolean bOuput    = linea.matches("(.*)OUTPUT(.*)");
        
        if(parnLeft && parnRight) {
            if(bInput && comma && bOuput) {
                Boolean order = linea.matches(".*INPUT.*OUTPUT.*");
                if(order == false) {
                    String error = "Error 212: Línea " + numLinea + " El orden de INPUT y OUTPUT no es la correcta";
                    fileErr.write("\t\t\t" + error + "\n");
                    System.err.println(error);
                }
            } else if(bInput && comma == false && bOuput) {
                String error = "Error 207: Línea " + numLinea + " Le falta comma ente INPUT y OUTPUT";
                fileErr.write("\t\t\t" + error + "\n");
                System.err.println(error);
            } else if(bInput && comma && bOuput == false) {
                String error = "Error 211: Línea " + numLinea + " Tiene comma de sobra";
                fileErr.write("\t\t\t" + error + "\n");
                System.err.println(error);
            } else if(bInput == false && comma && bOuput) {
                String error = "Error 211: Línea " + numLinea + " Tiene comma de sobra";
                fileErr.write("\t\t\t" + error + "\n");
                System.err.println(error);
            }
        } else if(parnLeft && parnRight == false) {
            String error = "Error 208: Línea " + numLinea + " Falta de cierre de parentesis";
            fileErr.write("\t\t\t" + error + "\n");
            System.err.println(error);
        } else if(parnLeft == false && parnRight) {
            String error = "Error 209: Línea " + numLinea + " Falta de apertura de parentesis";
            fileErr.write("\t\t\t" + error + "\n");
            System.err.println(error);
        } else {
            String error = "Error 210: Línea " + numLinea + " Exiten los parentesis pero no los tokens";
            fileErr.write("\t\t\t" + error + "\n");
            System.err.println(error);
        }
        
        //Revisa si tiene semicolon
        Boolean exSM = linea.matches(".*;.*");
        if(exSM == false) {
            String error = "Error 200: Línea " + numLinea + " Falta el semicolon ;";
            fileErr.write("\t\t\t" + error + "\n");
            System.err.println(error);
        }
    }
    
    public static void analizador(String linea, String nameFile, int ln, FileWriter err) throws IOException {
        StringTokenizer st = new StringTokenizer(linea);
        numLinea = ln;
        nombreDelArchivo = nameFile;
        fileErr = err;
        
        while (st.hasMoreTokens()) {
            String token = st.nextToken().toUpperCase();
            
            if(KEYWORDS_TOKEN.containsKey(token)) {
                if(token.contains("PROGRAM")) {
                    analizarProgramLine(linea);
                    break;
                }
                if(token.contains("BEGIN")) {
                    analizarProgramLine(linea);
                    break;
                }
                if(token.contains("END")) {
                    analizarProgramLine(linea);
                    break;
                }
            }
        }
    }
    
}
