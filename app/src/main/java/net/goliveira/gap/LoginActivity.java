package net.goliveira.gap;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by goliveira on 04/08/16.
 */
public class LoginActivity extends AppCompatActivity {

    private AdminQuery query = new AdminQuery();
    private Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        cursor = query.searchDb( this, "user", "SELECT name FROM user LIMIT 1", null);

            if ( cursor.getCount() > 0  ) {
                //Si existe algun usuario registrado, se muestra la pantalla de login
                setContentView(R.layout.activity_login);
            }
            else {
                //De lo contrario, va al registro de usuario nuevo
                goRegister();
            }

    }

    /**
     * Menu de opciones
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_new_user, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch ( item.getItemId() ) {

            case R.id.new_user:
                goRegister();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    /**
     * Registro de usuario nuevo
     */
    public void goRegister() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity( intent );
    }

    /*
     * Entrar a la aplicacion
     * @param view
     */
    public void login(View view) {

        Cursor cursor;
        //Componentes de Texto de login_activity
        EditText s_user     = (EditText) findViewById(R.id.al_user);       //Usuari@
        EditText s_password = (EditText) findViewById(R.id.al_password);   //Contraseña

        cursor = query.searchDb(this, "user", "SELECT id, user, password FROM user " +
                "WHERE user =? AND password =?", new String[]{ s_user.getText().toString(),
                s_password.getText().toString() });

        if ( cursor.getCount() > 0 ) {
            //Si el usuario existe y la contraseña es correcta,
            // va a la pantalla principal de la app
            cursor.moveToNext();
            int id = cursor.getColumnIndex( "id" );
            goToHome( view, cursor.getString( id ) );

        }
        else {
            //de lo contrario muestra un mensaje
            Toast.makeText(this, getResources().getString( R.string.db_user_pass_not_valid ),
                    Toast.LENGTH_LONG).show();
        }

    }


    /*
     * Va a la pantalla principal de la aplicacion
     * @param view
     * @param user_id
     */
    public void goToHome(View view, String user_id) {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra( "USER_ID", user_id );
        startActivity( intent );
        finish();
    }

}

