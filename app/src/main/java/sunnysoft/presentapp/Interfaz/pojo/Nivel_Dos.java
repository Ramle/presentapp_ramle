package sunnysoft.presentapp.Interfaz.pojo;

/**
 * Created by gustavo on 25/11/17.
 */

public class Nivel_Dos {

    private String Nombre;
    private String view_all_url;

    public Nivel_Dos(String nombre) {
        Nombre = nombre;
    }

    public Nivel_Dos(String nombre, String view_all_url) {
        this.Nombre = nombre;
        this.view_all_url = view_all_url;
    }

    public String getView_all_url() {
        return view_all_url;
    }

    public void setView_all_url(String view_all_url) {
        this.view_all_url = view_all_url;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }
}
