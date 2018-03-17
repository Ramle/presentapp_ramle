package sunnysoft.presentapp.Interfaz.pojo;

/**
 * Created by esantopc on 20/12/17.
 */

public class FieldsEvento {

    private String titulo;
    private String detalle;
    private Integer indice;

    public FieldsEvento(String titulo, String detalle, Integer indice) {
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
