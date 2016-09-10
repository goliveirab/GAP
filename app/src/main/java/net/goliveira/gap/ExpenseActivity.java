package net.goliveira.gap;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
 * Agrega un gasto segun usuario
 */
public class ExpenseActivity extends AppCompatActivity {

    private String account_id, user_id;
    private AdminQuery query = new AdminQuery();
    private  Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        account_id = intent.getStringExtra("ACCOUNT_ID");
        user_id    = intent.getStringExtra("USER_ID");

        setContentView(R.layout.activity_expense);

        TextView instruction = (TextView) findViewById( R.id.ea_instruction_text );

        //Acceso a BD
        cursor = query.searchDb(this, "account", "SELECT id as _id, name FROM account WHERE user_id=? " +
                        "AND _id=?", new String[]{ user_id, account_id });


        //Texto instructivo
        cursor.moveToFirst();
        instruction.setText( "Debitar de "+  cursor.getString( cursor.getColumnIndex( "name" ) ) + ":");

        final Button button = (Button) findViewById(R.id.ea_expense_save_buton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                addExpense(account_id);
            }
        });

    }

    /*
     * Agrega un gasto a a la base de datos en una cuenta determinada de el usuario
     * @param view
     * @param user_id
     */
    public void addExpense(String account_id) {

        EditText s_expense_name    = (EditText) findViewById(R.id.ea_expense_name);
        EditText s_expense_expense = (EditText) findViewById(R.id.ea_expense_expense);
        Float f_expense, db_balance;

        //Acceso a BD: ACCOUNT
        cursor = query.searchDb(this, "account", "SELECT balance FROM account " +
                "WHERE id =? AND user_id=?", new String[]{ account_id, user_id });

        cursor.moveToFirst();
        f_expense  = Float.parseFloat( s_expense_expense.getText().toString() );
        db_balance = cursor.getFloat(0);

        if (  f_expense >  db_balance) {
            //Si el gasto es mayor al balance en la cuenta
            Toast.makeText(this, R.string.expense_not_posible ,Toast.LENGTH_LONG).show();
        }
        else {
            //De lo contrario

            //Fecha de la transaccion
            Date date = new Date();
            String stringDate = DateFormat.getDateTimeInstance().format(date);

            //Acceso a BD: MOVE
            ContentValues contentValues = new ContentValues();
            contentValues.put("account_id", account_id);
            contentValues.put("user_id", user_id);
            contentValues.put("move_type", "-");
            contentValues.put("name", s_expense_name.getText().toString());
            contentValues.put("balance", s_expense_expense.getText().toString());
            contentValues.put("date", stringDate);

            query.insertDb(this, "move", null, contentValues);

            s_expense_expense.setText("");
            s_expense_name.setText("");

            //Se ajusta el balance de la cuenta
            ContentValues contentValues_account = new ContentValues();
            Float total = db_balance - f_expense;
            contentValues_account.put("balance", total);
            query.updateDb( this, "account", "id = ? AND user_id=?", contentValues_account,
                    new String[]{ account_id, user_id, });

            //Si todo fue exitoso
            Toast.makeText(this, R.string.db_debit_register, Toast.LENGTH_LONG).show();

        }

    }
}

