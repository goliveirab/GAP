package net.goliveira.gap;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

/**
 * Created by goliveira on 27/08/16.
 * Historial de movimientos de la cuenta
 */
public class HistoryAccountActivity extends AppCompatActivity {

    private String user_id, account_id, account_name;
    private Intent intent;
    private AdminQuery query = new AdminQuery();
    private Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_history);

        //Variables
        TextView history_info = (TextView) findViewById( R.id.account );
        String text_info;

        //Recepcion de datos {
        intent = getIntent();
        user_id = intent.getStringExtra("USER_ID");
        account_id = intent.getStringExtra("ACCOUNT_ID");
        account_name = intent.getStringExtra("ACCOUNT");

        //Acceso de BD
        cursor = query.searchDb( this, "move", "SELECT id as _id, name, balance, date, move_type " +
                "FROM move WHERE user_id=? AND account_id=?",new String[]{ user_id, account_id});

        cursor.moveToFirst();

        text_info = getString( R.string.account );
        history_info.setText( text_info + " " + account_name );

        if ( cursor.getCount() > 0 ){
            //ListView {
            String[] fromColumns = { "name", "move_type", "balance", "date" };
            int[] toViews = { R.id.name, R.id.move_type, R.id.balance, R.id.date }; //R.id.id,

            SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.items_list_account_history,
                    cursor, fromColumns, toViews, 0);

            ListView listView = (ListView) findViewById( R.id.hia_account_list );
            listView.setAdapter( adapter );
            // }


            adapter.notifyDataSetChanged();

        }


    }
}
