package za.co.ktpsolutions.www.adamscoffeebar;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import za.co.ktpsolutions.www.AppAPIClasses.Customer;
import za.co.ktpsolutions.www.AppAPIClasses.Product;
import za.co.ktpsolutions.www.AppAPIClasses.SMS;
import za.co.ktpsolutions.www.AppAPIClasses.SQL_INFO;

import static java.sql.Types.NULL;

public class NewOrderActivity extends AppCompatActivity {

    static Map<Integer, Product> cart = new HashMap<Integer, Product>();
    static ResultSet QUERY_RESULTS;
    static double TotalPrice;
    Button food;
    Button beverage;
    Button cancel;
    Button checkout;
    TextView cartNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_order);

        food = (Button) findViewById(R.id.btn_Food);
        beverage = (Button) findViewById(R.id.btn_Beverage);
        cancel = (Button) findViewById(R.id.btn_Cancel);
        checkout = (Button) findViewById(R.id.btn_CheckOut);
        cartNumber = (TextView) findViewById(R.id.txtv_cartNumber);
        cartNumber.setText(cart.size() + "");

        Intent intent = getIntent();
        final Customer loggin_Customer = (Customer) intent.getSerializableExtra("Customer");

        food.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newOrderIntent = new Intent(NewOrderActivity.this, FoodActivity.class);
                newOrderIntent.putExtra("Customer", loggin_Customer);
                startActivity(newOrderIntent);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(NewOrderActivity.this,"Order cancelled", Toast.LENGTH_SHORT).show();
                cart.clear();
                Intent newOrderIntent = new Intent(NewOrderActivity.this, LandingActivity.class);
                newOrderIntent.putExtra("Customer", loggin_Customer);
                startActivity(newOrderIntent);
            }
        });

        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(NewOrderActivity.this,"Processing your order",Toast.LENGTH_SHORT).show();
                //Create order here

                String time_string = DateFormat.getTimeInstance().format(new java.util.Date()).replaceAll("\\s+","");
                String date_string = getDate();
                int orderID = 0;

                String sqlstring = "INSERT INTO Order_\n" +
                        "                  (order_NumberOfProducts, order_TotalPrice, order_DatePlaced, order_TimePlaced, order_TimeDelivered, order_CompletionStatus, employee_ID, cust_ID)\n" +
                        "VALUES ('0','0', '"+date_string+"' , '"+time_string+"' ,"+null+",'Not Complete','7','"+loggin_Customer.m_ID+"');" +
                        "SELECT order_ID, order_NumberOfProducts, order_TotalPrice, order_DatePlaced, order_TimePlaced, order_TimeDelivered, order_CompletionStatus, employee_ID, cust_ID FROM Order_ WHERE (order_ID = SCOPE_IDENTITY())";

                NewOrderActivity.SQLQueryExecutor SQLQS9 = new NewOrderActivity.SQLQueryExecutor();

                SQLQS9.execute(sqlstring);

                String sqlString2 = "SELECT TOP 1 * FROM Order_ ORDER BY order_ID DESC\n";

                NewOrderActivity.SQLQueryExecutor SQLQS11 = new NewOrderActivity.SQLQueryExecutor();

                SQLQS11.execute(sqlString2);

                try{
                    ResultSet QUERY_RESULTS;
                    QUERY_RESULTS = SQLQS11.get();

                    QUERY_RESULTS.next();

                    if(!QUERY_RESULTS.isBeforeFirst()){
                        Toast.makeText(NewOrderActivity.this,QUERY_RESULTS.getString(1),Toast.LENGTH_LONG).show();
                        orderID = QUERY_RESULTS.getInt(1);
                    }
                    else {
                       orderID = QUERY_RESULTS.getInt(1);
                    }

                    QUERY_RESULTS.close();
                    SQLQS11.CLOSE();
                }
                catch (InterruptedException e){
                    e.printStackTrace();
                    Toast.makeText(NewOrderActivity.this, "Internal Error: InterruptedException", Toast.LENGTH_SHORT).show();
                    SQLQS11.CLOSE();
                }
                catch (ExecutionException e){
                    e.printStackTrace();
                    Toast.makeText(NewOrderActivity.this, "Internal Error: ExecutionException", Toast.LENGTH_SHORT).show();
                    SQLQS11.CLOSE();
                }
                catch (SQLException e){
                    e.printStackTrace();
                    Toast.makeText(NewOrderActivity.this, "No internet connection!", Toast.LENGTH_SHORT).show();
                    SQLQS11.CLOSE();
                }


                Iterator it = cart.entrySet().iterator();

                        while(it.hasNext()){
                            Map.Entry pair = (Map.Entry)it.next();
                            Product p = (Product)pair.getValue();
                            TotalPrice += p.productPrice;
                            //create each order product here

                            String queryString = "SELECT FreshlyMadeProduct_Ingredient.*, fmproduct_ID AS Expr1\n" +
                                    "FROM     FreshlyMadeProduct_Ingredient\n" +
                                    "WHERE  (fmproduct_ID = '"+p.productID+"')";

                            String insertQuery = "INSERT INTO Order_Product\n" +
                                    "             (order_ID, fmproduct_ID, otsproduct_ID, orderproduct_Quantity, orderproduct_CurrentPrice, producttype_ID)\n" +
                                    "VALUES ('"+orderID+"','"+p.productID+"',"+null+",'"+pair.getKey()+"',"+p.productPrice+",'1')";

                            NewOrderActivity.SQLQueryExecutor r = new NewOrderActivity.SQLQueryExecutor();

                            r.execute(insertQuery);

                            Map<Integer,Integer> recipie = new HashMap<Integer, Integer>();

                            NewOrderActivity.SQLQueryExecutor SQLQS1 = new NewOrderActivity.SQLQueryExecutor();

                            SQLQS1.execute(queryString);

                            try{
                                ResultSet QUERY_RESULTS;
                                QUERY_RESULTS = SQLQS1.get();

                                if(!QUERY_RESULTS.isBeforeFirst()){

                                }
                                else {
                                    while(QUERY_RESULTS.next()){
                                        recipie.put(Integer.parseInt(QUERY_RESULTS.getString(2)), Integer.parseInt(QUERY_RESULTS.getString(3)));
                                    }
                                }

                                QUERY_RESULTS.close();
                                SQLQS1.CLOSE();
                            }
                            catch (InterruptedException e){
                                e.printStackTrace();
                                Toast.makeText(NewOrderActivity.this, "Internal Error: InterruptedException", Toast.LENGTH_SHORT).show();
                                SQLQS1.CLOSE();
                            }
                            catch (ExecutionException e){
                                e.printStackTrace();
                                Toast.makeText(NewOrderActivity.this, "Internal Error: ExecutionException", Toast.LENGTH_SHORT).show();
                                SQLQS1.CLOSE();
                            }
                            catch (SQLException e){
                                e.getStackTrace()[0].getLineNumber();
                                System.out.println("ERROR HERE!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!: 1");
                                e.printStackTrace();
                                Toast.makeText(NewOrderActivity.this, "No internet connection!", Toast.LENGTH_SHORT).show();
                                SQLQS1.CLOSE();
                            }

                            Iterator iit = recipie.entrySet().iterator();

                            while(iit.hasNext()){
                                Map.Entry pair2 = (Map.Entry)iit.next();

                                String SELECT_QUERY3 = "SELECT Ingredient.*, ingredient_ID AS Expr1\n" +
                                        "FROM     Ingredient\n" +
                                        "WHERE  (ingredient_ID = '"+pair2.getKey()+"')";

                                NewOrderActivity.SQLQueryExecutor SQLQS2 = new NewOrderActivity.SQLQueryExecutor();

                                SQLQS2.execute(SELECT_QUERY3);

                                try{
                                    ResultSet QUERY_RESULTS;
                                    QUERY_RESULTS = SQLQS2.get();

                                    if(!QUERY_RESULTS.isBeforeFirst()){

                                    }
                                    else {
                                        while(QUERY_RESULTS.next()){

                                            Integer qty = QUERY_RESULTS.getInt(3);
                                            String updateQS = "UPDATE Ingredient\n" +
                                                    "SET       ingredient_Quantity = '"+(qty - ((Integer)pair.getKey() * (Integer)pair2.getValue()))+"'\n" +
                                                    "WHERE (ingredient_ID = '"+pair2.getKey()+"')";

                                            NewOrderActivity.SQLQueryExecutor SQLQS4 = new NewOrderActivity.SQLQueryExecutor();

                                            SQLQS4.execute(updateQS);
                                        }
                                    }


                                    SQLQS2.CLOSE();
                                }
                                catch (InterruptedException e){
                                    e.printStackTrace();
                                    Toast.makeText(NewOrderActivity.this, "Internal Error: InterruptedException", Toast.LENGTH_SHORT).show();
                                    SQLQS2.CLOSE();
                                }
                                catch (ExecutionException e){
                                    e.printStackTrace();
                                    Toast.makeText(NewOrderActivity.this, "Internal Error: ExecutionException", Toast.LENGTH_SHORT).show();
                                    SQLQS2.CLOSE();
                                }
                                catch (SQLException e){
                                    e.getStackTrace()[0].getLineNumber();
                                    e.printStackTrace();
                                    Toast.makeText(NewOrderActivity.this, "No internet connection!", Toast.LENGTH_SHORT).show();
                                    SQLQS2.CLOSE();
                                }
                            }


                        }

                        String updateOrder = "UPDATE Order_\n" +
                                "SET       order_NumberOfProducts = '"+cart.size()+"', order_TotalPrice = '"+TotalPrice+"'\n" +
                                "WHERE (order_ID = '"+orderID+"')";

                        NewOrderActivity.SQLQueryExecutor d = new NewOrderActivity.SQLQueryExecutor();

                        d.execute(updateOrder);

                        Toast.makeText(NewOrderActivity.this,"Complete",Toast.LENGTH_SHORT).show();

                        Intent newOrderIntent = new Intent(NewOrderActivity.this,CheckoutActivity.class);
                        newOrderIntent.putExtra("Customer", loggin_Customer);
                        newOrderIntent.putExtra("OrderNumber", orderID);

                        startActivity(newOrderIntent);
                    }

            });

    }

    private static String getDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "YYYY-MM-dd", Locale.getDefault());
        java.util.Date date = new java.util.Date();
        return dateFormat.format(date);
    }

    private static String getTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "HH-MM-SS", Locale.getDefault());
        java.util.Date date = new java.util.Date();
        return dateFormat.format(date);
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
        Toast.makeText(NewOrderActivity.this, "Please cancel order first!", Toast.LENGTH_SHORT).show();
    }
}
