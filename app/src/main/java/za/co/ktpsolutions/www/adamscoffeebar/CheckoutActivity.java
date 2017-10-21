package za.co.ktpsolutions.www.adamscoffeebar;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import za.co.ktpsolutions.www.AppAPIClasses.Customer;
import za.co.ktpsolutions.www.AppAPIClasses.SMS;
import za.co.ktpsolutions.www.AppAPIClasses.SQL_INFO;

public class CheckoutActivity extends AppCompatActivity {

    static ResultSet QUERY_RESULTS;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        Intent intent = getIntent();
        final int orderID = (int) intent.getSerializableExtra("OrderNumber");
        final Customer loggin_Customer = (Customer) intent.getSerializableExtra("Customer");

        String s = "Adams Coffee Bar" +
                "\nThank you for placing your order." +
                "\nYour order number is: " + orderID;

        SMS sms = new SMS();
        SMS.smsData = s;

        Thread t = new Thread(sms);
        t.start();
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
