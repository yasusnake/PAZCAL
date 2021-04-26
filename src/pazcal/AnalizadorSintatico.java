package pazcal;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.HashMap;
import java.util.List;

public class AnalizadorSintatico {
    private static int numLinea;
    private static String nombreDelArchivo;
    private static Boolean exVar = true;
    static FileWriter fileErr;
    private static String varVariable = "";
    private static String[] variables = new String[99];
    private static int arrNum = 0;
    
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
    
    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
    
    private static void analizarWritelnLine(String linea) throws IOException {   
        linea = linea.replaceAll("'", " DGFS ");
        linea = linea.replaceAll("\\(|\\)|OUTPUT|WRITELN|DGFS+[a-zA-Z_0-9_ \\t\\n\\x0b\\r\\f_%_\\D]+DGFS|;|,|:", "");
        List<String> list = Arrays.asList(variables);
        
        if(!linea.isEmpty()) {
            StringTokenizer st = new StringTokenizer(linea);
            while (st.hasMoreTokens()) {
                String token = st.nextToken().replaceAll(" ", "");

                if(!list.contains(token) && !token.isEmpty() && !isNumeric(token)){
                    String error = "Error 016: Línea " + numLinea + " Error de variables, la variable " + token + " no está declarada";
                    fileErr.write("\t\t\t" + error + "\n");
                    System.err.println(error);
                }
            }
        }
    }
    
    private static void analizarReadlnLine(String linea) throws IOException {        
        linea = linea.replaceAll("\\(|\\)|INPUT|READLN|;|,| ", "");
        List<String> list = Arrays.asList(variables);
        
        if(!linea.isEmpty()) {
            StringTokenizer st = new StringTokenizer(linea);
            while (st.hasMoreTokens()) {
                String token = st.nextToken();

                if(!list.contains(token)){
                    String error = "Error 016: Línea " + numLinea + " Error de variables, la variable " + token + " no está declarada";
                    fileErr.write("\t\t\t" + error + "\n");
                    System.err.println(error);
                }
            }
        }
    }
    
    private static void analizarVariablesLine(String linea) throws IOException {        
        StringTokenizer st = new StringTokenizer(linea);
        Boolean sintaxis = linea.matches(".*\\w.*:.*(REAL|CHAR|INTEGER).*;.*");
        
        if(!sintaxis) {
            String error = "Error 021: Línea " + numLinea + " Error de sintaxis";
            fileErr.write("\t\t\t" + error + "\n");
            System.err.println(error);
        }
        
        int n = 0;
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            token = token.replaceAll(":", "");
            
            if(n == 0 && sintaxis) {
                variables[arrNum] = token;
                arrNum++;
            }
            
            n++;
        }
    }
    
    private static void analizarUntilLine(String linea) throws IOException {        
        Boolean untlLeft      = linea.matches(".*\\w.*UNTIL.*");
        Boolean ordenCorrecto = linea.matches(".*UNTIL.*\\w.*(>|<|==|=).*\\w.*;.*");
        
        if(untlLeft) {
            String error = "Error 012: Línea " + numLinea + " Error de sintaxis, existe comandos antes de UNTIL";
            fileErr.write("\t\t\t" + error + "\n");
            System.err.println(error);
        }
        
        if(!ordenCorrecto) {
            String error = "Error 013: Línea " + numLinea + " Error de sintaxis, falta de algún elemento";
            fileErr.write("\t\t\t" + error + "\n");
            System.err.println(error);
        }
        
        String cleanLine = linea.replaceAll("\\(|\\)|>|<|==|=|UNTIL|;", "");
        StringTokenizer st = new StringTokenizer(cleanLine);
        List<String> list = Arrays.asList(variables);
        
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            
            if(!list.contains(token)){
                String error = "Error 016: Línea " + numLinea + " Error de variables, la variable " + token + " no está declarada";
                fileErr.write("\t\t\t" + error + "\n");
                System.err.println(error);
            }
        }
    }
    
    private static void analizarRepeatLine(String linea) throws IOException {        
        Boolean rptLeft   = linea.matches(".*\\w.*REPEAT.*");
        Boolean rptRight  = linea.matches(".*REPEAT.*\\w.*");
        Boolean semicolon = linea.matches(".*;.*");
        
        if(rptLeft || rptRight) {
            String error = "Error 014: Línea " + numLinea + " Error de sintaxis, REPEAT solo deber estar solo, sin comandos o variables en la izquierda o derecha";
            fileErr.write("\t\t\t" + error + "\n");
            System.err.println(error);
        }
        
        if(semicolon) {
            String error = "Error 015: Línea " + numLinea + " Error de sintaxis, REPEAT contiene un semicolon \";\"";
            fileErr.write("\t\t\t" + error + "\n");
            System.err.println(error);
        }
    }
    
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
    } // FIN analizarProgramLine
    
    public void analizador(String linea, String nameFile, int ln, FileWriter err) throws IOException {
        linea = linea.replaceAll("\\(", " ( ");
        linea = linea.replaceAll("\\)", " ) ");
        
        StringTokenizer st = new StringTokenizer(linea);
        numLinea = ln;
        nombreDelArchivo = nameFile;
        fileErr = err;
        
        while (st.hasMoreTokens()) {
            String token = st.nextToken().toUpperCase().replaceAll(";", "");
            
            if(KEYWORDS_TOKEN.containsKey(token)) {
                Boolean comentario_1 = linea.matches(".*\\{.*\\w.*\\}.*");
                Boolean comentario_2 = linea.matches(".*\\(\\*.*\\w.*\\*\\).*");
            
                if(token.contains("PROGRAM")) {
                    analizarProgramLine(linea);
                    break;
                }
                
                if(token.contains("REPEAT")) {
                    analizarRepeatLine(linea);
                    break;
                }
                
                if(token.contains("UNTIL")) {
                    analizarUntilLine(linea);
                    break;
                }
                
                if(token.contains("VAR") && (!comentario_1 || !comentario_2)) {
                    varVariable = token;
                    break;
                }
                
                if(token.contains("INTEGER") || token.contains("REAL") || token.contains("CHAR")) {                    
                    if(!varVariable.equals("VAR") && exVar) {
                        String error = "Error 020: Línea " + numLinea + " falta la declaración de VAR antes de las variables;";
                        fileErr.write("\t\t\t" + error + "\n");
                        System.err.println(error);
                        exVar = false;
                    }
                    analizarVariablesLine(linea);
                    break;
                }
                
                if(token.contains("READLN")) {
                    analizarReadlnLine(linea);
                    break;
                }
                
                if(token.contains("WRITELN")) {
                    analizarWritelnLine(linea);
                    break;
                }
            }
        }
    }
    
}
