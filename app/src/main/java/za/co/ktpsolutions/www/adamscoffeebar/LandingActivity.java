package za.co.ktpsolutions.www.adamscoffeebar;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import za.co.ktpsolutions.www.AppAPIClasses.Customer;

public class LandingActivity extends AppCompatActivity {

    TextView username;
    Button newOrder;
    Button currentOrderStatus;
    Button orderHistory;
    Button myProfile;
    Button logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        username = (TextView) findViewById(R.id.txtv_username);
        newOrder = (Button) findViewById(R.id.btn_NewOrder);
        currentOrderStatus = (Button) findViewById(R.id.btn_CurrentOrderStatus);
        orderHistory = (Button) findViewById(R.id.btn_OrderHistory);
        myProfile = (Button) findViewById(R.id.btn_MyProfile);
        logout = (Button) findViewById(R.id.btn_Logout);

        Intent intent = getIntent();
        final Customer loggin_Customer = (Customer) intent.getSerializableExtra("Customer");

        username.setText(loggin_Customer.m_Name);

        newOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent newOrderIntent = new Intent(LandingActivity.this, NewOrderActivity.class);
                newOrderIntent.putExtra("Customer", loggin_Customer);
                startActivity(newOrderIntent);

            }
        });

        currentOrderStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newOrderIntent = new Intent(LandingActivity.this, CurrentOrderActivity.class);
                newOrderIntent.putExtra("Customer", loggin_Customer);
                startActivity(newOrderIntent);
            }
        });

        orderHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newOrderIntent = new Intent(LandingActivity.this, OrderHistoryActivity.class);
                newOrderIntent.putExtra("Customer", loggin_Customer);
                startActivity(newOrderIntent);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LandingActivity.this, MainActivity.class));
            }
        });
    }

    @Override
    public void onBackPressed(){
        Toast.makeText(LandingActivity.this, "Please log out first!", Toast.LENGTH_SHORT).show();
    }
}
