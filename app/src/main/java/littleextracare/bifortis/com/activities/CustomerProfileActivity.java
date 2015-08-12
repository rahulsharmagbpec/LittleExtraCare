package littleextracare.bifortis.com.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import littleextracare.bifortis.com.Constants.SharedPrefConstants;
import littleextracare.bifortis.com.data.SharedPref;

public class CustomerProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_profile);

        // Edit Payment Click Listener :: open EditPaymentActivity
        TextView paymentMethodEditText = (TextView) findViewById(R.id.textView10);
        paymentMethodEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustomerProfileActivity.this, EditPaymentActivity.class);
                startActivity(intent);
            }
        });

    }

    public void editButtonClicked(View v)
    {
        Intent intent = new Intent(this, EditCustomerProfileActivity.class);
        startActivity(intent);
        //Toast.makeText(getApplicationContext(), "edit clciked", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout)
        {
            Toast.makeText(getApplicationContext(), "Logout", Toast.LENGTH_SHORT).show();
            SharedPref.removeData(this, SharedPrefConstants.userId);
            SharedPref.removeData(this, SharedPrefConstants.password);

            Intent intent = new Intent(this, LetsGoActivity.class);
            startActivity(intent);
            finish();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
