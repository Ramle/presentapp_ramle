package sunnysoft.presentapp.Interfaz.pojo;

/**
 * Created by dchizavo on 13/12/17.
 */

public class Photos {

    String original_name;
    String url;

    public Photos(String original_name, String url){
        this.original_name = original_name;
        this.url= url;
    }
    public String getoriginal_name() {
        return original_name;
    }
    public String getUrl() {
        return url;
    }
}
