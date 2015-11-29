package teamtreehouse.com.stormy.ui;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import teamtreehouse.com.stormy.R;

public class HourlyForecastActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hourly_forecast);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
