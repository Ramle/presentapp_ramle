package sunnysoft.presentapp.Interfaz.pojo;

import java.util.List;

/**
 * Created by Chris on 12/3/17.
 */

public class Entradas {

    private String nombre;
    private String fecha;
    private String [] nomtags;
    private Integer indice;
    private String detalle;
    private String image_persona;
    private String [] tags;
    private String url_entrada_detail;
    private String url_tabs_entradas;

    public Entradas(String nombre, String detalle, Integer indice, String image_persona, String [] nomtags, String url_detalle) {
        this.nombre = nombre;
        this.detalle = detalle;
        this.nomtags = nomtags;
        this.indice = indice;
        this.image_persona = image_persona;
        this.url_entrada_detail = url_detalle;
    }


    public Entradas(String nombre, String fecha, String [] tags, String url_entrada_detail,String url_tabs_entradas) {
        this.nombre = nombre;
        this.fecha = fecha;
        this.tags = tags;
        this.url_entrada_detail = url_entrada_detail;
        this.url_tabs_entradas = url_tabs_entradas;
    }

    public String getUrl_tabs_entradas() {
        return url_tabs_entradas;
    }

    public void setUrl_tabs_entradas(String url_tabs_entradas) {
        this.url_tabs_entradas = url_tabs_entradas;
    }

    public Entradas() {

    }

    public String getUrl_entrada_detail() {
        return url_entrada_detail;
    }

    public void setUrl_entrada_detail(String url_entrada_detail) {
        this.url_entrada_detail = url_entrada_detail;
    }

    public String getImage_persona() {
        return image_persona;
    }

    public void setImage_persona(String image_persona) {
        this.image_persona = image_persona;
    }

    public Entradas(String proceso_name, String created_at, List<String> nomtags) {
        this.nombre = nombre;
        this.fecha = fecha;
        this.tags = tags;
    }

    public String[] getNomtags() {
        return nomtags;
    }

    public void setNomtags(String[] nomtags) {
        this.nomtags = nomtags;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }

    public String getNombre() {
        return nombre;
    }

    public String getFecha() {
        return fecha;
    }

    public Integer getIndice() {
        return indice;
    }

    public void setIndice(Integer indice) {
        this.indice = indice;
    }



    //public String[] getTags() {
    //  return tags;
    //}

}
