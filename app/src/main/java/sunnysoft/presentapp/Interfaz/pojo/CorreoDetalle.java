package sunnysoft.presentapp.Interfaz.pojo;

import java.util.HashMap;
import java.util.List;

/**
 * Created by gustavo on 1/02/18.
 */

public class CorreoDetalle {

    private String nombre;
    private String imagen_usuario;
    private String contenido;
    private String fecha;
    private String hora;
    private List<String> name_files;
    private List<String> url_files;
    private List<String> urls_images;


    public CorreoDetalle(String nombre, String imagen_usuario, String contenido, String fecha, String hora, List<String> name_files, List<String> url_files, List<String> urls_images) {
        this.nombre = nombre;
        this.imagen_usuario = imagen_usuario;
        this.contenido = contenido;
        this.fecha = fecha;
        this.hora = hora;
        this.name_files = name_files;
        this.url_files = url_files;
        this.urls_images = urls_images;
    }

    public List<String> getName_files() {
        return name_files;
    }

    public void setName_files(List<String> name_files) {
        this.name_files = name_files;
    }

    public List<String> getUrl_files() {
        return url_files;
    }

    public void setUrl_files(List<String> url_files) {
        this.url_files = url_files;
    }

    public List<String> getUrls_images() {
        return urls_images;
    }

    public void setUrls_images(List<String> urls_images) {
        this.urls_images = urls_images;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getImagen_usuario() {
        return imagen_usuario;
    }

    public void setImagen_usuario(String imagen_usuario) {
        this.imagen_usuario = imagen_usuario;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }


}
