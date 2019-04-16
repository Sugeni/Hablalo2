package isware.uneg.es.chateando.Logica;

public class Usuarios {

    public String nombre;
    public String status;
    public String status2;
    public String imagenA;
    public String imagenS;

    public String getStatus2() {
        return status2;
    }

    public void setStatus2(String status2) {
        this.status2 = status2;
    }

    public String getIgnorados() {
        return ignorados;
    }

    public void setIgnorados(String ignorados) {
        this.ignorados = ignorados;
    }

    public String ignorados;


    public Usuarios(){

    }

    public Usuarios(String nombre, String status, String imagenA, String imagenS) {
        this.nombre = nombre;
        this.status = status;
        this.imagenA = imagenA;
        this.imagenS = imagenS;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImagenA() {
        return imagenA;
    }

    public void setImagenA(String imagenA) {
        this.imagenA = imagenA;
    }

    public String getImagenS() {
        return imagenS;
    }

    public void setImagenS(String imagenS) {
        this.imagenS = imagenS;
    }
}
