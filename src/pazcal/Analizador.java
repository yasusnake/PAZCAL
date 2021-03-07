package pazcal;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.StringTokenizer;

public class Analizador{
    private static String nombreDelArchivo;
    private static String extensionDelArchivo;
    private static String contenidoDelArchivo;
    
    private static void analizarContenido(String archivo) throws FileNotFoundException, IOException {
        String cadena;
        String caracteres = "";
        int n = 0;
        FileReader file = null;
        FileWriter pas = null;
        
        try {
            file = new FileReader(archivo);
            pas  = new FileWriter( nombreDelArchivo + ".pas");
            BufferedReader b = new BufferedReader(file);
        
            while((cadena = b.readLine())!=null) {
                pas.write(cadena + "\n");
                
                if(n > 0)
                    caracteres = caracteres + "\n" + cadena.replaceAll(" +", " ");
                else
                    caracteres = caracteres + cadena.replaceAll(" +", " ");
                n++;
            }       

            contenidoDelArchivo = caracteres;
            
            System.out.println("Fin escritura en: " + nombreDelArchivo + ".pas");

            pas.close();
            b.close();
        } catch (Exception e) {
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
    
    private static void analizador(String pazcal) {
        StringTokenizer st = new StringTokenizer(pazcal);
        
        while (st.hasMoreTokens()) {
            System.out.println(st.nextToken());
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
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e);
        }
    }
    
    public static void AnalizandoArchivo(String ruta) throws IOException {
        getFileExtension(new File(ruta));
        
        if(extensionDelArchivo.equals(".pazcal")) {
            analizarContenido(ruta);
            
//            analizador();
            
//            System.out.println(contenidoDelArchivo);
        } else {
            System.out.println("Extensión de archivo incorrecto" );
        }
    }
}