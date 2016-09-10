package net.goliveira.gap;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by goliveira on 04/08/16.
 * Interfaz principal, se visaliza al hacer login
 */
public class HomeActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private String account_id, user_id, account_name;
    private AdminQuery query = new AdminQuery();
    private Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        user_id = intent.getStringExtra( "USER_ID" );
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);

        if ( user_id == null ){
            user_id = prefs.getString("USER_ID", null);
        }
        else {

            String restoredUser = prefs.getString("USER_ID", null);
            if (restoredUser == null) {
                SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
                editor.putString("USER_ID", user_id);
                editor.apply();
            }
        }

        cursor = query.searchDb( this, "account", "SELECT id as _id, name, balance FROM account " +
                                        "WHERE user_id=? ", new String[]{user_id });

        if ( cursor.getCount() > 0 ) {
            //Si el usuario tiene cuenta registrada
            setContentView(R.layout.activity_home);

            //ListView {
            String[] fromColumns = {"name", "balance"};
            int[] toViews = {R.id.name, R.id.balance};

            //Primer registro:
            cursor.moveToFirst();

            //nombre de la cuenta
            account_name = cursor.getString( cursor.getColumnIndex("name") );

            SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.items_list_account,
                    cursor, fromColumns, toViews, 0);

            ListView listView = (ListView) findViewById(R.id.ha_account_list);
            listView.setAdapter(adapter);
            // }

            //Registrar el menu contextual al listView:
            registerForContextMenu(listView);

            listView.setOnItemClickListener(this);
            adapter.notifyDataSetChanged();

            //Muestra el balance total del usuario {
            TextView s_total = (TextView) findViewById(R.id.ha_total_balance);
            s_total.setText(getUserBalance(user_id));
            // }

        } else {
            // Si no existe ninguna cuenta registrada para ese usuario
            setContentView(R.layout.activity_home_empty);

            //Boton de agregar cuenta (inicial)
            final Button new_account_button = (Button) findViewById(R.id.hae_button_new_account);
            new_account_button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    addAccount(user_id);
                }
            });
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
        inflater.inflate(R.menu.menu_new_account, menu);
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

            case R.id.new_account:
                addAccount(user_id);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Seleccion de item de a lista
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        Intent intent = new Intent(this, AccountActivity.class);
        intent.putExtra("ACCOUNT_ID", String.valueOf( id ));
        intent.putExtra("USER_ID", user_id);
        startActivity(intent);
    }

    /**
     * Menu Contextual para el ListView de cuentas
     * @param menu
     * @param v
     * @param menuInfo
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu( menu, v, menuInfo);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate( R.menu.menu_account_context, menu);
    }

    /***
     * Selecion de las opciones del listView
     * @param item
     * @return
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        switch ( item.getItemId() ) {

            case R.id.debit:
                account_id = String.valueOf( info.id );
                addExpense( account_id, user_id );
                return true;

            case R.id.credit:
                account_id = String.valueOf( info.id );
                addCredit( account_id, user_id );
                return true;

            case R.id.history:
                account_id = String.valueOf( info.id );
                goToHistory(account_id, user_id, account_name);

            default:
                return super.onContextItemSelected(item);
        }

    }

    /***
     * Crea una cuenta nueva la primera vez que el usuario ingresa
     * @param user_id
     */
    public void addAccount(String user_id) {
        Intent intent = new Intent(this, AccountActivity.class);
        intent.putExtra( "USER_ID", user_id );
        startActivity( intent );
    }

    /**
     * Obtiene el balance general del usuario
     * @param user_id
     */
    public String getUserBalance(String user_id) {

        cursor = query.searchDb( this, "account", "SELECT sum(balance) as total FROM account " +
                                        "WHERE user_id =? AND status=0", new String[]{ user_id });
        cursor.moveToFirst();
        String sum = cursor.getString(0);

        return sum;

    }

    /**
     * Va a Agregar un gasto
     * @param account_id
     * @param user_id
     */
    public void addExpense( String account_id, String user_id ) {
        Intent intent = new Intent(this, ExpenseActivity.class);
        intent.putExtra("ACCOUNT_ID", account_id);
        intent.putExtra("USER_ID", user_id);
        startActivity( intent );
    }

    /**
     * Va a Agregar un credito
     * @param account_id
     * @param user_id
     */
    public void addCredit( String account_id, String user_id ) {
        Intent intent = new Intent(this, CreditActivity.class);
        intent.putExtra("ACCOUNT_ID", account_id);
        intent.putExtra("USER_ID", user_id);
        startActivity( intent );
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
