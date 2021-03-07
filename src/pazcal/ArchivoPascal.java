
package pazcal;

public class ArchivoPascal {
    public String nombreDelArchivo;
    public String contenidoDelArchivo;

    public ArchivoPascal(String nombreDelArchivo, String contenidoDelArchivo) {
        this.nombreDelArchivo = nombreDelArchivo;
        this.contenidoDelArchivo = contenidoDelArchivo;
    }

    public void setNombreDelArchivo(String nombreDelArchivo) {
        this.nombreDelArchivo = nombreDelArchivo;
    }

    public void setContenidoDelArchivo(String contenidoDelArchivo) {
        this.contenidoDelArchivo = contenidoDelArchivo;
    }

    public String getNombreDelArchivo() {
        return nombreDelArchivo;
    }

    public String getContenidoDelArchivo() {
        return contenidoDelArchivo;
    }
}
