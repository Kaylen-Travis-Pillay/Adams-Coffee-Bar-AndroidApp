package za.co.ktpsolutions.www.adamscoffeebar;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import net.sourceforge.jtds.jdbc.JtdsConnection;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ExecutionException;

import za.co.ktpsolutions.www.AppAPIClasses.Customer;
import za.co.ktpsolutions.www.AppAPIClasses.SQL_INFO;

public class MainActivity extends AppCompatActivity {

    Button login;
    Button register;
    EditText username;
    EditText password;
    static ResultSet QUERY_RESULTS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        login = (Button) findViewById(R.id.btn_login);
        register = (Button) findViewById(R.id.btn_register);
        username = (EditText) findViewById(R.id.et_username);
        password = (EditText) findViewById(R.id.et_password_login);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String SELECT_QUERY = "SELECT cust_ID, cust_FirstName, cust_Surname, cust_EmailAddr, cust_CellNumber, cust_Addr_Street, cust_Addr_City, cust_Addr_AreaCode, cust_Password\n" +
                        "FROM   Customer\n" +
                        "WHERE (cust_EmailAddr = '"+username.getText().toString()+"') AND (cust_Password = '"+password.getText().toString()+"')";

                SQLQueryExecutor SQLQS = new SQLQueryExecutor();

                if(isValidEmail(username.getText())){
                    /*
                        Need to check if the email is a valid username now!
                     */
                    if(isValidPassword(password.getText().toString())){

                        SQLQS.execute(SELECT_QUERY);

                        try{
                            QUERY_RESULTS = SQLQS.get();

                            if(!QUERY_RESULTS.isBeforeFirst()){
                                Toast.makeText(MainActivity.this, "INVALID LOGIN DETAILS!", Toast.LENGTH_LONG).show();
                                password.setText("");
                            }
                            else {
                                QUERY_RESULTS.next();
                                Toast.makeText(MainActivity.this, "Welcome " + QUERY_RESULTS.getString(2), Toast.LENGTH_SHORT).show();

                                Customer loggedin_Customer = new Customer();
                                loggedin_Customer.m_ID = Integer.parseInt(QUERY_RESULTS.getString(1));
                                loggedin_Customer.m_Name = QUERY_RESULTS.getString(2);
                                loggedin_Customer.m_Surname = QUERY_RESULTS.getString(3);
                                loggedin_Customer.m_Email = QUERY_RESULTS.getString(4);
                                loggedin_Customer.m_Cell = QUERY_RESULTS.getString(5);
                                loggedin_Customer.m_StreetAddress = QUERY_RESULTS.getString(6);
                                loggedin_Customer.m_City = QUERY_RESULTS.getString(7);
                                loggedin_Customer.m_AreaCode = QUERY_RESULTS.getString(8);
                                loggedin_Customer.m_Password = QUERY_RESULTS.getString(9);

                                Intent move_to_landingPage = new Intent(MainActivity.this, LandingActivity.class);
                                move_to_landingPage.putExtra("Customer", loggedin_Customer);

                                startActivity(move_to_landingPage);
                            }

                            SQLQS.CLOSE();
                        }
                        catch (InterruptedException e){
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, "Internal Error: InterruptedException", Toast.LENGTH_SHORT).show();
                            SQLQS.CLOSE();
                        }
                        catch (ExecutionException e){
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, "Internal Error: ExecutionException", Toast.LENGTH_SHORT).show();
                            SQLQS.CLOSE();
                        }
                        catch (SQLException e){
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, "No internet connection!", Toast.LENGTH_SHORT).show();
                            SQLQS.CLOSE();
                        }
                    }
                    else{
                        Toast.makeText(MainActivity.this,"Password must be 5 or more characters long",Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(MainActivity.this,"Invalid email address",Toast.LENGTH_SHORT).show();
                }
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, CustActivity.class));
            }
        });

    }

    public final static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    public final static boolean isValidPassword(String target){
        if(target.equals("")){
            return false;
        }
        else if(target.length() < 5){
            return false;
        }
        else{
            return true;
        }
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

    @Override
    public void onBackPressed(){

    }
}
