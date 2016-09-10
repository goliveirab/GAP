package net.goliveira.gap;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by goliveira on 04/08/16.
 * Registro de nuevos ususarios
 */
public class RegisterActivity extends AppCompatActivity {

    private AdminQuery query = new AdminQuery();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register);
    }

    /*
     * Registro de nuevo usuario
     * @param view
     */
    public void register(View view) {

        long cursor;

        //Componentes de Texto de register_activity
        EditText s_name     = (EditText) findViewById(R.id.ar_name);       //Nombre de usuari@
        EditText s_user     = (EditText) findViewById(R.id.ar_user);       //Usuari@
        EditText s_password = (EditText) findViewById(R.id.ar_password);   //Contraseña

        //Valores a almacenar
        ContentValues contentValues = new ContentValues();
        contentValues.put("user", s_user.getText().toString());
        contentValues.put("password", s_password.getText().toString());
        contentValues.put("name", s_name.getText().toString());

        try {
            //Ingresar datos
            cursor = query.insertDb(this, "user", null, contentValues );
            Toast.makeText( this, s_user.getText().toString() +
                            getResources().getString( R.string.db_user_register ),
                    Toast.LENGTH_LONG ).show();

            //Si se han ingresado los datos correctamente, se reinicia la aplicacion
            //para acceder a la pantalla de login
            Intent intent = getBaseContext().getPackageManager().getLaunchIntentForPackage(
                    getBaseContext().getPackageName() );
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

        }
        //Si ocurre algn error se arroja un mensaje
        catch (SQLiteConstraintException e) {
            Toast.makeText( this, s_user.getText().toString() +
                            getResources().getString( R.string.db_user_register_already ),
                    Toast.LENGTH_LONG ).show();
        }

    }
}
