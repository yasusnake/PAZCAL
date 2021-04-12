package pazcal;

import java.io.FileNotFoundException;
import java.io.IOException;

public class PAZCAL {
    public static void main(String[] args) throws FileNotFoundException, IOException {        
        String ruta = "malo.pazcal";
//        String ruta = args[0];
        
//        Analizador analizador = new Analizador();        
        
        if(ruta.length() > 0)
            Analisis.AnalizandoArchivo(ruta);
        else 
            System.err.println("No hay archivo, coloque uno");
    }  
}
