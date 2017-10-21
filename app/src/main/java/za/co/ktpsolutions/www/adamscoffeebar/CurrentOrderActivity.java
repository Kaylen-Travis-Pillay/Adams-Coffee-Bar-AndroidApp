package za.co.ktpsolutions.www.adamscoffeebar;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ExecutionException;

import za.co.ktpsolutions.www.AppAPIClasses.Customer;
import za.co.ktpsolutions.www.AppAPIClasses.SQL_INFO;

public class CurrentOrderActivity extends AppCompatActivity {

    static ResultSet QUERY_RESULTS;
    TextView status;
    Button returnHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_order);

        Intent intent = getIntent();
        final Customer loggin_Customer = (Customer) intent.getSerializableExtra("Customer");

        status = (TextView) findViewById(R.id.txt_Status);
        returnHome = (Button) findViewById(R.id.btn_77657);

        returnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newOrderIntent = new Intent(CurrentOrderActivity.this, LandingActivity.class);
                newOrderIntent.putExtra("Customer", loggin_Customer);
                startActivity(newOrderIntent);
            }
        });

        String checkOrders = "SELECT order_ID, order_NumberOfProducts, order_TotalPrice, order_DatePlaced, order_TimePlaced, order_TimeDelivered, order_CompletionStatus, employee_ID, cust_ID\n" +
                "FROM   Order_\n" +
                "WHERE (cust_ID = '"+loggin_Customer.m_ID+"')";

        CurrentOrderActivity.SQLQueryExecutor SQLEX1 = new CurrentOrderActivity.SQLQueryExecutor();

        SQLEX1.execute(checkOrders);

        try{
            ResultSet QUERY_RESULTS;
            QUERY_RESULTS = SQLEX1.get();

            if(!QUERY_RESULTS.isBeforeFirst()){
                status.setText("You have not ordered anything");
            }
            else {

                String notcompleteOrders = "SELECT order_ID, order_NumberOfProducts, order_TotalPrice, order_DatePlaced, order_TimePlaced, order_TimeDelivered, order_CompletionStatus, employee_ID, cust_ID\n" +
                        "FROM   Order_\n" +
                        "WHERE (cust_ID = '"+loggin_Customer.m_ID+"') AND (order_CompletionStatus = 'Not Complete')";

                CurrentOrderActivity.SQLQueryExecutor SQLEX2 = new CurrentOrderActivity.SQLQueryExecutor();

                SQLEX2.execute(notcompleteOrders);

                try{
                    ResultSet QR;
                    QR = SQLEX2.get();

                    if(!QR.isBeforeFirst()){
                        status.setText("All orders complete");
                    }
                    else{
                        int count = 0;

                        while(QR.next()){
                            count++;
                        }

                        status.setText(count + " orders in progress");
                    }

                    QR.close();
                    SQLEX2.CLOSE();
                }
                catch (InterruptedException e){
                    e.printStackTrace();
                    SQLEX2.CLOSE();
                }
                catch (ExecutionException e){
                    e.printStackTrace();
                    SQLEX2.CLOSE();
                }
                catch (SQLException e){
                    e.printStackTrace();
                    SQLEX2.CLOSE();
                }


            }

            QUERY_RESULTS.close();
            SQLEX1.CLOSE();
        }
        catch (InterruptedException e){
            e.printStackTrace();
            SQLEX1.CLOSE();
        }
        catch (ExecutionException e){
            e.printStackTrace();
            SQLEX1.CLOSE();
        }
        catch (SQLException e){
            e.printStackTrace();
            SQLEX1.CLOSE();
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
        Toast.makeText(CurrentOrderActivity.this, "Inactive", Toast.LENGTH_SHORT).show();
    }
}
