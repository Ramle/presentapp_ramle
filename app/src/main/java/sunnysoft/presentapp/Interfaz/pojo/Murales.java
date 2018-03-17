package sunnysoft.presentapp.Interfaz.pojo;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by gustavo on 12/11/17.
 */

public class Murales {

    private String nombre;
    private String fecha;
    private String contenido;
    private String imagen_persona;
    private  String readmore;
    private Boolean adjuntos;
    private Boolean adjutos_imagen;
    private HashMap<String, String> files = new HashMap<>();
    private HashMap<String, String> photos = new HashMap<>();
    private String url_detalle;

    public Murales(String nombre, String fecha, String contenido, String imagen_persona, String readmore, Boolean adjuntos, Boolean adjutos_imagen, HashMap<String, String> files, HashMap<String, String> photos, String url_detalle) {
        this.nombre = nombre;
        this.fecha = fecha;
        this.contenido = contenido;
        this.imagen_persona = imagen_persona;
        this.readmore = readmore;
        this.adjuntos = adjuntos;
        this.adjutos_imagen = adjutos_imagen;
        this.files = files;
        this.photos = photos;
        this.url_detalle = url_detalle;
    }


    public String getUrl_detalle() {
        return url_detalle;
    }

    public void setUrl_detalle(String url_detalle) {
        this.url_detalle = url_detalle;
    }

    public String getReadmore() {
        return readmore;
    }

    public void setReadmore(String readmore) {
        this.readmore = readmore;
    }

    public Boolean getAdjuntos() {
        return adjuntos;
    }

    public Boolean getAdjutos_imagen() {
        return adjutos_imagen;
    }

    public HashMap<String, String> getFiles() {
        return files;
    }

    public void setFiles(HashMap<String, String> files) {
        this.files = files;
    }

    public HashMap<String, String> getPhotos() {
        return photos;
    }

    public void setPhotos(HashMap<String, String> photos) {
        this.photos = photos;
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

    public String getreadmore() {
        return readmore;
    }

    public void setreadmore(String readmore) {
        this.readmore = readmore;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public Boolean isAdjuntos() {
        return adjuntos;
    }

    public void setAdjuntos(Boolean adjuntos) {
        this.adjuntos = adjuntos;
    }

    public Boolean isAdjutos_imagen() {
        return adjutos_imagen;
    }

    public void setAdjutos_imagen(Boolean adjutos_imagen) {
        this.adjutos_imagen = adjutos_imagen;
    }







}
