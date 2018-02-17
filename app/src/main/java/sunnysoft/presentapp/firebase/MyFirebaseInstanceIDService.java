package sunnysoft.presentapp.firebase;

import android.content.Context;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import sunnysoft.presentapp.Datos.DatabaseHelper;
import sunnysoft.presentapp.Interfaz.LoginActivity;

/**
 * Created by dchizavo on 7/02/18.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";
    //private ImpuestosClientesDbHelper mImpuestosClientesDbHelper;
    Context context;
    // declaracion de BD
    private DatabaseHelper midb;


    public MyFirebaseInstanceIDService(){

    }

    public MyFirebaseInstanceIDService(Context context){
        this.context = context;

    }

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        sendRegistrationToServer(refreshedToken);
    }

    public void  sendRegistrationToServer(String refreshedToken){

        Log.e("Token", refreshedToken);

        // Inicia base de datos
       // midb.registrartoken(refreshedToken);
        try {

            midb = new DatabaseHelper(context);
            midb.registrartoken(refreshedToken);
            Log.e(TAG, "sendRegistrationToServer: "+"Done");

        }catch (Exception e){

            Log.e(TAG, "sendRegistrationToServer: "+e);

        }
      //  midb.registrartoken(refreshedToken);

        //Aqui se debe realizar la logica para enviar token al servidor

      //  mImpuestosClientesDbHelper = new ImpuestosClientesDbHelper(context);
      //  mImpuestosClientesDbHelper.mockToken(new Token(refreshedToken));

    }




}
