package za.co.ktpsolutions.www.adamscoffeebar;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import za.co.ktpsolutions.www.AppAPIClasses.Customer;
import za.co.ktpsolutions.www.AppAPIClasses.SQL_INFO;

public class OrderHistoryActivity extends AppCompatActivity {

    ListView list;
    static ResultSet QUERY_RESULTS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        list = (ListView) findViewById(R.id.lview);

        HashMap<String, String> orderHistory = new HashMap<>();

        Intent intent = getIntent();
        final Customer loggin_Customer = (Customer) intent.getSerializableExtra("Customer");

        String s1 = "SELECT order_ID, order_TotalPrice, order_DatePlaced, order_CompletionStatus\n" +
                "FROM   Order_\n" +
                "WHERE (cust_ID = '"+loggin_Customer.m_ID+"')";


        OrderHistoryActivity.SQLQueryExecutor SQLEX1 = new OrderHistoryActivity.SQLQueryExecutor();

        SQLEX1.execute(s1);

        try{
            ResultSet QR;
            QR = SQLEX1.get();

            if(!QR.isBeforeFirst()){
                Toast.makeText(OrderHistoryActivity.this,"Sorry " + loggin_Customer.m_Name + ", you" +
                        " have no orders!", Toast.LENGTH_LONG).show();
            }
            else{

                while(QR.next()){
                    String Title = "Order No. "+QR.getString(1) + ", Date: " + QR.getString(3);

                    String s2 = "SELECT FreshlyMade_Product.fmproduct_Name, Order_Product.orderproduct_Quantity\n" +
                            "FROM   Order_ INNER JOIN\n" +
                            "             Order_Product ON Order_.order_ID = Order_Product.order_ID INNER JOIN\n" +
                            "             FreshlyMade_Product ON Order_Product.fmproduct_ID = FreshlyMade_Product.fmproduct_ID\n" +
                            "WHERE (Order_.order_ID = '"+QR.getInt(1)+"')";

                    OrderHistoryActivity.SQLQueryExecutor SQLEX2 = new OrderHistoryActivity.SQLQueryExecutor();

                    SQLEX2.execute(s2);

                    try{
                        ResultSet QR2;
                        QR2 = SQLEX2.get();

                        StringBuilder k = new StringBuilder();

                        while(QR2.next()){
                            k.append(QR2.getString(2) + "x " + QR2.getString(1) + "\n");
                        }

                        k.append("Total: R" + Double.parseDouble(QR.getString(2)) + "\n");
                        k.append("Order Status: " + QR.getString(4));


                        orderHistory.put(Title,k.toString());
                        QR2.close();
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


            }

            QR.close();
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


        List<HashMap<String, String>> listItems = new ArrayList<>();
        SimpleAdapter adapter = new SimpleAdapter(this, listItems, R.layout.list_item,
                new String[]{"First Line","Second Line"},
                new int[] {R.id.title_txt, R.id.subtitle_txt});

        Iterator it = orderHistory.entrySet().iterator();

        while (it.hasNext()){
            HashMap<String, String> resultsMap = new HashMap<>();
            Map.Entry pair = (Map.Entry) it.next();
            resultsMap.put("First Line", pair.getKey().toString());
            resultsMap.put("Second Line", pair.getValue().toString());
            listItems.add(resultsMap);
        }

        list.setAdapter(adapter);

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
