package za.co.ktpsolutions.www.adamscoffeebar;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Set;

public class CustActivity extends AppCompatActivity {

    Button newtoadams;
    Button existing;
    Button returntologgin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cust);

        newtoadams = (Button) findViewById(R.id.btn_newtoadams);
        existing = (Button) findViewById(R.id.btn_alreadyregisted);
        returntologgin = (Button) findViewById(R.id.btn_cust_return);

        newtoadams.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CustActivity.this, RegisterActivity.class));
            }
        });

        existing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CustActivity.this, SetPasswordActivity.class));
            }
        });

        returntologgin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CustActivity.this, MainActivity.class));
            }
        });
    }

    @Override
    public void onBackPressed(){
        Toast.makeText(CustActivity.this, "Inactive", Toast.LENGTH_SHORT).show();
    }
}
