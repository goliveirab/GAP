package net.goliveira.gap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import java.security.Key;
import java.util.ArrayList;

/**
 * Created by goliveira on 04/08/16.
 * Administracion de transacciones de la base de datos
 */
public class AdminQuery {

    private Cursor scursor;         //sentencia select
    private long icursor;           //sentencia insert
    private int dcursor, ucursor;   //sentencia delete y update

    /**
     * Agrega un registro en la base de datos
     * @param tabla
     * @param query
     * @param value
     */
    public long insertDb( Context context, String tabla, String query, ContentValues value) {
        //Apertura de BD
        AdminSQL table = new AdminSQL(context, tabla, null, 1);
        SQLiteDatabase db = table.getWritableDatabase();

        if( db != null){
            icursor =  db.insertOrThrow( tabla, null, value );
        }
        else {
            dbExistMsg( context );
        }

        db.close();
        table.close();
        return icursor;
    }

    /**
     * Busca un registro en la base de datos
     * @param context
     * @param query
     * @param value
     */
    public Cursor searchDb(Context context, String tabla, String query, String[] value) {

        //Apertura de BD
        AdminSQL table = new AdminSQL(context, tabla, null, 1);
        SQLiteDatabase db = table.getWritableDatabase();

        //Si existe la base de datos
        if ( db != null ) {
                scursor = db.rawQuery(query, value);
        }
        else {
            dbExistMsg( context );
        }

        //db.close();
        //table.close();
        return scursor;
    }

    /**
     * Elimina un registro de la base de datos
     * @param tabla
     * @param query
     * @param value
     */
    public int deleteDb( Context context, String tabla, String query, String[] value) {
        //Apertura de BD
        AdminSQL table = new AdminSQL(context, tabla, null, 1);
        SQLiteDatabase db = table.getWritableDatabase();

        if( db != null) {
            dcursor = db.delete(tabla, query, value);
        }
        else {
            dbExistMsg(context);
        }

        db.close();
        table.close();
        return dcursor;
    }

    /**
     * Actualizar un registro de la base de datos
     * @param tabla
     * @param query
     * @param value
     */
    public int updateDb( Context context, String tabla, String query, ContentValues value, String[] queryValues) {
        //Apertura de BD
        AdminSQL table = new AdminSQL(context, tabla, null, 1);
        SQLiteDatabase db = table.getWritableDatabase();

        if( db != null ){
            ucursor = db.updateWithOnConflict( tabla, value, query, queryValues, 0 );
        }
        else {
            dbExistMsg( context );
        }


        db.close();
        table.close();
        return ucursor;

    }

    public void dbExistMsg( Context context ) {
        //Arrojar error de la base de datos
        Toast.makeText( context, String.valueOf( R.string.db_not_exist ),
                Toast.LENGTH_LONG).show();

    }

}
