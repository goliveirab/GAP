package net.goliveira.gap;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by goliveira on 22/08/16.
 * Gestion de cuentas
 */
public class AccountActivity extends AppCompatActivity {

    private String user_id, account_id, account_name;
    private EditText s_name, s_balance;
    private Intent intent;

    private AdminQuery query = new AdminQuery();
    private  Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        //asignacion de variables {
        s_name    = (EditText) findViewById(R.id.aa_account_name);
        s_balance = (EditText) findViewById(R.id.aa_acount_balance);
        // }

        //Recepcion de datos {
        intent = getIntent();
        user_id = intent.getStringExtra( "USER_ID" );

        if ( intent.hasExtra("ACCOUNT_ID") ) {
            account_id = intent.getStringExtra( "ACCOUNT_ID" );

            //BD
            //AdminQuery query = new AdminQuery();
            cursor = query.searchDb( this, "account", "SELECT name, balance FROM account " +
                    "WHERE id=? AND user_id=?", new String[]{ account_id, user_id });


            cursor.moveToFirst();
            account_name = cursor.getString( 0 );
            s_name.setText( cursor.getString( 0 ) );
            s_balance.setText( cursor.getString( 1 ) );
            s_balance.setEnabled(false);

            cursor.close();

        }

        final Button button = (Button) findViewById(R.id.aa_account_save_button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if ( intent.hasExtra("ACCOUNT_ID") ) {
                    updateAccount(account_id, user_id);
                }
                else {
                    newAccount(v, user_id);
                }
            }
        });

    }

    /**
     * Menu de opciones
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_account_account, menu);
        return true;
    }

    /**
     * Seleccion de item en menu de opciones
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch ( item.getItemId() ) {

            case R.id.delete_account:
                deleteAccount( account_id, user_id );
                return true;

            case R.id.history_account:
                goToHistory( account_id, user_id, account_name );
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*
     * Agregar una cuenta nueva
     * @param view
     * @param user_id
     */
    public void newAccount (View view, String user_id) {
        //Valores a almacenar
        ContentValues contentValues = new ContentValues();
        contentValues.put("user_id", user_id);
        contentValues.put("status", "0");
        contentValues.put("name", s_name.getText().toString());
        contentValues.put("balance", s_balance.getText().toString());

        //BD
        cursor = query.searchDb( this, "account", "SELECT name FROM account " +
                "WHERE user_id=? AND name =?", new String[]{ user_id, s_name.getText().toString() });

        cursor.moveToFirst();

        if (cursor.getCount() > 0) {

            Toast.makeText( this, getResources().getString( R.string.db_account_already_register),
                    Toast.LENGTH_LONG ).show();
        }
        else {
            //Ingresar datos
            query.insertDb( this, "account", null, contentValues);

            Toast.makeText( this, s_name.getText().toString() +
                            getResources().getString( R.string.db_account_register ),
                    Toast.LENGTH_LONG ).show();

            s_name.setText("");
            s_balance.setText("");

            cursor.close();

        }

    }

    /**
     * Actualizar la cuenta
     * @param account_id
     * @param user_id
     */
    public void updateAccount(String account_id, String user_id){
        //Valores a almacenar
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", s_name.getText().toString());
        contentValues.put("balance", s_balance.getText().toString());

        //Actualizar datos
        query.updateDb( this, "account", "id=? AND user_id=?", contentValues,
                new String[] { account_id, user_id } );

        Toast.makeText(this, R.string.db_account_updated, Toast.LENGTH_LONG).show();
    }

    /**
     * Borra una cuenta
     * @param account_id
     * @param user_id
     */
    public void deleteAccount( String account_id, String user_id ){

        //Actualizar datos
        query.deleteDb( this, "account", "id=? AND user_id=?", new String[] { account_id, user_id });


        //Se borran tambien los registros de la tabla move
        query.deleteDb( this, "move", "id=? AND user_id=?", new String[] { account_id, user_id });

        Toast.makeText(this, R.string.db_account_deleted, Toast.LENGTH_LONG).show();
    }

    /**
     * Historial de movimientos de cuenta
     * @param account_id
     * @param user_id
     */
    public void goToHistory(String account_id, String user_id, String account_name){
        Intent intent = new Intent(this, HistoryAccountActivity.class);
        intent.putExtra("ACCOUNT_ID", account_id);
        intent.putExtra("USER_ID", user_id);
        intent.putExtra("ACCOUNT", account_name);
        startActivity(intent);
    }
}
