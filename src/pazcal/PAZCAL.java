package pazcal;

import java.util.StringTokenizer;
//import java.util.Scanner;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class PAZCAL {
    private static String muestraContenido(String archivo) throws FileNotFoundException, IOException {
        String cadena;
        String caracteres = "";
        int n = 0;
        FileReader f = new FileReader(archivo);
        BufferedReader b = new BufferedReader(f);
        while((cadena = b.readLine())!=null) {
            if(n > 0)
                caracteres = caracteres + "\n" + cadena.replaceAll(" +", " ");
            else
                caracteres = caracteres + cadena.replaceAll(" +", " ");
            n++;
        }
        b.close();
        
        return caracteres;
    }
    
    private static String getFileExtension(File file) {
        String extension = "";
        
        System.out.println("Analizando la extensión del archivo");
 
        try {
            if (file != null && file.exists()) {
                String name = file.getName();
                String fileName = name.replaceFirst("[.][^.]+$", "");
                extension = name.substring(name.lastIndexOf("."));
                extension = extension.toLowerCase();
                
                if(extension.equals(".pazcal"))
                    creandoArchivodeErrores(fileName);
            }
        } catch (Exception e) {
            extension = "";
            System.err.println(e);
        }
 
        return extension;
    }
    
    public static void analizador(String pazcal) {
        StringTokenizer st = new StringTokenizer(pazcal);
        
        while (st.hasMoreTokens()) {
            System.out.println(st.nextToken());
        }
        
//        return temp;
    }
    
    public static void creandoArchivodeErrores(String nombre) {
        System.out.println("Creando archivo de errores");
        try {
            String ruta = nombre + "-errores.txt";
            File file = new File(ruta);
            // Si el archivo no existe es creado
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fw = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e);
        }
    }

    public static void main(String[] args) throws FileNotFoundException, IOException {
        System.out.println("Lectura del archivo");
        String ruta = "INFLACION.pazcal";
//        String ruta = args[0];
        
//        ArrayList<Token> tokenArrayList = TokenScanner.scan(new File(ruta));
//        ArrayList<Token> tokenArrayList = TokenScanner.scan(new File(ruta));
        
        String extension = getFileExtension(new File(ruta));
        
        if(extension.equals(".pazcal")) {
            String pazcal = muestraContenido(ruta);
            
//            analizador(pazcal);
            
            System.out.println(pazcal);
        } else {
            System.out.println("Extensión de archivo incorrecto" );
        }
    }
    
}
