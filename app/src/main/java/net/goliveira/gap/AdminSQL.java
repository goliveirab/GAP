package net.goliveira.gap;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by goliveira on 04/08/16.
 * Administración de base de datos
 */
public class AdminSQL extends SQLiteOpenHelper {

    public AdminSQL(Context context, String name, SQLiteDatabase.CursorFactory factory,
                                 int version) {
        super(context, name, factory, version);
    }

    public AdminSQL(Context context, String name, SQLiteDatabase.CursorFactory factory,
                                 int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @Override
    //Crea la base de datos
    public void onCreate(SQLiteDatabase db) {

        //Tabla: User
        db.execSQL("CREATE TABLE user (id INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL  UNIQUE, " +
                "user VARCHAR NOT NULL  UNIQUE , password VARCHAR NOT NULL , " +
                "name VARCHAR NOT NULL)");

        //Tabla: Account
        db.execSQL("CREATE TABLE account (id INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL  UNIQUE, " +
                "user_id INTEGER NOT NULL, name VARCHAR NOT NULL, " +
                "balance FLOAT NOT NULL  DEFAULT 0.00, status INTEGER NOT NULL DEFAULT 0)");

        //Tabla: Move
        db.execSQL("CREATE TABLE move (id INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL  UNIQUE," +
                "account_id INTEGER NOT NULL, user_id INTEGER NOT NULL, move_type VARCHAR NOT NULL, " +
                "name VARCHAR NOT NULL, balance FLOAT NOT NULL, date DATETIME NOT NULL  DEFAULT 20160101)");

        //Tabla: Notificacion
        db.execSQL("CREATE TABLE notification (id INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL  UNIQUE, " +
                "id_account INTEGER NOT NULL, user_id INTEGER NOT NULL, amount FLOAT NOT NULL, " +
                "sign VARCHAR NOT NULL)");

    }

    @Override
    //Actualiza la base de datos
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

    }
}
