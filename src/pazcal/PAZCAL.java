package pazcal;

import java.io.FileNotFoundException;
import java.io.IOException;

public class PAZCAL {
    public static void main(String[] args) throws FileNotFoundException, IOException {        
        String ruta = "array.pazcal";
//        String ruta = args[0];
        
        Analizador amalizador = new Analizador();
        amalizador.AnalizandoArchivo(ruta);
    }  
}
