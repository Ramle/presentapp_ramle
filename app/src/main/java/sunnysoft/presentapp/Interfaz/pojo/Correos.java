package sunnysoft.presentapp.Interfaz.pojo;



/**
 * Created by dchizavo on 8/01/18.
 */

public class Correos {


    private String nombre;
    private String fecha;
    private String Hora;
    private String asunto;
    private String imagen_persona;
    private String url_detalle;
    private String isread;

    public Correos(String nombre, String fecha, String hora, String imagen_persona, String asunto, String url_detalle, String isread) {
        this.nombre = nombre;
        this.fecha = fecha;
        this.Hora = hora;
        this.asunto = asunto;
        this.imagen_persona = imagen_persona;
        this.url_detalle = url_detalle;
        this.isread = isread;
    }

    public String getIsread() {
        return isread;
    }

    public void setIsread(String isread) {
        this.isread = isread;
    }

    public String getUrl_detalle() {
        return url_detalle;
    }

    public void setUrl_detalle(String url_detalle) {
        this.url_detalle = url_detalle;
    }

    public String getImagen_persona() {
        return imagen_persona;
    }

    public void setImagen_persona(String imagen_persona) {
        this.imagen_persona = imagen_persona;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return Hora;
    }

    public void setHora(String hora) {
        this.Hora = hora;
    }


    public String getAsunto() {
        return asunto;
    }

    public void setAsunto(String asunto) {
        this.asunto = asunto;
    }

}


