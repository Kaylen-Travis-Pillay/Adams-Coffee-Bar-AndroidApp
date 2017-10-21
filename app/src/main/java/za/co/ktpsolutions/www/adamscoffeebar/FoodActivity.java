package za.co.ktpsolutions.www.adamscoffeebar;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import za.co.ktpsolutions.www.AppAPIClasses.Customer;
import za.co.ktpsolutions.www.AppAPIClasses.Product;
import za.co.ktpsolutions.www.AppAPIClasses.SQL_INFO;

public class FoodActivity extends AppCompatActivity {

    static ResultSet QUERY_RESULTS;
    Map<String, Product> productMap = new HashMap<String, Product>();
    Spinner s;
    Spinner q;
    Button addToCart;
    Button back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food);

        String SELECT_QUERY = "SELECT fmproduct_ID, fmproduct_UnitCost, fmproduct_Description, fmproduct_UnitPrice, fmproduct_Name, fmproduct_Activity, fmproduct_Type\n" +
                "FROM   FreshlyMade_Product\n" +
                "WHERE (fmproduct_Type = 'Food') AND (fmproduct_Activity = 'Active')";

        FoodActivity.SQLQueryExecutor SQLQS = new FoodActivity.SQLQueryExecutor();

        SQLQS.execute(SELECT_QUERY);

        ArrayList<String> j = new ArrayList<String>();

        String[] qArray = new String[]{"1","2","3","4","5","6","7","8","9","10"};

        s = (Spinner) findViewById(R.id.fm_foodspinner);
        q = (Spinner) findViewById(R.id.fm_quantityspinner);
        addToCart = (Button) findViewById(R.id.btn_AddToCart);
        back = (Button) findViewById(R.id.btn_back);

        Intent intent = getIntent();
        final Customer loggin_Customer = (Customer) intent.getSerializableExtra("Customer");

        try{
            QUERY_RESULTS = SQLQS.get();

            if(!QUERY_RESULTS.isBeforeFirst()){

            }
            else {
                while(QUERY_RESULTS.next()){
                    j.add(QUERY_RESULTS.getString(5));
                    Product p = new Product();
                    p.productID = Integer.parseInt(QUERY_RESULTS.getString(1));
                    p.productName = QUERY_RESULTS.getString(5);
                    p.productPrice = QUERY_RESULTS.getDouble(4);

                    productMap.put(p.productName, p);
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                        android.R.layout.simple_spinner_item, j);

                ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this,
                        android.R.layout.simple_spinner_item, qArray);

                q.setAdapter(adapter2);
                s.setAdapter(adapter);
            }


            SQLQS.CLOSE();
        }
        catch (InterruptedException e){
            e.printStackTrace();
            Toast.makeText(FoodActivity.this, "Internal Error: InterruptedException", Toast.LENGTH_SHORT).show();
            SQLQS.CLOSE();
        }
        catch (ExecutionException e){
            e.printStackTrace();
            Toast.makeText(FoodActivity.this, "Internal Error: ExecutionException", Toast.LENGTH_SHORT).show();
            SQLQS.CLOSE();
        }
        catch (SQLException e){
            e.printStackTrace();
            Toast.makeText(FoodActivity.this, "No internet connection!", Toast.LENGTH_SHORT).show();
            SQLQS.CLOSE();
        }

        addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String productName = s.getSelectedItem().toString();
                int quantitySelected = Integer.parseInt(q.getSelectedItem().toString());
                int productID = -1;
                double productPrice = -1;
                boolean engoughIngredients = true;

                String SELECT_QUERY1 = "SELECT fmproduct_ID, fmproduct_UnitCost, fmproduct_Description, fmproduct_UnitPrice, fmproduct_Name\n" +
                        "FROM   FreshlyMade_Product\n" +
                        "WHERE (fmproduct_Name = '"+productName+"')";

                FoodActivity.SQLQueryExecutor SQLQS = new FoodActivity.SQLQueryExecutor();

                SQLQS.execute(SELECT_QUERY1);

                try{
                    ResultSet QUERY_RESULTS;
                    QUERY_RESULTS = SQLQS.get();

                    if(!QUERY_RESULTS.isBeforeFirst()){

                    }
                    else {
                        QUERY_RESULTS.next();

                        productID = Integer.parseInt(QUERY_RESULTS.getString(1));

                        productPrice = Double.parseDouble(QUERY_RESULTS.getString(4));
                    }

                    QUERY_RESULTS.close();
                    SQLQS.CLOSE();
                }
                catch (InterruptedException e){
                    e.printStackTrace();
                    Toast.makeText(FoodActivity.this, "Internal Error: InterruptedException", Toast.LENGTH_SHORT).show();
                    SQLQS.CLOSE();
                }
                catch (ExecutionException e){
                    e.printStackTrace();
                    Toast.makeText(FoodActivity.this, "Internal Error: ExecutionException", Toast.LENGTH_SHORT).show();
                    SQLQS.CLOSE();
                }
                catch (SQLException e){
                    e.printStackTrace();
                    Toast.makeText(FoodActivity.this, "No internet connection!", Toast.LENGTH_SHORT).show();
                    SQLQS.CLOSE();
                }


                String SELECT_QUERY2 = "SELECT fmproduct_ID, ingredient_ID, fmproductingredient_Quantity, fmproductingredient_RecipeCost\n" +
                        "FROM   FreshlyMadeProduct_Ingredient\n" +
                        "WHERE (fmproduct_ID = '"+productID+"')";

                Map<Integer,Integer> recipie = new HashMap<Integer, Integer>();

                FoodActivity.SQLQueryExecutor SQLQS1 = new FoodActivity.SQLQueryExecutor();

                SQLQS1.execute(SELECT_QUERY2);

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
                    Toast.makeText(FoodActivity.this, "Internal Error: InterruptedException", Toast.LENGTH_SHORT).show();
                    SQLQS1.CLOSE();
                }
                catch (ExecutionException e){
                    e.printStackTrace();
                    Toast.makeText(FoodActivity.this, "Internal Error: ExecutionException", Toast.LENGTH_SHORT).show();
                    SQLQS1.CLOSE();
                }
                catch (SQLException e){
                    e.printStackTrace();
                    Toast.makeText(FoodActivity.this, "No internet connection!", Toast.LENGTH_SHORT).show();
                    SQLQS1.CLOSE();
                }


                Iterator it = recipie.entrySet().iterator();

                while(it.hasNext()){
                    Map.Entry pair = (Map.Entry)it.next();

                    String SELECT_QUERY3 = "SELECT Ingredient.*, ingredient_ID AS Expr1\n" +
                            "FROM     Ingredient\n" +
                            "WHERE  (ingredient_ID = '"+pair.getKey()+"')";

                    FoodActivity.SQLQueryExecutor SQLQS2 = new FoodActivity.SQLQueryExecutor();

                    SQLQS2.execute(SELECT_QUERY3);

                    try{
                        ResultSet QUERY_RESULTS;
                        QUERY_RESULTS = SQLQS2.get();

                        if(!QUERY_RESULTS.isBeforeFirst()){

                        }
                        else {
                            while(QUERY_RESULTS.next()){
                                if(((int)pair.getValue()) * quantitySelected >= Integer.parseInt(QUERY_RESULTS.getString(3))){
                                        engoughIngredients = false;
                                        break;
                                }
                            }
                        }


                        SQLQS2.CLOSE();
                    }
                    catch (InterruptedException e){
                        e.printStackTrace();
                        Toast.makeText(FoodActivity.this, "Internal Error: InterruptedException", Toast.LENGTH_SHORT).show();
                        SQLQS2.CLOSE();
                    }
                    catch (ExecutionException e){
                        e.printStackTrace();
                        Toast.makeText(FoodActivity.this, "Internal Error: ExecutionException", Toast.LENGTH_SHORT).show();
                        SQLQS2.CLOSE();
                    }
                    catch (SQLException e){
                        e.printStackTrace();
                        Toast.makeText(FoodActivity.this, "No internet connection!", Toast.LENGTH_SHORT).show();
                        SQLQS2.CLOSE();
                    }
                }


                if(engoughIngredients){

                    Toast.makeText(FoodActivity.this,"Product Added to cart",Toast.LENGTH_SHORT).show();
                    NewOrderActivity.cart.put(quantitySelected, productMap.get(productName));
                }
                else{
                    Toast.makeText(FoodActivity.this,"Not Enough Ingredients to make this product!",Toast.LENGTH_SHORT).show();
                }

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newOrderIntent = new Intent(FoodActivity.this,NewOrderActivity.class);
                newOrderIntent.putExtra("Customer", loggin_Customer);
                startActivity(newOrderIntent);
            }
        });


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
        Toast.makeText(FoodActivity.this, "Unavailable", Toast.LENGTH_SHORT).show();
    }
}
