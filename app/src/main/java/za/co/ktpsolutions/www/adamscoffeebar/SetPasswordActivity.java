package za.co.ktpsolutions.www.adamscoffeebar;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ExecutionException;

import za.co.ktpsolutions.www.AppAPIClasses.Customer;
import za.co.ktpsolutions.www.AppAPIClasses.EMAIL;
import za.co.ktpsolutions.www.AppAPIClasses.SMS;
import za.co.ktpsolutions.www.AppAPIClasses.SQL_INFO;

public class SetPasswordActivity extends AppCompatActivity {

    Button savepassword;
    EditText username;
    EditText password;
    int cust_ID;
    String cust_name;
    static ResultSet QUERY_RESULTS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_password);

        savepassword = (Button) findViewById(R.id.btn_savepassord);
        username = (EditText) findViewById(R.id.ET_username);
        password = (EditText) findViewById(R.id.ET_password);

        savepassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(validEmail()){
                    String sql2 = "UPDATE Customer\n" +
                            "SET       cust_Password = '"+password.getText()+"'\n" +
                            "WHERE (cust_ID = '"+cust_ID + "" +"')";

                    SetPasswordActivity.SQLQueryExecutor SQLEX3 = new SetPasswordActivity.SQLQueryExecutor();

                    SQLEX3.execute(sql2);

                    SMS sms = new SMS();
                    String data = "Adams Coffee Bar" +
                            "\nThank you " + cust_name + "" +
                            "\nYour password has been set, here are your details:" +
                            "\nUsername: " + username.getText()+"" +
                            "\nPassword: " + password.getText();

                    SMS.smsData = data;

                    Thread t = new Thread(sms);
                    t.start();

                    startActivity(new Intent(SetPasswordActivity.this, MainActivity.class));

                }
                else{
                    username.setText("");
                    password.setText("");
                }
            }
        });




    }

    private boolean validEmail(){

        String sql1 = "SELECT Customer.*\n" +
                "FROM     Customer\n" +
                "WHERE  (cust_EmailAddr = '"+username.getText()+"')";

        SetPasswordActivity.SQLQueryExecutor SQLEX = new SetPasswordActivity.SQLQueryExecutor();

        SQLEX.execute(sql1);

        try{
            ResultSet QR;
            QR = SQLEX.get();

            if(!QR.isBeforeFirst()){
                QR.close();
                SQLEX.CLOSE();
                Toast.makeText(SetPasswordActivity.this,"Error",Toast.LENGTH_LONG).show();
                return false;
            }
            QR.next();

            cust_ID = QR.getInt(1);
            cust_name = QR.getString(2);

            QR.close();
            SQLEX.CLOSE();
            return true;
        }
        catch (InterruptedException e){
            e.printStackTrace();
            SQLEX.CLOSE();
        }
        catch (ExecutionException e){
            e.printStackTrace();
            SQLEX.CLOSE();
        }
        catch (SQLException e){
            e.printStackTrace();
            SQLEX.CLOSE();
        }
        return false;
    }

    public static class SQLQueryExecutor extends AsyncTask<String, String, ResultSet> {

        Connection connection;
        Statement statement;
        ResultSet resultSet;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(ResultSet resultSet) {
            super.onPostExecute(resultSet);
            QUERY_RESULTS = resultSet;
        }

        @Override
        protected ResultSet doInBackground(String... params) {

            try
            {
                connection = DriverManager.getConnection(
                        SQL_INFO.getDatabaseURL(), SQL_INFO.getDatabaseUsername(), SQL_INFO.getDatabasePassword());
                statement = connection.createStatement();

                if(params.length > 1){
                    statement.executeUpdate(params[0]);
                    return null;
                }
                else{
                    resultSet = statement.executeQuery(params[0]);

                    return resultSet;
                }

            }
            catch(SQLException e) {
                e.printStackTrace();
                return null;
            }

        }

        public void CLOSE(){
            try {
                resultSet.close();
                statement.close();
                connection.close();
            }
            catch(SQLException e){
                e.printStackTrace();
            }

        }
    }

}
