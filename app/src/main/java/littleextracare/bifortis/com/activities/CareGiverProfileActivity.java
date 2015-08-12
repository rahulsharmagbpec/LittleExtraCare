package littleextracare.bifortis.com.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import littleextracare.bifortis.com.Constants.Constants;
import littleextracare.bifortis.com.Constants.SharedPrefConstants;
import littleextracare.bifortis.com.data.SharedPref;

public class CareGiverProfileActivity extends AppCompatActivity {
    TextView tvName;
    TextView tvDist;
    ImageView ivHeart;
    ImageView ivEdu;
    ImageView ivFinger;
    ImageView ivCar;
    ImageView ivDog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_care_giver_profile);

        String name = SharedPref.getData(getApplicationContext(), SharedPrefConstants.PREF_NAME);
        String distance =  SharedPref.getData(getApplicationContext(), SharedPrefConstants.PREF_DISTANCE);
        if(distance.length() > 7) {
            distance = distance.substring(0, 7);
        }

        tvName = (TextView) findViewById(R.id.care_giver_name);
        tvName.setText(name);

        tvDist = (TextView) findViewById(R.id.textviewDistance);
        tvDist.setText(distance);

        ivHeart = (ImageView) findViewById(R.id.imageIcon1);
        ivEdu = (ImageView) findViewById(R.id.imageIcon2);
        ivFinger = (ImageView) findViewById(R.id.imageIcon3);
        ivCar = (ImageView) findViewById(R.id.imageIcon4);
        ivDog = (ImageView) findViewById(R.id.imageIcon5);


        if(SharedPref.getData(getApplicationContext(), SharedPrefConstants.PREF_HEART).equalsIgnoreCase("true"))
            ivHeart.setImageResource(R.drawable.heart_color);
        else
            ivHeart.setImageResource(R.drawable.heart_gray);


        if(SharedPref.getData(getApplicationContext(), SharedPrefConstants.PREF_EDU).equalsIgnoreCase("true"))
            ivEdu.setImageResource(R.drawable.edu_color);
        else
            ivEdu.setImageResource(R.drawable.edu_gray);


        if(SharedPref.getData(getApplicationContext(), SharedPrefConstants.PREF_FINGER).equalsIgnoreCase("true"))
            ivFinger.setImageResource(R.drawable.finger_color);
        else
            ivFinger.setImageResource(R.drawable.finger_gray);


        if(SharedPref.getData(getApplicationContext(), SharedPrefConstants.PREF_CAR).equalsIgnoreCase("true"))
            ivCar.setImageResource(R.drawable.car_color);
        else
            ivCar.setImageResource(R.drawable.car_gray);


        if(SharedPref.getData(getApplicationContext(), SharedPrefConstants.PREF_DOG).equalsIgnoreCase("true"))
            ivDog.setImageResource(R.drawable.dog_color);
        else
            ivDog.setImageResource(R.drawable.dog_gray);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_care_giver_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void startButtonClicked(View v)
    {
        //SharedPref.setData(this, Constants.REQUESTING, "true");
        Intent intent = new Intent(this, UserSideMap.class);
        intent.putExtra(Constants.REQUESTING, true);
        startActivity(intent);
    }
}
