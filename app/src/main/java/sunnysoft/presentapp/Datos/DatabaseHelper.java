package sunnysoft.presentapp.Datos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by dchizavo on 16/11/17.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    // variables

    private static final String DATABASE_NOM = "Present4.db";
    private static final String TABLE1_NOM = "login";
    private static final String TABLE2_NOM = "token";
    private static final int DATABASE_VER = 1;

    // campos tablas
    public static final String t1_user = "user";
    public static final String t1_user_name = "user_name";
    public static final String t1_user_image = "user_image";
    public static final String t1_user_type = "usertype";
    public static final String t1_token = "token";
    public static final String t1_logo = "logo";
    public static final String t1_subdomain = "subdomain";

    public static final String t2_token = "token";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NOM, null, DATABASE_VER);
        SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE "+TABLE1_NOM+" ("+t1_user+" TEXT PRIMARY KEY, "+t1_user_name+" TEXT,"+t1_user_image+" TEXT ," + t1_token
                +" TEXT, "+t1_logo + " TEXT, " +t1_user_type + " TEXT, "+t1_subdomain + " )");

        db.execSQL("CREATE TABLE "+TABLE2_NOM+" ("+t2_token+" TEXT )");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE1_NOM);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE2_NOM);

    }


    //almacena en la BD los usuarios y contraseñas de la aplicacion principal
    public boolean registrarlogin(String usuario, String nombre, String image, String token, String logo, String user_type, String subdomain){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(t1_user, usuario);
        values.put(t1_user_name, nombre);
        values.put(t1_user_image, image);
        values.put(t1_token, token);
        values.put(t1_logo, logo);
        values.put(t1_user_type, user_type);
        values.put(t1_subdomain, subdomain);
        long resultado = db.insert(TABLE1_NOM,null,values);
        if (resultado == -1){
            return false;
        }else{
            return true;
        }
    }

    public boolean registrartoken(String token){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(t2_token, token);
        long resultado = db.insert(TABLE2_NOM,null,values);
        if (resultado == -1){
            return false;
        }else{
            return true;
        }
    }

    public Cursor Gettoken (){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE2_NOM,null,null,null,null,null,null);
        if (cursor != null){
            cursor.moveToFirst();
        }
        return cursor;
    }


    //muestra el detalle del pedido recibido por parametro retorna un cursor para recorrerlo


    public Cursor Session (){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE1_NOM,null,null,null,null,null,null);
        if (cursor != null){
            cursor.moveToFirst();
        }
        return cursor;
    }


    public void logouth() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL ("DROP TABLE IF EXISTS "+ TABLE1_NOM);
        db.execSQL ("DROP TABLE IF EXISTS "+ TABLE2_NOM);
        onCreate(db);
    }

    public void oncreateusers(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("CREATE TABLE "+TABLE1_NOM+" ("+t1_user+" TEXT PRIMARY KEY, "+t1_user_name+" TEXT,"+t1_user_image+" TEXT ," + t1_token
                +" TEXT, "+t1_logo + " TEXT, " +t1_user_type + " TEXT, "+t1_subdomain + " )");

        db.execSQL("CREATE TABLE "+TABLE2_NOM+" ("+t2_token+" TEXT )");
    }


}
