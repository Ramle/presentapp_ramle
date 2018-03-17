package sunnysoft.presentapp.Interfaz.pojo;

import java.util.UUID;

/**
 * Created by apple on 4/12/17.
 */

public class Eventos {
    private String id;
    private String evento;
    private String tipoevento;
    private String fechaeevento;
    private String horaevento;
    private String mId;
    private String detail_url;
    private Integer indice;
    private String fechaeventoString;

    public Eventos(String id, String evento, String tipoevento, String fechaevento, String horaevento, String detail_url, Integer indice, String fechaeventoString) {

        mId = UUID.randomUUID().toString();
        this.id = id;
        this.evento = evento;
        this.tipoevento = tipoevento;
        this.fechaeevento = fechaevento;
        this.horaevento = horaevento;
        this.detail_url = detail_url;
        this.indice = indice;
        this.fechaeventoString = fechaeventoString;
    }

    public String getFechaeventoString() {
        return fechaeventoString;
    }

    public void setFechaeventoString(String fechaeventoString) {
        this.fechaeventoString = fechaeventoString;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEvento() {
        return evento;
    }

    public void setEvento(String evento) {
        this.evento = evento;
    }

    public String getTipoevento() {
        return tipoevento;
    }

    public void setTipoevento(String tipoevento) {
        this.tipoevento = tipoevento;
    }

    public String getFechaeevento() {
        return fechaeevento;
    }

    public void setFechaeevento(String fechaeevento) {
        this.fechaeevento = fechaeevento;
    }

    public String getHoraevento() {
        return horaevento;
    }

    public void setHoraevento(String horaevento) {
        this.horaevento = horaevento;
    }

    public String getmId() {
        return mId;
    }

    public void setmId(String mId) {
        this.mId = mId;
    }

    public String getDetail_url() {
        return detail_url;
    }

    public void setDetail_url(String detail_url) {
        this.detail_url = detail_url;
    }

    public Integer getIndice() {
        return indice;
    }

    public void setIndice(Integer indice) {
        this.indice = indice;
    }

}
