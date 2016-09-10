package net.goliveira.gap;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.text.DateFormat;
import java.util.Date;

/**
 * Created by goliveira on 27/08/16.
 * Agrega un credito segun el usuario
 */
public class CreditActivity extends AppCompatActivity {

    private String account_id, user_id;
    private AdminQuery query = new AdminQuery();
    private Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        account_id = intent.getStringExtra("ACCOUNT_ID");
        user_id    = intent.getStringExtra("USER_ID");

        setContentView(R.layout.activity_credit);
        TextView instruction = (TextView) findViewById( R.id.ca_instruction_text );

        //Acceso a BD
        cursor = query.searchDb(this, "account", "SELECT id as _id, name FROM account " +
                 "WHERE user_id=? AND _id=?", new String[]{ user_id, account_id });

        //Texto instructivo
        cursor.moveToFirst();
        instruction.setText( "Agregar deposito a "+
                cursor.getString( cursor.getColumnIndex( "name" ) ) +
                ":");

        final Button button = (Button) findViewById(R.id.ca_credit_save_buton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                addCredit(account_id);
            }
        });

    }

    /*
     * Agrega un credito a a la base de datos en una cuenta determinada de usuario
     * @param view
     * @param user_id
     */
    public void addCredit(String account_id) {

        EditText s_credit_name    = (EditText) findViewById(R.id.ca_credit_name);
        EditText s_credit_credit  = (EditText) findViewById(R.id.ca_credit_credit);
        Float f_credit, db_balance;

        //Acceso a BD: ACCOUNT
        cursor = query.searchDb(this, "account", "SELECT balance FROM account " +
                        "WHERE id =? AND user_id=?",new String[]{ account_id, user_id });

        cursor.moveToFirst();
        f_credit  = Float.parseFloat( s_credit_credit.getText().toString() );
        db_balance = cursor.getFloat(0);

        //Fecha de la transaccion
        Date date = new Date();
        String stringDate = DateFormat.getDateTimeInstance().format(date);

        //Acceso de BD: MOVE
        ContentValues contentValues = new ContentValues();
        contentValues.put("account_id", account_id);
        contentValues.put("user_id", user_id);
        contentValues.put("move_type", "+");
        contentValues.put("name", s_credit_name.getText().toString());
        contentValues.put("balance", s_credit_credit.getText().toString());
        contentValues.put("date", stringDate);

        query.insertDb(this, "move", null, contentValues);

        s_credit_credit.setText("");
        s_credit_name.setText("");

        //Se ajusta el balance de la cuenta
        ContentValues contentValues_account = new ContentValues();
        Float total = db_balance + f_credit;
        contentValues_account.put("balance", total);
        query.updateDb( this,"account", "id = ? AND user_id=?", contentValues_account,
                new String[]{ account_id, user_id, } );

        //Si todo fue exitoso
        Toast.makeText(this, R.string.db_debit_register, Toast.LENGTH_LONG).show();

    }
}
