package sunnysoft.presentapp.Datos;

import java.io.IOException;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;

/**
 * Created by dchizavo on 9/12/17.
 */

public class datosentradas {

    public String enviardatos(HttpPost httppost){

        HttpClient httpclient;

        httpclient = new DefaultHttpClient();

        try {
            HttpResponse response = httpclient.execute(httppost);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "se ha enviado";

    }
}
