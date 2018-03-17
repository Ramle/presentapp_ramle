package sunnysoft.presentapp.Interfaz.pojo;

/**
 * Created by gustavo on 25/11/17.
 */

public class Nivel_Tres {
    private String Nombre;
    private String Url;

    public Nivel_Tres(String nombre , String url) {
        Nombre = nombre;
        Url = url;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }

    public String getUrl() {
        return Url;
    }

    public void setUrl(String url) {
        Url = url;
    }
}
