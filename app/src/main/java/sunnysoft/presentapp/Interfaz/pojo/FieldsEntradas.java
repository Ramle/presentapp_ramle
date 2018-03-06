package sunnysoft.presentapp.Interfaz.pojo;

/**
 * Created by dchizavo on 1/02/18.
 */

public class FieldsEntradas {


    private String titulo;
    private String detalle;
    private Integer indice;

    public FieldsEntradas(String titulo, String detalle, Integer indice) {
        this.titulo = titulo;
        this.detalle = detalle;
        this.indice = indice;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }

    public Integer getIndice() {
        return indice;
    }

    public void setIndice(Integer indice) {
        this.indice = indice;
    }


}
