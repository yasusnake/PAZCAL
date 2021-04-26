package pazcal;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import static java.lang.Thread.sleep;

public class Analisis {
    private static String nombreDelArchivo;
    private static String extensionDelArchivo;
    private static String contenidoDelArchivo;
    static FileWriter fileErr;
    
    private static void analizarContenido(String archivo) throws FileNotFoundException, IOException {
        AnalizadorSintactico analizadorStc = new AnalizadorSintactico();
        AnalizadorSintatico analizadorStc2 = new AnalizadorSintatico();
        String cadena;
        FileWriter pas;
        String ceros      = "";
        String caracteres = ""; 
        Boolean haveErr   = false;
        
        try {
            FileReader file = new FileReader(archivo);
            pas  = new FileWriter( nombreDelArchivo + ".pas");
            fileErr  = new FileWriter( nombreDelArchivo + "-errores.txt");
            
            analizadorStc.setFileErr(nombreDelArchivo, fileErr);
            
            try (BufferedReader b = new BufferedReader(file)) {
                int n  = 0;
                int x  = 1;
                int ln = 1;
                while((cadena = b.readLine())!=null) {
                    pas.write(cadena + "\n");
                    
                    switch (Integer.toString(x).length()) {
                        case 1:
                            ceros = "000";
                            break;
                        case 2:
                            ceros = "00";
                            break;
                        case 3:
                            ceros = "0";
                            break;
                        default:
                            break;
                    }
                    
                    fileErr.write(ceros + x + "  " + cadena + "\n");
                    
                    //Limpieza espacios en blanco
                    if (n > 0) {
                        caracteres = caracteres + "\n" + cadena.replaceAll(" +", " ");
                    } else {
                        caracteres = caracteres + cadena.replaceAll(" +", " ");
                    }
                    
                    //Validación de error si la línea tiene más de 150 caracteres
                    if(cadena.length() > 150) {
                        haveErr = true;
                        String error = "Error 205: La línea " + ln + " contiene más de 150 caracteres. Contiene " + cadena.length() + " caracteres.";
                        fileErr.write("\t\t\t" + error + "\n");      
                        System.err.println(error);
                    }
                    
                    //Analizar linea
                    analizadorStc2.analizador(cadena.replaceAll(" +", " "), nombreDelArchivo, ln, fileErr);
                    analizadorStc.ValidarExpresionXLinea(cadena, ln);
                    analizadorStc.ValidarExpresionStringXLinea(cadena, ln);
                    
                    n++;
                    x++;
                    ln++;
                }
                
                //AnalizarTodo el script
//                analizadorStc.analizadorSintactico(caracteres);
                analizadorStc.vaciasPilas();
                
                pas.close();
                fileErr.close();
            } 

            contenidoDelArchivo = caracteres;
            
            System.out.println("Fin escritura en: " + nombreDelArchivo + ".pas");
            
            if(haveErr) {
                System.err.println("El script " 
                    + nombreDelArchivo 
                    + ".pazcal tiene algunos errores, para más detalles ver el archivo de errores: " 
                    + nombreDelArchivo 
                    + "-errores.txt"
                );
            } else {
                //Creando archivo ejecutable de pascal
                Boolean creadoExe = false;
                
                try {
                    Process process = Runtime.getRuntime().exec("fpc " + nombreDelArchivo + ".pas");
                    InputStream inputstream = process.getInputStream();
                    try (BufferedInputStream bufferedinputstream = new BufferedInputStream(inputstream)) {
                        System.out.println("Creando archivo " + nombreDelArchivo + ".exe");
                        sleep(2000);
                        creadoExe = true;
                    }
                } catch (IOException e) {
                    System.err.println("Error al ejecutar comando fpc " + e);
                }
                
                String SO = System.getProperty("os.name").toLowerCase();
                
                //Verificar que el SO es Windows
                if(creadoExe && SO.contains("win")) {
                    try {
                        // Se lanza el ejecutable.
//                        Process p=Runtime.getRuntime().exec ("cmd /c dir");
                        Process p=Runtime.getRuntime().exec(nombreDelArchivo + ".exe");
                        InputStream is = p.getInputStream(); // Se obtiene el stream de salida del programa
                        // Se prepara un bufferedReader para poder leer la salida más comodamente.
                        BufferedReader br = new BufferedReader (new InputStreamReader (is));
                        String aux = br.readLine(); // Se lee la primera linea

                        // Se empieza a leer cada linea
                        while (aux!=null)
                        {
                            System.out.println (aux);
                            aux = br.readLine();
                        }
                    } catch (IOException e) {
                        System.err.println("Error al correr el programa " + e);
                    }
                } else {
                    if(!creadoExe)
                        System.err.println("No se pudo crear el archivo .exe");
                    
                    if(!SO.contains("win"))
                        System.err.println("No se puede ejecutar el programa "
                            + nombreDelArchivo
                            + ".exe en este Sistema Operativo"
                        );
                }// Fin if/else ejecutar .exe
            }// Fin if/else si no tiene errores
        } catch (IOException | InterruptedException e) {
            System.err.println("Ocurrio un error durante el analisis: " + e.getMessage());
        }
    }
    
    private static void getFileExtension(File file) {        
        System.out.println("Analizando la extensión del archivo");
 
        try {
            if (file != null && file.exists()) {
                String name = file.getName();
                nombreDelArchivo = name.replaceFirst("[.][^.]+$", "");
                String extension = name.substring(name.lastIndexOf("."));
                extensionDelArchivo = extension.toLowerCase();
                
                if(extension.equals(".pazcal"))
                    creandoArchivos();
            }
        } catch (Exception e) {
            extensionDelArchivo = "";
            System.err.println(e);
        }
    }
    
    private static void creandoArchivos() {
        System.out.println("Creando archivo de errores");
        try {
            // Archivo de errores
            String errores = nombreDelArchivo + "-errores.txt";
            File fileError = new File(errores);
            // Si el archivo de errores no existe es creado
            if (!fileError.exists()) {
                fileError.createNewFile();
            }
            FileWriter fw = new FileWriter(fileError);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.close();
            
            // Archivo de .pas
            String pascal = nombreDelArchivo + ".pas";
            File filePas = new File(pascal);
            // Si el archivo de errores no existe es creado
            if (!filePas.exists()) {
                filePas.createNewFile();
            }
            FileWriter fwp = new FileWriter(filePas);
            BufferedWriter bwp = new BufferedWriter(fwp);
            bwp.close();
        } catch (IOException e) {
            System.err.println(e);
        }
    }
    
    public static void AnalizandoArchivo(String ruta) throws IOException {        
        getFileExtension(new File(ruta));
        
        if(extensionDelArchivo.equals(".pazcal")) {
            analizarContenido(ruta);
        } else {
            System.err.println("Extensión de archivo incorrecto" );
        }
    }
}
